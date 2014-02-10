package fr.isima.container;

/**
 * @author Thomas MARQUES && Florian ROTAGNON
 * 
 */
public class Transaction {
	
	/**
	 * Identifiant utilisé comme tag pour les logs.
	 */
	private String transactionIdent;

	/**
	 * Création de la transaction.
	 * @param ejbUsingTransaction EJB utilisant la transaction.
	 */
	public Transaction(Object ejbUsingTransaction) {
		transactionIdent = "Transaction for " + ejbUsingTransaction.getClass().getSimpleName();
		Log.v(transactionIdent, "Created");
	}
	
	/**
	 * Permet de démarrer la transaction.
	 */
	public void begin() {
		Log.v(transactionIdent, "Begin");
	}
	
	/**
	 * Permet d'endormir la transaction.
	 */
	public void sleep() {  
		Log.v(transactionIdent, "Sleep");
	}
	
	/**
	 * Permet de reveiller la transaction
	 */
	public void awake() {
		Log.v(transactionIdent, "Awake");
	}
	
	/**
	 * Permet de mettre fin à la transaction.
	 */
	public void end() {
		Log.v(transactionIdent, "End");
	}
}
