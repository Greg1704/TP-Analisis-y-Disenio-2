package modelo;

import java.net.Socket;
import java.util.ArrayList;

public class Chat {
	private ArrayList<Mensaje> mensajes = new ArrayList();
	Socket cliente1, cliente2;
	
	public Chat (Socket cliente1, Socket cliente2) {
		this.cliente1 = cliente1;
		this.cliente2 = cliente2;
	}
	
	public void agregarMensajes(Mensaje mensaje) {
		this.mensajes.add(mensaje);
	}

	public ArrayList<Mensaje> getMensajes() { // por si se quiere llegar a recuperar el chat en algun lado
		return mensajes;
	}

	public Socket getCliente1() {
		return cliente1;
	}

	public Socket getCliente2() {
		return cliente2;
	}
	
	public int getPuerto1() {
		return cliente1.getPort();
	}
	
	public int getPuerto2() {
		return cliente2.getPort();
	}
	
}
