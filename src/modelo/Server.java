package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
	private List<Observador> observadores = new ArrayList<>();
	private ExecutorService pool;
	
	public Server(int port) {
		conexiones = new ArrayList();
		this.port = port;
		this.modoEscucha = true;
	}
	
	@Override
	public void run() {
		try {
			// InetAddress localHost = InetAddress.getLocalHost();
			// System.out.println(localHost.getHostAddress());
			server = new ServerSocket(port);
			pool = Executors.newCachedThreadPool();
			while (!listo) {
				Socket cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m);
				pool.execute(m); // agrego igual al cliente a la lista de conexiones del servidor, para avisarle que su pedido de conexion es rechazado
				System.out.println("se añade cliente al servidor");
				if (this.modoEscucha == true) {
					System.out.println("se conecta");
					System.out.println(conexiones.size());
					if (conexiones.size() < 2) { // SI ALGUIEN MAS SE QUIERE CONECTAR AL SERVIDOR , AVISAR Q YA HAY DOS
													// PERSONAS HABLANDO. TRATAR ESTE ASUNTO Q CAPAZ LLEVA UN POCO DE
													// TIEMPO
						observadores.get(0).update(observadores);
					} 
				} else {
					PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
					out.println("/enCharla/");
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
	
	public class ManejaConexiones implements Runnable {
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
		
		public void ponerModoEscuchaFalse(String mensaje) {
			out.println(mensaje);
		}

	}
	
	public void addObserver(Observador channel) {
        this.observadores.add(channel);
    }

    public void removeObserver(Observador channel) {
        this.observadores.remove(channel);
    }
    
    public void setModoEscucha(boolean modo) {
    	this.modoEscucha = modo;
    }
    

	public boolean isModoEscucha() {
		return modoEscucha;
	}
	

	public List<Observador> getObservadores() {
		return observadores;
	}
	
	public String getIpSolicitante() {
		InetAddress aux = this.conexiones.get(0).cliente.getLocalAddress();
		return (aux.toString());
	}
	
	public void setListo() {
		listo = false;
	}
 }
