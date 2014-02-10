/**
 * 
 */
package fr.isima.container.exceptions;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class EJBStrategyNotFoundException extends Exception {

	private static final long serialVersionUID = -4766811355021316766L;

	@Override
	public String getMessage() {
		return "Exception : EJB strategy not found";
	}
}
