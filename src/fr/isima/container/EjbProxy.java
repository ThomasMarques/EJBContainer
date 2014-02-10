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
	 * L'interface à proxyer.
	 */
	private Class<?> interfaceProxy;

	/**
	 * Liste des transactions créées au cours d'un appel.
	 */
	private static List<Transaction> listOfCurrentTransaction = new ArrayList<Transaction>();

	/**
	 * Transaction courante à l'instance.
	 */
	private Transaction currentTransaction = null;

	/**
	 * Index de la transaction dans la liste statique.
	 */
	private int indexOfTransaction = -1;

	/**
	 * Constructeur par défaut.
	 * @param interfaceProxy
	 */
	public EjbProxy(Class<?> interfaceProxy) {
		this.interfaceProxy = interfaceProxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// On récupère l'instance en fonction de la strategie du singleton.
		Object instance = EjbInstanceManager.getInstance().getEjbInstance(
				interfaceProxy, proxy);

		// Création de la transaction
		beginTransaction(instance, method);

		// on invoque la méthode.
		Object returnObject = method.invoke(instance, args);

		// On termine la transaction
		endTransaction();

		// On retourn l'objet retourné par l'invocation de la méthode.
		return returnObject;
	}

	/**
	 * Gère la création de la transaction.
	 * @param instance Instance de l'EJB à transactionner (récupération de l'annotation).
	 * @param method method appelé pour la transaction (récupération de l'annotation).
	 * @throws NoSuchMethodException
	 * @throws SecurityException 
	 */
	private void beginTransaction(Object instance, Method method) throws NoSuchMethodException, SecurityException {
		TransactionAttribute annotation = null;
		TransactionAttributeType type = TransactionAttributeType.REQUIRED;

		// On récupère le transactionAttribute de la classe si elle existe.
		annotation = instance.getClass().getAnnotation(
				TransactionAttribute.class);
		if (annotation != null) {
			type = annotation.transactionAttributeType();
		}
		// On récupère le transactionAttribute de la méthode si elle existe.
		// Pour cela on doit déjà récupérer la déclaration de la méthode dans la classe fille
		Class<?>[] param = method.getParameterTypes();
		Method childMethod = instance.getClass().getMethod(method.getName(), (param.length > 0 ? param : null));
		annotation = childMethod.getAnnotation(TransactionAttribute.class);
		if (annotation != null) {
			// L'annotation de la méthode prédomine.
			type = annotation.transactionAttributeType();
		}

		switch (type) {
		case REQUIRED:
			indexOfTransaction = listOfCurrentTransaction.size();
			if (indexOfTransaction == 0) {
				// Pas de transaction actuellement créée, on doit en créé une
				// nouvelle.
				currentTransaction = new Transaction(instance);
				listOfCurrentTransaction.add(currentTransaction);
				currentTransaction.begin();
			} else {
				// On récupère la dernière transaction créée
				currentTransaction = listOfCurrentTransaction
						.get(indexOfTransaction - 1);
				// On passe l'index à -1 car c'est le proxy qui a créé la
				// transaction qui la détruira.
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
			// On créé la transaction et on la stocke
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
	 * Termine et détruit la transaction de l'instance.
	 */
	private void endTransaction() {
		// Si l'index est égal à -1 la transaction n'a pas été créée dans cette
		// instance ce n'est donc pas notre responsabilité de la terminer.
		if (indexOfTransaction != -1) {
			// On met fin à la transaction et on la supprime
			currentTransaction.end();
			listOfCurrentTransaction.remove(currentTransaction);
			// On réveille la précédente si elle existe.
			if (indexOfTransaction > 0) {
				listOfCurrentTransaction.get(indexOfTransaction - 1).awake();
			}
		}
	}
}
