package View;

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
		
		List<HashMap> tanList = DBManager.retornaTanList((String)user.get("email"));
		c.add(new FirstBody("Total de OTPS", tanList.size()));
		
		JLabel sairLabel = new JLabel();
		JButton sairOuVoltarButton = new JButton();
		
		if (tanList.size() > 0) {
			sairLabel.setText("Pressione o botão Sair para confirmar.");
			sairOuVoltarButton.setText("Sair");
			
			sairOuVoltarButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					DBManager.insereRegistro(1002);
					dispose();
					System.exit(0);
				}
			});
		}
		else {
			sairLabel.setText("Retorne e gere uma nova TAN List antes de sair.");
			sairOuVoltarButton.setText("Voltar para o Menu Principal");
			
			sairOuVoltarButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					dispose();
					new MainView(user);
				}
			});
		}
		
		sairLabel.setBounds(30, 200, 300, 40);
		c.add(sairLabel);
		
		sairOuVoltarButton.setBounds(30, 250, 300, 40);
		c.add(sairOuVoltarButton);
		
		
		
		
	}	
}
