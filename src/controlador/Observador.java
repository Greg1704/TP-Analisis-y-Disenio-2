package controlador;

public interface Observador {
	 public void mostrarIntentoDeConexion();
	 public void mostrarMensajeTextArea(String mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarCierreSesion();
	 public void cerrarInstancia();
	 public void aceptaInicioSesion();
}
