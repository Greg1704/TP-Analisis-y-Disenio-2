package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controlador.Controlador;
import controlador.Observador;

public class Server implements Runnable {

	private ServerSocket server;
	private ArrayList<ManejaConexiones> conexiones;
	private int port;
	private boolean listo = false;
	private boolean modoEscucha;
	private Observador observador;
	private ExecutorService pool;
	
	public Server(int port, Observador observador) {
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
				Socket cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m); // agrego igual al cliente a la lista de conexiones del servidor, para avisarle que su pedido de conexion es rechazado
				pool.execute(m);
				System.out.println("se añade cliente al servidor");
				if (this.modoEscucha == true) {
					System.out.println("se conecta");
					System.out.println(conexiones.size());
					if (conexiones.size() < 2) { 
						observador.mostrarIntentoDeConexion(); // hay menos de 2 personas charlando entonces popea ventana de intento de conexión
					} 
				} else {
					PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
					out.println("/enCharla/"); // le mando al que se quiso conectar que no se puede conectar con este servidor
					int ultimoElemento = conexiones.size() - 1;
					conexiones.remove(ultimoElemento);
					cliente.close();
				}
			}
		} catch (IOException e) {
			
		}
	}

	public void reparte(String mensaje) {
		System.out.println("el tamaño de conexiones o sea cant de clientes es " + conexiones.size());
		for (ManejaConexiones cliente: conexiones) {
			if (cliente != null) {
				cliente.mandarMensaje(mensaje);
			}
		}
	}
	
	public void rechaza() {
		reparte("/rechaza/");
		conexiones.get(0).cerrarCliente();
		conexiones.remove(0);
	}
	
	
	
	public void setModoEscucha(boolean modoEscucha) {
		this.modoEscucha = modoEscucha;
	}
	
	private class ManejaConexiones implements Runnable {
		private Socket cliente;
		private BufferedReader in;
		private PrintWriter out;
		
		public ManejaConexiones(Socket cliente) {
			this.cliente = cliente;
		}
		
		@Override
		public void run() {
			try {
				out = new PrintWriter(cliente.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
				String mensaje;
				while ((mensaje = in.readLine()) != null) {
					if (mensaje.equals("/modoEscuchaFalse/")) {
						modoEscucha = false;
					} else if (mensaje.equals("/cerrar/")) {
						reparte(mensaje);
						System.out.println("se ejecuta cerrar servidor");
						cerrarServidor();
					} else {
						reparte(mensaje);
					}
				}
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
			}
		}

		public void mandarMensaje(String mensaje) {
			out.println(mensaje);
		}
		
		public void cerrarCliente() {
			try {
				in.close();
				out.close();
				if (!cliente.isClosed()) {
					cliente.close();
				}
			} catch (IOException e) {
				System.out.println("ke carajo"); // esto no deberia pasar
			}
		}
		
		public void cerrarServidor() {
			try {
				listo = true;
				modoEscucha = true;
				in.close();
				out.close();
			//	if (!server.isClosed()) {
				//	server.close();
				//}
				for (ManejaConexiones cliente : conexiones) {
					cliente.cerrarCliente();
				}
				int i = 0;
				while (i < conexiones.size()) {
					conexiones.remove(i);
				}
			//	pool.shutdown();
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
