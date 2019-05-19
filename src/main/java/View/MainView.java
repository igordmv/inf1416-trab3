package View;

import Auth.Authentification;
import Database.DBManager;
import Component.*;
import Database.LoggedUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class MainView extends DefaultFrame {

	private final int width = 400;
	private final int height = 500;

	private HashMap user = null;

	public MainView() {

		this.user = LoggedUser.getInstance().getUser();;

		//------------------------ Register ------------------------------------

		DBManager.insereRegistro(5001, (String) user.get("email"));

		//------------------------ Set View ------------------------------------

		this.setView();

		int grupoId = (Integer) user.get("grupoId");

		//To-Do:
//		c.add(new Header((String)user.get("email"), (String)user.get("grupoId"), (String)user.get("name")));
//
//		c.add(new FirstBody("Total de acessos", Integer.parseInt(user.get("countAccess").toString())));
//
//		JLabel mainManu = new JLabel("Menu principal:");
//		mainManu.setBounds(30, 150, 300, 40);
//		c.add(mainManu);
//
//
//		if (grupoId == 1) {
//			JButton cadastroButton = new JButton("Cadastrar novo usuário");
//			cadastroButton.setBounds(30, 200, 350, 40);
//			c.add(cadastroButton);
//			cadastroButton.addActionListener(new ActionListener () {
//				public void actionPerformed (ActionEvent e) {
//					DBManager.insereRegistro(5002, (String) user.get("email"));
//					dispose();
//					new CadastroView(user);
//				}
//			});
//		}
//
//		JButton alterarButton = new JButton("Alterar senha pessoal e certificado digital do usuário");
//		alterarButton.setBounds(30, 250, 350, 40);
//		c.add(alterarButton);
//		alterarButton.addActionListener(new ActionListener () {
//			public void actionPerformed (ActionEvent e) {
//				DBManager.insereRegistro(5003, (String) user.get("email"));
//				dispose();
//				new AlterarView(Authentification.autenticaEmail((String)user.get("email")));
//			}
//		});
//
//		JButton consultarButton = new JButton("Consultar pasta de arquivos secretos");
//		consultarButton.setBounds(30, 300, 350, 40);
//		c.add(consultarButton);
//		consultarButton.addActionListener(new ActionListener () {
//			public void actionPerformed (ActionEvent e) {
//				DBManager.insereRegistro(5004, (String) user.get("email"));
//				dispose();
//				new ConsultarArquivosView(Authentification.autenticaEmail((String)user.get("email")));
//			}
//		});
//
//		JButton sairButton = new JButton("Sair do Sistema");
//		sairButton.setBounds(30, 350, 350, 40);
//		c.add(sairButton);
//		sairButton.addActionListener(new ActionListener () {
//			public void actionPerformed (ActionEvent e) {
//				DBManager.insereRegistro(5005, (String) user.get("email"));
//				dispose();
//				new SaidaView(Authentification.autenticaEmail((String)user.get("email")));
//			}
//		});
	}

	/* **************************************************************************************************
	 **
	 **  Set View
	 **
	 ****************************************************************************************************/

	private void setView() {

		//------------------------ Set Title ------------------------------------

		setTitle("Menu Principal");

		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//------------------------ Title Label -----------------------------------

		JLabel titleLabel = new JLabel("Menu Principal:");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(0, yPosition, this.width, 40);

		Font f = titleLabel.getFont();
		titleLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 25));

		this.getContainer().add(titleLabel);

		yPosition = yPosition + titleLabel.getSize().height + 10;

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

}
