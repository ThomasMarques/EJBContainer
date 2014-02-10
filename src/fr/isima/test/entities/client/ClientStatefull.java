package fr.isima.test.entities.client;

import fr.isima.container.annotations.EJB;
import fr.isima.test.entities.singleton.StatefullEJBLocal;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class ClientStatefull {

	@EJB
	public StatefullEJBLocal ejbLocal = null;
	@EJB
	public StatefullEJBLocal ejbLocal2 = null;
	
	public ClientStatefull() {
	}
}
