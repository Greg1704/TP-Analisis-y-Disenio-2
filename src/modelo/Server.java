package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server implements Runnable {
	
	private ServerSocket server;
	private Socket cliente;
	private int port;
	private boolean listo;
	private manejaMensajes m;
	
	
	private Server(int port, boolean listo) {
		this.port = port;
		this.listo = false;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			while (!listo) {
				cliente = server.accept();
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
	

	/*
	public void recibirMensaje(String mensaje) {
		in.readLine();
	}
	*/
}
