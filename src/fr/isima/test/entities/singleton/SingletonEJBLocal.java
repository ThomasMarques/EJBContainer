package fr.isima.test.entities.singleton;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public interface SingletonEJBLocal {

	public Boolean postConstructCalled();
	
	public int getState();
	
	public void incrementState();

	public IncludedEJBLocal getIncludedEjb();
	
	public void useClassTransactionAttribute();

	public void useMethodTransactionAttribute();

	public void callIncludedMethodWithTransactionAttributeRequiredNew();
	
}
