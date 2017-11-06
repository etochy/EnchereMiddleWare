package serveur;

import java.rmi.Remote;

public interface IObjet extends Remote {

	String getNom();

	void setNom(String nom);

	String getDescription();

	void setDescription(String description);

	int getPrixBase();

	void setPrixBase(int prixBase);

	boolean isDisponible();

	void setDisponible(boolean disponible);

	int getPrixCourant();

	void setPrixCourant(int prixCourant);

	String getGagnant();

	void setGagnant(String gagnant);

}