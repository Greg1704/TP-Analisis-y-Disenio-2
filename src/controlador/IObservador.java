package controlador;

import modelo.Mensaje;

public interface IObservador {
	 public void mostrarIntentoDeConexion(String ip);
	 public void mostrarMensajeTextArea(Mensaje mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarUsuarioNoDisponible();
	 public void mostrarCierreSesion();
	 public void mostrarConexionErronea();
	 public void mostrarPuertoErroneo();
	 public void mostrarPuertoEnUso();
	 public void cerrarInstancia();
	 public void aceptaInicioSesion();
}
