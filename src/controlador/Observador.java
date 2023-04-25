package controlador;

public interface Observador {
	 public void update(Object o);
	 public void mostrarMensajeTextArea(String mensaje);
	 public void mostrarUsuarioOcupado();
	 public void mostrarCierreSesion();
	 }
