package fr.isima.container;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import fr.isima.container.annotations.*;
import fr.isima.container.exceptions.EJBStrategyNotFoundException;
import fr.isima.container.exceptions.InterfaceAlreadyImplementedException;
import fr.isima.container.exceptions.InterfaceNotFoundException;
import fr.isima.container.exceptions.PersistenceUnitNotFound;
import fr.isima.container.persistence.EntityManagerFactory;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 */
public class EJBContainer {

	/**
	 * @see Singleton
	 * @see getInstance
	 */
	private static EJBContainer INSTANCE;

	/**
	 * @see Singleton
	 * @return the current instance of EjbContainer
	 * @throws InterfaceAlreadyImplementedException @see bootstrapInit
	 */
	public static EJBContainer getInstance()
			throws InterfaceAlreadyImplementedException {
		if (INSTANCE == null) {
			INSTANCE = new EJBContainer();
		}
		return INSTANCE;
	}

	/**
	 * The constructor of the class.
	 * 
	 * @throws InterfaceAlreadyImplementedException @see bootstrapInit
	 */
	private EJBContainer() throws InterfaceAlreadyImplementedException {
		bootstrapInit();
	}

	/**
	 * Collecte donn�e savoir annotations. Cr�ation du cache de proxy
	 * 
	 * @throws InterfaceAlreadyImplementedException Exception rejett�e si
	 *             l'interface est d�j� associ�e � une base.
	 */
	private void bootstrapInit() throws InterfaceAlreadyImplementedException {

		// Scan all classes of the classloader
		// find EJB interface map EJB to implementation
		Reflections reflections = new Reflections();

		// On commence par r�cup�rer tous les ejb.
		Set<Class<?>> classToAnalysed = reflections
				.getTypesAnnotatedWith(Singleton.class);
		classToAnalysed.addAll(reflections
				.getTypesAnnotatedWith(Stateless.class));
		classToAnalysed.addAll(reflections
				.getTypesAnnotatedWith(Statefull.class));

		for (Class<?> ejbClass : classToAnalysed) {
			for (Class<?> infacesImplemented : ejbClass.getInterfaces()) {
				// / On associe l'interface et la classe r�elle
				EjbInstanceManager.getInstance().addInterfaceEjb(
						infacesImplemented, ejbClass);
			}
		}
	}

	/**
	 * Permet d'instancier les sous EJB et appel les m�thodes annot�e
	 * postConstruct
	 * 
	 * @param instance Instance de l'EJB venant d'�tre inject�e.
	 * @throws EJBStrategyNotFoundException @see inject
	 * @throws InterfaceNotFoundException @see inject
	 * @throws IllegalAccessException @see inject
	 * @throws IllegalArgumentException @see inject
	 */
	void postConstruct(Object instance) throws EJBStrategyNotFoundException,
			InterfaceNotFoundException, IllegalAccessException,
			IllegalArgumentException {
		// / On inject dans l'ejb dans le cas ou l'ejb en contient un autre
		inject(instance);

		// / On appelle l'�ventuel @postConstruct.
		for (Method method : instance.getClass().getMethods()) {
			if (method.getAnnotation(PostConstruct.class) != null) {
				try {
					method.invoke(instance, new Object[0]);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Permet de d�truire un EJB et d'appeler les m�thodes annot�es
	 * postConstruct
	 * 
	 * @param ejb L'instance � d�truire
	 * @throws IllegalAccessException @see Method.invoke
	 * @throws IllegalArgumentException @see Method.invoke
	 */
	public void destroyEJB(Object ejb) throws IllegalAccessException,
			IllegalArgumentException {

		// On appel l'�ventuel @PreDestroy.
		Reflections reflections = new Reflections(ClasspathHelper.forClass(ejb
				.getClass()), new MethodAnnotationsScanner());
		for (Method method : reflections
				.getMethodsAnnotatedWith(PreDestroy.class)) {
			try {
				method.invoke(ejb, new Object[0]);
			} catch (InvocationTargetException e) {
			}
		}
	}

	/**
	 * injecte un EJB dans une instance pass�e en param�tre.
	 * 
	 * @param objectInjected L'objet dans lequel on doit injecter les ejb.
	 * @throws EJBStrategyNotFoundException Exception rejett�e si l'ejb � instancier n'a pas pr�cis� sa strat�gie.
	 */
	public void inject(Object objectInjected)
			throws EJBStrategyNotFoundException {

		// On va cr�er un proxy par client et par interface.
		Map<Class<?>, Object> mapProxyByInterface = new HashMap<Class<?>, Object>();

		// scan all @EJB and inject EJB implementations
		for (Field field : objectInjected.getClass().getFields()) {
			if (field.getAnnotation(EJB.class) != null) {
				try {
					// / On r�cup�re l'interface et la classe associ�e
					Class<?> myInterface = field.getType();

					// / On cr�� le proxy uniquement lorsqu'on trouve un
					// attribut � impl�menter
					Object proxy = mapProxyByInterface.get(myInterface);
					if (proxy == null) {
						proxy = Proxy.newProxyInstance(myInterface
								.getClassLoader(), new Class[] { myInterface },
								new EjbProxy(myInterface));
						mapProxyByInterface.put(myInterface, proxy);
					}

					field.set(objectInjected, proxy);

				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}

		// manage @PersistenceContext injection
		for (Field field : objectInjected.getClass().getFields()) {

			if (field.getAnnotation(PersistenceContext.class) != null) {

				String namePU = field.getAnnotation(PersistenceContext.class)
						.unitName();
				try {
					EntityManagerFactory.getInstance().instanciateField(
							objectInjected, field, namePU);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (PersistenceUnitNotFound e) {
					e.printStackTrace();
				}
			}
		}
	}
}
