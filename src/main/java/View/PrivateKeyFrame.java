package View;

import Util.AccessFileFunctions;
import Database.DBControl;
import Component.*;
import Database.LoggedUser;
import Util.MensagemType;
import org.apache.commons.io.FileUtils;

import javax.crypto.*;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

public class PrivateKeyFrame extends DefaultFrame {

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

    public PrivateKeyFrame() {

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

                HashMap updatedUser = AccessFileFunctions.checkEmail((String) user.get("email"));

                if( privateKeyTextField.getText() != "" && pathTextField.getText() != "" ){

                    chavePrivada = readPrivateKey(privateKeyTextField.getText(), pathTextField.getText());

                    if (chavePrivada == null) {
                        AccessFileFunctions.incrementWrongAccessPrivateKey();

                        if( AccessFileFunctions.shouldBlockUserForPrivateKey() ) {

                            DBControl.getInstance().insertRegister(MensagemType.ACESSO_USUARIO_BLOQUEADO_PELA_ETAPA_3, LoggedUser.getInstance().getEmail());

                            DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_3_ENCERRADA, LoggedUser.getInstance().getEmail());

                            JOptionPane.showMessageDialog(null, "Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
                            dispose();
                            new LoginFrame();

                            return;

                        }

                    } else {

                        if (AccessFileFunctions.verifyPrivateKey(chavePrivada)) {

                            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_POSITIVAMENTE, LoggedUser.getInstance().getEmail());

                            LoggedUser.getInstance().setPrivateKey(chavePrivada);
                            LoggedUser.getInstance().setSecretWord(privateKeyTextField.getText());

                            DBControl.getInstance().clearWrongAccessPrivateKey();
                            DBControl.getInstance().increaseCountAccess();

                            dispose();

                            DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_3_ENCERRADA, LoggedUser.getInstance().getEmail());
                            new MenuFrame();

                        } else {

                            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_ASSINATURA_DIGITAL, LoggedUser.getInstance().getEmail());

                            AccessFileFunctions.incrementWrongAccessPrivateKey();

                            if( AccessFileFunctions.shouldBlockUserForPrivateKey() ) {

                                DBControl.getInstance().insertRegister(MensagemType.ACESSO_USUARIO_BLOQUEADO_PELA_ETAPA_3, LoggedUser.getInstance().getEmail());

                                DBControl.getInstance().insertRegister(MensagemType.AUTENTICACAO_ETAPA_3_ENCERRADA, LoggedUser.getInstance().getEmail());

                                JOptionPane.showMessageDialog(null, "Assinatura digital não é valida. Número total de erros atingido. Aguarde até 2 minutos para tentar novamente.");
                                dispose();
                                new LoginFrame();

                                return;

                            } else {

                                JOptionPane.showMessageDialog(null, "Assinatura digital não é valida");

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

    /* **************************************************************************************************
     **
     **  Read Private Key
     **
     ****************************************************************************************************/

    public static PrivateKey readPrivateKey(String seed, String path) {

        HashMap user = LoggedUser.getInstance().getUser();

        SecureRandom rand = null;
        try {
            rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
        rand.setSeed(seed.getBytes());

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        keyGen.init(56, rand);
        Key chave = keyGen.generateKey();

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, chave);
        }
        catch (Exception e) {
            return null;
        }

        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(new File(path));
        }
        catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Caminho inválido");

            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());

            return null;
        }

        String chavePrivadaBase64 = null;
        try {
            chavePrivadaBase64 = new String(cipher.doFinal(bytes), "UTF8");
        } catch (UnsupportedEncodingException e) {

            JOptionPane.showMessageDialog(null, "Caminho inválido");

            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {

            JOptionPane.showMessageDialog(null, "Caminho inválida");

            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Frase secreta inválida");

            DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_FRASE_SECRETA, LoggedUser.getInstance().getEmail());
            return null;
        }
        chavePrivadaBase64 = chavePrivadaBase64.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
        byte[] chavePrivadaBytes = DatatypeConverter.parseBase64Binary(chavePrivadaBase64);

        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return factory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaBytes));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }

    }

}
