package View;

import javax.swing.*;

public class FirstBody extends JPanel {

	public FirstBody(String subHeader, int totalAccess) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBounds(30, 100, 300, 30);
		
		JLabel totalAccessLabel = new JLabel(String.format("%s: %d",subHeader, totalAccess));
		add(totalAccessLabel);
	
	}
}
