package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import controlador.ControladorServer;
import controlador.IObservador;

public class Server implements Runnable, ConsultaEstado {

	private ServerSocket server;
	private Socket cliente;
	private ArrayList<ManejaConexiones> conexiones;
	private Chat chat = new Chat();
	private int port;
	private boolean listo = false;
	private boolean modoEscucha;
	private IObservador observador;
	private ExecutorService pool;
	private ControladorServer cs;
	
	public Server(int port, IObservador observador) {
		conexiones = new ArrayList(); // arrayList de 2 max por ahora
		this.observador = observador;
		this.port = port;
		this.modoEscucha = true;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			pool = Executors.newCachedThreadPool();
			while (!listo) {
				cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m); // agrego igual al cliente a la lista de conexiones del servidor, para avisarle
									// que su pedido de conexion es rechazado
				this.cs.cambioCantConectados(1);
				System.out.println("se conecto una persona");
				System.out.println(conexiones.size());
				pool.execute(m);
			}
		} catch (IOException e) {

		}
	}
	
	@Override
	public void consultaDisponibilidad(Mensaje mensaje, int puerto) {
		int i = 0;
		boolean encontrado = false, encontrado2 = false;
		while (i < conexiones.size() && encontrado == false) { // busco hasta encontrar la persona a la que le solicito la conexion
			if (conexiones.get(i).getPuerto() == puerto && conexiones.get(i).isHablando() == false) {
				encontrado = true;
			} else {
				i++;
			}
		}
		if (encontrado == true) {
			Mensaje mensaje2 = new Mensaje("/solicitud/", mensaje.getIpEmisor(), mensaje.getPuertoEmisor());
			conexiones.get(i).setHablando(true);
			conexiones.get(i).setPuertoOtroUsuario(mensaje.getPuertoEmisor());
			System.out.println("andara esto?");
			try {
				conexiones.get(i).mandarMensaje(mensaje2);
			} catch (IOException e) {
				System.out.println("hay error al mandar mensaje");
				System.out.println(e.getLocalizedMessage());
			}
			i = 0;
			while (i < conexiones.size() && encontrado2 == false) { // me busco a mi mismo asi ya no me puede contactar ninguna otra persona ya que estoy esperando respuesta
				if (conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) {
					encontrado2 = true;
				} else {
					i++;
				}
			}
			if (encontrado2 == true) {
				conexiones.get(i).setHablando(true);
				conexiones.get(i).setPuertoOtroUsuario(puerto);
			}
		} else { // no se encontró o estaba charlando
			
		}
	}
	
	public void cierraChat(Mensaje mensaje) {
		int i=0;
		int cerrados = 0;
		while (i < conexiones.size() && cerrados < 2) {
			if (conexiones.get(i).getPuertoOtroUsuario() == mensaje.getPuertoEmisor() || conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) {
				conexiones.get(i).setPuertoOtroUsuario(0);
				conexiones.get(i).setHablando(false);
				cerrados++;
			} else {
				i++;
			}
		}
	}

	public void reparte(Mensaje mensaje) {
		for (ManejaConexiones cliente: conexiones) {
			if (cliente != null) {
				try {
					if (cliente.puerto == mensaje.getPuertoEmisor() || cliente.puertoOtroUsuario == mensaje.getPuertoEmisor()) {
						cliente.mandarMensaje(mensaje);
					}
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage() + "mandando mensaje");
				}
			}
		}
	}
	
	public void setModoEscucha(boolean modoEscucha) {
		this.modoEscucha = modoEscucha;
	}
	
	
	
	private class ManejaConexiones implements Runnable {
		private Socket cliente;
		private int puerto = 0;
		private boolean hablando;
		private int puertoOtroUsuario;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		
		public ManejaConexiones(Socket cliente) {
			this.cliente = cliente;
		}
		
		@Override
		public void run() {
			try {
		        os = new ObjectOutputStream(cliente.getOutputStream());
				is = new ObjectInputStream(cliente.getInputStream());
				Mensaje mensaje;
				while ((mensaje = (Mensaje) is.readObject()) != null) {
					if (mensaje.getMensaje().equals("/cerrar/")) { 
						reparte(mensaje);
						cierraChat(mensaje);
					} else if (mensaje.getMensaje().contains("/puerto/")) {
						String[] cadena = mensaje.getMensaje().split(" ");
						this.puerto = Integer.parseInt(cadena[1]);
					} else if (mensaje.getMensaje().contains("/intentoConexion/")) {
						String[] cadena = mensaje.getMensaje().split(" ");
						int puertoAConectar = Integer.parseInt(cadena[1]);
						consultaDisponibilidad(mensaje, puertoAConectar);
					} else if (mensaje.getMensaje().contains("/aceptar/")) {
						reparte(mensaje);
					} else if (mensaje.getMensaje().contains("/rechazar/")) {
						cierraChat(mensaje);
					} else {
						reparte(mensaje);
						chat.agregarMensajes(mensaje);
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getLocalizedMessage() + "run de server manejaconex");
			}
		}

		public void mandarMensaje(Mensaje mensaje) throws IOException {
			os.writeObject(mensaje);
		}
		
		public void cerrarCliente() {
			try {
				is.close();
				os.close();
				if (!cliente.isClosed()) {
					cliente.close();
				}
			} catch (IOException e) {
				// try x obligacion, no deberia pasar
			}
		}
		
		public void cerrarServidor() {
			try {
				listo = true;
				modoEscucha = true;
				is.close();
				os.close();
				for (ManejaConexiones cliente : conexiones) {
					cliente.cerrarCliente();
				}
				int i = 0;
				while (i < conexiones.size()) {
					conexiones.remove(i);
				}
			} catch (IOException e) {
				//
			}
		}
		
		public int getPuerto() {
			return this.puerto;
		}

		public boolean isHablando() {
			return hablando;
		}

		public int getPuertoOtroUsuario() {
			return puertoOtroUsuario;
		}

		public Socket getCliente() {
			return cliente;
		}

		public void setHablando(boolean hablando) {
			this.hablando = hablando;
		}

		public void setPuertoOtroUsuario(int puertoOtroUsuario) {
			this.puertoOtroUsuario = puertoOtroUsuario;
		}
		
	}
	
	public String getIpSolicitante() {
		InetAddress aux = this.conexiones.get(0).cliente.getLocalAddress();
		return (aux.toString());
	}

	public void setCs(ControladorServer cs) {
		this.cs = cs;
	}
	
 }
