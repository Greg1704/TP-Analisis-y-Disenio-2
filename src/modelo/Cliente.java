package modelo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import controlador.Controlador;
import controlador.IComunicacion;

import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Cliente implements IComunicacion {

	private String nombre;
	private int puertoCliente;
	private int puertoAConectar;
	private String ipAConectar, ipLocal;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private boolean listo = false;
	private IComunicacion observador;
	private String claveEncriptacion;
	
	public Cliente(String ipAConectar, int puerto, int puertoCliente, String nombre, IComunicacion observador) {
		this.puertoAConectar = puerto;
		this.puertoCliente = puertoCliente;
		this.ipAConectar = ipAConectar;
		this.observador = observador;
		this.nombre = nombre;
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			this.ipLocal = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//conecta();
		this.maneja();
	}
    
	public String getIpLocal() {
		return ipLocal;
	}
	
	@Override
	public void mandarMensaje(Mensaje mensaje) {
		try {
			Socket cliente = new Socket(ipAConectar, puertoAConectar);
			ObjectOutputStream os1 = new ObjectOutputStream(cliente.getOutputStream());
			os1.writeObject(mensaje);
			os1.flush();
			cliente.close();
		} catch (IOException e) {
		//	System.out.println(e.getLocalizedMessage());
		} // SE MANDA DIRECTAMENTE A SERVIDOR. SE VE EN LA VENTANA EL MENSAJE ENVIADO YA QUE SE RECIBE DEL SERVIDOR LUEGO (EN LA PARTE DONDE SE INVOCA REPARTE())
	}

	public void maneja() {
		new Thread() {
			public void run() {
				try {
					ServerSocket socketEsperaMensajes = new ServerSocket(puertoCliente);
					while (true) {
						Socket mensajeServidor = socketEsperaMensajes.accept();
						is = new ObjectInputStream(mensajeServidor.getInputStream());
						Mensaje mensaje = (Mensaje) is.readObject();
						if (mensaje != null) {
							if (mensaje.getMensaje().equals("/enCharla/")) {
								observador.mostrarUsuarioOcupado();
							} else if (mensaje.getMensaje().equals("/cerrar/")) {
								observador.mostrarCierreSesion();// entra mensaje de servidor, entonces MUESTRO
								// cerrarConversacion();
								observador.cerrarInstancia();
							} else if (mensaje.getMensaje().equals("/rechazar/")) {
								observador.mostrarUsuarioNoDisponible();
							} else if (mensaje.getMensaje().equals("/aceptar/")) {
								observador.aceptaInicioSesion();
							} else if (mensaje.getMensaje().contains("/intentoConexion/")) {
								String[] arraySplit = mensaje.getMensaje().split("/");
								setClaveEncriptacion(arraySplit[3]);
								observador.mostrarIntentoDeConexion(mensaje.getIpEmisor(), mensaje.getPuertoEmisor()); 
							} else if (mensaje.getMensaje().equals("/erroneo/")) {
								observador.mostrarConexionErronea();
							} else if (mensaje.getConexiones() != null) {
								Controlador c = Controlador.getInstancia();
							//	System.out.println("llega al menos " + mensaje.getConexiones().size());
								c.actualizarListaConectados(mensaje.getConexiones());
							} else {
								String desencriptado = Encriptacion.desencriptadoMensaje(mensaje.getMensaje(),
										claveEncriptacion);
								mensaje.setMensaje(desencriptado);
								observador.mostrarMensajeTextArea(mensaje);
							//	System.out.println(mensaje.getMensaje());
							//	System.out.println("LLEGA MENSAJE EN PERFECTAS CONDICIONES");
							}
						}
					}
				} catch (BindException e) {
					observador.mostrarPuertoEnUso();
				} catch (IOException | ClassNotFoundException e) {
				//	System.out.println(e.getLocalizedMessage());
				}
			}
		}.start();
	}

	/*
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
	*/ // esto capaz se puede resolver mandando un mensaje

	public void setClaveEncriptacion(String claveEncriptacion) {
		this.claveEncriptacion = claveEncriptacion;
	}

	public String getClaveEncriptacion() {
		return claveEncriptacion;
	}

	@Override
	public void mostrarIntentoDeConexion(String ip, int puerto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarMensajeTextArea(Mensaje mensaje) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarUsuarioOcupado() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarUsuarioNoDisponible() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarCierreSesion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarConexionErronea() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarConexxionErroneaServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarPuertoErroneo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarPuertoEnUso() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cerrarInstancia() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aceptaInicioSesion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mandarMensaje(Mensaje mensaje, int puerto) {
		// TODO Auto-generated method stub
		
	}	
	
}