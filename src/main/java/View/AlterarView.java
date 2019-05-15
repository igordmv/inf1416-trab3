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
import java.util.HashMap;
import java.util.List;


public class AlterarView extends JFrame {

	private final int width = 450;
	private final int height = 630;
	
	private HashMap user = null;
	
	public AlterarView (final HashMap user) {
		this.user = user;
		DBManager.insereRegistro(7001, (String) user.get("email"));
		
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
		List<HashMap> tanList = DBManager.retornaTanList((String)user.get("email"));
		c.add(new FirstBody("Total de OTPS", tanList.size()));
		
		
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
		
		final JLabel tanListLabel = new JLabel();
		tanListLabel .setBounds(30, 210, 300, 30);
		c.add(tanListLabel);
		JButton tanListButton = new JButton("Escolha uma pasta para a TAN List");
		c.add(tanListButton);
		tanListButton .setBounds(30, 250, 300, 30);
		tanListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser tanListchooser = new JFileChooser(); 
				tanListchooser.setCurrentDirectory(new java.io.File("."));
				tanListchooser.setDialogTitle("Caminho da TAN List");
				tanListchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if (tanListchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					tanListLabel.setText(tanListchooser.getSelectedFile().getAbsolutePath());
				}
			    else {
			      System.out.println("No Selection ");
			    }
			}
		});
		
		JLabel senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(30, 290, 300, 40);
		c.add(senhaLabel);
		final JPasswordField senhaField = new JPasswordField();
		senhaField.setBounds(30, 330, 300, 40);
		c.add(senhaField);
		
		JLabel senhaConfirmacaoLabel = new JLabel("Confirme a senha:");
		senhaConfirmacaoLabel.setBounds(30, 370, 300, 40);
		c.add(senhaConfirmacaoLabel);
		final JPasswordField senhaConfirmacaoField = new JPasswordField();
		senhaConfirmacaoField.setBounds(30, 410, 300, 40);
		c.add(senhaConfirmacaoField);
		
		JButton alterarButton = new JButton("Alterar e voltar");
		alterarButton.setBounds(30, 450, 300, 40);
		c.add(alterarButton);
		alterarButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				DBManager.insereRegistro(7007, (String) user.get("email"));
				
				String errorMsg = "";
				String senha = new String( senhaField.getPassword());
				if (!senha.equals("")) {
					String confirmacao = new String(senhaConfirmacaoField.getPassword());
					if (senha.equals(confirmacao)) {
						if (Authentification.verificaRegrasSenha(senha) == false) {
							errorMsg += "Senha não está de acordo com a regra.\n";
							DBManager.insereRegistro(7002, (String) user.get("email"));
						} 
						else {
							senha = Authentification.geraSenhaProcessada(senha, (String) user.get("salt"));
							DBManager.alterarSenha(senha, (String) user.get("email")) ;
						}
					}
					else {
						errorMsg += "Senha e confirmação de senha não são iguais.\n";
						DBManager.insereRegistro(7002, (String) user.get("email"));
					}
				}
				
				String pathCertificado = certificadoDigitalLabel.getText();
				if (pathCertificado.equals("") == false) {
					byte[] certDigBytes = null;
					try {
						certDigBytes = FileUtils.readFileToByteArray(new File(pathCertificado));
					} catch (Exception a) {
						a.printStackTrace();
						DBManager.insereRegistro(7003, (String) user.get("email"));
						return;
					}
					
					X509Certificate cert = Authentification.leCertificadoDigital(certDigBytes);
					if (cert ==  null) {
						DBManager.insereRegistro(7003, (String) user.get("email"));
						return;
					}
					String infoString = cert.getVersion() +"\n"+ cert.getNotBefore() +"\n"+ cert.getType() +"\n"+ cert.getIssuerDN() +"\n"+ cert.getSubjectDN();
					int ret = JOptionPane.showConfirmDialog(null, infoString);
					
					if (ret != JOptionPane.YES_OPTION) {
						System.out.println("Cancelou");
						DBManager.insereRegistro(7006, (String) user.get("email"));
						return;
					}
					else {
						DBManager.insereRegistro(7005, (String) user.get("email"));
					}
					
					String certString = Authentification.certToString(cert);
					DBManager.alterarCertificadoDigital(certString, (String) user.get("email"));
				}

				if (errorMsg.equals("")== false) {
					JOptionPane.showMessageDialog(null, errorMsg);
				}
				dispose();
				new MainView(Authentification.autenticaEmail((String) user.get("email")));
			}
		});
		
	}	
}
