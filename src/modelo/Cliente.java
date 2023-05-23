package modelo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import controlador.IComunicacion;

import java.net.InetAddress;
import java.net.ServerSocket;

public class Cliente implements IComunicacion {

	private int puertoCliente;
	private int puertoAConectar;
	private String ipAConectar, ipLocal;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private boolean listo = false;
	private IComunicacion observador;
	private String claveEncriptacion;
	
	public Cliente(String ipAConectar, int puerto, int puertoCliente, IComunicacion observador) {
		this.puertoAConectar = puerto;
		this.puertoCliente = puertoCliente;
		this.ipAConectar = ipAConectar;
		this.observador = observador;
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			this.ipLocal = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//conecta();
		maneja();
	}
    
	public String getIpLocal() {
		return ipLocal;
	}
	
	public void conecta() {
		new Thread() {
			public void run() {
				/*
				try {
				
					cliente = new Socket(ipAConectar, puertoAConectar);
					os = new ObjectOutputStream(cliente.getOutputStream());
					is = new ObjectInputStream(cliente.getInputStream());
					ManejaInput m = new ManejaInput();

				} catch (IOException e) {
					observador.mostrarConexxionErroneaServer();
				}
				*/
			}
			
		}.start();
	}
	
	@Override
	public void mandarMensaje(Mensaje mensaje) {
		try {
			Socket cliente = new Socket(ipAConectar, puertoAConectar);
			os = new ObjectOutputStream(cliente.getOutputStream());
			os.writeObject(mensaje);
			cliente.close();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
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
								observador.mostrarIntentoDeConexion(mensaje.getIpEmisor(), mensaje.getPuertoEmisor()); // aca
																														// deberia
																														// mostrar
																														// con
																														// el
																														// campo
																														// de
																														// ip
																														// del
																														// emisor
																														// que
																														// contiene
																														// el
																														// mensaje
							} else if (mensaje.getMensaje().equals("/sinDisponibilidad/")) {
								observador.mostrarPuertoEnUso();
							} else if (mensaje.getMensaje().equals("/erroneo/")) {
								observador.mostrarConexionErronea();
							} else {
								String desencriptado = Encriptacion.desencriptadoMensaje(mensaje.getMensaje(),
										claveEncriptacion);
								mensaje.setMensaje(desencriptado);
								observador.mostrarMensajeTextArea(mensaje);
							}
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					System.out.println(e.getLocalizedMessage());
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