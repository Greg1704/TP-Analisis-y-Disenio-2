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
	
	private PrintWriter out;
	private BufferedReader in;
	
	private Server(int port, boolean listo) {
		this.port = port;
		this.listo = false;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			if (!listo) {
				cliente = server.accept();
				PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			}
		} catch (IOException e) {
			this.cerrarConversacion();
		}
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
	
	public void mandarMensaje(String mensaje) {
		out.println(mensaje);
	}
	/*
	public void recibirMensaje(String mensaje) {
		in.readLine();
	}
	*/
}
