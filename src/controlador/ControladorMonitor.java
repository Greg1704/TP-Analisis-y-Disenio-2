package controlador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import controlador.ControladorServer;
import controlador.interfaces.IComunicacion;
import modelo.Monitor;
import ventana.VentanaMonitor;
import ventana.interfaces.IVistaMonitor;

public class ControladorMonitor implements ActionListener, WindowListener {

	Monitor monitor;
	VentanaMonitor vm;
	private static ControladorMonitor instancia = null;
	
	public ControladorMonitor() {
		this.monitor = Monitor.getInstance();
		this.vm = new VentanaMonitor();
		this.vm.setControlador(this);
	}
	
	public static ControladorMonitor getInstancia() {
		if (instancia == null)
			instancia = new ControladorMonitor();
		return instancia;
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(IVistaMonitor.cerrarMonitor)) {
			System.exit(0);
		}
	}
}
