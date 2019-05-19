package View;

import Auth.Authentification;
import Database.DBControl;
import Database.DBManager;
import Database.LoggedUser;
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
	private final int height = 530;
	
	private HashMap user = null;

	private int grupoId;
	private JLabel certificadoDigitalLabel;
	private JPasswordField senhaField;
	private JLabel senhaConfirmacaoLabel;
	private JLabel senhaLabel;
	private JButton certificadoDigitalButton;
	private JPasswordField senhaConfirmacaoField;
	private JButton alterarButton;

	public AlterarView () {
		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

//		DBManager.insereRegistro(7001, (String) user.get("email"));

		//------------------------ Set View ------------------------------------

		this.setView();

		//------------------------ Certificado Button ------------------------------------

		certificadoDigitalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser certificadoDigitalchooser = new JFileChooser();
				certificadoDigitalchooser.setCurrentDirectory(new java.io.File("."));
				certificadoDigitalchooser.setDialogTitle("Caminho do Certificado Digital");
				certificadoDigitalchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				if (certificadoDigitalchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					certificadoDigitalLabel.setText(certificadoDigitalchooser.getSelectedFile().getAbsolutePath());
				}
				else {
					System.out.println("No Selection ");
				}
			}
		});

		//------------------------ Alterar Button ------------------------------------

		alterarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
//				DBManager.insereRegistro(7007, (String) user.get("email"));

				String errorMsg = "";
				String senha = new String( senhaField.getPassword());
				if (!senha.equals("")) {
					String confirmacao = new String(senhaConfirmacaoField.getPassword());

					//Confere senha:

					if (!senha.equals(confirmacao)) {

//						DBManager.insereRegistro(70002, (String) user.get("email"));
						JOptionPane.showMessageDialog(null, "Senha e confirmação de senha não são iguais.");

						return;

					}

					Boolean senhaOk = Authentification.conferirSenha(senha, confirmacao, user);

					if( !senhaOk ){

//						DBManager.insereRegistro(7002, (String) user.get("email"));
						JOptionPane.showMessageDialog(null, "Senha não está no padrão correto.");

						return;

					}

					DBControl.getInstance().changePassword(Authentification.geraSenhaProcessada(senha, (String) user.get("salt")), (String) user.get("email"));

				}

				String pathCertificado = certificadoDigitalLabel.getText();
				if (pathCertificado.equals("") == false) {
					byte[] certDigBytes = null;
					try {
						certDigBytes = FileUtils.readFileToByteArray(new File(pathCertificado));
					} catch (Exception a) {
						a.printStackTrace();
//						DBManager.insereRegistro(7003, (String) user.get("email"));
						return;
					}

					X509Certificate cert = Authentification.leCertificadoDigital(certDigBytes);
					if (cert ==  null) {
//						DBManager.insereRegistro(7003, (String) user.get("email"));
						return;
					}
					String infoString = cert.getVersion() +"\n"+ cert.getNotBefore() +"\n"+ cert.getType() +"\n"+ cert.getIssuerDN() +"\n"+ cert.getSubjectDN();
					int ret = JOptionPane.showConfirmDialog(null, infoString);

					if (ret != JOptionPane.YES_OPTION) {
						System.out.println("Cancelou");
//						DBManager.insereRegistro(7006, (String) user.get("email"));
						return;
					}
					else {
//						DBManager.insereRegistro(7005, (String) user.get("email"));
					}

					DBControl.getInstance().changePrivateKey(Authentification.certToString(cert), (String) user.get("email"));
				}

				if (errorMsg.equals("")== false) {
					JOptionPane.showMessageDialog(null, errorMsg);
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


		certificadoDigitalLabel = new JLabel();
		certificadoDigitalLabel .setBounds(60, 120, 300, 30);
		this.getContainer().add(certificadoDigitalLabel);

		certificadoDigitalButton = new JButton("Escolha o arquivo do Certificado Digital");
		certificadoDigitalButton .setBounds(60, 200, 300, 30);
		this.getContainer().add(certificadoDigitalButton);


		senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(60, 240, 300, 40);
		this.getContainer().add(senhaLabel);
		senhaField = new JPasswordField();
		senhaField.setBounds(60, 280, 300, 40);
		this.getContainer().add(senhaField);

		senhaConfirmacaoLabel = new JLabel("Confirme a senha:");
		senhaConfirmacaoLabel.setBounds(60, 320, 300, 40);
		this.getContainer().add(senhaConfirmacaoLabel);
		senhaConfirmacaoField = new JPasswordField();
		senhaConfirmacaoField.setBounds(60, 360, 300, 40);
		this.getContainer().add(senhaConfirmacaoField);

		alterarButton = new JButton("Alterar e voltar");
		alterarButton.setBounds(60, 410, 300, 40);
		this.getContainer().add(alterarButton);

		super.setVisible(true);
	}
}
