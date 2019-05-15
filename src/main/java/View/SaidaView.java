package View;

import Auth.Authentification;
import Database.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;


public class SaidaView extends JFrame {

	private final int width = 400;
	private final int height = 500;
	
	private HashMap user = null;
	
	public SaidaView(final HashMap user) {
		this.user = user;
		
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
		c.add(new Header((String)user.get("email"), (String)user.get("groupName"), (String)user.get("name")));

		c.add(new FirstBody("Total de acessos", Integer.parseInt(user.get("totalAcessos").toString())));
		
		JLabel sairLabel = new JLabel();
		JButton sairButton = new JButton();
		JButton voltarButton = new JButton();

		sairLabel.setText("Pressione o botão Sair para confirmar.");
		sairButton.setText("Sair");
		voltarButton.setText("Voltar");

		sairButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(1002);
				dispose();
				System.exit(0);
			}
		});

		voltarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				dispose();
				new MainView(Authentification.autenticaEmail((String) user.get("email")));
			}
		});
		
		sairLabel.setBounds(30, 200, 300, 40);
		c.add(sairLabel);
		
		sairButton.setBounds(30, 250, 135, 40);
		c.add(sairButton);

		voltarButton.setBounds(185, 250, 135, 40);
		c.add(voltarButton);

	}	
}
