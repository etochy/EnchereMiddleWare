package com.alma.control;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.alma.data.Donnees;
import com.alma.data.EtatVente;

public class Serveur{

	private final static int port = 8090;
	private static Donnees bdd = new Donnees();


	public static void main(String[] argv) {

		try {
			System.out.println("@ IP : " + InetAddress.getLocalHost());
			VenteImpl vente = new VenteImpl();

			LocateRegistry.createRegistry(port);
			Naming.bind("//localhost:"+port+"/enchere", vente);

			// Serveur lanc� en continu, m�me si aucune enchere
			while(true){

				//On recrée une nouvelle vente
				if(vente.getEtatVente() == EtatVente.TERMINE){
					//TODO : Recréer correctement les ventes ?
				}

			}

		} catch(RemoteException |  MalformedURLException | UnknownHostException | AlreadyBoundException e){
			e.printStackTrace();
		}		
	}	
}

