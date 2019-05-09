package View;

import Auth.Authentification;
import Database.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class MainView extends JFrame {

	private final int width = 400;
	private final int height = 500;
	
	private HashMap user = null;
	
	public MainView(final HashMap user) {
		this.user = user;
		DBManager.insereRegistro(5001, (String) user.get("email"));
		
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
		
		setTitle("Menu Principal");
		
		Container c = getContentPane();
		c.add(new Header((String)user.get("email"), (String)user.get("groupName"), (String)user.get("name")));
		c.add(new FirstBody("Total de acessos", Integer.parseInt(user.get("totalAcessos").toString())));
		
		JLabel mainManu = new JLabel("Menu principal:");
		mainManu.setBounds(30, 150, 300, 40);
		c.add(mainManu);
		
		
		if (user.get("groupName").equals("administrador")) {
			JButton cadastroButton = new JButton("Cadastrar novo usuário");
			cadastroButton.setBounds(30, 200, 300, 40);
			c.add(cadastroButton);
			cadastroButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					DBManager.insereRegistro(5002, (String) user.get("email"));
					dispose();
					new CadastroView(user);
				}
			});
		}
		
		JButton alterarButton = new JButton("Alterar senha pessoal, certificado digital e TAN List");
		alterarButton.setBounds(30, 250, 300, 40);
		c.add(alterarButton);
		alterarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(5003, (String) user.get("email"));
				dispose();
				new AlterarView(Authentification.autenticaEmail((String)user.get("email")));
			}
		});
		
		JButton consultarButton = new JButton("Consultar pasta de arquivos secretos");
		consultarButton.setBounds(30, 300, 300, 40);
		c.add(consultarButton);
		consultarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(5004, (String) user.get("email"));
				dispose();
				new ConsultarArquivosView(Authentification.autenticaEmail((String)user.get("email")));
			}
		});
		
		JButton sairButton = new JButton("Sair do Sistema");
		sairButton.setBounds(30, 350, 300, 40);
		c.add(sairButton);
		sairButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(5005, (String) user.get("email"));
				dispose();
				new SaidaView(Authentification.autenticaEmail((String)user.get("email")));
			}
		});
	}	
}
