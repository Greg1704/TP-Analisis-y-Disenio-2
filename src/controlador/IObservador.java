package controlador;

public interface IObservador {
	 public void mostrarIntentoDeConexion();
	 public void mostrarMensajeTextArea(String mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarUsuarioNoDisponible();
	 public void mostrarCierreSesion();
	 public void cerrarInstancia();
	 public void aceptaInicioSesion();
}
