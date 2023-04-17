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

import controlador.Controlador;
import controlador.Observador;

public class Server implements Runnable {
	
	private ServerSocket server;
	private ArrayList<ManejaConexiones> conexiones;
	private int port;
	private boolean listo = false;
	private List<Observador> observadores = new ArrayList<>();
	//private Controlador controlador;
	
	public Server(int port) {
		conexiones = new ArrayList();
		this.port = port;
	}
	/*
	public void setControlador(Controlador c) {
		this.controlador = c;
	}
	*/
	
	@Override
	public void run() {
		try {
			// InetAddress localHost = InetAddress.getLocalHost();
			// System.out.println(localHost.getHostAddress());
			server = new ServerSocket(port);
			while (!listo) {
				Socket cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m);
				System.out.println("se conecta");
				if (conexiones.size() < 2) {
					observadores.get(0).update(observadores);
				}
			}
		} catch (IOException e) {
			//cerrarServidor();
		}
	}
	
	public void reparte(String mensaje) {
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
		} catch (IOException e) {
			//
		}
	}
	
	/*
	public void cerrarConversacion() {
		m.cerrarConversacion();
	}
	*/
	
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
					reparte(mensaje);
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
	

	/*
	public void recibirMensaje(String mensaje) {
		in.readLine();
	}
	*/
}
