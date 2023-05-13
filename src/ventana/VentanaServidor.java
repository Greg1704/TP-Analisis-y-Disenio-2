package ventana;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;

public class VentanaServidor extends JFrame {

	private JPanel contentPane;
	private JPanel panelServer;
	private JLabel lblServer;
	private JLabel lblCantidadConectados;
	private JLabel lblEstadoServidor;
	private JButton btnCerrarServidor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaServidor frame = new VentanaServidor();
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
	public VentanaServidor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 233);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.panelServer = new JPanel();
		this.contentPane.add(this.panelServer, BorderLayout.CENTER);
		this.panelServer.setLayout(null);
		
		this.lblServer = new JLabel("Server");
		this.lblServer.setFont(new Font("Times New Roman", Font.PLAIN, 40));
		this.lblServer.setBounds(10, 11, 159, 45);
		this.panelServer.add(this.lblServer);
		
		this.lblCantidadConectados = new JLabel("Cantidad de conectados: 0");
		this.lblCantidadConectados.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.lblCantidadConectados.setBounds(21, 115, 219, 31);
		this.panelServer.add(this.lblCantidadConectados);
		
		this.lblEstadoServidor = new JLabel("Estado del Servidor: Abierto");
		this.lblEstadoServidor.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.lblEstadoServidor.setBounds(20, 67, 219, 31);
		this.panelServer.add(this.lblEstadoServidor);
		
		this.btnCerrarServidor = new JButton("Cerrar");
		this.btnCerrarServidor.setBounds(124, 157, 89, 23);
		this.panelServer.add(this.btnCerrarServidor);
	}
}
