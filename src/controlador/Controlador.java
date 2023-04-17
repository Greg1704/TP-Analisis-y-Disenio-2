package controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import modelo.Cliente;
import modelo.Server;
import ventana.*;

public class Controlador implements ActionListener,Observador {

	VentanaDefinitiva v;
	VentanaSolicitudDeSesion vs;
	private static Controlador instancia = null;
	String mensaje;
	Cliente cliente, cliente2;
	Server server;
	
	private Controlador () {
		this.v = new VentanaDefinitiva();
		this.vs = new VentanaSolicitudDeSesion();
		this.v.setControlador(this);
		this.vs.setControlador(this);
		server = new Server(1235); // random x ahora
		
	//	server.setControlador(this);
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
			
			cliente = new Cliente(v.getTextFieldIp(), Integer.parseInt(v.getTextFieldPuerto()));
		//	vs.aparece();
		}else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			cliente.mandarMensaje(mensaje);
			
			
			
			
			v.agregarMensajeAlChat(mensaje);
			
		}else if(e.getActionCommand().equals(IVista.cerrarSesion)) {
			server.cerrarConversacion();
			cliente.cerrarConversacion();
		}else if(e.getActionCommand().equals(IVista.aceptarSolicitud)) {
			cliente2 = new Cliente("192.168.0.241", 1234);
		}else if(e.getActionCommand().equals(IVista.rechazarSolicitud)) {
			vs.desaparece();
		} 
		
	}

	@Override
	public void update(Object o) {
		this.vs.aparece();
	}
}
