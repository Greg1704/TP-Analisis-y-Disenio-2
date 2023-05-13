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
				pool.execute(m);
				consultoDisponibilidad();
			}
		} catch (IOException e) {

		}
	}
	
	@Override
	public void consultoDisponibilidad() {
		if (this.modoEscucha == true) {
			if (conexiones.size() < 2) { // redundante xq se verifica bien el modo escucha
				observador.mostrarIntentoDeConexion(); // hay menos de 2 personas charlando entonces popea ventana de
														// intento de conexión
			}
		} else {
			try {
				Mensaje mensaje = new Mensaje("/enCharla/", "100.000.000.000", "1500"); // no importa la info acá, va
																						// hardcodeada. solo importa q
																						// no se puede conectar
				ObjectOutputStream os = new ObjectOutputStream(cliente.getOutputStream());
				os.writeObject(mensaje);
				int ultimoElemento = conexiones.size() - 1;
				conexiones.remove(ultimoElemento);
				cliente.close();
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
			}
		}

	}

	public void reparte(Mensaje mensaje) {
		for (ManejaConexiones cliente: conexiones) {
			if (cliente != null) {
				try {
					cliente.mandarMensaje(mensaje);
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage() + "mandando mensaje");
				}
			}
		}
	}
	
	public void rechaza() {
		Mensaje mensaje = new Mensaje("/rechaza/", "100.000.000.000", "1500");
			reparte(mensaje);
		conexiones.get(0).cerrarCliente();
		conexiones.remove(0);
	}
	
	public void setModoEscucha(boolean modoEscucha) {
		this.modoEscucha = modoEscucha;
	}
	
	private class ManejaConexiones implements Runnable {
		private Socket cliente;
		private int puerto = 0;
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
					if (mensaje.getMensaje().equals("/modoEscuchaFalse/")) {
						modoEscucha = false;
					} else if (mensaje.getMensaje().equals("/cerrar/")) {
						reparte(mensaje);
						cerrarServidor();
					} else if (mensaje.getMensaje().contains("/puerto/")) {
						this.puerto = 0; // el puerto que me pasen ahora
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
	}
	
	public String getIpSolicitante() {
		InetAddress aux = this.conexiones.get(0).cliente.getLocalAddress();
		return (aux.toString());
	}
	
 }
