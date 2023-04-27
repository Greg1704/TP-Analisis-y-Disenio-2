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
			cliente = new Cliente(v.getTextFieldIp(), Integer.parseInt(v.getTextFieldPuerto()));
		//	server.cambiaModoEscucha(false);
			cliente.addObserver(this);
		}else if(e.getActionCommand().equals(IVista.enviarMensaje)) {
			mensaje = v.getTextFieldChatMensajeUsuario();
			mensaje = "ip: " +cliente.getIpLocal() + " puerto: " + this.puerto + " : " + mensaje;
			cliente.mandarMensaje(mensaje);
		}else if(e.getActionCommand().equals(IVista.cerrarSesion)) {
			cliente.mandarMensaje("/cerrar/");
		} else if(e.getActionCommand().equals(IVista.aceptarSolicitud)) {
			server.setListo();
			cliente = new Cliente("localhost", puerto); 
			cliente.mandarMensaje("/modoEscuchaFalse/");
			cliente.mandarMensaje("/aceptaInicioSesion/");
		//	server.cambiaModoEscucha(false);
			cliente.addObserver(this);
			this.vs.desaparece();
		}else if(e.getActionCommand().equals(IVista.rechazarSolicitud)) {
			server.rechaza(); // si rechazo deberia mostrarle al otro q no se pudo establecer la conex (y esta linea no anda)
			vs.desaparece();
		} 
		
	}

	@Override
	public void update(Object o) {
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
	}

	@Override
	public void mostrarCierreSesion() {
		JOptionPane.showMessageDialog(null, "La sesión ha sido cerrada");
	}

	@Override
	public void cierraInstancia() {
		v.setVisible(false);
        System.exit(0); // finaliza el proceso
	}

	@Override
	public void aceptaInicioSesion() {
		v.getTabbedPane().setEnabledAt(0, false);
		v.getTabbedPane().setEnabledAt(1, true);
	}
	
}
