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
	private IObjet currentObjet;
	private EtatClient etat = EtatClient.ATTENTE;
	private Chrono chrono = new Chrono(30000, this); // Chrono de 30sc

	public Acheteur(String pseudo, String ip) throws RemoteException {
		super();
		this.chrono.start();
		this.pseudo = pseudo;
		this.setAdresseServeur(ip);
		this.serveur = connexionServeur();
		
		// Plantage ici -------------------
		this.currentObjet = serveur.getObjet();
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
		if (prix <= this.currentObjet.getPrixCourant() && prix != -1) {
			System.out.println("Prix trop bas, ne soyez pas radin !");
		} else if (etat == EtatClient.RENCHERI) {
			chrono.arreter();
			vue.attente();
			etat = EtatClient.ATTENTE;
			serveur.rencherir(prix, this);
		}
	}

	// REMOTE
	public void objetVendu(String gagnant) throws RemoteException {
		this.currentObjet = serveur.getObjet();
		this.vue.actualiserObjet();
		this.vue.reprise();
		
		if (gagnant != null) { //Fin de l'objet
			this.etat = EtatClient.ATTENTE;
		}else{ //inscription & objet suivant
			this.etat = EtatClient.RENCHERI;
			this.chrono.demarrer();
		}
	}
	
	// REMOTE
	public void nouveauPrix(int prix, IAcheteur gagnant) throws RemoteException {
		try {
			this.currentObjet.setPrixCourant(prix);
			this.currentObjet.setGagnant(gagnant.getPseudo());
			this.vue.actualiserPrix();
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
//		IObjet nouveau = new Objet(nom, description, prix);
		try {
//			serveur.ajouterObjet(nouveau);
			serveur.ajouterObjet(nom, description, prix);
			System.out.println("Soumission de l'objet " + nom + " au serveur.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// getters and setters
	public IObjet getCurrentObjet() {
		return currentObjet;
	}

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
