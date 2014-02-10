package fr.isima.container.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TransactionAttribute {
	
	public enum TransactionAttributeType {
		REQUIRED,
		REQUIRES_NEW,
		NEVER
	}
	
	TransactionAttributeType transactionAttributeType() default TransactionAttributeType.REQUIRED;
}
