package fr.isima.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.isima.container.annotations.TransactionAttribute;
import fr.isima.container.annotations.TransactionAttribute.TransactionAttributeType;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class EjbProxy implements InvocationHandler {

	/**
	 * L'interface � proxyer.
	 */
	private Class<?> interfaceProxy;

	/**
	 * Liste des transactions cr��es au cours d'un appel.
	 */
	private static List<Transaction> listOfCurrentTransaction = new ArrayList<Transaction>();

	/**
	 * Transaction courante � l'instance.
	 */
	private Transaction currentTransaction = null;

	/**
	 * Index de la transaction dans la liste statique.
	 */
	private int indexOfTransaction = -1;

	/**
	 * Constructeur par d�faut.
	 * @param interfaceProxy
	 */
	public EjbProxy(Class<?> interfaceProxy) {
		this.interfaceProxy = interfaceProxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// On r�cup�re l'instance en fonction de la strategie du singleton.
		Object instance = EjbInstanceManager.getInstance().getEjbInstance(
				interfaceProxy, proxy);

		// Cr�ation de la transaction
		beginTransaction(instance, method);

		// on invoque la m�thode.
		Object returnObject = method.invoke(instance, args);

		// On termine la transaction
		endTransaction();

		// On retourn l'objet retourn� par l'invocation de la m�thode.
		return returnObject;
	}

	/**
	 * G�re la cr�ation de la transaction.
	 * @param instance Instance de l'EJB � transactionner (r�cup�ration de l'annotation).
	 * @param method method appel� pour la transaction (r�cup�ration de l'annotation).
	 * @throws NoSuchMethodException
	 * @throws SecurityException 
	 */
	private void beginTransaction(Object instance, Method method) throws NoSuchMethodException, SecurityException {
		TransactionAttribute annotation = null;
		TransactionAttributeType type = TransactionAttributeType.REQUIRED;

		// On r�cup�re le transactionAttribute de la classe si elle existe.
		annotation = instance.getClass().getAnnotation(
				TransactionAttribute.class);
		if (annotation != null) {
			type = annotation.transactionAttributeType();
		}
		// On r�cup�re le transactionAttribute de la m�thode si elle existe.
		// Pour cela on doit d�j� r�cup�rer la d�claration de la m�thode dans la classe fille
		Class<?>[] param = method.getParameterTypes();
		Method childMethod = instance.getClass().getMethod(method.getName(), (param.length > 0 ? param : null));
		annotation = childMethod.getAnnotation(TransactionAttribute.class);
		if (annotation != null) {
			// L'annotation de la m�thode pr�domine.
			type = annotation.transactionAttributeType();
		}

		switch (type) {
		case REQUIRED:
			indexOfTransaction = listOfCurrentTransaction.size();
			if (indexOfTransaction == 0) {
				// Pas de transaction actuellement cr��e, on doit en cr�� une
				// nouvelle.
				currentTransaction = new Transaction(instance);
				listOfCurrentTransaction.add(currentTransaction);
				currentTransaction.begin();
			} else {
				// On r�cup�re la derni�re transaction cr��e
				currentTransaction = listOfCurrentTransaction
						.get(indexOfTransaction - 1);
				// On passe l'index � -1 car c'est le proxy qui a cr�� la
				// transaction qui la d�truira.
				indexOfTransaction = -1;
			}
			break;
		case REQUIRES_NEW:
			indexOfTransaction = listOfCurrentTransaction.size();
			// S'il existait une transaction, on l'endors;
			if (indexOfTransaction != 0) {
				listOfCurrentTransaction.get(
						listOfCurrentTransaction.size() - 1).sleep();
			}
			// On cr�� la transaction et on la stocke
			currentTransaction = new Transaction(instance);
			listOfCurrentTransaction.add(currentTransaction);
			currentTransaction.begin();
			break;
		case NEVER:
			indexOfTransaction = -1;
			break;
		}
	}

	/**
	 * Termine et d�truit la transaction de l'instance.
	 */
	private void endTransaction() {
		// Si l'index est �gal � -1 la transaction n'a pas �t� cr��e dans cette
		// instance ce n'est donc pas notre responsabilit� de la terminer.
		if (indexOfTransaction != -1) {
			// On met fin � la transaction et on la supprime
			currentTransaction.end();
			listOfCurrentTransaction.remove(currentTransaction);
			// On r�veille la pr�c�dente si elle existe.
			if (indexOfTransaction > 0) {
				listOfCurrentTransaction.get(indexOfTransaction - 1).awake();
			}
		}
	}
}
