package controlador;

import modelo.Mensaje;

public interface IComunicacion {
	 public void mostrarIntentoDeConexion(String ip,int puerto);
	 public void mostrarMensajeTextArea(Mensaje mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarUsuarioNoDisponible();
	 public void mostrarCierreSesion();
	 public void mostrarConexionErronea();
	 public void mostrarConexxionErroneaServer();
	 public void mostrarPuertoErroneo();
	 public void mostrarPuertoEnUso();
	 public void cerrarInstancia();
	 public void aceptaInicioSesion();
	 public void mandarMensaje(Mensaje mensaje);
	 public void mandarMensaje(Mensaje mensaje, int puerto);
}
