package ventana;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlador.ControladorMonitor;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class VentanaMonitor extends JFrame implements MouseListener,IVista {

	private JPanel contentPane;
	private JLabel lblMonitor;
	private JLabel lblMonitoreando;
	private JButton btnCerrarMonitor;
	private ControladorMonitor c;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaMonitor frame = new VentanaMonitor();
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
	public VentanaMonitor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setVisible(true);
		
		setBounds(100, 100, 249, 194);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		this.lblMonitor = new JLabel("Monitor");
		this.lblMonitor.setFont(new Font("Arial Black", Font.PLAIN, 30));
		this.lblMonitor.setBounds(10, 11, 299, 58);
		this.contentPane.add(this.lblMonitor);
		
		this.lblMonitoreando = new JLabel("Estado: Activo");
		this.lblMonitoreando.setBounds(10, 80, 179, 19);
		this.contentPane.add(this.lblMonitoreando);
		
		this.btnCerrarMonitor = new JButton("Cerrar");
		this.btnCerrarMonitor.setBounds(59, 121, 89, 23);
		this.contentPane.add(this.btnCerrarMonitor);
	}
	
	public void setControlador(ControladorMonitor controladorMonitor) {
		this.c = controladorMonitor;
		this.addWindowListener(controladorMonitor);
		setDefault();	
	}
	
	public void setDefault() {
		this.btnCerrarMonitor.addMouseListener(this);
		this.btnCerrarMonitor.addActionListener(c);
		this.btnCerrarMonitor.setActionCommand(cerrarMonitor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
