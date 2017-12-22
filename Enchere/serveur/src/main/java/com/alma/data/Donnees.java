package com.alma.data;

import java.util.Stack;

import com.alma.api.IObjet;


public class Donnees {

	private Stack<Objet> listeObjets = new Stack<Objet>();

	public Stack<Objet> getListeObjets() {
		return listeObjets;
	}

	public void setListeObjets(Stack<Objet> listeObjets) {
		this.listeObjets = listeObjets;
	}

	
	//TODO a virer par la suite
	public void initObjets(){

	}
	
	
	
	
	/**
	 * Methode permettant l'ajout d'un nouvel objet aux enchere. Ajoute l'objet dans la liste des objets a vendre.
	 * @param objet l'objet a vendre.
	 * @throws Exception so l'objet est deja en vente ou si l'acheteur n'est pas encore inscrit.
	 */
	public void ajouterArticle(Objet objet) throws Exception{
		for(IObjet each : this.listeObjets){
			if(each.equals(objet)){
				throw new Exception("Objet deja existant");
			}
		}

		this.listeObjets.add(objet);
	}
	
	
}
