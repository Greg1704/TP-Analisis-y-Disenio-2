package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import modelo.Cliente;
import modelo.Server;
import ventana.*;

public class Controlador implements ActionListener, IObservador, WindowListener {

	private VentanaDefinitiva v;
	private VentanaSolicitudDeSesion vs;
	private static Controlador instancia = null;
	private String mensaje;
	private int puerto;
	private Cliente cliente;
	private Server server;
	
	private Controlador () {
		puerto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto que desea usar(valor mayor a 1024)"));
		while (puerto<1025 || puerto>65535) {
			if(puerto<1025)
				puerto = Integer.parseInt(JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor mayor a 1024)"));
			else
				puerto = Integer.parseInt(JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor menor a 65536)"));
		}
		this.v = new VentanaDefinitiva();
		this.vs = new VentanaSolicitudDeSesion();
		this.v.setControlador(this);
		this.vs.setControlador(this);
		server = new Server(puerto, this); 
		server.run();
	}
	
	public static Controlador getInstancia() {
		if (instancia == null)
			instancia = new Controlador();
		return instancia;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(IVista.intentoDeConexion)) {
			try {
				if (Integer.parseInt(v.getTextFieldPuerto()) != puerto) {
					cliente = new Cliente(v.getTextFieldIp(), Integer.parseInt(v.getTextFieldPuerto()), this);
					server.setModoEscucha(false);
				} else {
					JOptionPane.showMessageDialog(null, "Ingrese un puerto diferente al suyo");
				}
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "El puerto debe ser un numero entero positivo");
			}
		}else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			mensaje = "ip: " +cliente.getIpLocal() + " puerto: " + this.puerto + " : " + mensaje;
			cliente.mandarMensaje(mensaje);
		}else if(e.getActionCommand().equals(IVista.cerrarSesion)) {
			cliente.mandarMensaje("/cerrar/");
		} else if(e.getActionCommand().equals(IVista.aceptarSolicitud)) {
			cliente = new Cliente("localhost", puerto, this); 
			cliente.mandarMensaje("/modoEscuchaFalse/");
			cliente.mandarMensaje("/aceptaInicioSesion/");
			this.vs.desaparece();
		}else if(e.getActionCommand().equals(IVista.rechazarSolicitud)) {
			server.rechaza(); // si rechazo deberia mostrarle al otro q no se pudo establecer la conex (y esta linea no anda)
			vs.desaparece();
		} 
	}

	@Override
	public void mostrarIntentoDeConexion() {
		this.vs.setLblIp(server.getIpSolicitante()); 
		this.vs.aparece();
	}

	@Override
	public void mostrarMensajeTextArea(String mensaje) {
		v.agregarMensajeAlChat(mensaje);
	}

	@Override
	public void mostrarUsuarioOcupado() {
		JOptionPane.showMessageDialog(null, "El usuario con el que se quiere contactar está en conversación");
		server.setModoEscucha(true); // te rechazan la sesion entonces volves a escuchar en tu servidor
	}
	
	@Override
	public void mostrarUsuarioNoDisponible() {
		JOptionPane.showMessageDialog(null, "El usuario con el que se quiere contactar no está disponible");
		server.setModoEscucha(true);
	}

	@Override
	public void mostrarCierreSesion() {
		JOptionPane.showMessageDialog(null, "La sesión ha sido cerrada");
	}

	@Override
	public void cerrarInstancia() {
		v.setVisible(false);
        System.exit(0); // finaliza el proceso
	}

	@Override
	public void aceptaInicioSesion() {
		v.getTabbedPane().setEnabledAt(0, false);
		v.getTabbedPane().setEnabledAt(1, true);
		v.getTabbedPane().setSelectedIndex(1);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (cliente != null) {
			cliente.mandarMensaje("/cerrar/");
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
