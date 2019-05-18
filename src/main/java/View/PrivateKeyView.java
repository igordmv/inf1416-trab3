package View;

import Auth.Authentification;
import Database.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PrivateKey;
import java.util.HashMap;

public class PrivateKeyView extends JFrame {

    private final int width = 350;
    private final int height = 400;

    PrivateKey chavePrivada = null;

    String privateKeyPEM = null;

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

                HashMap updatedUser = Authentification.autenticaEmail((String) user.get("email"));

                if( chaveSecretaField.getText() != "" && chavePrivadaLabel.getText() != "" ){

                    chavePrivada = Authentification.leChavePrivada(chaveSecretaField.getText(), chavePrivadaLabel.getText(), user);

                    if (chavePrivada == null) {

                        DBManager.insereRegistro(8003, (String) user.get("email"));

                        Authentification.incrementWrongAccess();

                        if( Authentification.shouldBlockUser() ) {

                            DBManager.insereRegistro(3007, (String) updatedUser.get("email"));
                            JOptionPane.showMessageDialog(null, "Chave secreta ou certificado inválido. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
                            dispose();
                            new LoginView();

                            return;

                        } else {

                            JOptionPane.showMessageDialog(null, "Chave secreta ou certificado inválido");

                        }

                    } else {

                        if (Authentification.testaChavePrivada(chavePrivada, user)) {

                            DBManager.insereRegistro(8002, (String) user.get("email"));
                            DBManager.zeraAcessoErrado((String)updatedUser.get("email"));
                            dispose();
                            new MainView(Authentification.autenticaEmail((String)user.get("email")));

                        } else {

                            DBManager.insereRegistro(8003, (String) user.get("email"));

                            Authentification.incrementWrongAccess();

                            if( Authentification.shouldBlockUser() ) {

                                DBManager.insereRegistro(3007, (String) updatedUser.get("email"));
                                JOptionPane.showMessageDialog(null, "Certificado não válido. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
                                dispose();
                                new LoginView();

                                return;

                            } else {

                                JOptionPane.showMessageDialog(null, "Certificado não válido");

                            }

                        }

                    }

                } else {

                    JOptionPane.showMessageDialog(null, "Escolha um certificado e digite a senha secreta");

                }

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
