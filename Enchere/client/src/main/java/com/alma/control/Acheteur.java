package com.alma.control;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.alma.api.IAcheteur;
import com.alma.api.IObjet;
import com.alma.api.IVente;
import com.alma.data.Chrono;
import com.alma.data.EtatClient;
import com.alma.view.VueClient;

public class Acheteur extends UnicastRemoteObject implements IAcheteur {

	private static final long serialVersionUID = 1L;
	
	private String adresseServeur;
	private String pseudo;
	private VueClient vue;
	private IVente serveur;
	private EtatClient etat = EtatClient.ATTENTE;
	private Chrono chrono = new Chrono(30000, this); // Chrono de 30sc

	public Acheteur(String pseudo, String ip) throws RemoteException {
		super();
		this.chrono.start();
		this.pseudo = pseudo;
		this.setAdresseServeur(ip);
		this.serveur = connexionServeur();
	}

	private IVente connexionServeur() {
		try {
			IVente serveur = (IVente) Naming.lookup("//" + adresseServeur);
			System.out.println("Connexion au serveur " + adresseServeur + " reussi.");
			return serveur;
		} catch (Exception e) {
			System.out.println("Connexion au serveur " + adresseServeur + " impossible.");
			e.printStackTrace();
			return null;
		}
	}

	public void inscription() throws Exception {
		if(!serveur.inscriptionAcheteur(pseudo, this)){
			this.vue.attente();
		}
	}

	public void encherir(int prix) throws RemoteException, Exception {
		
		// TODO : Coder getPrixCourant()
		
		if (prix <= this.serveur.getPrixCourant() && prix != -1) {
			System.out.println("Prix trop bas, ne soyez pas radin !");
		} else if (etat == EtatClient.RENCHERI) {
			chrono.arreter();
			vue.attente();
			etat = EtatClient.ATTENTE;
			serveur.rencherir(prix, this);
		}
	}

	// REMOTE, appelé quand un objet est vendu
	// Le serveur appelle la méthode avec les bons paramètres
	public void objetVendu(String gagnant, int prix, String descObj, String objNom) throws RemoteException {
		this.vue.actualiserObjet(prix, gagnant, descObj, objNom);
		this.vue.reprise();
		this.etat = EtatClient.RENCHERI;
		this.chrono.demarrer();
	}
	
	// REMOTE, appelée lorsqu'un nouveau participant arrive mais qu'aucune enchère n'est en cours.
	public void nouveauParticipant() {
		this.vue.attente();
		this.etat = EtatClient.ATTENTE;
	}
	
	// REMOTE, appelée lorsqu'un nouveau participant arrive et qu'une enchère est en cours.
	public void nouveauParticipant(String gagnant, int prix, String descObj, String objNom) {
		this.vue.actualiserObjet(prix, gagnant, descObj, objNom);
		this.vue.reprise();
		this.etat = EtatClient.RENCHERI;
		this.chrono.demarrer();
	}
	
	// REMOTE, appelé quand une nouvelle enchère survient
	// Le serveur appelle la méthode avec les bons paramètres
	public void nouveauPrix(int prix, IAcheteur gagnant) throws RemoteException {
		try {
			
			this.vue.actualiserPrix(prix, gagnant.getPseudo());
			this.vue.reprise();
			this.etat = EtatClient.RENCHERI;
			this.chrono.demarrer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// REMOTE
	public void finEnchere() throws RemoteException {
		this.etat = EtatClient.TERMINE;
		System.exit(0);
	}
	
	public void nouvelleSoumission(String nom, String description, int prix) {
		try {
			serveur.ajouterObjet(nom, description, prix);
			System.out.println("Soumission de l'objet " + nom + " au serveur.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// getters and setters

	// REMOTE
	public long getChrono() {
		return chrono.getTemps();
	}

	public IVente getServeur() {
		return serveur;
	}

	public void setServeur(IVente serveur) {
		this.serveur = serveur;
	}

	public void setVue(VueClient vueClient) {
		vue = vueClient;
	}

	public EtatClient getEtat() {
		return this.etat;
	}
	
	// REMOTE
	public String getPseudo() throws RemoteException {
		return pseudo;
	}
	
	public void updateChrono(){
		this.vue.updateChrono(this.chrono.getTemps(), this.chrono.getTempsFin());
	}

	public String getAdresseServeur() {
		return adresseServeur;
	}

	public void setAdresseServeur(String adresseServeur) {
		this.adresseServeur = adresseServeur;
	}

}
