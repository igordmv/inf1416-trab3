package View;

import Auth.Authentification;
import Database.DBControl;
import Component.DefaultFrame;
import Database.LoggedUser;
import Util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginView extends DefaultFrame {

	/* **************************************************************************************************
	 **
	 **  Variables declaration
	 **
	 ****************************************************************************************************/

	private final int width = 400;
	private final int height = 320;

	private Date time = null;

	private JButton loginButton;
	private JTextField loginTextField;

	/* **************************************************************************************************
	 **
	 **  Login View Init
	 **
	 ****************************************************************************************************/

	public LoginView() {

		//--------------------- Insert Register --------------------------------

		DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_1_INICIADA);

		//------------------------ Set View ------------------------------------

		this.setView();


		//------------------------ On close Event ------------------------------------

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DBControl.getInstance().insertRegister(MensagemType.SISTEMA_ENCERRADO, null, null);
				System.exit(0);
			}
		});


		//---------------------- Login Button Action ---------------------------

		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				String email = loginTextField.getText();

				if( email == "" ) {

					JOptionPane.showMessageDialog(null, "Para fazer login, digite um e-mail");

					return;

				}

				//---------------------- Check if Is a Valid E-mail ---------------------------

				if( !Util.isValidEmailAddress(email) ) {

					JOptionPane.showMessageDialog(null, "Para fazer login, digite um e-mail válido");

					return;

				}

				LoggedUser.getInstance().setEmail(email);

				if (LoggedUser.getInstance().getUser() == null) {

					DBControl.getInstance().insertRegister(MensagemType.LOGIN_NAME_NAO_IDENTIFICADO, LoggedUser.getInstance().getEmail());

					JOptionPane.showMessageDialog(null, "Usuário não cadastrado.");

				} else {

					if (Authentification.shouldBlockUserForPassword() || Authentification.shouldBlockUserForPrivateKey()) {

						String lastTry = (String) LoggedUser.getInstance().getUser().get("lastTryWrongAcess");

						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						try {
							time = formatter.parse(lastTry);
						} catch (ParseException e1) {
							e1.printStackTrace();
							System.exit(1);
						}

						Date twoMinutesLater = Util.dateAfterXMinutes(2);

						if (time.before(twoMinutesLater)) {

							validateLogin();

							if( Authentification.shouldBlockUserForPassword() ) {

								DBControl.getInstance().clearWrongAccessPassword();

							} else {

								DBControl.getInstance().clearWrongAccessPrivateKey();

							}

						}  else {

							DBControl.getInstance().insertRegister(MensagemType.LOGIN_NAME_IDENTIFICADO_COM_ACESSO_BLOQUEADO, LoggedUser.getInstance().getEmail());
							JOptionPane.showMessageDialog(null, "Usuário com acesso bloquado. Tente novamente em 2 minutos");

						}

					} else {

						validateLogin();

					}

				}
			}
		});

//		//Run SQL File:
//
//		DBControl.getInstance().runSqlFile();

//		//Delete User:
//
//		DBControl.getInstance().deleteUserFromEmail("user01@inf1416.puc-rio.br");

	}

	/* **************************************************************************************************
	 **
	 **  Set View
	 **
	 ****************************************************************************************************/

	private void setView() {

		//------------------------ Set Title ------------------------------------

		setTitle("Login");

		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//------------------------ Title Label -----------------------------------

		JLabel titleLabel = new JLabel("Login:");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(0, yPosition, this.width, 40);

		Font f = titleLabel.getFont();
		titleLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 25));

		this.getContainer().add(titleLabel);

		yPosition = yPosition + titleLabel.getSize().height + 10;

		//------------------------ Login Text Field -----------------------------------

		loginTextField = new JTextField();
		loginTextField.setBounds(50, yPosition, 300, 40);

		//To-do: remover isso;
		loginTextField.setText("user01@inf1416.puc-rio.br");

		this.getContainer().add(loginTextField);

		yPosition = yPosition + loginTextField.getSize().height + 10;

		//---------------------------- Login Button --------------------------------

		loginButton = new JButton("login");
		loginButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(loginButton);

		yPosition = yPosition + loginButton.getSize().height + 10;

		//------------------------ Group Label -----------------------------------

		JLabel groupLabel = new JLabel("Grupo:");
		groupLabel.setBounds(50, yPosition, 300, 30);

		groupLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 20));

		this.getContainer().add(groupLabel);

		yPosition = yPosition + groupLabel.getSize().height + 5;

		//------------------------ Igor Label -----------------------------------

		JLabel igorLabel = new JLabel(" • Igor Duarte - 1410492");
		igorLabel.setBounds(50, yPosition, 300, 20);

		igorLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(igorLabel);

		yPosition = yPosition + igorLabel.getSize().height + 5;

		//------------------------ Matheus Label -----------------------------------

		JLabel matheusLabel = new JLabel(" • Matheus Falcão - 1410962");
		matheusLabel.setBounds(50, yPosition, 300, 20);

		matheusLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(matheusLabel);

		yPosition = yPosition + matheusLabel.getSize().height + 5;

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

	/* **************************************************************************************************
	 **
	 **  Validate Login
	 **
	 ****************************************************************************************************/

	private void validateLogin() {

		DBControl.getInstance().insertRegister(MensagemType.LOGIN_NAME_IDENTIFICADO_COM_ACESSO_LIBERADO, LoggedUser.getInstance().getEmail());

		DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_1_ENCERRADA, LoggedUser.getInstance().getEmail());

		dispose();

      	new MainView();

	}
}