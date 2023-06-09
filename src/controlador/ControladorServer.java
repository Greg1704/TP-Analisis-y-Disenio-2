package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;

import controlador.interfaces.IConectados;
import modelo.Server;
import ventana.VentanaServidor;
import ventana.interfaces.IVistaServer;

public class ControladorServer implements ActionListener, WindowListener,IConectados, Serializable { // la interfaz IOBservador es util para el new Server
	private Server server;
	private VentanaServidor vs;
	private static ControladorServer instancia = null;
	
	public ControladorServer() {
		vs = new VentanaServidor();
		this.vs.setControlador(this);
		server = new Server(this);
	}
	
	public static ControladorServer getInstancia() {
		if (instancia == null)
			instancia = new ControladorServer();
		return instancia;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(IVistaServer.cerrarServidor)) {
			System.exit(0);
		}
	}
	
	@Override
	public void cambioCantConectados(int sumaOresta) {
		String texto = this.vs.getLblCantidadConectados();
		String[] cadena = texto.split(": ");
		int nuevoValor = Integer.parseInt(cadena[1]);
		nuevoValor=sumaOresta;
		String nuevaCadena = cadena[0] + ": " + nuevoValor;
		this.vs.setLblCantidadConectados(nuevaCadena);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
		this.vs.fusilarVentana();
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
