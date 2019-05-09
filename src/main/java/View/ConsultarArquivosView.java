package View;

import Auth.Authentification;
import Database.DBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.HashMap;


public class ConsultarArquivosView extends JFrame {

	private final int width = 800;
	private final int height = 500;
	
	private HashMap user = null;
	String indexArq = null;
	PrivateKey chavePrivada = null;
	
	public ConsultarArquivosView(final HashMap user) {
		this.user = user;
		DBManager.insereRegistro(8001, (String) user.get("email"));
		
		setLayout(null);
		setSize (this.width, this.height);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setTitle("Login");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);
		
		
		Container c = getContentPane();
		c.add(new Header((String)user.get("email"), (String)user.get("groupName"), (String)user.get("name")));
		c.add(new FirstBody("Total de acessos", Integer.parseInt(user.get("totalAcessos").toString())));
		
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
		
		final JLabel consultaLabel = new JLabel();
		JButton consultarButton = new JButton("Escolha uma pasta para consultar");
		consultarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser consultarchooser = new JFileChooser(); 
				consultarchooser.setCurrentDirectory(new java.io.File("."));
				consultarchooser.setDialogTitle("Caminho da chave privada");
				consultarchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if (consultarchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					consultaLabel.setText(consultarchooser.getSelectedFile().getAbsolutePath());
				}
			    else {
			      System.out.println("No Selection ");
			    }
			}
		});
		
		
		String[] columnNames = {"Nome código","Nome secreto", "Dono", "Grupo"};
		Object[][] data = {};
		final DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
		final JTable table = new JTable(tableModel){
            public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(380, 10, 400, 400);
		//table.setFillsViewportHeight(true);
		c.add(table.getTableHeader());
		c.add(scrollPane);
		
		final JButton decriptarButton = new JButton("Decriptar");
		decriptarButton.setEnabled(false);
		decriptarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				String nomeArquivo = (String) table.getValueAt(index, 1);
				if (Authentification.acessarArquivo(user, indexArq, nomeArquivo, chavePrivada, consultaLabel.getText())) {
					System.out.println("Decriptou arquivo com sucesso!");
				}
				else {
					JOptionPane.showMessageDialog(null, "Usuário não possui permissão para ler o arquivo selecionado");
				}
			}
		});
		
		JButton voltarButton = new JButton("Voltar");
		voltarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBManager.insereRegistro(8006, (String) user.get("email"));
				dispose();
				new MainView(user);
			}
		});
		
		JButton listarButton = new JButton("Listar arquivos");
		listarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBManager.insereRegistro(8007, (String) user.get("email"));
				chavePrivada = Authentification.leChavePrivada(chaveSecretaField.getText(), chavePrivadaLabel.getText(), user);
				if (chavePrivada == null) {
					DBManager.insereRegistro(8003, (String) user.get("email"));
				}
				if (Authentification.testaChavePrivada(chavePrivada, user)) {
					DBManager.insereRegistro(8002, (String) user.get("email"));
				}
				
				try {
					indexArq = new String(Authentification.decriptaArquivo(user, consultaLabel.getText(), "index", chavePrivada), "UTF8");
				} catch (UnsupportedEncodingException ex) {
					JOptionPane.showMessageDialog(null, "Não foi possível listar os arquivos com este credencial.");
					return;
				}
				String[] listaArquivos = indexArq.split("\n");
				for (String arq: listaArquivos) {
					String[] items = arq.split(" ");
					tableModel.addRow(items);
				}
				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
				table.setModel(tableModel);
				tableModel.fireTableDataChanged();
				decriptarButton.setEnabled(true);
				DBManager.insereRegistro(8009, (String) user.get("email"));
				
			}
		});
		
		chaveSecretaLabel.setBounds(30, 140, 300, 40);
		chaveSecretaField.setBounds(30, 180, 300, 40);
		chavePrivadaLabel.setBounds(30, 220, 370, 40);
		chavePrivadaButton.setBounds(30, 260, 300, 40);
		consultaLabel.setBounds(30, 300, 300, 30);
		consultarButton.setBounds(30, 340, 300, 40);
		listarButton.setBounds(30, 380, 300, 40);
		decriptarButton.setBounds(600, 420, 100, 40);
		voltarButton.setBounds(450, 420, 100, 40);
		
		c.add(chaveSecretaLabel);
		c.add(chaveSecretaField);
		c.add(chavePrivadaLabel);
		c.add(chavePrivadaButton);
		c.add(consultaLabel);
		c.add(consultarButton);
		c.add(listarButton);
		c.add(decriptarButton);
		c.add(voltarButton);
	}	
}
