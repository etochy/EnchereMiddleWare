package com.alma.control;

import com.alma.view.VueClient;

public class Client {	
	
	public static void main(String[] argv) {
		try {
			// Mise en place de l'interface client
			new VueClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
