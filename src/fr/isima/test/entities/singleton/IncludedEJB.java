package fr.isima.test.entities.singleton;

import fr.isima.container.annotations.Singleton;
import fr.isima.container.annotations.TransactionAttribute;
import fr.isima.container.annotations.TransactionAttribute.TransactionAttributeType;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
@Singleton
public class IncludedEJB implements IncludedEJBLocal/*, IncludedEJBRemote*/ {

	public IncludedEJB() {
	}

	@Override
	@TransactionAttribute(transactionAttributeType=TransactionAttributeType.REQUIRES_NEW)
	public void includedMethodWithTransactionAttributeRequiredNew() {
		
	}
}
