package com.alma.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.alma.api.IObjet;
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
	 * @throws Exception
	 *             so l'objet est deja en vente ou si l'acheteur n'est pas
	 *             encore inscrit.
	 */
	public void ajouterArticle(Objet objet) throws Exception {
		for (IObjet each : this.listeObjets) {
			if (each.equals(objet)) {
				throw new Exception("Objet deja existant");
			}
		}

		this.listeObjets.add(objet);
	}

	/**
	 * Permet de sauvegarder les objets courants dans un fichier.
	 * @throws IOException 
	 */
	public void save() throws IOException {
		Writer writer;
		String json = gson.toJson(listeObjets);

		writer = new FileWriter(FILE_URL);
		writer.write(json);
		writer.close();
	}

	public void load() throws FileNotFoundException {
		Reader reader;
		String json = "";
		
		reader = new FileReader(FILE_URL);
		
		// TODO charger le fichier
		
		Type listType = new TypeToken<Stack<Objet>>() {
		}.getType();
	}

	public Stack<Objet> getListeObjets() {
		return listeObjets;
	}

	public void setListeObjets(Stack<Objet> listeObjets) {
		this.listeObjets = listeObjets;
	}

	// TEST

	public static void main(String[] args) {
		Objet obj1 = new Objet("table", "très belle table", 14);
		Objet obj2 = new Objet("cuillere", "Miaou", 40);

		try {
			Donnees d = Donnees.getInstance();
			d.ajouterArticle(obj1);
			d.ajouterArticle(obj2);

			d.save();
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
