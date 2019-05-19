package View;

import Auth.Authentification;
import Database.DBControl;
import Database.DBManager;
import Database.LoggedUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.HashMap;
import Component.*;


public class ConsultarArquivosView extends DefaultFrame {

	private final int width = 800;
	private final int height = 500;
	private final Integer grupoId;

	private HashMap user = null;
	String indexArq = null;
	private JLabel consultaLabel;
	private JButton consultarButton;
	private JLabel groupHeaderLabel;
	private JLabel emailHeaderLabel;
	private JLabel nameHeaderLabel;
	private JLabel numberOfAccess;
	private JButton decriptarButton;
	private JButton listarButton;
	private JButton voltarButton;
	private JTable table;
	private DefaultTableModel tableModel;

	public ConsultarArquivosView() {
		this.user = LoggedUser.getInstance().getUser();

		grupoId = (Integer) user.get("grupoId");

		DBManager.insereRegistro(8001, (String) user.get("email"));


		this.setView();


		//------------------------ On close Event ------------------------------------

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DBControl.getInstance().insertRegister(MensagemType.SISTEMA_ENCERRADO, LoggedUser.getInstance().getEmail(), null);
				System.exit(0);
			}
		});

		consultarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser consultarchooser = new JFileChooser();
				consultarchooser.setCurrentDirectory(new java.io.File("."));
				consultarchooser.setDialogTitle("Caminho dos arquivos");
				consultarchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (consultarchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					consultaLabel.setText(consultarchooser.getSelectedFile().getAbsolutePath());
				}
				else {
					System.out.println("No Selection ");
				}
			}
		});

		//------------------------ Decripitar Button  ------------------------------------

		decriptarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				String nomeArquivo = (String) table.getValueAt(index, 1);
				if (Authentification.acessarArquivo(user, indexArq, nomeArquivo, LoggedUser.getInstance().getPrivateKey(), consultaLabel.getText())) {
					System.out.println("Decriptou arquivo com sucesso!");
				}
				else {
					JOptionPane.showMessageDialog(null, "Usuário não possui permissão para ler o arquivo selecionado");
				}
			}
		});


		//------------------------ Listar Button  ------------------------------------

		listarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBControl.getInstance().insertRegister(8007, (String) user.get("email"));

				try {
					byte[] temp = Authentification.decriptaArquivo(user, consultaLabel.getText(), "index", LoggedUser.getInstance().getPrivateKey());
					indexArq = new String((temp), "UTF8");
				} catch (UnsupportedEncodingException ex) {
					JOptionPane.showMessageDialog(null, "Não foi possível listar os arquivos com este credencial.");
					return;
				}
				String[] listaArquivos = indexArq.split("\n");
				for (String arq: listaArquivos) {
					String[] items = arq.split(" ");
					tableModel.addRow(items);
				}
				tableModel = (DefaultTableModel) table.getModel();
				table.setModel(tableModel);
				tableModel.fireTableDataChanged();
				decriptarButton.setEnabled(true);
				DBManager.insereRegistro(8009, (String) user.get("email"));

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
			groupName = "Administrado";
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

		consultaLabel = new JLabel();
		consultarButton = new JButton("Escolha uma pasta para consultar");

		String[] columnNames = {"Nome código","Nome secreto", "Dono", "Grupo"};
		Object[][] data = {};
		tableModel = new DefaultTableModel(data, columnNames);
		table = new JTable(tableModel){
			public boolean isCellEditable(int nRow, int nCol) {
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(380, 10, 400, 400);
		//table.setFillsViewportHeight(true);
		this.getContainer().add(table.getTableHeader());
		this.getContainer().add(scrollPane);

		decriptarButton = new JButton("Decriptar");
		decriptarButton.setEnabled(false);


		voltarButton = new JButton("Voltar");
		voltarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBManager.insereRegistro(8006, (String) user.get("email"));
				dispose();
				new MainView();
			}
		});

		listarButton = new JButton("Listar arquivos");

		consultaLabel.setBounds(30, 300, 300, 30);
		consultarButton.setBounds(30, 330, 300, 40);
		listarButton.setBounds(30, 380, 300, 40);
		decriptarButton.setBounds(600, 420, 100, 40);
		voltarButton.setBounds(450, 420, 100, 40);

		this.getContainer().add(consultaLabel);
		this.getContainer().add(consultarButton);
		this.getContainer().add(listarButton);
		this.getContainer().add(decriptarButton);
		this.getContainer().add(voltarButton);

		super.setVisible(true);
	}
}
