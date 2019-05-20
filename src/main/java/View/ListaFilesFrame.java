package View;

import Auth.Authentification;
import Database.DBControl;
import Database.LoggedUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import Component.*;
import Util.MensagemType;


public class ListaFilesFrame extends DefaultFrame {

	private final int width = 400;
	private final int height = 600;

	private final Integer grupoId;

	private HashMap user = null;

	String indexArq = null;

	private JTextField pathTextField;
	private JButton choiceFolderButton;
	private JButton listButton;
	private DefaultTableModel tableModel;
	private JTable table;

	private JLabel groupHeaderLabel;
	private JLabel emailHeaderLabel;
	private JLabel nameHeaderLabel;
	private JLabel numberOfAccess;

	private JButton decriptButton;

	private JButton backButton;

	public ListaFilesFrame() {
		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		this.setView();

		DBControl.getInstance().insertRegister(MensagemType.TELA_DE_CONSULTA_DE_ARQUIVOS_SECRETOS_APRESENTADA, LoggedUser.getInstance().getEmail());

		//------------------------ On close Event ------------------------------------

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DBControl.getInstance().insertRegister(MensagemType.SISTEMA_ENCERRADO, LoggedUser.getInstance().getEmail(), null);
				System.exit(0);
			}
		});

		//------------------------ Choice Folder Button ------------------------------------

		choiceFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new java.io.File("."));
				fileChooser.setDialogTitle("Caminho dos arquivos");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					pathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());

				} else {

					DBControl.getInstance().insertRegister(MensagemType.CAMINHO_DE_PASTA_INVALIDO, LoggedUser.getInstance().getEmail());

					System.out.println("No Selection ");

				}
			}
		});

		//------------------------ Decript Button  ------------------------------------

		decriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String fileName = (String) table.getValueAt(table.getSelectedRow(), 1);

				DBControl.getInstance().insertRegister(MensagemType.ARQUIVO_SELECIONADO, LoggedUser.getInstance().getEmail(), fileName);

				if (Authentification.acessarArquivo(user, indexArq, fileName, LoggedUser.getInstance().getPrivateKey(), pathTextField.getText())) {

					JOptionPane.showMessageDialog(null, "Arquivo decriptado!");

				} else {

					JOptionPane.showMessageDialog(null, "Usuário não possui permissão para ler o arquivo selecionado");

				}

			}
		});


		//------------------------ Listar Button  ------------------------------------

		listButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				table.removeAll();

				DBControl.getInstance().insertRegister(MensagemType.BOTAO_LISTAR_DE_CONSULTA_PRESSIONADO, LoggedUser.getInstance().getEmail());

				try {
					byte[] temp = Authentification.decriptaArquivo(user, pathTextField.getText(), "index", LoggedUser.getInstance().getPrivateKey());
					indexArq = new String((temp), "UTF8");
				} catch (UnsupportedEncodingException ex) {
					JOptionPane.showMessageDialog(null, "Erro ao listar os arquivos");
					return;
				}
				String[] listaArquivos = indexArq.split("\n");
				for (String arq: listaArquivos) {
					String[] items = arq.split(" ");
					tableModel.addRow(items);
				}

				DBControl.getInstance().insertRegister(MensagemType.LISTA_DE_ARQUIVOS_PRESENTE_NO_INDICE_APRESENTADA, LoggedUser.getInstance().getEmail());

				tableModel = (DefaultTableModel) table.getModel();
				table.setModel(tableModel);
				tableModel.fireTableDataChanged();

			}
		});

		//--------------------- Back Button Action ----------------------------

		backButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {

				DBControl.getInstance().insertRegister(MensagemType.BOTAO_VOLTAR_DE_CONSULTA_PARA_MENU_PRINCIPAL_PRESSIONADO, LoggedUser.getInstance().getEmail());

				dispose();
				new MenuFrame();
			}
		});

	}

	private void setView() {
		//------------------------ Set Size ------------------------------------

		setSize(this.width, this.height);

		this.setDimension();

		//------------------------ Y Position -----------------------------------

		int yPosition = 10;

		//------------------------ Set Title ------------------------------------

		setTitle("Login");

		//---------------------- E-mail Header Label -----------------------------

		emailHeaderLabel = new JLabel(String.format(" • Login: %s", (String)user.get("email")));
		emailHeaderLabel.setBounds(50, yPosition, this.width, 25);

		Font f = emailHeaderLabel.getFont();
		emailHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(emailHeaderLabel);

		yPosition = yPosition + emailHeaderLabel.getSize().height + 5;

		//---------------------- Group Header Label -----------------------------

		String groupName = "";

		if( grupoId == 1 ) {
			groupName = "Administrador";
		} else {
			groupName = "Usuário";
		}

		groupHeaderLabel = new JLabel(String.format(" • Grupo: %s", groupName));
		groupHeaderLabel.setBounds(50, yPosition, this.width, 25);

		groupHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(groupHeaderLabel);

		yPosition = yPosition + groupHeaderLabel.getSize().height + 5;

		//---------------------- Name Header Label -----------------------------

		nameHeaderLabel = new JLabel(String.format(" • Nome: %s", (String)user.get("name")));

		nameHeaderLabel.setBounds(50, yPosition, this.width, 25);

		nameHeaderLabel.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(nameHeaderLabel);

		yPosition = yPosition + nameHeaderLabel.getSize().height + 10;

		//---------------------- Number of Access Label -----------------------------

		numberOfAccess = new JLabel(String.format("Total de acessos do usuário: %s", Integer.parseInt(user.get("countAccess").toString())));
		numberOfAccess.setBounds(50, yPosition, this.width, 25);

		numberOfAccess.setFont(f.deriveFont(f.getStyle() | Font.PLAIN, 12));

		this.getContainer().add(numberOfAccess);

		yPosition = yPosition + numberOfAccess.getSize().height + 10;

		//---------------------- Choice Folder Button -----------------------------

		choiceFolderButton = new JButton("Escolha uma pasta para consultar");
		choiceFolderButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(choiceFolderButton);

		yPosition = yPosition + choiceFolderButton.getSize().height + 10;

		//------------------------ Path Text Field -----------------------------------

		pathTextField = new JTextField();
		pathTextField.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(pathTextField);

		yPosition = pathTextField.getY() + pathTextField.getSize().height + 10;

		//------------------------- List Button --------------------------------

		listButton = new JButton("Listar arquivos");
		listButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(listButton);

		yPosition = yPosition + listButton.getSize().height + 10;

		//------------------------- Table --------------------------------

		String[] columnNames = {"Nome código","Nome secreto", "Dono", "Grupo"};
		Object[][] data = {};

		tableModel = new DefaultTableModel(data, columnNames);

		table = new JTable(tableModel){
			public boolean isCellEditable(int nRow, int nCol) {
				return false;
			}
		};

		JScrollPane scrollPane = new JScrollPane(table);

		scrollPane.setBounds(25, yPosition, 350, 150);

		this.getContainer().add(scrollPane);

		yPosition = yPosition + 150 + 10;

		//------------------------- Decript Button --------------------------------

		decriptButton = new JButton("Decriptar");
		decriptButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(decriptButton);

		yPosition = yPosition + decriptButton.getSize().height + 10;

		//------------------------- Back Button --------------------------------

		backButton = new JButton("Voltar");
		backButton.setBounds(50, yPosition, 300, 40);

		this.getContainer().add(backButton);

		yPosition = yPosition + backButton.getSize().height + 10;

		//------------------------- Set Visible --------------------------------

		super.setVisible(true);

	}
}
