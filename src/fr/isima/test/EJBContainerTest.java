package fr.isima.test;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.isima.container.EJBContainer;
import fr.isima.container.Log;
import fr.isima.container.exceptions.EJBStrategyNotFoundException;
import fr.isima.container.exceptions.InterfaceAlreadyImplementedException;
import fr.isima.test.entities.client.ClientSingleton;
import fr.isima.test.entities.client.ClientStatefull;
import fr.isima.test.entities.client.ClientStateless;
import fr.isima.test.entities.persistence.MyEntityManagerInterface;
import fr.isima.test.entities.singleton.IncludedEJBLocal;
import fr.isima.test.entities.singleton.SingletonEJBLocal;
import fr.isima.test.entities.singleton.SingletonEJBRemote;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class EJBContainerTest {
	
	@Test
	public void beforeInjectTest() {	
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		assertFalse(client.singletonEJBLocal instanceof SingletonEJBLocal);
		assertFalse(client.singletonEJBRemote instanceof SingletonEJBRemote);
	}

	@Test
	public void ejbInjectionTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);

		assertTrue(client.singletonEJBLocal instanceof SingletonEJBLocal);
		assertTrue(client.singletonEJBRemote instanceof SingletonEJBRemote);
		assertTrue(client.singletonEJBLocal.getIncludedEjb() instanceof IncludedEJBLocal);
	}
	
	@Test
	public void postConstructCalledTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {	
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);

		assertTrue(client.singletonEJBLocal.postConstructCalled());
	}
	
	@Test
	public void persistenceContextTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);

		assertTrue(client.entityManager instanceof MyEntityManagerInterface);

		ClientSingleton client2 = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client2);

		assertTrue(client2.entityManager instanceof MyEntityManagerInterface);
		assertTrue(client.entityManager == client2.entityManager);
	}
	
	@Test
	public void statelessTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {	
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientStateless client = new ClientStateless();
		
		EJBContainer.getInstance().inject(client);

		assertEquals(client.ejbLocal.getState(), 0);
		client.ejbLocal.incrementState();
		assertEquals(client.ejbLocal.getState(), 0);
	}
	
	@Test
	public void statefullTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {	
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientStatefull client = new ClientStatefull();
		ClientStatefull client2 = new ClientStatefull();
		
		EJBContainer.getInstance().inject(client);
		EJBContainer.getInstance().inject(client2);

		assertEquals(client.ejbLocal.getState(), 0);
		assertEquals(client.ejbLocal2.getState(), 0);
		assertEquals(client2.ejbLocal.getState(), 0);
		assertEquals(client2.ejbLocal2.getState(), 0);

		client.ejbLocal.incrementState();

		assertEquals(client.ejbLocal.getState(), 1);
		assertEquals(client.ejbLocal2.getState(), 1);
		assertEquals(client2.ejbLocal.getState(), 0);
		assertEquals(client2.ejbLocal2.getState(), 0);

		client2.ejbLocal.incrementState();
		client2.ejbLocal2.incrementState();

		assertEquals(client.ejbLocal.getState(), 1);
		assertEquals(client.ejbLocal2.getState(), 1);
		assertEquals(client2.ejbLocal.getState(), 2);
		assertEquals(client2.ejbLocal2.getState(), 2);
	}
	
	@Test
	public void singletonTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {
		Log.setVerbose(false);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		ClientSingleton client2 = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);
		EJBContainer.getInstance().inject(client2);

		assertEquals(client.singletonEJBLocal.getState(), 0);
		assertEquals(client2.singletonEJBLocal.getState(), 0);

		client.singletonEJBLocal.incrementState();
		
		assertEquals(client.singletonEJBLocal.getState(), 1);
		assertEquals(client2.singletonEJBLocal.getState(), 1);

		client2.singletonEJBLocal.incrementState();
		
		assertEquals(client.singletonEJBLocal.getState(), 2);
		assertEquals(client2.singletonEJBLocal.getState(), 2);
	}
	
	@Test
	public void singleTransactionTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {
		Log.setVerbose(true);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);
		
		client.singletonEJBLocal.useClassTransactionAttribute();
		
		String verificationString = "Transaction for SingletonEJB : Created<|>";
		verificationString += "Transaction for SingletonEJB : Begin<|>";
		verificationString += "Transaction for SingletonEJB : End";
		assertEquals(verificationString, Log.getV());
		
		client.singletonEJBLocal.useMethodTransactionAttribute();
		verificationString = "Transaction for SingletonEJB : Created<|>";
		verificationString += "Transaction for SingletonEJB : Begin<|>";
		verificationString += "Transaction for SingletonEJB : End";
		assertEquals(verificationString, Log.getV());
	}
	
	@Test
	public void includedTransactionTest() throws InterfaceAlreadyImplementedException, EJBStrategyNotFoundException {
		Log.setVerbose(true);
		Log.clearCache();
		
		ClientSingleton client = new ClientSingleton();
		
		EJBContainer.getInstance().inject(client);
				
		client.singletonEJBLocal.callIncludedMethodWithTransactionAttributeRequiredNew();
		
		String verificationString = "Transaction for SingletonEJB : Created<|>";
		verificationString += "Transaction for SingletonEJB : Begin<|>";
		verificationString += "Transaction for SingletonEJB : Sleep<|>";
		verificationString += "Transaction for IncludedEJB : Created<|>";
		verificationString += "Transaction for IncludedEJB : Begin<|>";
		verificationString += "Transaction for IncludedEJB : End<|>";
		verificationString += "Transaction for SingletonEJB : Awake<|>";
		verificationString += "Transaction for SingletonEJB : End";
		assertEquals(verificationString, Log.getV());
	}
}
