package client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import serveur.IObjet;

public class VueClient extends JFrame implements ActionListener{

	private static final long serialVersionUID = 9070911591784925769L;
	
	// Informations sur de l'Etat de la vente
	private Client currentClient;
	
	// Elements SWING
	private JPanel mainPanel = new JPanel();
	private JPanel inscriptionPanel = new JPanel();
	
	private JLabel lblPrixObjet = new JLabel();
	private JLabel lblNomObjet = new JLabel();
	private JTextArea lblDescriptionObjet = new JTextArea(3,1);
	private JLabel lblPseudo = new JLabel();
	private JLabel lblEncherir = new JLabel();
	private JLabel lblChrono = new JLabel("chrono");

	private JButton btnEncherir = new JButton("Encherir");
	private JButton btnPseudo = new JButton("Inscription");
	private JButton btnSoumettre = new JButton("Soumettre une enchere");
	private JButton btnSoumettreObjet = new JButton("Soumettre");
	private JButton btnStop = new JButton("Passer");
	
	private JTextField txtEncherir = new JTextField();
	private JTextField txtPseudo = new JTextField();
	private JTextField txtIP = new JTextField("localhost:8090/enchere");
	private JTextField txtSoumettreNomObjet = new JTextField();
	private JTextField txtSoumettreDescriptionObjet = new JTextField();
	private JTextField txtSoumettrePrixObjet = new JTextField();
	
	private JFrame frmSoumettre = new JFrame("Soumettre une enchere");

	public JLabel getLblEncherir() {
		return lblEncherir;
	}

	public VueClient() throws Exception {
		super();

		//Definition de la fenetre
		this.setSize(400,400);
		this.setTitle("Vente aux encheres");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Font fontBtn = new Font("Serif", Font.PLAIN, 10);

		// PANEL INSCRIPTION
		inscriptionPanel.setLayout(new GridBagLayout());
	    txtPseudo.setPreferredSize(new Dimension(200, 40));
	    txtIP.setPreferredSize(new Dimension(200, 40));
	    btnPseudo.setPreferredSize(new Dimension(200,40));
		GridBagConstraints gbc = new GridBagConstraints();

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridheight = 1;
	    gbc.gridwidth = 3;
	    inscriptionPanel.add(new JLabel("Pseudo : "), gbc);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridheight = 1;
	    gbc.gridwidth = 3;
		inscriptionPanel.add(txtPseudo, gbc);
		
		gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridheight = 1;
	    gbc.gridwidth = 3;
		inscriptionPanel.add(new JLabel("IP serveur : "), gbc);
		gbc.gridx = 0;
	    gbc.gridy = 4;
	    gbc.gridheight = 1;
	    gbc.gridwidth = 3;
		inscriptionPanel.add(txtIP, gbc);
		
	    gbc.gridx = 0;
	    gbc.gridy = 5;
	    gbc.gridheight = 1;
	    gbc.gridwidth = 1;
		inscriptionPanel.add(btnPseudo, gbc);

		// PANEL VENTE
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(400,400));
		lblDescriptionObjet.setPreferredSize(new Dimension(300,100));
		lblDescriptionObjet.setEditable(false);
		lblDescriptionObjet.setLineWrap(true);
		txtEncherir.setPreferredSize(new Dimension(300,40));
		btnEncherir.setPreferredSize(new Dimension(100,40));
		btnEncherir.setFont(fontBtn);
		btnStop.setPreferredSize(new Dimension(100,40));
		btnStop.setFont(fontBtn);
		btnSoumettre.setPreferredSize(new Dimension(100,40));
		btnSoumettre.setFont(fontBtn);
				
		int yGrid = 0;
		
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		
		//1ere ligne
		gbc.gridx = 0;
		gbc.gridy = yGrid;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(lblNomObjet, gbc);
		
		gbc.gridy = ++yGrid;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(lblPrixObjet, gbc);
		
		gbc.gridy = ++yGrid;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(lblPseudo, gbc);
		
		gbc.gridy = ++yGrid;
		mainPanel.add(lblChrono, gbc);
		
//		gbc.anchor = GridBagConstraints.;
		
		//2eme ligne
		gbc.gridx = 0;
		gbc.gridy = ++yGrid;
		gbc.gridheight = 3;
		gbc.gridwidth = 3;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(lblDescriptionObjet, gbc);
		yGrid += 2;
		
		//3eme ligne
		gbc.gridy = ++yGrid;
		gbc.gridx = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(txtEncherir, gbc);
		
		//4eme ligne
		gbc.gridy = ++yGrid;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		mainPanel.add(btnEncherir, gbc);
		
		gbc.gridx=1;
		gbc.gridwidth=1;
		mainPanel.add(btnStop, gbc);
		
		gbc.gridx=2;
		gbc.gridwidth=1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(btnSoumettre, gbc);

		// Ajout des liaison avec les boutons
		btnEncherir.addActionListener(this);
		btnPseudo.addActionListener(this);
		btnSoumettre.addActionListener(this);
		btnSoumettreObjet.addActionListener(this);
		btnStop.addActionListener(this);

		this.setContentPane(inscriptionPanel);
		this.setVisible(true);
	}
	
	public void actualiserPrix() {
		lblPrixObjet.setText("Prix courant : " + currentClient.getCurrentObjet().getPrixCourant() + " euros");
		lblPseudo.setText("Gagnant : " + this.currentClient.getCurrentObjet().getGagnant());
		txtEncherir.setText("");
	}
	
	public void actualiserObjet() {
		IObjet objet = currentClient.getCurrentObjet();
		lblPrixObjet.setText("Prix actuel : " + objet.getPrixCourant() + " euros");
		lblPseudo.setText("Gagnant : " + objet.getGagnant());
		lblDescriptionObjet.setText(objet.getDescription());
		txtEncherir.setText("");
		lblNomObjet.setText(objet.getNom());
//		if (objet.isDisponible()) {
//			lblNomObjet.setText(objet.getNom() + "(disponible)");
//		}
//		else{
//			lblNomObjet.setText(objet.getNom() + "(vendu)");
//		}
	}
	
	private void setClient(Client client) {
		currentClient = client;
		client.setVue(this);
	}
	
	
	
	@Override
	public synchronized void actionPerformed(ActionEvent arg0) {
		// ENCHERIR			
		if(arg0.getSource().equals(this.btnEncherir)){
			if(!txtEncherir.getText().isEmpty()){
				try {	
					currentClient.encherir(Integer.parseInt(txtEncherir.getText()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//STOP
		else if(arg0.getSource().equals(this.btnStop)){
			try {
				currentClient.encherir(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		// INSCRIPTION
		else if(arg0.getSource().equals(btnPseudo)) {
			try {
				setClient(new Client(txtPseudo.getText(), txtIP.getText()));
				currentClient.inscription();
				changerGUI(this.mainPanel);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Inscription impossible");
			}
		}
		
		else if(arg0.getSource().equals(btnSoumettre)) {
			soumettre();
		}
		
		else if(arg0.getSource().equals(btnSoumettreObjet)) {
			try {
				currentClient.nouvelleSoumission(txtSoumettreNomObjet.getText(), txtSoumettreDescriptionObjet.getText(), Integer.parseInt(txtSoumettrePrixObjet.getText()));
			} catch (NumberFormatException e) {
				System.out.println("Impossible de soumettre cet objet.");
			}
			frmSoumettre.dispose();
		}
	}
	

	/**
	 * Methode servant a changer l affichage pour le panel passe en parametre.
	 * @param vue le JPanel a afficher.
	 * @throws RemoteException 
	 */
	public void changerGUI(JPanel vue) throws RemoteException{
		if(this.currentClient.getCurrentObjet() != null){
			actualiserObjet();
		}
		this.getContentPane().removeAll();
		this.setContentPane(vue);
		this.getContentPane().revalidate();
		this.getContentPane().repaint();
	}
	
	public void attente(){
		this.btnEncherir.setEnabled(false);
		this.btnStop.setEnabled(false);
	}
	
	public void reprise(){
		this.btnEncherir.setEnabled(true);
		this.btnStop.setEnabled(true);
	}

	private void soumettre() {
		frmSoumettre.setSize(400,400);
		JPanel pnlSoumettre = new JPanel(new GridLayout(3,3));
		frmSoumettre.add(pnlSoumettre);
		
		pnlSoumettre.add(new JLabel("Nom : "));
		pnlSoumettre.add(new JLabel("Description : "));
		pnlSoumettre.add(new JLabel("Prix initial : "));

		pnlSoumettre.add(txtSoumettreNomObjet);
		pnlSoumettre.add(txtSoumettreDescriptionObjet);
		pnlSoumettre.add(txtSoumettrePrixObjet);
		
		pnlSoumettre.add(btnSoumettreObjet);
		
		frmSoumettre.setVisible(true);
	}
	
	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getInscriptionPanel() {
		return inscriptionPanel;
	}
	
	public void updateChrono(long temps, long tempsMax){
		this.lblChrono.setText("Chrono : "+ temps+"/"+tempsMax);
	}


}
