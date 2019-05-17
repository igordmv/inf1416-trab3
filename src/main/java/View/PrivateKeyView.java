package View;

import Auth.Authentification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class PrivateKeyView extends JFrame {

    private final int width = 350;
    private final int height = 400;

    public PrivateKeyView(final HashMap user) {

        setLayout(null);
        setSize (this.width, this.height);
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setTitle("Private Key View");

        Container c = getContentPane();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        JLabel chaveSecretaLabel = new JLabel(String.format("Chave secreta:"));
        final JTextField chaveSecretaField = new JTextField();

        final JLabel chavePrivadaLabel = new JLabel();
        JButton chavePrivadaButton = new JButton("Escolha arquivo chave privada");
        chavePrivadaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chavePrivadachooser = new JFileChooser();
                chavePrivadachooser.setCurrentDirectory(new java.io.File("."));
                chavePrivadachooser.setDialogTitle("Caminho da chave privada");
                chavePrivadachooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (chavePrivadachooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    chavePrivadaLabel.setText(chavePrivadachooser.getSelectedFile().getAbsolutePath());
                }
                else {
                    System.out.println("No Selection ");
                }
            }
        });

        JButton checkButton = new JButton("Conferir");
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {



                dispose();
                HashMap updatedUser = Authentification.autenticaEmail((String) user.get("email"));
                new MainView(Authentification.autenticaEmail((String)updatedUser.get("email")));

            }
        });

        chaveSecretaLabel.setBounds(30, 40, 300, 40);
        chaveSecretaField.setBounds(30, 80, 300, 40);
        chavePrivadaLabel.setBounds(30, 120, 300, 40);
        chavePrivadaButton.setBounds(30, 170, 300, 40);
        checkButton.setBounds(30, 220, 300, 40);

        c.add(chaveSecretaLabel);
        c.add(chaveSecretaField);
        c.add(chavePrivadaLabel);
        c.add(chavePrivadaButton);
        c.add(checkButton);

    }

}
