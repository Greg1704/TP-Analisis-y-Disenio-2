package controlador;

import modelo.Mensaje;
import modelo.Server;

public class ControladorServer implements IObservador{
	private Server server;
	public ControladorServer() {
		server = new Server(1234, this);
		server.run();
	}
	@Override
	public void mostrarIntentoDeConexion() {
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
}
