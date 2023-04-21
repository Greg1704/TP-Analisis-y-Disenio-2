package controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import modelo.Cliente;
import modelo.Server;
import ventana.*;

public class Controlador implements ActionListener, Observador {

	VentanaDefinitiva v;
	VentanaSolicitudDeSesion vs;
	private static Controlador instancia = null;
	String mensaje;
	int puerto;
	Cliente cliente;
	Server server;
	
	private Controlador () {
		puerto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto que desea usar(valor mayor a 1024)"));
		this.v = new VentanaDefinitiva();
		this.vs = new VentanaSolicitudDeSesion();
		this.v.setControlador(this);
		this.vs.setControlador(this);
		server = new Server(puerto); 
		server.addObserver(this);
		server.run();
	}
	
	public static Controlador getInstancia() {
		if (instancia == null)
			instancia = new Controlador();
		return instancia;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(IVista.intentoDeConexion)) {
			server.cambiaModoEscucha(false);
			cliente = new Cliente(v.getTextFieldIp(), Integer.parseInt(v.getTextFieldPuerto()));
			cliente.addObserver(this);
		//	vs.aparece();
		}else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			cliente.mandarMensaje(mensaje);
			
		}else if(e.getActionCommand().equals(IVista.cerrarSesion)) {
			server.cerrarServidor();
			cliente.cerrarConversacion();
		}else if(e.getActionCommand().equals(IVista.aceptarSolicitud)) {
			server.cambiaModoEscucha(false);
			cliente = new Cliente("localhost", 1234); // hardcodeado
			cliente.addObserver(this);
			this.vs.desaparece();
		}else if(e.getActionCommand().equals(IVista.rechazarSolicitud)) {
			vs.desaparece();
		} 
		
	}

	@Override
	public void update(Object o) {
		this.vs.aparece();
	}

	@Override
	public void mostrarMensajeTextArea(String mensaje) {
		v.agregarMensajeAlChat(mensaje);
	}
	
}
