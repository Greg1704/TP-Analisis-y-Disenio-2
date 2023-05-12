package modelo;

import java.io.Serializable;

public class Mensaje implements Serializable{
	private String mensaje;
	private String ipEmisor;
	private String puertoEmisor;
	
	public Mensaje(String mensaje, String ipEmisor, String puertoEmisor) {
		this.mensaje = mensaje;
		this.ipEmisor = ipEmisor;
		this.puertoEmisor = puertoEmisor;
	}
	
	public String getMensaje() {
		return this.mensaje;
	}

	public String getIpEmisor() {
		return ipEmisor;
	}

	public String getPuertoEmisor() {
		return puertoEmisor;
	}
	
}
