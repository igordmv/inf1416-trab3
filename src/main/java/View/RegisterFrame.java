package View;

import Util.AccessFileFunctions;
import Database.DBControl;
import Database.LoggedUser;
import Util.MensagemType;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import Component.*;

import java.security.cert.X509Certificate;
import java.util.HashMap;


public class RegisterFrame extends DefaultFrame {

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

	public RegisterFrame() {

		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		DBControl.getInstance().insertRegister(MensagemType.TELA_CADASTRO_APRESENTADA, LoggedUser.getInstance().getEmail());

		//------------------------ Set View ------------------------------------

		this.setView();

		//------------------------ On close Event ------------------------------------
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DBControl.getInstance().insertRegister(MensagemType.SISTEMA_ENCERRADO, LoggedUser.getInstance().getEmail(), null);
				System.exit(0);
			}
		});

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
				DBControl.getInstance().insertRegister(MensagemType.BOTAO_VOLTAR_DE_CADASTRO_PARA_MENU_PRINCIPAL_PRESSIONADO, LoggedUser.getInstance().getEmail());
				dispose();
				new MenuFrame();
			}
		});


		//------------------------ Cadastrar Button ------------------------------------

		cadastrarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBControl.getInstance().insertRegister(MensagemType.BOTAO_CADASTRAR_PRESSIONADO, LoggedUser.getInstance().getEmail());

				String senha = new String( senhaField.getPassword());
				String confirmacao = new String(senhaConfirmacaoField.getPassword());

				//Confere senha:

				if (!senha.equals(confirmacao)) {

					DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_INVALIDA_TELA_CADASTRO, LoggedUser.getInstance().getEmail());
					JOptionPane.showMessageDialog(null, "Senha e confirmação de senha não são iguais.");

					return;

				}

				Boolean senhaOk = AccessFileFunctions.validatePassword(senha, confirmacao, user);

				if( !senhaOk ){

					DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_INVALIDA_TELA_CADASTRO, LoggedUser.getInstance().getEmail());
					JOptionPane.showMessageDialog(null, "Senha não está no padrão correto.");

					return;

				}

				//Confere dados e le certificado:

				byte[] certDigBytes = null;
				try {
					certDigBytes = FileUtils.readFileToByteArray(new File(certificadoDigitalLabel.getText()));
				} catch (Exception a) {
					a.printStackTrace();
					DBControl.getInstance().insertRegister(MensagemType.CAMINHO_CERTIFICADO_INVELIDO, LoggedUser.getInstance().getEmail());
					return;
				}

				X509Certificate cert = AccessFileFunctions.readDigitalCertificate(certDigBytes);
				if (cert == null) {
					DBControl.getInstance().insertRegister(MensagemType.CAMINHO_CERTIFICADO_INVELIDO, LoggedUser.getInstance().getEmail());
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
					DBControl.getInstance().insertRegister(MensagemType.CONFIRMACAO_DE_DADOS_REJEITADA, LoggedUser.getInstance().getEmail());
					return;
				}

				if (AccessFileFunctions.registerUser(comboBox.getSelectedIndex() + 1, senha, certificadoDigitalLabel.getText())) {
					DBControl.getInstance().insertRegister(MensagemType.CONFIRMACAO_DE_DADOS_ACEITA_TELA_CADASTRO, LoggedUser.getInstance().getEmail());
					JOptionPane.showMessageDialog(null, "Usuário cadastrado!");
					dispose();
					new RegisterFrame();
				}
				else {
					DBControl.getInstance().insertRegister(MensagemType.SENHA_PESSOAL_INVALIDA_TELA_CADASTRO, LoggedUser.getInstance().getEmail());
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

		String[] choices = {"Administrador", "Usuario"};

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
