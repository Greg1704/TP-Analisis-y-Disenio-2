package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import modelo.Mensaje;
import modelo.Server;
import ventana.IVista;
import ventana.VentanaServidor;

public class ControladorServer implements ActionListener, IObservador, WindowListener{
	private Server server;
	private VentanaServidor vs;
	private static ControladorServer instancia = null;
	
	public ControladorServer() {
		server = new Server(65535, this);
		server.setCs(this);
		vs = new VentanaServidor();
		this.vs.setControlador(this);
		server.run();
	}
	
	public static ControladorServer getInstancia() {
		if (instancia == null)
			instancia = new ControladorServer();
		return instancia;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals(IVista.cerrarServidor)) {
				this.server.cerrarServidor();
				this.vs.fusilarVentana();
		}
		
	}
	
	public void cambioCantConectados(int sumaOresta) {
		String texto = this.vs.getLblCantidadConectados();
		String[] cadena = texto.split(": ");
		int nuevoValor = Integer.parseInt(cadena[1]);
		nuevoValor+=sumaOresta;
		String nuevaCadena = cadena[0] + ": " + nuevoValor;
		this.vs.setLblCantidadConectados(nuevaCadena);
	}
	
	@Override
	public void mostrarIntentoDeConexion(String ip) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mostrarMensajeTextArea(Mensaje mensaje) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mostrarUsuarioOcupado() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mostrarUsuarioNoDisponible() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mostrarCierreSesion() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mostrarConexionErronea() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void cerrarInstancia() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void aceptaInicioSesion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
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
