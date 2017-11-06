package serveur;

import java.io.Serializable;

public class Objet implements IObjet, Serializable{

	private static final long serialVersionUID = 1L;
	private String nom;
	private String description;
	private int prixBase;
	private int prixCourant;
	private boolean disponible;
	private String gagnant;
	
	
	
	public Objet(String nom, String description, int prixBase) {
		super();
		this.nom = nom;
		this.description = description;
		this.prixBase = prixBase;
		this.prixCourant = prixBase;
		this.disponible = true;
		this.gagnant = "";
	}
	
	/* (non-Javadoc)
	 * @see serveur.IObjet#getNom()
	 */
	@Override
	public String getNom() {
		return nom;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#setNom(java.lang.String)
	 */
	@Override
	public void setNom(String nom) {
		this.nom = nom;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#getPrixBase()
	 */
	@Override
	public int getPrixBase() {
		return prixBase;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#setPrixBase(int)
	 */
	@Override
	public void setPrixBase(int prixBase) {
		this.prixBase = prixBase;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#isDisponible()
	 */
	@Override
	public boolean isDisponible() {
		return disponible;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#setDisponible(boolean)
	 */
	@Override
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#getPrixCourant()
	 */
	@Override
	public int getPrixCourant() {
		return prixCourant;
	}
	/* (non-Javadoc)
	 * @see serveur.IObjet#setPrixCourant(int)
	 */
	@Override
	public void setPrixCourant(int prixCourant) {
		this.prixCourant = prixCourant;
	}

	/* (non-Javadoc)
	 * @see serveur.IObjet#getGagnant()
	 */
	@Override
	public String getGagnant() {
		return gagnant;
	}

	/* (non-Javadoc)
	 * @see serveur.IObjet#setGagnant(java.lang.String)
	 */
	@Override
	public void setGagnant(String gagnant) {
		this.gagnant = gagnant;
	}
	
	
	
}
