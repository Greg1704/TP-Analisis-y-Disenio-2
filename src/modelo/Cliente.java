package modelo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import controlador.IObservador;

import java.net.InetAddress;

public class Cliente implements Runnable {

	private Socket cliente;
	private int puertoAConectar;
	private String ipAConectar, ipLocal;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private boolean listo = false;
	private IObservador observador;
	
	public Cliente(String ipAConectar, int puerto, IObservador observador) {
		this.puertoAConectar = puerto;
		this.ipAConectar = ipAConectar;
		this.observador = observador;
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			this.ipLocal = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.run();
	}
    
	public String getIpLocal() {
		return ipLocal;
	}
	
	@Override
	public void run() {

		try {
			cliente = new Socket(ipAConectar, puertoAConectar);
			os = new ObjectOutputStream(cliente.getOutputStream());
			is = new ObjectInputStream(cliente.getInputStream());
			ManejaInput m = new ManejaInput();
			Thread t = new Thread(m);
			t.start();
			
		} catch (IOException e) {
			observador.mostrarConexionErronea();
		}
}
	
	public void mandarMensaje(Mensaje mensaje) {
		try {
			os.writeObject(mensaje);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		} // SE MANDA DIRECTAMENTE A SERVIDOR. SE VE EN LA VENTANA EL MENSAJE ENVIADO YA QUE SE RECIBE DEL SERVIDOR LUEGO (EN LA PARTE DONDE SE INVOCA REPARTE())
	}
	
	public void cerrarConversacion() {
		listo = true;
		try {
			is.close();
			os.close();
			if (!cliente.isClosed()) {
				cliente.close();
			}
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage()); // no deberia suceder nunca
		}
	}
	
	private class ManejaInput implements Runnable {

		@Override
		public void run() {
			try {
				while (!listo) {
					Mensaje mensaje;
					try {
						while ((mensaje = (Mensaje) is.readObject()) != null) {
							if (mensaje.getMensaje().equals("/enCharla/")) {
								observador.mostrarUsuarioOcupado();
								cerrarConversacion();
							} else if (mensaje.getMensaje().equals("/cerrar/")){
								observador.mostrarCierreSesion();// entra mensaje de servidor, entonces MUESTRO
								cerrarConversacion();
								observador.cerrarInstancia();
							}  else if (mensaje.getMensaje().equals("/rechaza/")) {
								observador.mostrarUsuarioNoDisponible();
								cerrarConversacion();
							} else if (mensaje.getMensaje().equals("/aceptaInicioSesion/")) {
								observador.aceptaInicioSesion();
							} else {
								observador.mostrarMensajeTextArea(mensaje);
							}
						}
					} catch (ClassNotFoundException e) {
						System.out.println(e.getLocalizedMessage());
					}
				}
			} catch (IOException e) {
				cerrarConversacion();
			}
		}
	}

	public Socket getCliente() {
		return cliente;
	}
}