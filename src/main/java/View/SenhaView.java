package View;

import Auth.Authentification;
import Auth.Node;
import Database.DBControl;
import Database.DBManager;
import Database.LoggedUser;
import Component.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SenhaView extends DefaultFrame {

	/* **************************************************************************************************
	 **
	 **  Variables desclaration
	 **
	 ****************************************************************************************************/
	
	private final int width = 400;
	private final int height = 300;

	private JTextField passwordField;
	private JButton checkButton;

	private Node root = new Node("");
	private int numCliques = 0;

	/* **************************************************************************************************
	 **
	 **  Senha View Init
	 **
	 ****************************************************************************************************/
	
	public SenhaView() {

		//--------------------- Insert Register --------------------------------

		DBControl.getInstance().insertRegister(3001, (String) LoggedUser.getInstance().getUser().get("email"));

		//------------------------ Set View ------------------------------------

		this.setView();

		//------------------------ Check Button Action --------------------------
		
		checkButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				HashMap updatedUser = Authentification.autenticaEmail((String) LoggedUser.getInstance().getUser().get("email"));
				Integer acessosNegados = ((Integer) updatedUser.get("numAcessoErrados"));
				if (acessosNegados >= 3) {		
					DBManager.insereRegistro(3007, (String) updatedUser.get("email"));
					JOptionPane.showMessageDialog(null, "Senha incorreta. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
					dispose();
					new LoginView();
				}
				
				int tamSenha = passwordField.getText().length();
				if (tamSenha < 6 || tamSenha > 8) {
					JOptionPane.showMessageDialog(null, "Senha deve conter de 6 a 8 números.");
					return;
				}
				if (Authentification.verificaArvoreSenha(root, updatedUser, "")) {
					DBManager.insereRegistro(3003, (String) updatedUser.get("email"));
					DBManager.insereRegistro(3002, (String) updatedUser.get("email"));
					DBManager.zeraAcessoErrado((String)updatedUser.get("email"));
					dispose();
					new PrivateKeyView(Authentification.autenticaEmail((String)updatedUser.get("email")));
				}
				else {
					DBManager.incrementaAcessoErrado((String)updatedUser.get("email"));
					updatedUser = Authentification.autenticaEmail((String) updatedUser.get("email"));
					acessosNegados = ((Integer) updatedUser.get("numAcessoErrados"));
					
					if (acessosNegados == 1) {
						DBManager.insereRegistro(3004, (String) updatedUser.get("email"));
					}
					else if (acessosNegados == 2) {
						DBManager.insereRegistro(3005, (String) updatedUser.get("email"));
					}
					else if (acessosNegados == 3) {		
						DBManager.insereRegistro(3006, (String) updatedUser.get("email"));
					}
					
					JOptionPane.showMessageDialog(null, "Senha incorreta");
					
					root = new Node("");;
					passwordField.setText("");
					numCliques = 0;
				}
			}
		});
	}

	/* **************************************************************************************************
	 **
	 **  Set View
	 **
	 ****************************************************************************************************/

	private void setView() {

		//------------------------ Set Title ------------------------------------

		setTitle("Senha");

		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//------------------------ Title Label -----------------------------------

		JLabel titleLabel = new JLabel("Senha:");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(0, yPosition, this.width, 40);

		Font f = titleLabel.getFont();
		titleLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 25));

		this.getContainer().add(titleLabel);

		yPosition = yPosition + titleLabel.getSize().height + 10;

		//------------------------ Password Field -----------------------------------

		passwordField = new JTextField();
		passwordField.setEnabled(false);
		passwordField.setBounds(50, yPosition, 300, 50);

		this.getContainer().add(passwordField);

		yPosition = passwordField.getY() + passwordField.getSize().height + 10;

		//---------------------------- Login Button --------------------------------

		checkButton = new JButton("login");
		checkButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(checkButton);

		yPosition = yPosition + checkButton.getSize().height + 10;

		//---------------------- Generate Password Options ----------------------

		List<List<String>> options = geraOpcoes();

		final List<JButton> listButtons = new ArrayList<JButton>();

		for (int i=0; i<5; i++) {

			String textToBtn = "";

			for(String text : options.get(i)) {

				if( text == "" )
					textToBtn += text + " ou ";
				else
					textToBtn += text + " ";

			}

			JButton passwordButton = new JButton(textToBtn);

			passwordButton.setBounds(15 + (i * 75), yPosition, 75, 60);

			//---------------------- Password Button Action ----------------------

			passwordButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {

					//Precisa?
//					if (numCliques == 8) {
//
//						JOptionPane.showMessageDialog(null, "Senha tem no máximo 8 caracteres");
//
//						return;
//					}

					numCliques++;
					passwordField.setText(passwordField.getText() + "*");
					insereNosFolhas(root, ((JButton)e.getSource()).getText().replace("ou", ""));

					sorteiaBotoes(listButtons);
				}
			});
			listButtons.add(passwordButton);
			this.getContainer().add(passwordButton);

		}

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

	private void insereNosFolhas(Node root, String opcoes) {
		if (root.dir == null && root.esq == null) {
			root.esq = new Node(""+opcoes.charAt(0));
			root.dir = new Node(""+opcoes.charAt(2));
			return;
		}
		insereNosFolhas(root.dir, opcoes);
		insereNosFolhas(root.esq, opcoes);
	}
	
	private void sorteiaBotoes(List<JButton> lista) {
		List<List<String>> opcoes = geraOpcoes();
		for (int i=0; i<5; i++) {
			JButton btn = lista.get(i);

			String textToBtn = "";

			for(String text : opcoes.get(i)){

				if( text == "" )
					textToBtn += text + " ou ";
				else
					textToBtn += text + " ";

			}

			btn.setText(textToBtn);
		}
	}
	
	private List<List<String>> geraOpcoes() {
		List<List<String>> list = new ArrayList<List<String>>();
		String numeros = "0123456789";
		
		for (int i=0; i < 5; i++) {
			Random rand = new Random();
			List<String> opcao = new ArrayList<String>();
			int index = rand.nextInt(numeros.length());
			opcao.add(""+numeros.charAt(index));
			numeros = numeros.replaceAll("" + numeros.charAt(index), "");
			index = rand.nextInt(numeros.length());
			opcao.add(""+numeros.charAt(index));
			numeros = numeros.replaceAll("" + numeros.charAt(index), "");
			list.add(opcao);
		}
		return list;
	}
}
