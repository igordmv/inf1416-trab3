package View;

import Auth.Authentification;
import Database.DBManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;


public class CadastroView extends JFrame {

	private final int width = 450;
	private final int height = 610;
	
	private HashMap user = null;
	
	public CadastroView(final HashMap user) {
		this.user = user;
		DBManager.insereRegistro(6001, (String) user.get("email"));
		
		setLayout(null);
		setSize (this.width, this.height);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setTitle("Cadastro");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);
		
		Container c = getContentPane();
		
		c.add(new Header((String)user.get("email"), (String)user.get("groupName"), (String)user.get("name")));
		c.add(new FirstBody("Total de usuários do sistema", DBManager.retornaNumUsuarios()));
		
		
		final JLabel certificadoDigitalLabel = new JLabel();
		certificadoDigitalLabel .setBounds(30, 130, 300, 30);
		c.add(certificadoDigitalLabel);
		JButton certificadoDigitalButton = new JButton("Escolha o arquivo do Certificado Digital");
		certificadoDigitalButton .setBounds(30, 170, 300, 30);
		c.add(certificadoDigitalButton);
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
		

		JLabel grupoLabel = new JLabel("Grupo:");
		grupoLabel.setBounds(30, 250, 300, 40);
		c.add(grupoLabel);
		String[] choices = {"Usuario", "Administrador"};
		final JComboBox comboBox = new JComboBox(choices);
		comboBox .setBounds(30, 290, 300, 40);
		comboBox.setVisible(true);
		c.add(comboBox );
		
		JLabel senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(30, 330, 300, 40);
		c.add(senhaLabel);
		final JPasswordField senhaField = new JPasswordField();
		senhaField.setBounds(30, 370, 300, 40);
		c.add(senhaField);
		
		JLabel senhaConfirmacaoLabel = new JLabel("Confirme a senha:");
		senhaConfirmacaoLabel.setBounds(30, 410, 300, 40);
		c.add(senhaConfirmacaoLabel);
		final JPasswordField senhaConfirmacaoField = new JPasswordField();
		senhaConfirmacaoField.setBounds(30, 450, 300, 40);
		c.add(senhaConfirmacaoField);
		
		JButton cadastrarButton = new JButton("Cadastrar");
		cadastrarButton.setBounds(30, 490, 300, 40);
		c.add(cadastrarButton);
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
					new CadastroView(user);
				}
				else {
					DBManager.insereRegistro(6003, (String) user.get("email"));
					JOptionPane.showMessageDialog(null, "Não foi possível cadastrar novo usuário.");
				}

			}
		});
		
		JButton voltarButton = new JButton("Voltar");
		voltarButton.setBounds(150, 530, 150, 40);
		c.add(voltarButton);
		voltarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(6008, (String) user.get("email"));
				dispose();
				new MainView();
			}
		});
		
	}

}
