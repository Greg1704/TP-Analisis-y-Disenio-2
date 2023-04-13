package controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ventana.*;

public class Controlador implements ActionListener{

	VentanaDefinitiva v;
	private static Controlador instancia = null;
	String mensaje;
	
	private Controlador () {
		this.v = new VentanaDefinitiva();
		this.v.setControlador(this);
	}
	
	public static Controlador getInstancia() {
		if (instancia == null)
			instancia = new Controlador();
		return instancia;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			v.agregarMensajeAlChat(mensaje);
		}
	}
}
