package View;

import Auth.Authentification;
import Database.DBControl;
import Database.LoggedUser;
import Component.*;
import Util.MensagemType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


public class PasswordFrame extends DefaultFrame {

	/* **************************************************************************************************
	 **
	 **  Variables declaration
	 **
	 ****************************************************************************************************/
	
	private final int width = 400;
	private final int height = 320;

	private JTextField passwordField;
	private JButton checkButton;
	private JButton clearButton;

	private List<String> list1 = new ArrayList<String>();
	private List<String> list2 = new ArrayList<String>();

	/* **************************************************************************************************
	 **
	 **  Senha View Init
	 **
	 ****************************************************************************************************/
	
	public PasswordFrame() {

		//--------------------- Insert Register --------------------------------

		DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_2_INICIADA, LoggedUser.getInstance().getEmail());

		//------------------------ Set View ------------------------------------

		this.setView();

		//------------------------ Clear Button Action --------------------------

		clearButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				list1.clear();
				list2.clear();

				passwordField.setText("");

			}
		});

		//------------------------ Check Button Action --------------------------
		
		checkButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				HashMap updatedUser = LoggedUser.getInstance().getUser();

				if (checkPasswordInArray()) {

					DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_VERIFICADA_POSITIVAMENTE, LoggedUser.getInstance().getEmail());
					DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_2_ENCERRADA, LoggedUser.getInstance().getEmail());
					DBControl.getInstance().clearWrongAccessPassword();
					dispose();

					new PrivateKeyFrame();

				} else {

					Authentification.incrementWrongAccessPassowrd();

					if( Authentification.shouldBlockUserForPassword() ) {

						DBControl.getInstance().insertRegister(MensagemType.ACESSO_USUARIO_BLOQUEADO_PELA_ETAPA_2, LoggedUser.getInstance().getEmail());
						JOptionPane.showMessageDialog(null, "Senha incorreta. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
						dispose();
						new LoginFrame();

						return;

					}

					int lenghtSenha = passwordField.getText().length();

					if (lenghtSenha < 6 || lenghtSenha > 8) {

						JOptionPane.showMessageDialog(null, "Senha deve conter de 6 a 8 números.");

					} else {

						JOptionPane.showMessageDialog(null, "Senha inválida");

					}

					list1.clear();
					list2.clear();

					passwordField.setText("");

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

		//---------------------- Generate Password Options ----------------------

		final List<JButton> listButtons = new ArrayList<JButton>();
		String numbers = "0123456789";

		for (int i=0; i<5; i++) {

			Random rand = new Random();

			int index = rand.nextInt(numbers.length());
			Character number1 = numbers.charAt(index);

			numbers = numbers.replaceAll(String.valueOf(numbers.charAt(index)), "");

			index = rand.nextInt(numbers.length());
			Character number2 = numbers.charAt(index);

			numbers = numbers.replaceAll(String.valueOf(numbers.charAt(index)), "");

			String textToBtn = String.valueOf(number1) + " ou " + String.valueOf(number2);

			JButton passwordButton = new JButton(textToBtn);

			passwordButton.setBounds(15 + (i * 75), yPosition, 75, 60);

			//---------------------- Password Button Action ----------------------

			passwordButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {

					passwordField.setText(passwordField.getText() + "*");

					createArrayOfOptions(((JButton)e.getSource()).getText().replace(" ou ", ""));

					setTextInButtons(listButtons);

				}
			});

			listButtons.add(passwordButton);

			this.getContainer().add(passwordButton);

		}

		yPosition = yPosition + 60 + 10;

		//---------------------------- Clear Button --------------------------------

		clearButton = new JButton("apagar");
		clearButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(clearButton);

		yPosition = yPosition + clearButton.getSize().height + 10;

		//---------------------------- Login Button --------------------------------

		checkButton = new JButton("login");
		checkButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(checkButton);

		yPosition = yPosition + checkButton.getSize().height + 10;

		//------------------------ Set Visible ------------------------------------

		setVisible(true);

	}

	/* **************************************************************************************************
	 **
	 **  Array Of Options
	 **
	 ****************************************************************************************************/

	private void createArrayOfOptions(String options) {

		if( list1.size() == 0 ) {

			if( list2.size() == 0 ){

				list1.add( String.valueOf(options.charAt(0)) );
				list1.add( String.valueOf(options.charAt(1)) );

			} else {

				for( String element : list2 ){

					list1.add( element + String.valueOf(options.charAt(0)) );
					list1.add( element + String.valueOf(options.charAt(1)) );

				}

				list2.clear();

			}

		} else {

			for( String element : list1 ){

				list2.add( element + String.valueOf(options.charAt(0)) );
				list2.add( element + String.valueOf(options.charAt(1)) );

			}

			list1.clear();

		}

	}

	/* **************************************************************************************************
	 **
	 **  Check Password In Array
	 **
	 ****************************************************************************************************/

	private boolean checkPasswordInArray() {

		if( list1.size() == 0 ) {

			for( String element : list2 ){

				if(Authentification.autenticaSenha(element, LoggedUser.getInstance().getUser())) {
					return true;
				}

			}

		} else {

			for( String element : list1 ){

				if(Authentification.autenticaSenha(element, LoggedUser.getInstance().getUser())) {
					return true;
				}

			}

		}

		return false;

	}

	/* **************************************************************************************************
	 **
	 **  St Text In Buttons
	 **
	 ****************************************************************************************************/

	private void setTextInButtons(List<JButton> list) {
		String numbers = "0123456789";

		Random rand = new Random();

		for (int i=0; i<5; i++) {

			JButton btn = list.get(i);

			int index = rand.nextInt(numbers.length());
			Character number1 = numbers.charAt(index);

			numbers = numbers.replaceAll(String.valueOf(numbers.charAt(index)), "");

			index = rand.nextInt(numbers.length());
			Character number2 = numbers.charAt(index);

			numbers = numbers.replaceAll(String.valueOf(numbers.charAt(index)), "");

			btn.setText( String.valueOf(number1) + " ou " + String.valueOf(number2) );

		}
	}

}
