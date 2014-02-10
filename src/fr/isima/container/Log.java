package fr.isima.container;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class Log {

	/**
	 * instance courante des logs.
	 */
	private static Log instance = new Log();
	
	/**
	 * Pour savoir si on affiche la verbose sur la sortie.
	 */
	private static boolean verbose = true;
	
	/**
	 * Séparateur entre chaque entrée de log, pour ne pas gérer de list.
	 */
	static public final String SEPARATOR = "<|>";
	
	/**
	 * Stockage du cache dans l'instance.
	 */
	private String verboseCache = new String();
	
	/**
	 * Constructeur par défaut privé.
	 */
	private Log() {
	}
	
	/**
	 * Permet d'écrire en verbose.
	 * @param tag
	 * @param textToLog
	 */
	static public void v(String tag, String textToLog) {
		String concat = tag + " : " + textToLog;
		if(verbose) {
			System.out.println(concat);
		}
		
		if(!instance.verboseCache.isEmpty())
			instance.verboseCache += SEPARATOR;
		instance.verboseCache += concat;
	}
	
	/**
	 * Permet de récupérer le cache correspondant à la verbose.
	 * @return le cache.
	 */
	static public String getV() {
		return instance.verboseCache;
	}
	
	/**
	 * Nettoie le cache.
	 */
	static public void clearCache() {
		instance.verboseCache = new String();
	}

	/**
	 * Permet de changer le changement de mode de verbose.
	 * @param b
	 */
	public static void setVerbose(boolean b) {
		verbose  = b;
	}
}
