package View;

import Database.DBControl;
import Component.*;
import Database.LoggedUser;
import Util.MensagemType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class SaidaView extends DefaultFrame {

	private final int width = 400;
	private final int height = 320;
	
	private HashMap user = null;

	private int grupoId;

	private JButton logoutButton;

	private JButton backButton;

	public SaidaView() {

		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		//------------------------ Register ------------------------------------

		DBControl.getInstance().insertRegister(MensagemType.TELA_DE_SAIDA_APRESENTADA, LoggedUser.getInstance().getEmail());

		//------------------------ Set View ------------------------------------

		this.setView();

		//--------------------- Logout Button Action ----------------------------

		logoutButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(MensagemType.BOTAO_SAIR_PRESSIONADO, LoggedUser.getInstance().getEmail());

				DBControl.getInstance().insertRegister(1002);
				dispose();
				System.exit(0);
			}
		});

		//--------------------- Back Button Action ----------------------------

		backButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(MensagemType.BOTAO_VOLTAR_DE_SAIR_PARA_MENU_PRINCIPAL_PRESSIONADO, LoggedUser.getInstance().getEmail());

				dispose();
				new MainView();
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

		setTitle("Sair");

		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//---------------------- E-mail Header Label -----------------------------

		JLabel emailHeaderLabel = new JLabel(String.format(" • Login: %s", (String)user.get("email")));
		emailHeaderLabel.setBounds(50, yPosition, this.width, 25);

		Font f = emailHeaderLabel.getFont();
		emailHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(emailHeaderLabel);

		yPosition = yPosition + emailHeaderLabel.getSize().height + 5;

		//---------------------- Group Header Label -----------------------------

		String groupName = "";

		if( grupoId == 1 ) {
			groupName = "Administrador";
		} else {
			groupName = "Usuário";
		}

		JLabel groupHeaderLabel = new JLabel(String.format(" • Grupo: %s", groupName));
		groupHeaderLabel.setBounds(50, yPosition, this.width, 25);

		groupHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(groupHeaderLabel);

		yPosition = yPosition + groupHeaderLabel.getSize().height + 5;

		//---------------------- Name Header Label -----------------------------

		JLabel nameHeaderLabel = new JLabel(String.format(" • Nome: %s", (String)user.get("name")));
		nameHeaderLabel.setBounds(50, yPosition, this.width, 25);

		nameHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(nameHeaderLabel);

		yPosition = yPosition + nameHeaderLabel.getSize().height + 10;

		//---------------------- Number of Access Label -----------------------------

		JLabel numberOfAccess = new JLabel(String.format("Total de acessos do usuário: %s", Integer.parseInt(user.get("countAccess").toString())));
		numberOfAccess.setBounds(50, yPosition, this.width, 25);

		numberOfAccess.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(numberOfAccess);

		yPosition = yPosition + numberOfAccess.getSize().height + 10;

		//------------------------- Logout Label ----------------------------------

		JLabel logoutLabel = new JLabel("Pressione o botão Sair para confirmar.");
		logoutLabel.setBounds(50, yPosition, this.width, 25);

		logoutLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 14));

		this.getContainer().add(logoutLabel);

		yPosition = yPosition + logoutLabel.getSize().height + 10;

		//------------------------- Logout Button ------------------------------------

		logoutButton = new JButton("Sair");
		logoutButton.setBounds(20, yPosition, 170, 40);

		this.getContainer().add(logoutButton);

		//------------------------- Back Button ------------------------------------

		backButton = new JButton("Voltar");
		backButton.setBounds(210, yPosition, 170, 40);

		this.getContainer().add(backButton);

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

}
