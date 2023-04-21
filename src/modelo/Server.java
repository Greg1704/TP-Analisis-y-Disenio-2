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
	private ArrayList<Cliente> clientes;
	private int port;
	private boolean listo = false;
	private boolean modoEscucha;
	private List<Observador> observadores = new ArrayList<>();
	private ExecutorService pool;
	
	public Server(int port) {
		conexiones = new ArrayList();
		clientes = new ArrayList();
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
				if (this.modoEscucha == true) {
					
					ManejaConexiones m = new ManejaConexiones(cliente);
					conexiones.add(m);
					pool.execute(m);
					System.out.println("se conecta");
					System.out.println(conexiones.size());
					if (conexiones.size() < 2) { // SI ALGUIEN MAS SE QUIERE CONECTAR AL SERVIDOR , AVISAR Q YA HAY DOS
													// PERSONAS HABLANDO. TRATAR ESTE ASUNTO Q CAPAZ LLEVA UN POCO DE
													// TIEMPO
						observadores.get(0).update(observadores);
					}
				} else {
					observadores.get(0).muestraConexionInvalida();
					cliente.close();
				}
			}
		} catch (IOException e) {
			// cerrarServidor();
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
	
	public void cerrarServidor() {
		try {
		this.listo = true;
		if (!server.isClosed()) {
			server.close();
		}
		for (ManejaConexiones cliente: conexiones) {
			cliente.cerrarCliente();
		}
		this.cambiaModoEscucha(true);
		} catch (IOException e) {
			//
		}
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
					reparte(mensaje);;
				}
			} catch (IOException e) {
				cerrarCliente();
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
    
    public void cambiaModoEscucha(boolean modo) {
    	System.out.println(clientes.size());
    	for (Cliente cliente: clientes) {
    		cliente.getServer().setModoEscucha(modo);
    		System.out.println(cliente.getServer().isModoEscucha());
    	}
    }

	public boolean isModoEscucha() {
		return modoEscucha;
	}
	
	
	public ArrayList<Cliente> getClientes() {
		return clientes;
	}
	
	public void agregaCliente(Cliente cliente) {
		this.clientes.add(cliente);
	}

	public List<Observador> getObservadores() {
		return observadores;
	}
	
	public String getIpSolicitante() {
		InetAddress aux = this.conexiones.get(0).cliente.getLocalAddress();
		return (aux.toString());
	}
	
}
