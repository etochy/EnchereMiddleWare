package serveur;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.Acheteur;

public interface Vente extends Remote {

	/**
	 * Methode servant a inscrire un acheteur a une vente. Ajoute l'acheteur dans la liste des acheteurs
	 * @param login 
	 * @param acheteur
	 * @throws RemoteException
	 */
	public boolean inscriptionAcheteur(String login, Acheteur acheteur) throws RemoteException, Exception;
	
	/**
	 * Augmente le prix de l'objet a vendre.
	 * @param nouveauPrix le nouveau prix que le client a donne
	 * @param acheteur l'acheteur ayant encherit 
	 * @return le nouveau prix de l'objet a vendre
	 * @throws RemoteException
	 */
	public int rencherir(int nouveauPrix, Acheteur acheteur) throws RemoteException, Exception;
	

	/**
	 * Methode permettant d ajouter un objet aux encheres.
	 * @param objet l'objet a vendre.
	 * @throws RemoteException
	 */
	public void ajouterObjet(Objet objet) throws RemoteException;
	
	
	public Objet getObjet() throws RemoteException;


}
