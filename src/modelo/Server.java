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
	private Socket cliente;
	private int port;
	private boolean listo = false;
	private manejaMensajes m;
	private List<Observador> observadores = new ArrayList<>();
	//private Controlador controlador;
	
	public Server(int port) {
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
		//	InetAddress localHost = InetAddress.getLocalHost();
		//	System.out.println(localHost.getHostAddress());
			server = new ServerSocket(port);
			while (!listo) {
					observadores.get(0).update(observadores);
					cliente = server.accept();
					System.out.println("se conecta");
					m = new manejaMensajes();
			}
		} catch (IOException e) {
			cerrarConversacion();
		}
	}
	
	
	
	public void cerrarConversacion() {
		m.cerrarConversacion();
	}
	
	public class manejaMensajes implements Runnable {
		private BufferedReader in;
		private PrintWriter out;
		
		@Override
		public void run() {
			try {
				out = new PrintWriter(cliente.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
				String mensaje;
				while ((mensaje = in.readLine()) != null) {
					mandarMensaje(mensaje);
				}
			} catch (IOException e) {
				// 
			}
		}
		
		
		public void mandarMensaje(String mensaje) {
			out.println(mensaje);
		}
		
		public void cerrarConversacion() {
			try {
				listo = true;
				in.close();
				out.close();
				if (!server.isClosed()) {
					server.close();
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
