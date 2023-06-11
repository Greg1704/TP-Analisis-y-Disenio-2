package modelo.chat;

import java.io.Serializable;
import java.util.ArrayList;

import modelo.Encriptacion;
import modelo.Server.ManejaConexiones;
import modelo.interfaces.IEncriptacion;

public class Mensaje implements Serializable{
	private String mensaje = "";
	private String ipEmisor;
	private int puertoEmisor;
	private ArrayList<ManejaConexiones> conexiones = null;
	private IEncriptacion iEncriptacion;
	
	public Mensaje(String mensaje, String ipEmisor, int puertoEmisor) {
		this.mensaje = mensaje;
		this.ipEmisor = ipEmisor;
		this.puertoEmisor = puertoEmisor;
		this.iEncriptacion = Encriptacion.getInstancia();
	}
	
	public Mensaje(ArrayList<ManejaConexiones> a, String ipEmisor, int puertoEmisor) {
		this.conexiones = a;
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

	public ArrayList<ManejaConexiones> getConexiones() {
		return conexiones;
	}
	
	public void encriptar(String clave) {
		String aux = iEncriptacion.encriptadoMensaje(this.getMensaje(), clave);
		this.mensaje = aux;
	}
	
	public void desencriptar(String clave) {
		String aux = iEncriptacion.desencriptadoMensaje(this.getMensaje(), clave);
		this.mensaje = aux;
	}
	
}
