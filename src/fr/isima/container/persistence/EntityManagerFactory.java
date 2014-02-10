package fr.isima.container.persistence;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import fr.isima.container.exceptions.PersistenceUnitNotFound;
import fr.isima.test.entities.persistence.MyEntityManager;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class EntityManagerFactory {

	/**
	 * Instance du manager.
	 */
	private static EntityManagerFactory instance = null;

	/**
	 * Map associant un nom à une classe. Permet d'associer une Persistence Unit à la factory.
	 */
	private Map<String, Class<?>> mapNameToType = new HashMap<String, Class<?>>();
	
	/**
	 * Map contenant les interfaces et objets correspondant.
	 */
	private Map<Class<?>, Object> mapObjectToType = new HashMap<Class<?>, Object>();
	
	/**
	 * Constructeur privé. @see Singleton
	 */
	private EntityManagerFactory() {
		/// Ceci fait partie de la connexion...
		mapNameToType.put("MyPersistenceUnit", MyEntityManager.class);
	}
	
	/**
	 * @see Singleton
	 * @return l'instance de la classe.
	 */
	public static EntityManagerFactory getInstance()
	{
		if(instance == null)
			instance = new EntityManagerFactory();
		return instance;
	}
	
	/**
	 * Permet d'instancier un field entityManager.
	 * @param o Objet contenant l'attribut.
	 * @param field Attribut à instancier.
	 * @param puName Nom de la Persistence Unit.
	 * @throws PersistenceUnitNotFound
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void instanciateField(Object o, Field field, String puName) 
			throws PersistenceUnitNotFound, IllegalArgumentException, IllegalAccessException, InstantiationException {
		
		if(!mapNameToType.containsKey(puName))
			throw new PersistenceUnitNotFound();
		
		Class<?> myPUInterface = field.getType();
		Class<?> myPU = mapNameToType.get(puName);
		if (!myPUInterface.isAssignableFrom(myPU))
			throw new PersistenceUnitNotFound();
		
		Object instance = mapObjectToType.get(myPU);
		if(instance == null) {
			instance = myPU.newInstance();
			mapObjectToType.put(myPU, instance);
		}
		
		field.set(o, instance);
	}
}
