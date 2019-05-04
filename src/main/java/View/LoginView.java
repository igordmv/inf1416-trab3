package View;

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

	public static final int BUTTON_POSITION_X = 160;
	public static final int BUTTON_POSITION_Y = 200;
	public static final int BUTTON_WIDTH = 300;
	public static final int BUTTON_HEIGHT = 40;
	private final int width = 600;
	private final int height = 700;
	
	private HashMap user = null;
	
	public LoginView() {
		//DBManager.insereRegistro(2001);
		
		setLayout(null);
		setSize (this.width, this.height);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setTitle("Login");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int positionX = (int) ((dimension.getWidth() - getWidth()) / 2);
		int positionY = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(positionX, positionY);
		
		
		Container container = getContentPane();
		JLabel loginLabel = new JLabel("Login:");
		JTextField loginField = new JTextField();
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				//HashMap user = Auth.autenticaEmail(loginField.getText());
				if (user == null) {
				//	DBManager.insereRegistro(2005, loginField.getText());
					JOptionPane.showMessageDialog(null, "Usuário não identificado.");
				}
				else {
					Integer acessosNegados = ((Integer) user.get("numAcessoErrados"));
					Integer tanNegados = ((Integer) user.get("numTanErrada"));
					System.out.println(acessosNegados);
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
						
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.MINUTE, -2);
						calendar.add(Calendar.HOUR, 2);// fuso horario
						System.out.println(horario);
						System.out.println(calendar.getTime());
						if (horario.before(calendar.getTime())) {
						//	DBManager.zeraAcessoErrado((String) user.get("email"));
						//	user = Auth.autenticaEmail((String) user.get("email"));
						}
						else {
						//	DBManager.insereRegistro(2004, (String) user.get("email"));
							JOptionPane.showMessageDialog(null, "Usuário com acesso bloquado.");
						}
					}
					else {
					//	DBManager.insereRegistro(2003, (String) user.get("email"));
					//	DBManager.insereRegistro(2002);
						dispose();
					//	new SenhaView(user);
					}
				}
			}
		});
		
		loginLabel.setBounds(BUTTON_POSITION_X, BUTTON_POSITION_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
		loginField.setBounds(BUTTON_POSITION_X, BUTTON_POSITION_Y + 40, BUTTON_WIDTH, BUTTON_HEIGHT);
		loginButton.setBounds(BUTTON_POSITION_X, BUTTON_POSITION_Y + 100, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		
		container.add(loginLabel);
		container.add(loginField);
		container.add(loginButton);
	}	
}
