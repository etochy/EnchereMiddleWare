package client;

public class Chrono extends Thread {

	private Client client;
	private long tempsFin;
	private long tempsEcoule;
	private boolean enCours = false;
	
	public Chrono(long secondes, Client c) {
		tempsFin = secondes;
		client = c;
	}
	
	
	public void run() {
		while(true) {
			if(enCours) {
				tempsEcoule = 0;
				while(tempsFin >= tempsEcoule && enCours) {
					try {
						sleep(1); // Attends 1ms
						tempsEcoule++;
						this.client.updateChrono();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if(enCours) {
					try {
						client.encherir(-1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			System.out.print("");
		}
	}
	


	
	public long getTemps() {
		return tempsEcoule;
	}

	public void demarrer() {
		enCours = true;
	}
	
	public void arreter() {
		enCours = false;
	}
	
	public boolean getFini() {
		return enCours;
	}

	public long getTempsFin() {
		return tempsFin;
	}	
	
}
