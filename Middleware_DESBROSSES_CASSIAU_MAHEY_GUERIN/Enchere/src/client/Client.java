package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import serveur.Objet;
import serveur.Vente;

public class Client extends UnicastRemoteObject implements Acheteur {

	private static final long serialVersionUID = 1L;
	private static final String adresseServeur = "localhost:8090/enchere";

	private String pseudo;
	private VueClient vue;
	private Vente serveur;
	private Objet currentObjet;
	private EtatClient etat = EtatClient.ATTENTE;
	private Chrono chrono = new Chrono(10000, this); // Chrono de 30sc

	public Client(String pseudo) throws RemoteException {
		super();
		this.chrono.start();
		this.pseudo = pseudo;
		this.serveur = connexionServeur();
		this.currentObjet = serveur.getObjet();
	}

	public static Vente connexionServeur() {
		try {
			Vente serveur = (Vente) Naming.lookup("//" + adresseServeur);
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

	@Override
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

	@Override
	public void nouveauPrix(int prix, Acheteur gagnant) throws RemoteException {
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
	
	@Override
	public void finEnchere() throws RemoteException {
		this.etat = EtatClient.TERMINE;
		System.exit(0);
	}
	
	public void nouvelleSoumission(String nom, String description, int prix) {
		Objet nouveau = new Objet(nom, description, prix);
		try {
			serveur.ajouterObjet(nouveau);
			System.out.println("Soumission de l'objet " + nom + " au serveur.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		try {
			new VueClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// getters and setters
	public Objet getCurrentObjet() {
		return currentObjet;
	}

	@Override
	public long getChrono() {
		return chrono.getTemps();
	}

	public Vente getServeur() {
		return serveur;
	}

	public void setServeur(Vente serveur) {
		this.serveur = serveur;
	}

	public void setVue(VueClient vueClient) {
		vue = vueClient;
	}

	public EtatClient getEtat() {
		return this.etat;
	}
	
	@Override
	public String getPseudo() throws RemoteException {
		return pseudo;
	}

	public void updateChrono(){
		this.vue.updateChrono(this.chrono.getTemps(), this.chrono.getTempsFin());
	}

}
