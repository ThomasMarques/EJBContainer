package fr.isima.container;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import fr.isima.container.annotations.Singleton;
import fr.isima.container.annotations.Statefull;
import fr.isima.container.annotations.Stateless;
import fr.isima.container.exceptions.EJBStrategyNotFoundException;
import fr.isima.container.exceptions.InterfaceAlreadyImplementedException;
import fr.isima.container.exceptions.InterfaceNotFoundException;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 */
public class EjbInstanceManager {

	/**
	 * Seule instance cr��e de la classe. @see Singleton
	 */
	private static EjbInstanceManager instance = new EjbInstanceManager();

	/**
	 * Map permettant d'associer une interface d'Ejb � la classe permettant de
	 * l'impl�menter.
	 */
	private Map<Class<?>, Class<?>> mapInterfaceEjb = new HashMap<Class<?>, Class<?>>();

	/**
	 * Map permettant de garder une instance cr��e par interface d'EJB. Utilis�
	 * par les EJB avec l'annotation Singleton
	 */
	private Map<Class<?>, Object> mapEjbSingleton = new IdentityHashMap<Class<?>, Object>();

	/**
	 * Map permettant de garder une instance cr��e par interface d'EJB et par
	 * client. Utilis� par les EJB avec l'annotation statefull
	 */
	private Map<Object, Object> mapEjbStatefull = new IdentityHashMap<Object, Object>();

	/**
	 * On garde en m�moire le dernier Ejb stateless cr�� pour le d�truire une
	 * fois qu'il aura fait son job.
	 */
	private Object lastEjbStateLess = null;

	/**
	 * Constructeur priv�. @see Singleton.
	 */
	private EjbInstanceManager() {
	}

	/**
	 * Permet d'associer une interface et un EJB.
	 * 
	 * @param interfaceEjb L'interface de l'EJB.
	 * @param classEjb La classe impl�mentant l'interface.
	 * @throws InterfaceAlreadyImplementedException
	 */
	public void addInterfaceEjb(Class<?> interfaceEjb, Class<?> classEjb)
			throws InterfaceAlreadyImplementedException {
		if (mapInterfaceEjb.containsKey(interfaceEjb))
			throw new InterfaceAlreadyImplementedException();
		mapInterfaceEjb.put(interfaceEjb, classEjb);
	}

	/**
	 * Permet de r�cup�rer une instance correspondant � l'interface pass�e en
	 * param�tre.
	 * 
	 * @param myInterface L'interface � "impl�menter".
	 * @param proxy Proxy appelant la fonction.
	 * @return l'instance.
	 * @throws InstantiationException @see Class.newInstance
	 * @throws IllegalAccessException @see postContruct
	 * @throws EJBStrategyNotFoundException Si la strategie de l'EJB n'est pas
	 *             d�finie.
	 * @throws IllegalArgumentException
	 * @throws InterfaceNotFoundException
	 * @throws InterfaceAlreadyImplementedException
	 */
	public Object getEjbInstance(Class<?> myInterface, Object proxy)
			throws InstantiationException, IllegalAccessException,
			EJBStrategyNotFoundException, IllegalArgumentException,
			InterfaceNotFoundException, InterfaceAlreadyImplementedException {
		Class<?> myClass = mapInterfaceEjb.get(myInterface);
		if (myClass == null) {
			throw new EJBStrategyNotFoundException();
		}

		Object instance = null;
		if (myClass.getAnnotation(Singleton.class) != null) {

			instance = mapEjbSingleton.get(myInterface);
			if (instance == null) {
				instance = myClass.newInstance();
				mapEjbSingleton.put(myInterface, instance);
				EJBContainer.getInstance().postConstruct(instance);
			}
		} else if (myClass.getAnnotation(Statefull.class) != null) {

			instance = mapEjbStatefull.get(proxy);
			if (instance == null) {
				instance = myClass.newInstance();
				mapEjbStatefull.put(proxy, instance);
				EJBContainer.getInstance().postConstruct(instance);
			}

		} else if (myClass.getAnnotation(Stateless.class) != null) {
			if (lastEjbStateLess != null) {
				EJBContainer.getInstance().destroyEJB(lastEjbStateLess);
			}
			instance = lastEjbStateLess = myClass.newInstance();
			EJBContainer.getInstance().postConstruct(instance);
		}

		return instance;
	}

	/**
	 * @see Singleton
	 * @return L'instance courante de la classe.
	 */
	static public EjbInstanceManager getInstance() {
		return instance;
	}
}
