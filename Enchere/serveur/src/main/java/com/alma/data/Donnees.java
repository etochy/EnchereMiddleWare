package com.alma.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Donnees {

	private static Donnees INSTANCE;
	private static String FILE_URL = "src/main/resources/data.json";

	private Stack<Objet> listeObjets;
	private GsonBuilder builder;
	private Gson gson;

	/**
	 * Constructeur du singleton
	 */
	private Donnees() {
		listeObjets = new Stack<Objet>();
		builder = new GsonBuilder();
		gson = builder.create();
	}

	/**
	 * Permet de récupérer l'instance de Donnees.
	 * 
	 * @return L'unique instance de Donnees
	 * @throws NoSuchElementException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static Donnees getInstance() throws NoSuchElementException, IllegalArgumentException, IOException {
		if (INSTANCE == null) {
			synchronized (Donnees.class) {
				if (INSTANCE == null) {
					INSTANCE = new Donnees();
				}
			}
		}

		return INSTANCE;
	}

	/**
	 * Methode permettant l'ajout d'un nouvel objet aux enchere. Ajoute l'objet
	 * dans la liste des objets a vendre.
	 * 
	 * @param objet
	 *            l'objet a vendre.
	 */
	public void ajouterArticle(Objet objet) throws Exception {
		this.listeObjets.add(objet);
	}

	/**
	 * Permet de sauvegarder les objets courants dans un fichier.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		Writer writer;
		String json = gson.toJson(listeObjets);

		writer = new FileWriter(FILE_URL);
		writer.write(json);
		writer.close();
	}

	/**
	 * Permet de charger les objets sauvegardés.
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(FILE_URL));
		Type listType = new TypeToken<Stack<Objet>>() {
		}.getType();

		this.listeObjets = gson.fromJson(reader, listType);

		reader.close();
	}
	
	// GETTER AND SETTER

	public Stack<Objet> getListeObjets() {
		return listeObjets;
	}

	public void setListeObjets(Stack<Objet> listeObjets) {
		this.listeObjets = listeObjets;
	}
}
