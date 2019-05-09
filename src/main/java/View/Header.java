package View;

import javax.swing.*;

public class Header extends JPanel {

	public Header(String email, String group ,String name) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBounds(30, 20, 300, 60);
		
		JLabel emailLabel = new JLabel(String.format("Email: %s", email));
		JLabel groupLabel = new JLabel(String.format("Grupo: %s", group));
		JLabel nameLabel = new JLabel(String.format("Nome: %s", name));
				
		add(emailLabel);
		add(groupLabel);
		add(nameLabel);
	}
}
