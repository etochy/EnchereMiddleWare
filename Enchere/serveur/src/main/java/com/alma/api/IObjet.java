package com.alma.api;

import java.io.Serializable;

public interface IObjet extends Serializable {

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