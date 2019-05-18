package View;

import Auth.Authentification;
import Database.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class LoginView extends JFrame {

	private final int width = 400;
	private final int height = 300;
	private JLabel loginLabel = null;
	private JButton loginButton = null;
	private Container container = null;
	private Dimension dimension = null;
	private JTextField loginField = null;
	private HashMap user = null;

	public LoginView() {
		DBManager.insereRegistro(2001);
		setupLoginScreenComponents();
		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				user = Authentification.autenticaEmail(loginField.getText());
				if (user == null) {
					DBManager.insereRegistro(2005, loginField.getText());
					JOptionPane.showMessageDialog(null, "Usuário não identificado.");
				}
				else {
					Integer acessosNegados = ((Integer) user.get("numAcessoErrados"));
					Integer tanNegados = ((Integer) user.get("numTanErrada"));
					if (acessosNegados >= 3 || tanNegados >= 3) {
						String ultimaTentativa = (String) user.get("ultimaTentativa");
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date horario = null;
						try {
							horario = formatter.parse(ultimaTentativa);
						} catch (ParseException e1) {
							e1.printStackTrace();
							System.exit(1);
						}
						Date twoMinutesLater = getTwoMinutesLaterDate();

						if (horario.before(twoMinutesLater)) {
							validaLogin();
						}
						else {
							DBManager.insereRegistro(2004, (String) user.get("email"));
							JOptionPane.showMessageDialog(null, "Usuário com acesso bloquado.");
						}
					}
					else {
						validaLogin();
					}
				}
			}
		});

		loginLabel.setBounds(30, 50, 300, 40);
		loginField.setBounds(30, 90, 300, 40);
		loginButton.setBounds(30, 150, 300, 40);


		container.add(loginLabel);
		container.add(loginField);
		container.add(loginButton);
	}

	private void setupLoginScreenComponents() {
		setLayout(null);
		setSize (this.width, this.height);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Login");

		dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);


		container = getContentPane();
		loginLabel = new JLabel("Login:");
		loginField = new JTextField();
		loginField.setText("admin@inf1416.puc-rio.br");
		loginButton = new JButton("Login");

		setVisible(true);
	}

	private Date getTwoMinutesLaterDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -2);
		cal.add(Calendar.HOUR, 3); //fuso horario
		return cal.getTime();
	}

	private void validaLogin() {
		DBManager.zeraAcessoErrado((String) user.get("email"));
		user = Authentification.autenticaEmail((String) user.get("email"));
		DBManager.insereRegistro(2003, (String) user.get("email"));
		DBManager.insereRegistro(2002);
		dispose();
		new SenhaView(user);
	}
}