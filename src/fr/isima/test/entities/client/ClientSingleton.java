package fr.isima.test.entities.client;

import fr.isima.container.annotations.EJB;
import fr.isima.container.annotations.PersistenceContext;
import fr.isima.test.entities.persistence.MyEntityManagerInterface;
import fr.isima.test.entities.singleton.SingletonEJBLocal;
import fr.isima.test.entities.singleton.SingletonEJBRemote;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class ClientSingleton {
	
	@EJB
	public SingletonEJBLocal singletonEJBLocal = null;
	
	@EJB
	public SingletonEJBRemote singletonEJBRemote = null;
	
	@PersistenceContext(unitName="MyPersistenceUnit")
	public MyEntityManagerInterface entityManager;
	
	public ClientSingleton() {
	}
}
