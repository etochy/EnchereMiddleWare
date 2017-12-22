package com.alma.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAcheteur extends Remote {

	/**
	 * 
	 * @param gagnant
	 *            Client qui a gagne l'enchere
	 * @throws RemoteException
	 */
	public void objetVendu(String gagnant, int prix, String descObj, String objNom) throws RemoteException;
	
	public void nouveauParticipant() throws RemoteException;

	public void nouveauParticipant(String gagnant, int prix, String descObj, String objNom) throws RemoteException;

	public void nouveauPrix(int prix, IAcheteur gagnant) throws RemoteException;
	
	public void finEnchere() throws RemoteException;

	public String getPseudo() throws RemoteException;

	public long getChrono() throws RemoteException;


}
