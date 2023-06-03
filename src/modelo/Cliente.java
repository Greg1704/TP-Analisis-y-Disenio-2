package modelo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import controlador.ControladorCliente;
import controlador.interfaces.IComunicacion;
import modelo.chat.Mensaje;

import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Cliente implements IComunicacion {

	private String nombre;
	private int puertoCliente;
	private int puertoAConectar;
	private String ipAConectar, ipLocal;
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
		this.maneja();
	}
    
	public String getIpLocal() {
		return ipLocal;
	}
	
	@Override
	public void mandarMensaje(Mensaje mensaje) {
			Socket cliente;
			try {
				cliente = new Socket(ipAConectar, puertoAConectar);
				ObjectOutputStream os1 = new ObjectOutputStream(cliente.getOutputStream());
				os1.writeObject(mensaje);
				os1.flush();
				cliente.close();
			} catch (UnknownHostException e) {
				observador.mostrarMensajeNoEnviado(mensaje);
			} catch (IOException e) {
				observador.mostrarMensajeNoEnviado(mensaje);
			}
		}

	public void maneja() {
		new Thread() {
			public void run() {
				try {
					ServerSocket socketEsperaMensajes = new ServerSocket(puertoCliente);
					while (true) {
						Socket mensajeServidor = socketEsperaMensajes.accept();
						ObjectInputStream is = new ObjectInputStream(mensajeServidor.getInputStream());
						Mensaje mensaje = (Mensaje) is.readObject();
						if (mensaje != null) {
							if (mensaje.getMensaje().equals("/enCharla/")) {
								observador.mostrarUsuarioOcupado();
							} else if (mensaje.getMensaje().equals("/cerrar/")) {
								observador.mostrarCierreSesion(); 
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
								ControladorCliente c = ControladorCliente.getInstancia();
								c.actualizarListaConectados(mensaje.getConexiones());
							} else {
								String desencriptado = Encriptacion.desencriptadoMensaje(mensaje.getMensaje(),
										claveEncriptacion);
								mensaje.setMensaje(desencriptado);
								observador.mostrarMensajeTextArea(mensaje);
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

	@Override
	public void mostrarMensajeNoEnviado(Mensaje mensaje) {
		// TODO Auto-generated method stub
		
	}	
	
}