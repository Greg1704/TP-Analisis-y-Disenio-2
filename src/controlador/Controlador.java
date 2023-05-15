package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import modelo.Cliente;
import modelo.Mensaje;
import modelo.Server;
import modelo.Encriptacion;
import ventana.*;

public class Controlador implements ActionListener, IObservador, WindowListener {

	private VentanaDefinitiva v;
	private VentanaSolicitudDeSesion vs;
	private static Controlador instancia = null;
	private int puerto;
	private Cliente cliente;
	private Server server;
	private int puertoServidor = 65535;
	
	private Controlador () {
		cliente = new Cliente("localhost", puertoServidor, this);
		puerto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto que desea usar(valor mayor a 1024)"));
		while (puerto<1025 || puerto>65534) {
			if(puerto<1025)
				puerto = Integer.parseInt(JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor mayor a 1024)"));
			else
				puerto = Integer.parseInt(JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor menor a 65535)"));
		}
		this.v = new VentanaDefinitiva();
		this.vs = new VentanaSolicitudDeSesion();
		this.v.setControlador(this);
		this.vs.setControlador(this);
		this.setPuertoReferencia(puerto);
		Mensaje mensaje = new Mensaje("/puerto/ " + this.puerto, cliente.getIpLocal(), this.puerto);
		cliente.mandarMensaje(mensaje);
	}
	
	public static Controlador getInstancia() {
		if (instancia == null)
			instancia = new Controlador();
		return instancia;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(IVista.intentoDeConexion)) {
			if (puertoServidor != Integer.parseInt(v.getTextFieldPuerto())) {
				Mensaje mensaje = new Mensaje("/intentoConexion/ " + Integer.parseInt(v.getTextFieldPuerto()), cliente.getIpLocal(), this.puerto);
				cliente.mandarMensaje(mensaje);
			} else {
				this.mostrarPuertoErroneo();
			}
		} else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			String cripto = Encriptacion.Encriptar(v.getTextFieldChatMensajeUsuario());
			Mensaje mensaje = new Mensaje(cripto, cliente.getIpLocal(), this.puerto);
			cliente.mandarMensaje(mensaje);
		} else if(e.getActionCommand().equals(IVista.cerrarSesion)) {
			Mensaje mensaje = new Mensaje("/cerrar/", cliente.getIpLocal(), this.puerto);
			cliente.mandarMensaje(mensaje);
		} else if(e.getActionCommand().equals(IVista.aceptarSolicitud)) {
			Mensaje mensaje = new Mensaje("/aceptar/", cliente.getIpLocal(), this.puerto);
			cliente.mandarMensaje(mensaje);
			this.vs.desaparece();
		}else if(e.getActionCommand().equals(IVista.rechazarSolicitud)) {
			Mensaje mensaje = new Mensaje("/rechazar/", cliente.getIpLocal(), this.puerto);
			cliente.mandarMensaje(mensaje);
			vs.desaparece();
		}
	}

	@Override
	public void mostrarIntentoDeConexion(String ip) {
		this.vs.setLblIp(ip); 
		this.vs.aparece();
	}

	@Override
	public void mostrarMensajeTextArea(Mensaje mensaje) {
		v.agregarMensajeAlChat(mensaje);
	}

	@Override
	public void mostrarUsuarioOcupado() {
		JOptionPane.showMessageDialog(null, "El usuario con el que se quiere contactar está en conversación");
	}
	
	@Override
	public void mostrarUsuarioNoDisponible() {
		JOptionPane.showMessageDialog(null, "El usuario con el que se quiere contactar no está disponible");
	}

	@Override
	public void mostrarCierreSesion() {
		JOptionPane.showMessageDialog(null, "La sesión ha sido cerrada");
	}
	
	@Override
	public void mostrarConexionErronea() {
		JOptionPane.showMessageDialog(null, "La ip dada y/o el puerto son erroneas");
	}
	
	@Override
	public void mostrarPuertoErroneo() {
		JOptionPane.showMessageDialog(null, "Ingrese un puerto diferente al del servidor");
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
		if (cliente != null && cliente.getCliente() != null) {
			Mensaje mensaje = new Mensaje("/cerrar/", cliente.getIpLocal(), this.puerto);
			cliente.mandarMensaje(mensaje);
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
	
	public void setPuertoReferencia(int puerto) {
		String mensaje = this.v.getLblPuertoReferencia();
		String[] cadena = mensaje.split(": ");
		String nuevoMensaje = cadena[0] + ": " + puerto;
		this.v.setLblPuertoReferencia(nuevoMensaje);
	}

}
