package com.alma.main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.bind.annotation.XmlRootElement;

import com.alma.api.IAcheteur;
import com.alma.api.IObjet;
import com.alma.api.IVente;

public class VenteImpl extends UnicastRemoteObject implements IVente{

	private static final long serialVersionUID = 1L;
	private List<IAcheteur> listeAcheteurs = new ArrayList<IAcheteur>();
	private List<IAcheteur> fileAttente = new ArrayList<IAcheteur>();
	private Map<IAcheteur, Integer> enchereTemp = new HashMap<IAcheteur, Integer>();
	private IObjet objetCourant;
	private Stack<Objet> listeObjets;
	private IAcheteur acheteurCourant;
	private EtatVente etatVente;
	private final int clientMin = 2;


	protected VenteImpl() throws RemoteException {
		super();
		listeObjets = new Stack<Objet>();
		this.etatVente = EtatVente.ATTENTE;
	}

	public synchronized boolean inscriptionAcheteur(String login, IAcheteur acheteur) throws Exception{
		for(IAcheteur each : listeAcheteurs){
			if(each.getPseudo().equals(login) || each.getPseudo().equals(acheteur.getPseudo())){
				throw new Exception("Login deja pris");
			}
		}			
		this.fileAttente.add(acheteur);

		if(this.fileAttente.size() >= clientMin && this.etatVente == EtatVente.ATTENTE){
			this.etatVente = EtatVente.ENCHERISSEMENT;	

			for(IAcheteur each : this.fileAttente){
				this.listeAcheteurs.add(each);
				if(objetCourant == null) each.nouveauParticipant();
				else each.nouveauParticipant(acheteurCourant.getPseudo(), objetCourant.getPrixCourant(), objetCourant.getDescription(), objetCourant.getNom());
			}
			this.fileAttente.clear();
			return true;
		}	
		return false;
	}

	public synchronized int rencherir(int nouveauPrix, IAcheteur acheteur) throws Exception{
		this.enchereTemp.put(acheteur, nouveauPrix);
		System.out.println(this.enchereTemp.size()+"/"+this.listeAcheteurs.size());

		//On a recu toutes les encheres
		if(this.enchereTemp.size() == this.listeAcheteurs.size()){
			if(enchereFinie()){
				return objetSuivant();
			}
			else{
				actualiserObjet();
				//On renvoie le resultat du tour
				for(IAcheteur each : this.listeAcheteurs){
					each.nouveauPrix(this.objetCourant.getPrixCourant(), this.acheteurCourant);
				}
			}
		}
		return objetCourant.getPrixCourant();
	}


	/**
	 * Permet de passer à l'objet suivant avec les bons acheteurs et bons objets.
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public int objetSuivant() throws RemoteException, InterruptedException{
		this.enchereTemp.clear();
		this.etatVente = EtatVente.ATTENTE;

		if(acheteurCourant != null){
			this.objetCourant.setDisponible(false);
			this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());

			//Envoie des resultats finaux pour l'objet courant
			for(IAcheteur each : this.listeAcheteurs){
				each.objetVendu(this.acheteurCourant.getPseudo(), objetCourant.getPrixCourant(), objetCourant.getDescription(), objetCourant.getNom());
			}
		}


		// Lenteur entre changements : 
		Thread.sleep(5000);
		
		this.acheteurCourant = null;
		this.listeAcheteurs.addAll(this.fileAttente);
		this.fileAttente.clear();

		//Il y a encore des objets à vendre
		if(!this.listeObjets.isEmpty()){
			this.objetCourant = this.listeObjets.pop();
			this.objetCourant.setGagnant("");
			this.etatVente = EtatVente.ENCHERISSEMENT;
			for(IAcheteur each : this.listeAcheteurs){
				each.objetVendu("", objetCourant.getPrixCourant(), objetCourant.getDescription(), objetCourant.getNom());
			}
		} else{
			this.etatVente = EtatVente.TERMINE;
			for(IAcheteur each : this.listeAcheteurs){
				each.finEnchere();
			}
			return 0;
		}
		return this.objetCourant.getPrixBase();
	}



	/**
	 * Methode utilitaire permettant d'actualiser le prix de l'objet et le gagnant selon les encheres recues.
	 * @throws RemoteException
	 */
	public void actualiserObjet() throws RemoteException{
		Set<IAcheteur> cles = this.enchereTemp.keySet();
		Iterator<IAcheteur> it = cles.iterator();

		while (it.hasNext()){
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(cle);

			if(valeur > this.objetCourant.getPrixCourant() || (valeur == this.objetCourant.getPrixCourant() && cle.getChrono() < acheteurCourant.getChrono())){
				this.objetCourant.setPrixCourant(valeur);
				this.acheteurCourant = cle;	
				this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());
			}
		}
		this.enchereTemp.clear();
	}


	/**
	 * méthode utilitaire qui permet de savoir si les encheres sont finis.
	 * @return true si on a reçu que des -1, donc si l'enchere est finie, sinon false.
	 */
	public boolean enchereFinie(){	
		Set<IAcheteur> cles = this.enchereTemp.keySet();
		Iterator<IAcheteur> it = cles.iterator();

		while (it.hasNext()){
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(cle);

			if(valeur != -1){
				return false;	
			}
		}
		return true;
	}

	public void ajouterObjet(String nom, String description, int prix) throws RemoteException {
		try {
			Objet newObj = new Objet(nom, description, prix);
			this.listeObjets.add(newObj);
			if(objetCourant == null) this.objetCourant = listeObjets.pop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	
	
	public IObjet getObjet() throws RemoteException {
		return this.objetCourant;
	}

	public List<IAcheteur> getListeAcheteurs() {
		return listeAcheteurs;
	}

	public void setListeAcheteurs(List<IAcheteur> listeAcheteurs) {
		this.listeAcheteurs = listeAcheteurs;
	}

	public IObjet getObjetCourant() {
		return objetCourant;
	}

	public void setObjetCourant(IObjet objetCourant) {
		this.objetCourant = objetCourant;
	}

	public Stack<Objet> getListeObjets() {
		return listeObjets;
	}

	public void setListeObjets(Stack<Objet> listeObjets) {
		this.listeObjets = listeObjets;
	}

	public IAcheteur getAcheteurCourant() {
		return acheteurCourant;
	}

	public void setAcheteurCourant(IAcheteur acheteurCourant) {
		this.acheteurCourant = acheteurCourant;
	}

	public EtatVente getEtatVente() {
		return etatVente;
	}

	public void setEtatVente(EtatVente etatVente) {
		this.etatVente = etatVente;
	}
	
	// REMOTE GETTERS
	
	@Override
	public int getPrixCourant() throws RemoteException {
		return this.objetCourant.getPrixCourant();
	}

	@Override
	public String getGagnantEnchere() throws RemoteException {
		return this.objetCourant.getGagnant();
	}
	
}
