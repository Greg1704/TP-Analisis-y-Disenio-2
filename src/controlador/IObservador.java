package controlador;

import modelo.Mensaje;

public interface IObservador {
	 public void mostrarIntentoDeConexion();
	 public void mostrarMensajeTextArea(Mensaje mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarUsuarioNoDisponible();
	 public void mostrarCierreSesion();
	 public void mostrarConexionErronea();
	 public void cerrarInstancia();
	 public void aceptaInicioSesion();
}
