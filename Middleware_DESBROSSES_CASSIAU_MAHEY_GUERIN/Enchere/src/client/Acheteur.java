package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Acheteur extends Remote {

	/**
	 * 
	 * @param gagnant
	 *            Client qui a gagne l'enchere
	 * @throws RemoteException
	 */
	public void objetVendu(String gagnant) throws RemoteException;

	public void nouveauPrix(int prix, Acheteur gagnant) throws RemoteException;
	
	public void finEnchere() throws RemoteException;

	public String getPseudo() throws RemoteException;

	public long getChrono() throws RemoteException;


}
