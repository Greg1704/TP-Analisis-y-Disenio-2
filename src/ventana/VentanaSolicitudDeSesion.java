package ventana;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlador.ControladorCliente;

import javax.swing.JLabel;
import javax.swing.JButton;

public class VentanaSolicitudDeSesion extends JFrame implements MouseListener,IVistaCliente{

	public JLabel getLblIp() {
		return lblIp;
	}

	public void setLblIp(String string) {
		this.lblIp.setText(string);
	}

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblIp;
	private JLabel lblTexto;
	private JButton btnAceptar;
	private JButton btnRechazar;
	private ControladorCliente c;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaSolicitudDeSesion frame = new VentanaSolicitudDeSesion();
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
	public VentanaSolicitudDeSesion() {
		
		this.setVisible(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 184);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.panel = new JPanel();
		this.contentPane.add(this.panel, BorderLayout.CENTER);
		this.panel.setLayout(null);
		
		this.lblIp = new JLabel("XXX.XXX.X.XXX");
		this.lblIp.setBounds(33, 26, 203, 14);
		this.panel.add(this.lblIp);
		
		this.lblTexto = new JLabel("              quiere iniciar una sesion con usted");
		this.lblTexto.setBounds(0, 41, 264, 20);
		this.panel.add(this.lblTexto);
		
		this.btnAceptar = new JButton("Aceptar");
		this.btnAceptar.setBounds(21, 106, 89, 23);
		this.panel.add(this.btnAceptar);
		
		this.btnRechazar = new JButton("Rechazar");
		this.btnRechazar.setBounds(147, 106, 89, 23);
		this.panel.add(this.btnRechazar);
	}
	
	public void setControlador(ControladorCliente c) {
		this.c = c;
		setDefault();
	}
	
	public void setDefault() {
		
		this.btnAceptar.addMouseListener(this);
		this.btnAceptar.addActionListener(c);
		this.btnAceptar.setActionCommand(aceptarSolicitud);
		
		this.btnRechazar.addMouseListener(this);
		this.btnRechazar.addActionListener(c);
		this.btnRechazar.setActionCommand(rechazarSolicitud);
	}
	
	public void aparece() {
		this.setVisible(true);
	}
	
	public void desaparece() {
		this.setVisible(false);
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
