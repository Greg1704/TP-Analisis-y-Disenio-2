package modelo;

import java.util.ArrayList;

public class Chat {
	private ArrayList<Mensaje> mensajes = new ArrayList();
	
	public void agregarMensajes(Mensaje mensaje) {
		this.mensajes.add(mensaje);
	}

	public ArrayList<Mensaje> getMensajes() { // por si se quiere llegar a recuperar el chat en algun lado
		return mensajes;
	}
	
}
