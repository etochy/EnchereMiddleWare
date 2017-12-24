package com.alma.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Data {

	private static Data INSTANCE;
	private static String URL_NOT_SOLD = "src/main/resources/not_sold.json";
	private static String URL_SOLD = "src/main/resources/sold.json";

	private List<Objet> listNotSoldObject;
	private List<Objet> listSoldObject;
	private GsonBuilder builder;
	private Gson gson;

	/**
	 * Creates a new instance of Data.
	 * 
	 * @throws IOException
	 */
	private Data() throws IOException {
		listNotSoldObject = new ArrayList<Objet>();
		listSoldObject = new ArrayList<Objet>();
		builder = new GsonBuilder();
		gson = builder.create();

		load(); // Loads stored objects that have not been sold
	}

	// PRIVATE METHODS

	/*
	 * Save current stacks in json data file.
	 */
	private void save() throws IOException {
		Writer writer;
		File fileNotSold = new File(URL_NOT_SOLD);
		File fileSold = new File(URL_SOLD);
		String jsonNotSold = gson.toJson(listNotSoldObject);
		String jsonSold = gson.toJson(listSoldObject);

		fileNotSold.createNewFile();
		fileSold.createNewFile();

		// Save not sold objects

		writer = new FileWriter(fileNotSold);
		writer.write(jsonNotSold);
		writer.close();

		// Save sold objects

		writer = new FileWriter(fileSold);
		writer.write(jsonSold);
		writer.close();

	}

	/*
	 * Load json data files.
	 */
	private void load() throws IOException {
		List<Objet> jsonData = null;
		File fileNotSold = new File(URL_NOT_SOLD);
		File fileSold = new File(URL_SOLD);
		BufferedReader readerNotSold = null;
		BufferedReader readerSold = null;
		Type listType = new TypeToken<List<Objet>>() {
		}.getType();

		// Load not sold objects
		if (fileNotSold.exists()) {
			readerNotSold = new BufferedReader(new FileReader(fileNotSold));
			jsonData = gson.fromJson(readerNotSold, listType);

			if (jsonData != null)
				listNotSoldObject.addAll(jsonData);

			readerNotSold.close();
		}

		// Load sold objects
		if (fileSold.exists()) {
			readerSold = new BufferedReader(new FileReader(URL_SOLD));
			jsonData = gson.fromJson(readerSold, listType);

			if (jsonData != null)
				listSoldObject.addAll(jsonData);

			readerSold.close();
		}
	}

	// PUBLIC METHODS

	/**
	 * Returns the unique instance of Data.
	 * 
	 * @return The instance of Data
	 * @throws NoSuchElementException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static Data getInstance() throws NoSuchElementException, IllegalArgumentException, IOException {
		if (INSTANCE == null) {
			synchronized (Data.class) {
				if (INSTANCE == null) {
					INSTANCE = new Data();
				}
			}
		}

		return INSTANCE;
	}

	/**
	 * Add a new object for sale and save it in json files.
	 * 
	 * @param obj
	 *            The new object.
	 * @throws IOException
	 */
	public synchronized void addObject(Objet obj) throws IOException {
		listNotSoldObject.add(obj);
		save();
	}

	/**
	 * Remove object from not sold list and add it into sold list. Then, save it
	 * in json files.
	 * 
	 * @throws IOException
	 */
	public synchronized void soldObject(String name, int nbRoom) throws IOException {
		for (Objet obj : listNotSoldObject) {
			if (obj.getNom().equals(name) && obj.getNumSalle() == nbRoom) {
				listNotSoldObject.remove(obj);
				listSoldObject.add(obj);
				break;
			}
		}

		save();
	}

	// GETTERS

	public List<Objet> getListNotSoldObject() {
		return listNotSoldObject;
	}

	public List<Objet> getListSoldObject() {
		return listSoldObject;
	}
}
