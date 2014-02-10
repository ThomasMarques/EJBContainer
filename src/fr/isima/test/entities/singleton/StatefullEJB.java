package fr.isima.test.entities.singleton;

import fr.isima.container.annotations.Statefull;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
@Statefull
public class StatefullEJB implements StatefullEJBLocal/*, StatefullEJBRemote*/ {

	private int state = 0;
	
	public StatefullEJB() {
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
