package View;

import Auth.Authentification;
import Database.DBManager;
import Database.LoggedUser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import Component.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;


public class CadastroView  extends DefaultFrame {

	private final int width = 450;
	private final int height = 610;
	
	private HashMap user = null;

	private JButton voltarButton;

	private JLabel certificadoDigitalLabel;

	private JButton certificadoDigitalButton;

	private JLabel grupoLabel;

	private JComboBox comboBox;

	private JLabel senhaLabel;

	private JPasswordField senhaField;

	private JLabel senhaConfirmacaoLabel;

	private JButton cadastrarButton;

	private JPasswordField senhaConfirmacaoField;

	private int grupoId;

	public CadastroView() {

		this.user = LoggedUser.getInstance().getUser();

		DBManager.insereRegistro(6001, (String) user.get("email"));

		//------------------------ Set View ------------------------------------

		this.setView();


		//------------------------ Certificado Digital Button ------------------------------------

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


		//------------------------ Voltar Button ------------------------------------

		voltarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(6008, (String) user.get("email"));
				dispose();
				new MainView();
			}
		});


		//------------------------ Cadastrar Button ------------------------------------

		cadastrarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(6002, (String) user.get("email"));

				String senha = new String( senhaField.getPassword());
				String confirmacao = new String(senhaConfirmacaoField.getPassword());
				String grupo = (String) comboBox.getSelectedItem().toString().toLowerCase();

				//Confere senha:

				if (!senha.equals(confirmacao)) {

					DBManager.insereRegistro(6003, (String) user.get("email"));
					JOptionPane.showMessageDialog(null, "Senha e confirmação de senha não são iguais.");

					return;

				}

				Boolean senhaOk = Authentification.conferirSenha(senha, confirmacao, user);

				if( !senhaOk ){

					DBManager.insereRegistro(6003, (String) user.get("email"));
					JOptionPane.showMessageDialog(null, "Senha não está no padrão correto.");

					return;

				}

				//Confere dados e le certificado:

				byte[] certDigBytes = null;
				try {
					certDigBytes = FileUtils.readFileToByteArray(new File(certificadoDigitalLabel.getText()));
				} catch (Exception a) {
					a.printStackTrace();
					DBManager.insereRegistro(6004, (String) user.get("email"));
					return;
				}

				X509Certificate cert = Authentification.leCertificadoDigital(certDigBytes);
				if (cert == null) {
					DBManager.insereRegistro(6004, (String) user.get("email"));
					return;
				}
//				String infoString = cert.getVersion() +"\n"+ cert.getNotBefore() +"\n"+ cert.getType() +"\n"+ cert.getIssuerDN() +"\n"+ cert.getSubjectDN();

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
					DBManager.insereRegistro(6007, (String) user.get("email"));
					return;
				}
				else {
					DBManager.insereRegistro(6006, (String) user.get("email"));
				}

				if (Authentification.cadastraUsuario(grupo, senha, certificadoDigitalLabel.getText())) {
					JOptionPane.showMessageDialog(null, "Usuário cadastrado!");
					dispose();
					new CadastroView();
				}
				else {
					DBManager.insereRegistro(6003, (String) user.get("email"));
					JOptionPane.showMessageDialog(null, "Não foi possível cadastrar novo usuário.");
				}

			}
		});

	}

	private void setView() {

		setLayout(null);

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

		JLabel cadastro = new JLabel("Cadastro:");
		cadastro.setHorizontalAlignment(SwingConstants.CENTER);
		cadastro.setBounds(0, yPosition, this.width, 40);

		cadastro.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 35));

		this.getContainer().add(cadastro);

		certificadoDigitalLabel = new JLabel();

		certificadoDigitalLabel .setBounds(60, 170, 300, 30);

		this.getContainer().add(certificadoDigitalLabel);

		certificadoDigitalButton = new JButton("Escolha o arquivo do Certificado Digital");

		certificadoDigitalButton .setBounds(60, 210, 300, 30);

		this.getContainer().add(certificadoDigitalButton);



		grupoLabel = new JLabel("Grupo:");

		grupoLabel.setBounds(60, 240, 300, 40);

		this.getContainer().add(grupoLabel);

		String[] choices = {"Usuario", "Administrador"};

		comboBox = new JComboBox(choices);

		comboBox .setBounds(60, 280, 300, 40);

		comboBox.setVisible(true);

		this.getContainer().add(comboBox );

		senhaLabel = new JLabel("Senha:");

		senhaLabel.setBounds(60, 320, 300, 40);

		this.getContainer().add(senhaLabel);

		senhaField = new JPasswordField();

		senhaField.setBounds(60, 350, 300, 40);

		this.getContainer().add(senhaField);

		senhaConfirmacaoLabel = new JLabel("Confirme a senha:");

		senhaConfirmacaoLabel.setBounds(60, 400, 300, 40);

		this.getContainer().add(senhaConfirmacaoLabel);

		senhaConfirmacaoField = new JPasswordField();

		senhaConfirmacaoField.setBounds(60, 430, 300, 40);

		this.getContainer().add(senhaConfirmacaoField);

		cadastrarButton = new JButton("Cadastrar");

		cadastrarButton.setBounds(50, 490, 150, 40);

		this.getContainer().add(cadastrarButton);

		voltarButton = new JButton("Voltar");

		voltarButton.setBounds(220, 490, 150, 40);

		this.getContainer().add(voltarButton);

		super.setVisible(true);
	}

}
