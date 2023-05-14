package modelo;

import java.io.Serializable;

public class Mensaje implements Serializable{
	private String mensaje;
	private String ipEmisor;
	private int puertoEmisor;
	
	public Mensaje(String mensaje, String ipEmisor, int puertoEmisor) {
		this.mensaje = mensaje;
		this.ipEmisor = ipEmisor;
		this.puertoEmisor = puertoEmisor;
	}
	
	public String getMensaje() {
		return this.mensaje;
	}
	
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getIpEmisor() {
		return ipEmisor;
	}

	public int getPuertoEmisor() {
		return puertoEmisor;
	}

	
}
