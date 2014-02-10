package fr.isima.test.entities.singleton;

import fr.isima.container.annotations.Stateless;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
@Stateless
public class StatelessEJB implements StatelessEJBLocal/*, StatelessEJBRemote*/ {
	
	private int state = 0;
	
	public StatelessEJB() {
	}

	@Override
	public int getState() {
		return state;
	}
	
	@Override
	public void incrementState() {
		++state;
	}
}
