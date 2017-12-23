package com.alma.control;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import com.alma.api.IAcheteur;
import com.alma.api.IObjet;
import com.alma.api.IVente;
import com.alma.data.Donnees;
import com.alma.data.EtatVente;
import com.alma.data.Objet;
import com.alma.main.Pair;

public class VenteImpl extends UnicastRemoteObject implements IVente {

	private static final long serialVersionUID = 1L;
//<<<<<<< HEAD
	private List<Pair<List<IAcheteur>, List<IAcheteur>>> salles = new ArrayList<Pair<List<IAcheteur>, List<IAcheteur>>>(); // listeAcheteurs - fileAttente
//	private List<IAcheteur> listeAcheteurs = new ArrayList<IAcheteur>();
//	private List<IAcheteur> fileAttente = new ArrayList<IAcheteur>();
	private List<Map<IAcheteur, Integer>> enchereTemp = new ArrayList<Map<IAcheteur, Integer>>();
	private List<Objet> objetCourant = new ArrayList<Objet>();
	private List<Stack<Objet>> listeObjets= new ArrayList<Stack<Objet>>();
	private List<IAcheteur> acheteurCourant= new ArrayList<IAcheteur>();
	private List<EtatVente> etatVente= new ArrayList<EtatVente>();
	private final int clientMin = 2;

	protected VenteImpl() throws RemoteException {
		super();
//<<<<<<< HEAD
		//init all variables
		try {
			Donnees.getInstance().load();
			
			// Copie des objets dans la stack de la vente courrante
			for (int i = 0; i < 10 ; ++i) {
				this.listeObjets.add(new Stack<Objet>());
				this.etatVente.add(EtatVente.ATTENTE);
				this.objetCourant.add(null);
				this.acheteurCourant.add(null);
				this.enchereTemp.add(new HashMap<IAcheteur, Integer>());
				this.salles.add(new Pair<List<IAcheteur>, List<IAcheteur>>(new ArrayList<IAcheteur>(), new ArrayList<IAcheteur>()));
				for(IObjet obj : Donnees.getInstance().getListeObjets())
					listeObjets.get(i).add((Objet) obj);
			}
			
			
		} catch (NoSuchElementException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
		
	}

	public synchronized boolean inscriptionAcheteur(String login, IAcheteur acheteur, int salle) throws Exception{
		System.out.println("add acheteur salle : " + salle);
		for(IAcheteur each : salles.get(salle).getFirst()){ //listeAcheteurs
			if(each.getPseudo().equals(login) || each.getPseudo().equals(acheteur.getPseudo())){
				throw new Exception("Login deja pris");
			}
		}			
		this.salles.get(salle).getSecond().add(acheteur);

		if(this.salles.get(salle).getSecond().size() >= clientMin && this.etatVente.get(salle) == EtatVente.ATTENTE){
//			this.etatVente = EtatVente.ENCHERISSEMENT;	
			this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);

			for(IAcheteur each : this.salles.get(salle).getSecond()){
				this.salles.get(salle).getFirst().add(each);
				if(objetCourant.get(salle) == null) each.nouveauParticipant();
				else each.nouveauParticipant(acheteurCourant.get(salle).getPseudo(), objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
//=======
//		listeObjets = new Stack<IObjet>();
//		
//		try {
//			Donnees.getInstance().load();
//			
//			// Copie des objets dans la stack de la vente courrante
//			for(IObjet obj : Donnees.getInstance().getListeObjets())
//				listeObjets.get(salle).push((Objet) obj);
//			
//		} catch (NoSuchElementException | IllegalArgumentException | IOException e) {
//			e.printStackTrace();
//		}
//
//		this.etatVente.set(salle, EtatVente.ATTENTE);
//	}
//
//	public synchronized boolean inscriptionAcheteur(String login, IAcheteur acheteur) throws Exception {
//		for (IAcheteur each : listeAcheteurs) {
//			if (each.getPseudo().equals(login) || each.getPseudo().equals(acheteur.getPseudo())) {
//				throw new Exception("Login deja pris");
//			}
//		}
//		this.fileAttente.add(acheteur);
//
//		if (this.fileAttente.size() >= clientMin && this.etatVente == EtatVente.ATTENTE) {
//			this.etatVente = EtatVente.ENCHERISSEMENT;
//
//			for (IAcheteur each : this.fileAttente) {
//				this.listeAcheteurs.add(each);
//				if (objetCourant == null)
//					each.nouveauParticipant();
//				else
//					each.nouveauParticipant(acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
//							objetCourant.getDescription(), objetCourant.getNom());
//>>>>>>> bdd
			}
			this.salles.get(salle).getSecond().clear();
			return true;
		}
		return false;
	}

//<<<<<<< HEAD
	public synchronized int rencherir(int nouveauPrix, IAcheteur acheteur, int salle) throws Exception{
		// On ajoute la proposition d'un acheteur à une liste temporaire
		this.enchereTemp.get(salle).put(acheteur, nouveauPrix);
		System.out.println(this.enchereTemp.get(salle).size()+"/"+this.salles.get(salle).getFirst().size());

		// Toutes les propositions des acheteurs ont été reçues
		if(this.enchereTemp.get(salle).size() == this.salles.get(salle).getFirst().size()){
			if(enchereFinie(salle)){
				return objetSuivant(salle);
			}
			else{
				actualiserObjet(salle);
				//On renvoie le resultat du tour
				for(IAcheteur each : this.salles.get(salle).getFirst()){
					each.nouveauPrix(this.objetCourant.get(salle).getPrixCourant(), this.acheteurCourant.get(salle));
				}
			}
		}
		return objetCourant.get(salle).getPrixCourant();
//=======
//	public synchronized int rencherir(int nouveauPrix, IAcheteur acheteur) throws Exception {
//
//		// On ajoute la proposition d'un acheteur à une liste temporaire
//		this.enchereTemp.put(acheteur, nouveauPrix);
//
//		System.out.println(this.enchereTemp.size() + "/" + this.listeAcheteurs.size()); // DEBUG
//																						// LINE
//
//		// Toutes les propositions des acheteurs ont été reçues
//		if (this.enchereTemp.size() == this.listeAcheteurs.size()) {
//			if (enchereFinie()) {
//				return objetSuivant();
//			} else {
//
//				actualiserObjet();
//
//				// On renvoit le résultat du tour
//				for (IAcheteur each : this.listeAcheteurs) {
//					each.nouveauPrix(this.objetCourant.getPrixCourant(), this.acheteurCourant);
//				}
//			}
//		}
//
//		return objetCourant.getPrixCourant();
//>>>>>>> bdd
	}

	/**
	 * Permet de passer à l'objet suivant avec les bons acheteurs et bons
	 * objets.
	 * 
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
//<<<<<<< HEAD
	public int objetSuivant(int salle) throws RemoteException, InterruptedException{
//=======
//	public int objetSuivant() throws RemoteException, InterruptedException {
//>>>>>>> bdd
		this.enchereTemp.get(salle).clear();
		this.etatVente.set(salle, EtatVente.ATTENTE);

//<<<<<<< HEAD
		if(acheteurCourant.get(salle) != null){
			this.objetCourant.get(salle).setDisponible(false);
			this.objetCourant.get(salle).setGagnant(this.acheteurCourant.get(salle).getPseudo());

			//Envoie des resultats finaux pour l'objet courant
			for(IAcheteur each : this.salles.get(salle).getFirst()){
				each.objetVendu(this.acheteurCourant.get(salle).getPseudo(), objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
//=======
//		if (acheteurCourant != null) {
//			this.objetCourant.setDisponible(false);
//			this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());
//
//			// Envoie des resultats finaux pour l'objet courant
//			for (IAcheteur each : this.listeAcheteurs) {
//				each.objetVendu(this.acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
//						objetCourant.getDescription(), objetCourant.getNom());
//>>>>>>> bdd
			}
		}

		// Lenteur entre changements :
		Thread.sleep(5000);
//<<<<<<< HEAD
		
		this.acheteurCourant.set(salle, null);
		this.salles.get(salle).getFirst().addAll(this.salles.get(salle).getSecond());
//		this.listeAcheteurs.addAll(this.fileAttente);
//		this.fileAttente.clear();
		this.salles.get(salle).getSecond().clear();

		//Il y a encore des objets à vendre
		if(!this.listeObjets.get(salle).isEmpty()){
			this.acheteurCourant.set(salle, null);
			this.objetCourant.set(salle, this.listeObjets.get(salle).pop());
			this.objetCourant.get(salle).setGagnant("");
			this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);
			for(IAcheteur each : this.salles.get(salle).getFirst()){
				each.reprendreEnchere("", objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
			}
		} else{
			this.etatVente.set(salle, EtatVente.TERMINE);
			for(IAcheteur each : this.salles.get(salle).getFirst()){
				each.objetVendu("", objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());

//=======
//
//		// On fait participer les Acheteurs en attente (ceux arriver pendant une
//		// enchère en cours)
//		this.listeAcheteurs.addAll(this.fileAttente);
//		this.fileAttente.clear();
//
//		// Il y a encore des objets à vendre
//		if (!this.listeObjets.isEmpty()) {
//			this.acheteurCourant = null;
//			this.objetCourant = this.listeObjets.pop();
//			this.objetCourant.setGagnant("");
//			this.etatVente = EtatVente.ENCHERISSEMENT;
//			for (IAcheteur each : this.listeAcheteurs) {
//				each.reprendreEnchere("", objetCourant.getPrixCourant(), objetCourant.getDescription(),
//						objetCourant.getNom());
//			}
//		} else {
//			this.etatVente = EtatVente.TERMINE;
//			for (IAcheteur each : this.listeAcheteurs) {
//				// each.finEnchere();
//				each.objetVendu(acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
//						objetCourant.getDescription(), objetCourant.getNom());
//>>>>>>> bdd
			}
			this.acheteurCourant.set(salle, null);
			return 0;
		}
		return this.objetCourant.get(salle).getPrixBase();
	}

	/**
	 * Actualise le prix de l'objet et le gagnant selon les enchères reçues.
	 * 
	 * @throws RemoteException
	 */
//<<<<<<< HEAD
	public void actualiserObjet(int salle) throws RemoteException{
		Set<IAcheteur> cles = this.enchereTemp.get(salle).keySet();
//=======
//	public void actualiserObjet() throws RemoteException {
//		Set<IAcheteur> cles = this.enchereTemp.keySet();
//>>>>>>> bdd
		Iterator<IAcheteur> it = cles.iterator();

		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(salle).get(cle);

//<<<<<<< HEAD
			if(valeur > this.objetCourant.get(salle).getPrixCourant() || (valeur == this.objetCourant.get(salle).getPrixCourant() && cle.getChrono() < acheteurCourant.get(salle).getChrono())){
				this.objetCourant.get(salle).setPrixCourant(valeur);
				this.acheteurCourant.set(salle, cle);	
				this.objetCourant.get(salle).setGagnant(this.acheteurCourant.get(salle).getPseudo());
//=======
//			if (valeur > this.objetCourant.getPrixCourant() || (valeur == this.objetCourant.getPrixCourant()
//					&& cle.getChrono() < acheteurCourant.getChrono())) {
//				this.objetCourant.setPrixCourant(valeur);
//				this.acheteurCourant = cle;
//				this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());
//>>>>>>> bdd
			}
		}
		this.enchereTemp.get(salle).clear();
	}

	/**
	 * Permet de savoir si les enchères pour l'object proposé sont finies.
	 * 
	 * @return true Si uniquement des -1 sont reçus, false sinon.
	 */
//<<<<<<< HEAD

	public boolean enchereFinie(int salle){	
		
		boolean ret = false;
		
		Set<IAcheteur> cles = this.enchereTemp.get(salle).keySet();
//=======
//	public boolean enchereFinie() {
//
//		boolean ret = false;
//
//		Set<IAcheteur> cles = this.enchereTemp.keySet();
//>>>>>>> bdd
		Iterator<IAcheteur> it = cles.iterator();

		int cpt = 0;

		// Compte le nombre d'acheteur passant l'enchère
		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(salle).get(cle);

			if (valeur == -1) {
				cpt++;
			}
		}

		// Uniquement des -1 de reçu : return true
		if (cpt == cles.size())
			ret = true;

		// Une seule nouvelle proposition
		else if (cpt == cles.size() - 1) {
			it = cles.iterator();
			String tempName = "";

			// On cherche l'acheteur faisant une nouvelle offre
			while (it.hasNext()) {
				IAcheteur current = it.next();
//<<<<<<< HEAD
				if(enchereTemp.get(salle).get(current) != -1) {
//=======
//				if (enchereTemp.get(current) != -1) {
//>>>>>>> bdd
					try {
						tempName = current.getPseudo();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			// Si l'acheteur proposant la nouvelle offre est déjà l'acheteur
			// courant : return true
			try {
//<<<<<<< HEAD
				if (tempName.equals(acheteurCourant.get(salle).getPseudo())) ret = true;
//=======
//				if (tempName.equals(acheteurCourant.getPseudo()))
//					ret = true;
//>>>>>>> bdd
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

//<<<<<<< HEAD
	public void ajouterObjet(String nom, String description, int prix, int salle) throws RemoteException {
		
		try {
//			System.out.println("ajouter objet : " + nom + " ; salle : " + salle);
			Objet newObj = new Objet(nom, description, prix, salle);
			this.listeObjets.get(salle).add(newObj);
			

			// Ajout de l'objet dans les données persistantes
			Donnees.getInstance().ajouterArticle(newObj);
			Donnees.getInstance().save();
			
			for (Objet o : listeObjets.get(salle))
				System.out.println("objet : "+o.getNom());
			if(objetCourant.get(salle) == null) {
				this.objetCourant.set(salle, listeObjets.get(salle).pop());
				this.objetCourant.get(salle).setGagnant("");
				this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);
				for(IAcheteur each : this.salles.get(salle).getFirst()){
					System.out.println("reprendre enchere");
					each.reprendreEnchere("", objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
////=======
//	public void ajouterObjet(String nom, String description, int prix, int numSalle) throws RemoteException {
//		try {
//			Objet newObj = new Objet(nom, description, prix, numSalle);
//			this.listeObjets.add(newObj);
//			
//			// Ajout de l'objet dans les données persistantes
//			Donnees.getInstance().ajouterArticle(newObj);
//			Donnees.getInstance().save();
//						
//			if (objetCourant == null) {
//				this.objetCourant = listeObjets.pop();
//				this.objetCourant.setGagnant("");
//				this.etatVente = EtatVente.ENCHERISSEMENT;
//				for (IAcheteur each : this.listeAcheteurs) {
//					each.reprendreEnchere("", objetCourant.getPrixCourant(), objetCourant.getDescription(),
//							objetCourant.getNom());
//>>>>>>> bdd
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//<<<<<<< HEAD
	}	
	
	public IObjet getObjet(int salle) throws RemoteException {
		return this.objetCourant.get(salle);
//=======
//	}
//
//	public IObjet getObjet() throws RemoteException {
//		return this.objetCourant;
//>>>>>>> bdd
	}

	public List<IAcheteur> getListeAcheteurs(int salle) {
		return this.salles.get(salle).getFirst();
	}

	public void setListeAcheteurs(List<IAcheteur> listeAcheteurs,int salle) {
		this.salles.get(salle).setFirst(listeAcheteurs);
	}

	public IObjet getObjetCourant(int salle) {
		return objetCourant.get(salle);
	}

	public void setObjetCourant(IObjet objetCourant,int salle) {
		this.objetCourant.set(salle, (Objet) objetCourant);
	}

//<<<<<<< HEAD
	public Stack<Objet> getListeObjets(int salle) {
		return listeObjets.get(salle);
	}

	public void setListeObjets(Stack<Objet> listeObjets,int salle) {
		this.listeObjets.set(salle, listeObjets);
//=======
//	public Stack<IObjet> getListeObjets() {
//		return listeObjets;
//	}
//
//	public void setListeObjets(Stack<IObjet> listeObjets) {
//		this.listeObjets = listeObjets;
//>>>>>>> bdd
	}

	public IAcheteur getAcheteurCourant(int salle) {
		return acheteurCourant.get(salle);
	}

	public void setAcheteurCourant(IAcheteur acheteurCourant,int salle) {
		this.acheteurCourant.set(salle, acheteurCourant);
	}

	public EtatVente getEtatVente(int salle) {
		return etatVente.get(salle);
	}

	public void setEtatVente(EtatVente etatVente,int salle) {
		this.etatVente.set(salle, etatVente);
	}

	// REMOTE GETTERS

	@Override
	public int getPrixCourant(int salle) throws RemoteException {
		return this.objetCourant.get(salle).getPrixCourant();
	}

	@Override
	public String getGagnantEnchere(int salle) throws RemoteException {
		return this.objetCourant.get(salle).getGagnant();
	}

}
