package View;

import Auth.Authentification;
import Database.DBControl;
import Database.LoggedUser;
import Util.MensagemType;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import Component.*;


public class AlterarView extends DefaultFrame {

	private final int width = 450;
	private final int height = 550;
	
	private HashMap user = null;

	private int grupoId;
	private JTextField certificadoDigitalTextField;
	private JPasswordField passwordField;
	private JLabel passwordConfirmLabel;
	private JLabel passwordLabel;
	private JButton certificateDigitalButton;
	private JPasswordField confirmationPasswordField;
	private JButton changeButton;

	public AlterarView () {
		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		DBControl.getInstance().insertRegister(MensagemType.TELA_DE_ALTERACAO_DE_SENHA_PESSOAL_E_CERTIFICADO_APRESENTADO, LoggedUser.getInstance().getEmail());

		//------------------------ Set View ------------------------------------

		this.setView();

		//------------------------ Certificado Button ------------------------------------

		certificateDigitalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser certificadoDigitalchooser = new JFileChooser();
				certificadoDigitalchooser.setCurrentDirectory(new java.io.File("."));
				certificadoDigitalchooser.setDialogTitle("Caminho do Certificado Digital");
				certificadoDigitalchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				if (certificadoDigitalchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					certificadoDigitalTextField.setText(certificadoDigitalchooser.getSelectedFile().getAbsolutePath());
				}
				else {
					System.out.println("No Selection ");
				}
			}
		});

		//------------------------ Alterar Button ------------------------------------

		changeButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				String senha = new String( passwordField.getPassword());
				if (!senha.equals("")) {
					String confirmacao = new String(confirmationPasswordField.getPassword());

					//Confere senha:

					if (!senha.equals(confirmacao)) {

						DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_INVALIDA_TELA_ALTERACAO_SENHA, LoggedUser.getInstance().getEmail());

						JOptionPane.showMessageDialog(null, "Senha e confirmação de senha não são iguais.");

						return;

					}

					Boolean senhaOk = Authentification.conferirSenha(senha, confirmacao, user);

					if( !senhaOk ){

						DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_INVALIDA_TELA_ALTERACAO_SENHA, LoggedUser.getInstance().getEmail());

						JOptionPane.showMessageDialog(null, "Senha não está no padrão correto.");

						return;

					}

					DBControl.getInstance().changePassword(Authentification.geraSenhaProcessada(senha, (String) user.get("salt")), (String) user.get("email"));

				}

				String pathCertificado = certificadoDigitalTextField.getText();

				if (pathCertificado.equals("") == false) {
					byte[] certDigBytes = null;
					try {
						certDigBytes = FileUtils.readFileToByteArray(new File(pathCertificado));
					} catch (Exception a) {
						a.printStackTrace();

						DBControl.getInstance().insertRegister(MensagemType.CAMINHO_CERTIFICADO_INVALIDO, LoggedUser.getInstance().getEmail());

						JOptionPane.showMessageDialog(null, "Caminho do certificado inválido");

						return;
					}

					X509Certificate cert = Authentification.leCertificadoDigital(certDigBytes);
					if (cert ==  null) {

						JOptionPane.showMessageDialog(null, "Certificado inválido");

						return;
					}

					String infoString = "";
					infoString = infoString + "\nOs dados estão corretos?\n";
					infoString = infoString + "Versão: " + cert.getVersion() + "\n";
					infoString = infoString + "Série: " + cert.getSerialNumber() + "\n";
					infoString = infoString + "Validade (Not Before): " + cert.getNotBefore() + "\n";
					infoString = infoString + "Validade (Not After): " + cert.getNotAfter() + "\n";
					infoString = infoString + "Tipo de assinatura: " + cert.getType() + "\n";
					infoString = infoString + "Emissor: " + cert.getIssuerDN() + "\n";
					infoString = infoString + "Sujeito: " + cert.getSubjectDN() + "\n";

					String subjectDN = cert.getSubjectDN().getName();
					int start = subjectDN.indexOf("=");
					int end = subjectDN.indexOf(",");
					String email = subjectDN.substring(start + 1, end);

					infoString = infoString + "E-mail: " + email + "\n";

					int ret = JOptionPane.showConfirmDialog(null, infoString);

					if (ret != JOptionPane.YES_OPTION) {

						System.out.println("Cancelou");

						DBControl.getInstance().insertRegister(MensagemType.CONFIRMACAO_DE_DADOS_REIEJITADA_TELA_ALTERACAO_SENHA, LoggedUser.getInstance().getEmail());

						return;

					}  else {

						DBControl.getInstance().insertRegister(MensagemType.CONFIRMACAO_DE_DADOS_ACEITA_TELA_ALTERACAO_SENHA, LoggedUser.getInstance().getEmail());

					}

					DBControl.getInstance().changePrivateKey(Authentification.certToString(cert), (String) user.get("email"));
				}

				dispose();
				new MainView();
			}
		});

	}

	private void setView() {

		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//------------------------ Set Title ------------------------------------
		setTitle("Cadastro");


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

		//------------------------ Title Label -----------------------------------

		JLabel alterar = new JLabel("Alterar:");
		alterar.setHorizontalAlignment(SwingConstants.CENTER);
		alterar.setBounds(0, yPosition + 10, this.width, 40);

		alterar.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 35));

		this.getContainer().add(alterar);

		setTitle("Cadastro");

		yPosition = yPosition + alterar.getSize().height + 10;

		certificadoDigitalTextField = new JTextField();
		certificadoDigitalTextField.setBounds(60, yPosition, 300, 30);
		this.getContainer().add(certificadoDigitalTextField);

		yPosition = yPosition + certificadoDigitalTextField.getSize().height + 10;

		certificateDigitalButton = new JButton("Escolha o arquivo do Certificado Digital");
		certificateDigitalButton.setBounds(60, yPosition, 300, 30);
		this.getContainer().add(certificateDigitalButton);

		yPosition = yPosition + certificateDigitalButton.getSize().height + 10;

		passwordLabel = new JLabel("Senha:");
		passwordLabel.setBounds(60, yPosition, 300, 40);
		this.getContainer().add(passwordLabel);

		yPosition = yPosition + passwordLabel.getSize().height + 10;

		passwordField = new JPasswordField();
		passwordField.setBounds(60, yPosition, 300, 40);
		this.getContainer().add(passwordField);

		yPosition = yPosition + passwordField.getSize().height + 10;

		passwordConfirmLabel = new JLabel("Confirme a senha:");
		passwordConfirmLabel.setBounds(60, yPosition, 300, 40);
		this.getContainer().add(passwordConfirmLabel);

		yPosition = yPosition + passwordConfirmLabel.getSize().height + 10;

		confirmationPasswordField = new JPasswordField();
		confirmationPasswordField.setBounds(60, yPosition, 300, 40);
		this.getContainer().add(confirmationPasswordField);

		yPosition = yPosition + confirmationPasswordField.getSize().height + 10;

		changeButton = new JButton("Alterar e voltar");
		changeButton.setBounds(60, yPosition, 300, 40);
		this.getContainer().add(changeButton);

		super.setVisible(true);
	}
}
