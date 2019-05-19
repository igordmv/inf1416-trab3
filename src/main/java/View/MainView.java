package View;

import Auth.Authentification;
import Database.DBControl;
import Component.*;
import Database.LoggedUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class MainView extends DefaultFrame {

	private final int width = 450;
	private final int height = 450;

	private HashMap user = null;

	private int grupoId;

	private JButton registerButton;

	private JButton alterButton;

	private JButton consultButton;

	private JButton logoutButton;

	public MainView() {

		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		//------------------------ Register ------------------------------------

		DBControl.getInstance().insertRegister(5001, (String) user.get("email"));

		//------------------------ Set View ------------------------------------

		this.setView();

		//-------------------- Register Button Action ---------------------------

		if (grupoId == 1) {

			registerButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {

					DBControl.getInstance().insertRegister(5002, (String) user.get("email"));
					dispose();
					new CadastroView();

				}
			});

		}

		//-------------------- Alter Button Action ---------------------------

		alterButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(5003, (String) user.get("email"));
				dispose();
				new AlterarView();

			}
		});

		//-------------------- Consult Button Action ---------------------------

		consultButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(5004, (String) user.get("email"));
				dispose();
				new ConsultarArquivosView();

			}
		});

		//-------------------- Logout Button Action ---------------------------

		logoutButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(5005, (String) user.get("email"));
				dispose();
				new SaidaView();

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

		setTitle("Menu Principal");

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
			groupName = "Administrado";
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

		//------------------------ Title Label -----------------------------------

		JLabel titleLabel = new JLabel("Menu Principal:");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(0, yPosition, this.width, 40);

		titleLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 25));

		this.getContainer().add(titleLabel);

		yPosition = yPosition + titleLabel.getSize().height + 10;

		//----------------------- Register Button -----------------------------------

		if (grupoId == 1) {

			registerButton = new JButton("Cadastrar um novo usuário");
			registerButton.setBounds(50, yPosition, 350, 40);

			this.getContainer().add(registerButton);

			yPosition = yPosition + registerButton.getSize().height + 10;


		}

		//------------------------- Alter Button ------------------------------------

		alterButton = new JButton("Alterar senha pessoal e certificado digital do usuário");
		alterButton.setBounds(50, yPosition, 350, 40);

		this.getContainer().add(alterButton);

		yPosition = yPosition + alterButton.getSize().height + 10;

		//------------------------- Consult Button ------------------------------------

		consultButton = new JButton("Consultar pasta de arquivos secretos do usuário");
		consultButton.setBounds(50, yPosition, 350, 40);

		this.getContainer().add(consultButton);

		yPosition = yPosition + consultButton.getSize().height + 10;

		//------------------------- Logout Button ------------------------------------

		logoutButton = new JButton("Sair do Sistema");
		logoutButton.setBounds(50, yPosition, 350, 40);

		this.getContainer().add(logoutButton);

		yPosition = yPosition + logoutButton.getSize().height + 10;

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

}
