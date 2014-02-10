package fr.isima.test.entities.client;

import fr.isima.container.annotations.EJB;
import fr.isima.test.entities.singleton.StatelessEJBLocal;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class ClientStateless {
	
	@EJB
	public StatelessEJBLocal ejbLocal = null;
	
	public ClientStateless() {
	}
}
