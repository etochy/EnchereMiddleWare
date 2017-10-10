package serveur;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;




public class Serveur{

	private final static int port = 8090;
	private static Donnees bdd = new Donnees();


	public static void main(String[] argv) {
		
		try {
			System.out.println("@ IP : " + InetAddress.getLocalHost());
			
			bdd.initObjets();
			VenteImpl vente = new VenteImpl(bdd.getListeObjets());
			LocateRegistry.createRegistry(port);
			Naming.bind("//localhost:"+port+"/enchere", vente);

	
		while(true){
			
			//On recrée une nouvelle vente
			if(vente.getEtatVente() == EtatVente.TERMINE){
				bdd.initObjets();
				vente = new VenteImpl(bdd.getListeObjets());
			}
			
		}
			
		} catch(RemoteException |  MalformedURLException | UnknownHostException | AlreadyBoundException e){
			e.printStackTrace();
		}		
	}	
}
	
