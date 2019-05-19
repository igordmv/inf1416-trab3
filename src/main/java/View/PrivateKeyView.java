package View;

import Auth.Authentification;
import Database.DBControl;
import Component.*;
import Database.LoggedUser;
import Util.MensagemType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.PrivateKey;
import java.util.HashMap;

public class PrivateKeyView extends DefaultFrame {

    /* **************************************************************************************************
     **
     **  Variables declaration
     **
     ****************************************************************************************************/

    private final int width = 400;
    private final int height = 320;

    private PrivateKey chavePrivada = null;

    private JTextField privateKeyTextField;

    private JTextField pathTextField;

    private JButton pathButton;

    private JButton checkButton;

    private HashMap user = null;

    /* **************************************************************************************************
     **
     **  Senha View Init
     **
     ****************************************************************************************************/

    public PrivateKeyView() {

        this.user = LoggedUser.getInstance().getUser();

        //--------------------- Insert Register --------------------------------

        DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_3_INICIADA, LoggedUser.getInstance().getEmail());

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

        //---------------------- Path Button Action -------------------------------

        pathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chavePrivadachooser = new JFileChooser();
                chavePrivadachooser.setCurrentDirectory(new java.io.File("."));
                chavePrivadachooser.setDialogTitle("Caminho da chave privada");
                chavePrivadachooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (chavePrivadachooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    pathTextField.setText(chavePrivadachooser.getSelectedFile().getAbsolutePath());
                }
                else {
                    System.out.println("No Selection ");
                }

            }
        });

        //---------------------- Check Button Action -------------------------------

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                HashMap updatedUser = Authentification.autenticaEmail((String) user.get("email"));

                if( privateKeyTextField.getText() != "" && pathTextField.getText() != "" ){

                    chavePrivada = Authentification.leChavePrivada(privateKeyTextField.getText(), pathTextField.getText(), user);

                    if (chavePrivada == null) {
                        Authentification.incrementWrongAccessPrivateKey();

                        if( Authentification.shouldBlockUserForPrivateKey() ) {

                            DBControl.getInstance().insertRegister(MensagemType.ACESSO_USUARIO_BLOQUEADO_PELA_ETAPA_3, LoggedUser.getInstance().getEmail());

                            JOptionPane.showMessageDialog(null, "Chave secreta ou certificado inválido. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
                            dispose();
                            new LoginView();

                            return;

                        } else {

                            JOptionPane.showMessageDialog(null, "Chave secreta ou certificado inválido");

                        }

                    } else {

                        if (Authentification.testaChavePrivada(chavePrivada, user)) {

                            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_POSITIVAMENTE, LoggedUser.getInstance().getEmail());

                            LoggedUser.getInstance().setPrivateKey(chavePrivada);
                            LoggedUser.getInstance().setSecretWord(privateKeyTextField.getText());

                            DBControl.getInstance().clearWrongAccessPrivateKey();
                            DBControl.getInstance().increaseCountAccess();

                            dispose();

                            DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_3_ENCERRADA, LoggedUser.getInstance().getEmail());
                            new MainView();

                        } else {

                            DBControl.getInstance().insertRegister(8003, (String) user.get("email"));

                            Authentification.incrementWrongAccessPrivateKey();

                            if( Authentification.shouldBlockUserForPrivateKey() ) {

                                DBControl.getInstance().insertRegister(3007, (String) updatedUser.get("email"));
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

                    if(privateKeyTextField.getText().equals("")){
                        DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_FRASE_SECRETA, LoggedUser.getInstance().getEmail());
                    }
                    JOptionPane.showMessageDialog(null, "Escolha um certificado e digite a senha secreta");

                }

            }
        });

    }

    /* **************************************************************************************************
     **
     **  Set View
     **
     ****************************************************************************************************/

    private void setView() {

        //------------------------ Set Title ------------------------------------

        setTitle("Chave Privada");

        //------------------------ Set Size ------------------------------------

        setSize(this.width, this.height);

        this.setDimension();

        //------------------------ Y Position -----------------------------------

        int yPosition = 10;

        //------------------------ Title Label -----------------------------------

        JLabel titleLabel = new JLabel("Chave Privada:");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, yPosition, this.width, 40);

        Font f = titleLabel.getFont();
        titleLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD, 25));

        this.getContainer().add(titleLabel);

        yPosition = yPosition + titleLabel.getSize().height + 10;

        //-------------------- Private Key Text Field ---------------------------------

        privateKeyTextField = new JTextField();
        privateKeyTextField.setBounds(50, yPosition, 300, 40);

        this.getContainer().add(privateKeyTextField);

        yPosition = privateKeyTextField.getY() + privateKeyTextField.getSize().height + 10;

        //---------------------- Private Key Label -----------------------------------

        JLabel privateKeyLabel = new JLabel("Escolha o arquivo da chave privada");
        privateKeyLabel.setBounds(50, yPosition, 300, 20);

        this.getContainer().add(privateKeyLabel);

        yPosition = yPosition + privateKeyLabel.getSize().height + 5;

        //------------------------ Path Text Field -----------------------------------

        pathTextField = new JTextField();
        pathTextField.setBounds(50, yPosition, 300, 40);

        this.getContainer().add(pathTextField);

        yPosition = pathTextField.getY() + pathTextField.getSize().height + 10;

        //------------------------- Private Key Button --------------------------------

        pathButton = new JButton("Escolher");
        pathButton.setBounds(50, yPosition, 300, 40);

        this.getContainer().add(pathButton);

        yPosition = yPosition + pathButton.getSize().height + 10;

        //---------------------------- Check Button ------------------------------------

        checkButton = new JButton("Conferir");
        checkButton.setBounds(50, yPosition, 300, 40);

        this.getContainer().add(checkButton);

        yPosition = yPosition + checkButton.getSize().height + 10;

        //------------------------ Set Visible ------------------------------------

        setVisible(true);

    }

}
