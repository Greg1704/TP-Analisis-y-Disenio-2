package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import controlador.interfaces.IComunicacion;
import modelo.Cliente;
import modelo.Server.ManejaConexiones;
import modelo.chat.Mensaje;
import modelo.Encriptacion;
import ventana.*;
import ventana.interfaces.IVistaCliente;

public class ControladorCliente implements ActionListener, IComunicacion, WindowListener {

	private VentanaCliente v;
	private VentanaSolicitudDeSesion vs;
	private static ControladorCliente instancia = null;
	private int puerto;
	private Cliente cliente;
	private int puertoServidor = 11000;
	private int contadorFallos = 0;
	
	private ControladorCliente () {
		String puertoTexto = JOptionPane.showInputDialog("Ingrese el puerto que desea usar(valor mayor a 1024)");
		if (puertoTexto != null && !puertoTexto.equals("")) {
			while (puertoTexto == null || puertoTexto.equals("") || puertoTexto.length()>5 || Integer.parseInt(puertoTexto)<1025 || Integer.parseInt(puertoTexto)>65534) {
				if (puertoTexto == null) 
					System.exit(0);
				else if(puertoTexto.equals("") || puertoTexto.length()>5)
					puertoTexto = JOptionPane.showInputDialog("Puerto invalido, ingresar un valor entre 1024 y 65534");
				else if (Integer.parseInt(puertoTexto)>65534)
					puertoTexto = JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor menor a 65535)");
				else
					puertoTexto = JOptionPane.showInputDialog("Puerto invalido, ingresar nuevamente(valor mayor a 1024)");
			}
			String nombreCliente = JOptionPane.showInputDialog("Ingrese el nombre de usuario que desee utilizar");
			this.puerto = Integer.parseInt(puertoTexto);
			cliente = new Cliente("localhost", puertoServidor, puerto, nombreCliente, this);
			this.v = new VentanaCliente();
			this.vs = new VentanaSolicitudDeSesion();
			this.v.setControlador(this);
			this.vs.setControlador(this);
			this.setPuertoReferencia(puerto);
			Mensaje mensaje = new Mensaje("/puerto/" + this.puerto + "/" + nombreCliente, cliente.getIpLocal(), this.puerto);
			this.mandarMensaje(mensaje);
		} else {
			System.exit(0);
		}
	}
	
	public static ControladorCliente getInstancia() {
		if (instancia == null)
			instancia = new ControladorCliente();
		return instancia;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(IVistaCliente.intentoDeConexion)) {
			if (puertoServidor != Integer.parseInt(v.getTextFieldPuerto())) {
				String claveCripto = JOptionPane.showInputDialog("Ingrese clave del chat de 8 caracteres");
				while(claveCripto.length()!=8) {
					claveCripto = JOptionPane.showInputDialog("La clave ingresada no cumple los requisitos pedidos. Ingrese una nueva, de 8 caracteres");
				}
				cliente.setClaveEncriptacion(claveCripto);
				Mensaje mensaje = new Mensaje("/intentoConexion/" + Integer.parseInt(v.getTextFieldPuerto()) + "/" + claveCripto, cliente.getIpLocal(), this.puerto);
				this.mandarMensaje(mensaje);
			} else {
				this.mostrarPuertoErroneo();
			}
		} else if(e.getActionCommand().equals(IVistaCliente.enviarMensaje)) {
			contadorFallos = 0;
			Mensaje mensaje = new Mensaje(v.getTextFieldChatMensajeUsuario(), cliente.getIpLocal(), this.puerto);
			mensaje.encriptar(cliente.getClaveEncriptacion());
			this.mandarMensaje(mensaje);
		} else if(e.getActionCommand().equals(IVistaCliente.cerrarSesion)) {
			Mensaje mensaje = new Mensaje("/cerrar/", cliente.getIpLocal(), this.puerto);
			this.mandarMensaje(mensaje);
		} else if(e.getActionCommand().equals(IVistaCliente.aceptarSolicitud)) {
			Mensaje mensaje = new Mensaje("/aceptar/", cliente.getIpLocal(), this.puerto);
			this.mandarMensaje(mensaje);
			this.vs.desaparece();
		}else if(e.getActionCommand().equals(IVistaCliente.rechazarSolicitud)) {
			Mensaje mensaje = new Mensaje("/rechazar/", cliente.getIpLocal(), this.puerto);
			this.mandarMensaje(mensaje);
			vs.desaparece();
		}
	}

	@Override
	public void mostrarIntentoDeConexion(String ip, int puerto) {
		this.vs.setLblIp("ip: "+ ip + " puerto: " + puerto); 
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
	public void mostrarConexxionErroneaServer() {
		JOptionPane.showMessageDialog(null, "El servidor no está abierto");
		System.exit(0); // finaliza el proceso
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
	
	public String getIpLocal() {
		return cliente.getIpLocal();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (cliente != null) {
			Mensaje mensaje = new Mensaje("/cerrar/", cliente.getIpLocal(), this.puerto);
			this.mandarMensaje(mensaje);
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

	@Override
	public void mostrarPuertoEnUso() {
		JOptionPane.showMessageDialog(null, "El puerto ya está siendo usado por otro usuario, por favor inicie la aplicacion de vuelta y elija otro puerto.");
		this.cerrarInstancia();
	}

	@Override
	public void mandarMensaje(Mensaje mensaje) {
		cliente.mandarMensaje(mensaje);
	}
	
	@Override
	public void mandarMensaje(Mensaje mensaje, int puerto) {
		// TODO Auto-generated method stub
		
	}
	
	public void actualizarListaConectados(ArrayList<ManejaConexiones> conexiones) {
		this.v.actualizarListaConectados(conexiones);
	}

	@Override
	public void mostrarMensajeNoEnviado(Mensaje mensaje) {
		contadorFallos++;
		if (contadorFallos < 4) {
			if (JOptionPane.showConfirmDialog(null, "El mensaje no pudo ser enviado. Quiere reintentar?", "ERROR",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				cliente.mandarMensaje(mensaje);
			} else {
				
			}
		} else {
			System.exit(0);
		}
	}
}
