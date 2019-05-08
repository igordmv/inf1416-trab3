package View;

import Auth.Authentification;
import Auth.Node;
import Database.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SenhaView extends JFrame {
	
	private final int width = 400;
	private final int height = 500;
	
	private HashMap user = null;
	private Node root = new Node("");
	private int numCliques = 0;
	
	public SenhaView(final HashMap user) {
		this.user = user;
		DBManager.insereRegistro(3001, (String) user.get("email"));
		
		setLayout(null);
		setSize (this.width, this.height);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setTitle("Login");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);
		
		
		Container c = getContentPane();
		
		JLabel senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(30, 100, 200, 50);
		c.add(senhaLabel);
		
		final JTextField passwordField = new JTextField();
		passwordField.setEnabled(false);
		passwordField.setBounds(30, 150, 200, 50);
		c.add(passwordField);
		
		List<List<String>> opcoes = geraOpcoes();
		
		final List<JButton> listaButtons = new ArrayList<JButton>();
		
		for (int i=0; i<5; i++) {

			String textToBtn = "";
			for(String text : opcoes.get(i))
				textToBtn += text + " ";
			JButton senhaButton = new JButton(textToBtn);
			senhaButton.setBounds(30 + (i * 65), 300, 60, 60);
			senhaButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					if (numCliques == 8) {
						System.out.println("Senha tem no max 8 numeros");
						return;
					}
					numCliques++;
					passwordField.setText(passwordField.getText() + "*");
					insereNosFolhas(root, ((JButton)e.getSource()).getText());
					
					sorteiaBotoes(listaButtons);
				}
			});
			listaButtons.add(senhaButton);
			c.add(senhaButton);
		}		
		JButton loginButton = new JButton("Login");
		loginButton.setBounds(60, 400, 300, 50);
		c.add(loginButton);
		
		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				HashMap updatedUser = Authentification.autenticaEmail((String) user.get("email"));
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
					//new TanListView(Authentification.autenticaEmail((String)updatedUser.get("email")));
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
			for(String text : opcoes.get(i))
				textToBtn += text + "\n";
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
