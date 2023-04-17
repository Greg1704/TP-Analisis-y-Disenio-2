package controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import modelo.Cliente;
import modelo.Server;
import ventana.*;

public class Controlador implements ActionListener{

	VentanaDefinitiva v;
	private static Controlador instancia = null;
	String mensaje;
	Cliente cliente;
	Server server;
	
	private Controlador () {
		this.v = new VentanaDefinitiva();
		this.v.setControlador(this);
		server = new Server(1234); // random x ahora
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
		}else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			
			
			
			
			
			v.agregarMensajeAlChat(mensaje);
		}else if(e.getActionCommand().equals(IVista.intentoDeConexion)) {
			
		}
		
	}
}
