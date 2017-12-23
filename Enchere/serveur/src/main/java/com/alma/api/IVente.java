package com.alma.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IVente extends Remote {

	/**
	 * Methode servant a inscrire un acheteur a une vente. Ajoute l'acheteur dans la liste des acheteurs
	 * @param login 
	 * @param acheteur
	 * @throws RemoteException
	 */
	public boolean inscriptionAcheteur(String login, IAcheteur acheteur) throws RemoteException, Exception;
	
	/**
	 * Augmente le prix de l'objet a vendre.
	 * @param nouveauPrix le nouveau prix que le client a donne
	 * @param acheteur l'acheteur ayant encherit 
	 * @return le nouveau prix de l'objet a vendre
	 * @throws RemoteException
	 */
	public int rencherir(int nouveauPrix, IAcheteur acheteur) throws RemoteException, Exception;
	

	/**
	 * Methode permettant d ajouter un objet aux encheres.
	 * @param nouveau l'objet a vendre.
	 * @throws RemoteException
	 */
	public void ajouterObjet(String nom, String description, int prix, int numSalle) throws RemoteException;
	
	public int getPrixCourant() throws RemoteException;
	
	public String getGagnantEnchere() throws RemoteException;
	
	public IObjet getObjet() throws RemoteException;


}
