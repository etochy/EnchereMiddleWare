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
import com.alma.api.IVente;
import com.alma.data.Data;
import com.alma.data.EtatVente;
import com.alma.data.Objet;
import com.alma.data.Pair;

public class VenteImpl extends UnicastRemoteObject implements IVente {

	private static final long serialVersionUID = 1L;
	private static final int CLIENT_MIN = 2;
	private static final int NB_SALLE = 10;

	private List<Pair<List<IAcheteur>, List<IAcheteur>>> salles;
	private List<Map<IAcheteur, Integer>> enchereCourante;
	private List<IAcheteur> acheteurCourant;
	private List<EtatVente> etatVente;
	private List<Stack<Objet>> listeObjets;
	private List<Objet> objetCourant;

	protected VenteImpl() throws RemoteException {
		super();

		// Les pairs permettent de séparer les utilisateurs de la file d'attente
		// des utilisateurs
		// participant à l'enchère dans la salle courante.
		salles = new ArrayList<Pair<List<IAcheteur>, List<IAcheteur>>>();
		enchereCourante = new ArrayList<Map<IAcheteur, Integer>>();
		acheteurCourant = new ArrayList<IAcheteur>();
		etatVente = new ArrayList<EtatVente>();
		listeObjets = new ArrayList<Stack<Objet>>();
		objetCourant = new ArrayList<Objet>();

		try {
			Data d = Data.getInstance();

			// Création des salles.
			for (int i = 0; i < NB_SALLE; ++i) {
				salles.add(new Pair<List<IAcheteur>, List<IAcheteur>>(new ArrayList<IAcheteur>(),
						new ArrayList<IAcheteur>()));
				listeObjets.add(new Stack<Objet>());
				etatVente.add(EtatVente.ATTENTE);
				objetCourant.add(null);
				acheteurCourant.add(null);
				enchereCourante.add(new HashMap<IAcheteur, Integer>());

				for (Objet obj : d.getListNotSoldObject()) {
					if (obj.getNumSalle() == i) {
						listeObjets.get(i).push(obj);
					}
				}

				if (!listeObjets.get(i).isEmpty()) {
					objetCourant.set(i, listeObjets.get(i).pop());
				}
			}

		} catch (NoSuchElementException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}

	}

	public synchronized boolean inscriptionAcheteur(String login, IAcheteur acheteur, int salle) throws Exception {
		System.out.println("add acheteur salle : " + salle);
		for (IAcheteur each : salles.get(salle).getFirst()) { // listeAcheteurs
			if (each.getPseudo().equals(login) || each.getPseudo().equals(acheteur.getPseudo())) {
				throw new Exception("Login deja pris");
			}
		}
		this.salles.get(salle).getSecond().add(acheteur);

		if (this.salles.get(salle).getSecond().size() >= CLIENT_MIN && this.etatVente.get(salle) == EtatVente.ATTENTE) {
			this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);

			for (IAcheteur each : this.salles.get(salle).getSecond()) {
				this.salles.get(salle).getFirst().add(each);
				if (objetCourant.get(salle) == null)
					each.nouveauParticipant();
				else
					each.nouveauParticipant(
							acheteurCourant.get(salle) != null ? acheteurCourant.get(salle).getPseudo() : null,
							objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(),
							objetCourant.get(salle).getNom());
			}
			this.salles.get(salle).getSecond().clear();
			return true;
		}
		return false;
	}

	public synchronized int rencherir(int nouveauPrix, IAcheteur acheteur, int salle) throws Exception {
		// On ajoute la proposition d'un acheteur à une liste temporaire
		this.enchereCourante.get(salle).put(acheteur, nouveauPrix);
		System.out.println(this.enchereCourante.get(salle).size() + "/" + this.salles.get(salle).getFirst().size());

		// Toutes les propositions des acheteurs ont été reçues
		if (this.enchereCourante.get(salle).size() == this.salles.get(salle).getFirst().size()) {
			if (enchereFinie(salle)) {
				return objetSuivant(salle);
			} else {
				actualiserObjet(salle);
				// On renvoie le resultat du tour
				for (IAcheteur each : this.salles.get(salle).getFirst()) {
					each.nouveauPrix(objetCourant.get(salle).getPrixCourant(), acheteurCourant.get(salle));
				}
			}
		}

		return objetCourant.get(salle).getPrixCourant();
	}

	/**
	 * Permet de passer à l'objet suivant avec les bons acheteurs et bons
	 * objets.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws NoSuchElementException
	 */
	public int objetSuivant(int salle)
			throws InterruptedException, NoSuchElementException, IllegalArgumentException, IOException {
		enchereCourante.get(salle).clear();
		etatVente.set(salle, EtatVente.ATTENTE);

		if (acheteurCourant.get(salle) != null) {
			objetCourant.get(salle).setDisponible(false);
			objetCourant.get(salle).setGagnant(this.acheteurCourant.get(salle).getPseudo());

			Data.getInstance().soldObject(objetCourant.get(salle).getNom(), salle);

			// Envoie des resultats finaux pour l'objet courant
			for (IAcheteur each : this.salles.get(salle).getFirst()) {
				each.objetVendu(this.acheteurCourant.get(salle).getPseudo(), objetCourant.get(salle).getPrixCourant(),
						objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
			}
		}

		// Lenteur entre changements :
		Thread.sleep(5000);

		this.acheteurCourant.set(salle, null);
		this.salles.get(salle).getFirst().addAll(this.salles.get(salle).getSecond());
		this.salles.get(salle).getSecond().clear();

		// Il y a encore des objets à vendre
		if (!this.listeObjets.get(salle).isEmpty()) {
			this.acheteurCourant.set(salle, null);
			this.objetCourant.set(salle, listeObjets.get(salle).pop());
			this.objetCourant.get(salle).setGagnant("");
			this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);
			for (IAcheteur each : this.salles.get(salle).getFirst()) {
				each.reprendreEnchere("", objetCourant.get(salle).getPrixCourant(),
						objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
			}
		} else {
			this.etatVente.set(salle, EtatVente.TERMINE);
			for (IAcheteur each : this.salles.get(salle).getFirst()) {
				each.objetVendu("", objetCourant.get(salle).getPrixCourant(), objetCourant.get(salle).getDescription(),
						objetCourant.get(salle).getNom());
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
	public void actualiserObjet(int salle) throws RemoteException {
		Set<IAcheteur> cles = this.enchereCourante.get(salle).keySet();
		Iterator<IAcheteur> it = cles.iterator();

		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereCourante.get(salle).get(cle);

			if (valeur > this.objetCourant.get(salle).getPrixCourant()
					|| (valeur == this.objetCourant.get(salle).getPrixCourant()
							&& cle.getChrono() < acheteurCourant.get(salle).getChrono())) {
				this.objetCourant.get(salle).setPrixCourant(valeur);
				this.acheteurCourant.set(salle, cle);
				this.objetCourant.get(salle).setGagnant(this.acheteurCourant.get(salle).getPseudo());
			}
		}
		this.enchereCourante.get(salle).clear();
	}

	/**
	 * Permet de savoir si les enchères pour l'objet proposé sont finies.
	 * 
	 * @return true Si uniquement des -1 sont reçus, false sinon.
	 */

	public boolean enchereFinie(int salle) {

		boolean ret = false;

		Set<IAcheteur> cles = this.enchereCourante.get(salle).keySet();
		Iterator<IAcheteur> it = cles.iterator();

		int cpt = 0;

		// Compte le nombre d'acheteur passant l'enchère
		while (it.hasNext()) {
			IAcheteur cle = it.next();
			Integer valeur = this.enchereCourante.get(salle).get(cle);

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
				if (enchereCourante.get(salle).get(current) != -1) {
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
				if (tempName.equals(acheteurCourant.get(salle).getPseudo()))
					ret = true;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public void ajouterObjet(String nom, String description, int prix, int salle) throws RemoteException {

		try {
			Objet newObj = new Objet(nom, description, prix, salle);
			this.listeObjets.get(salle).add(newObj);

			// Ajout de l'objet dans les données persistantes
			Data.getInstance().addObject(newObj);

			for (Objet o : listeObjets.get(salle))
				System.out.println("objet : " + o.getNom());
			if (objetCourant.get(salle) == null) {
				this.objetCourant.set(salle, listeObjets.get(salle).pop());
				this.objetCourant.get(salle).setGagnant("");
				this.etatVente.set(salle, EtatVente.ENCHERISSEMENT);
				for (IAcheteur each : this.salles.get(salle).getFirst()) {
					System.out.println("reprendre enchere");
					each.reprendreEnchere("", objetCourant.get(salle).getPrixCourant(),
							objetCourant.get(salle).getDescription(), objetCourant.get(salle).getNom());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void disconnect(IAcheteur acheteur) {
		for(int i = 0; i< salles.size(); ++i) {
			salles.get(i).getFirst().remove(acheteur);
			salles.get(i).getSecond().remove(acheteur);
		}
	}

	public List<IAcheteur> getListeAcheteurs(int salle) {
		return this.salles.get(salle).getFirst();
	}

	public void setListeAcheteurs(List<IAcheteur> listeAcheteurs, int salle) {
		this.salles.get(salle).setFirst(listeAcheteurs);
	}

	public Stack<Objet> getListeObjets(int salle) {
		return listeObjets.get(salle);
	}

	public void setListeObjets(Stack<Objet> listeObjets, int salle) {
		this.listeObjets.set(salle, listeObjets);
	}

	public IAcheteur getAcheteurCourant(int salle) {
		return acheteurCourant.get(salle);
	}

	public void setAcheteurCourant(IAcheteur acheteurCourant, int salle) {
		this.acheteurCourant.set(salle, acheteurCourant);
	}

	public EtatVente getEtatVente(int salle) {
		return etatVente.get(salle);
	}

	public void setEtatVente(EtatVente etatVente, int salle) {
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
