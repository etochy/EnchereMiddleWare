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

public class VenteImpl extends UnicastRemoteObject implements IVente {

	private static final long serialVersionUID = 1L;
	private List<IAcheteur> listeAcheteurs = new ArrayList<IAcheteur>();
	private List<IAcheteur> fileAttente = new ArrayList<IAcheteur>();
	private Map<IAcheteur, Integer> enchereTemp = new HashMap<IAcheteur, Integer>();
	private IObjet objetCourant;
	private Stack<IObjet> listeObjets;
	private IAcheteur acheteurCourant;
	private EtatVente etatVente;
	private final int clientMin = 2;

	protected VenteImpl() throws RemoteException {
		super();
		listeObjets = new Stack<IObjet>();
		
		try {
			Donnees.getInstance().load();
			
			// Copie des objets dans la stack de la vente courrante
			for(IObjet obj : Donnees.getInstance().getListeObjets())
				listeObjets.push(obj);
			
		} catch (NoSuchElementException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}

		this.etatVente = EtatVente.ATTENTE;
	}

	public synchronized boolean inscriptionAcheteur(String login, IAcheteur acheteur) throws Exception {
		for (IAcheteur each : listeAcheteurs) {
			if (each.getPseudo().equals(login) || each.getPseudo().equals(acheteur.getPseudo())) {
				throw new Exception("Login deja pris");
			}
		}
		this.fileAttente.add(acheteur);

		if (this.fileAttente.size() >= clientMin && this.etatVente == EtatVente.ATTENTE) {
			this.etatVente = EtatVente.ENCHERISSEMENT;

			for (IAcheteur each : this.fileAttente) {
				this.listeAcheteurs.add(each);
				if (objetCourant == null)
					each.nouveauParticipant();
				else
					each.nouveauParticipant(acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
							objetCourant.getDescription(), objetCourant.getNom());
			}
			this.fileAttente.clear();
			return true;
		}
		return false;
	}

	public synchronized int rencherir(int nouveauPrix, IAcheteur acheteur) throws Exception {

		// On ajoute la proposition d'un acheteur à une liste temporaire
		this.enchereTemp.put(acheteur, nouveauPrix);

		System.out.println(this.enchereTemp.size() + "/" + this.listeAcheteurs.size()); // DEBUG
																						// LINE

		// Toutes les propositions des acheteurs ont été reçues
		if (this.enchereTemp.size() == this.listeAcheteurs.size()) {
			if (enchereFinie()) {
				return objetSuivant();
			} else {

				actualiserObjet();

				// On renvoit le résultat du tour
				for (IAcheteur each : this.listeAcheteurs) {
					each.nouveauPrix(this.objetCourant.getPrixCourant(), this.acheteurCourant);
				}
			}
		}

		return objetCourant.getPrixCourant();
	}

	/**
	 * Permet de passer à l'objet suivant avec les bons acheteurs et bons
	 * objets.
	 * 
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public int objetSuivant() throws RemoteException, InterruptedException {
		this.enchereTemp.clear();
		this.etatVente = EtatVente.ATTENTE;

		if (acheteurCourant != null) {
			this.objetCourant.setDisponible(false);
			this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());

			// Envoie des resultats finaux pour l'objet courant
			for (IAcheteur each : this.listeAcheteurs) {
				each.objetVendu(this.acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
						objetCourant.getDescription(), objetCourant.getNom());
			}
		}

		// Lenteur entre changements :
		Thread.sleep(5000);

		// On fait participer les Acheteurs en attente (ceux arriver pendant une
		// enchère en cours)
		this.listeAcheteurs.addAll(this.fileAttente);
		this.fileAttente.clear();

		// Il y a encore des objets à vendre
		if (!this.listeObjets.isEmpty()) {
			this.acheteurCourant = null;
			this.objetCourant = this.listeObjets.pop();
			this.objetCourant.setGagnant("");
			this.etatVente = EtatVente.ENCHERISSEMENT;
			for (IAcheteur each : this.listeAcheteurs) {
				each.reprendreEnchere("", objetCourant.getPrixCourant(), objetCourant.getDescription(),
						objetCourant.getNom());
			}
		} else {
			this.etatVente = EtatVente.TERMINE;
			for (IAcheteur each : this.listeAcheteurs) {
				// each.finEnchere();
				each.objetVendu(acheteurCourant.getPseudo(), objetCourant.getPrixCourant(),
						objetCourant.getDescription(), objetCourant.getNom());
			}
			this.acheteurCourant = null;
			return 0;
		}
		return this.objetCourant.getPrixBase();
	}

	/**
	 * Actualise le prix de l'objet et le gagnant selon les enchères reçues.
	 * 
	 * @throws RemoteException
	 */
	public void actualiserObjet() throws RemoteException {
		Set<IAcheteur> cles = this.enchereTemp.keySet();
		Iterator<IAcheteur> it = cles.iterator();

		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(cle);

			if (valeur > this.objetCourant.getPrixCourant() || (valeur == this.objetCourant.getPrixCourant()
					&& cle.getChrono() < acheteurCourant.getChrono())) {
				this.objetCourant.setPrixCourant(valeur);
				this.acheteurCourant = cle;
				this.objetCourant.setGagnant(this.acheteurCourant.getPseudo());
			}
		}
		this.enchereTemp.clear();
	}

	/**
	 * Permet de savoir si les enchères pour l'object proposé sont finies.
	 * 
	 * @return true Si uniquement des -1 sont reçus, false sinon.
	 */
	public boolean enchereFinie() {

		boolean ret = false;

		Set<IAcheteur> cles = this.enchereTemp.keySet();
		Iterator<IAcheteur> it = cles.iterator();

		int cpt = 0;

		// Compte le nombre d'acheteur passant l'enchère
		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereTemp.get(cle);

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
				if (enchereTemp.get(current) != -1) {
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
				if (tempName.equals(acheteurCourant.getPseudo()))
					ret = true;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public void ajouterObjet(String nom, String description, int prix, int numSalle) throws RemoteException {
		try {
			Objet newObj = new Objet(nom, description, prix, numSalle);
			this.listeObjets.add(newObj);
			
			// Ajout de l'objet dans les données persistantes
			Donnees.getInstance().ajouterArticle(newObj);
			Donnees.getInstance().save();
						
			if (objetCourant == null) {
				this.objetCourant = listeObjets.pop();
				this.objetCourant.setGagnant("");
				this.etatVente = EtatVente.ENCHERISSEMENT;
				for (IAcheteur each : this.listeAcheteurs) {
					each.reprendreEnchere("", objetCourant.getPrixCourant(), objetCourant.getDescription(),
							objetCourant.getNom());
				}
			}
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

	public Stack<IObjet> getListeObjets() {
		return listeObjets;
	}

	public void setListeObjets(Stack<IObjet> listeObjets) {
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
