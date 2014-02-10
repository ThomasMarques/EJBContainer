package fr.isima.test.entities.singleton;

import fr.isima.container.annotations.EJB;
import fr.isima.container.annotations.PostConstruct;
import fr.isima.container.annotations.Singleton;
import fr.isima.container.annotations.TransactionAttribute;
import fr.isima.container.annotations.TransactionAttribute.TransactionAttributeType;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
@Singleton
@TransactionAttribute(transactionAttributeType=TransactionAttributeType.REQUIRED)
public class SingletonEJB implements SingletonEJBLocal, SingletonEJBRemote {

	private Boolean postConstructCalled = false;
	private int state = 0;
	
	@EJB
	public IncludedEJBLocal includedEjb;
	
	public SingletonEJB() {
	}
	
	@PostConstruct
	public void postConstruct() {
		postConstructCalled = true;
	}
	
	public Boolean postConstructCalled() {
		return postConstructCalled;
	}

	@Override
	public IncludedEJBLocal getIncludedEjb() {
		return includedEjb;
	}

	@Override
	public int getState() {
		return state;
	}
	
	@Override
	public void incrementState() {
		++state;
	}

	@Override
	public void useClassTransactionAttribute() {
		
	}

	@Override
	@TransactionAttribute(transactionAttributeType=TransactionAttributeType.NEVER)
	public void useMethodTransactionAttribute() {
		
	}

	@Override
	public void callIncludedMethodWithTransactionAttributeRequiredNew() {
		includedEjb.includedMethodWithTransactionAttributeRequiredNew();
	}
}
