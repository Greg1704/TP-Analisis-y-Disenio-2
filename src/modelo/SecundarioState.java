package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import modelo.Server.ManejaConexiones;

public class SecundarioState extends State {
	
	private static int puertoSecundario = 12000;

	public SecundarioState(Server server) {
		super(server);
		System.out.println("sv modo secundario");
	}

	@Override
	public void init() {
		Socket socket;
		try {
			socket = new Socket("localhost", Server.getPuertoTransferenciaDatos());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			
			ArrayList<ManejaConexiones> conexiones = (ArrayList<ManejaConexiones>) in.readObject();
			this.server.setConexiones(conexiones);
			ArrayList<Chat> chats = (ArrayList<Chat>) in.readObject();
			this.server.setChats(chats);
			
			out.close();
			socket.close(); // pasar a la vista estado del server
			this.espera();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	@Override
	public void cambiaEstado() {
		this.server.setEstado(new PrimarioState(this.server));
		this.server.init();
	}

	@Override
	public String getEstado() {
		return "Secundario"; // para la vista
	}
	
	public void espera() {
		new Thread() {
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(puertoSecundario);
					while (true) {
						Socket socket = serverSocket.accept();
						cambiaEstado(); // OJO
						socket.close();
					}
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
			}
		}.start();
		
		new Thread() {
			public void run() {
				try {
					// ACA VOY A IR AGREGANDO LAS COSAS
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
			}
		}.start();
	}
	
}
