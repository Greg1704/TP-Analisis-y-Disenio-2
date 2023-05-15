package modelo;

import java.util.ArrayList;

public class Chat {
	private ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
	private String ip1;
	private int puerto1, puerto2;
	
	public Chat(String ip1, int puerto1, int puerto2) {
		this.ip1 = ip1;
		this.puerto1 = puerto1;
		this.puerto2 = puerto2;
	}
	
	public void agregarMensajes(Mensaje mensaje) {
		this.mensajes.add(mensaje);
	}

	public ArrayList<Mensaje> getMensajes() { // por si se quiere llegar a recuperar el chat en algun lado
		return mensajes;
	}

	public String getIp1() {
		return ip1;
	}

	public int getPuerto1() {
		return puerto1;
	}

	public int getPuerto2() {
		return puerto2;
	}
	
	
	
}
