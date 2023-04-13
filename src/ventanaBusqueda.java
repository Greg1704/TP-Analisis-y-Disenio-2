import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;

public class ventanaBusqueda extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblNombreApp;
	private JLabel lblIp;
	private JLabel lblPuerto;
	private JTextField textFieldIp;
	private JTextField textFieldPuerto;
	private JButton btnConectar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ventanaBusqueda frame = new ventanaBusqueda();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ventanaBusqueda() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.panel = new JPanel();
		this.contentPane.add(this.panel, BorderLayout.CENTER);
		this.panel.setLayout(null);
		
		this.lblNombreApp = new JLabel("ChitChat");
		this.lblNombreApp.setFont(new Font("Arial Black", Font.PLAIN, 28));
		this.lblNombreApp.setBounds(23, 11, 198, 45);
		this.panel.add(this.lblNombreApp);
		
		this.lblIp = new JLabel("Ip:");
		this.lblIp.setBounds(82, 67, 20, 22);
		this.panel.add(this.lblIp);
		
		this.lblPuerto = new JLabel("Puerto:");
		this.lblPuerto.setBounds(82, 121, 46, 14);
		this.panel.add(this.lblPuerto);
		
		this.textFieldIp = new JTextField();
		this.textFieldIp.setBounds(147, 67, 165, 20);
		this.panel.add(this.textFieldIp);
		this.textFieldIp.setColumns(10);
		
		this.textFieldPuerto = new JTextField();
		this.textFieldPuerto.setColumns(10);
		this.textFieldPuerto.setBounds(147, 118, 165, 20);
		this.panel.add(this.textFieldPuerto);
		
		this.btnConectar = new JButton("Conectar");
		this.btnConectar.setBounds(166, 175, 89, 23);
		this.panel.add(this.btnConectar);
	}
}
