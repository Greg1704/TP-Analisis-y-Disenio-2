package modelo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;

public class Cliente implements Runnable {
	
	private Socket cliente;
	private int puertoAConectar;
	private String ipAConectar;
	private BufferedReader in;
	private PrintWriter out;
	private boolean listo = false;
	
	public Cliente(String ipAConectar, int puerto) {
		this.puertoAConectar = puerto;
		this.ipAConectar = ipAConectar;
		this.run();
	}
	
	@Override
	public void run() {

		try {
			//InetAddress localHost = InetAddress.getLocalHost(); esto iria en servidor
		//	cliente = new Socket(localHost.(), puertoAConectar);
			cliente = new Socket(ipAConectar, puertoAConectar);
			out = new PrintWriter(cliente.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			
			
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			//cerrarConversacion();
		}
		

	}
	
	public void mandarMensaje(String mensaje) {
		out.println(mensaje);
	}
	
	public void cerrarConversacion() {
		listo = true;
		try {
			in.close();
			out.close();
			if (!cliente.isClosed()) {
				cliente.close();
			}
		} catch (IOException e) {
			// no tendria que entrar aca ya q es por ventana la interaccion
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	InputUsuario inUsuario = new InputUsuario();
	//	Thread hiloInput = new Thread(inUsuario);
	//	hiloInput.start();
		
	//	String mensajeEntrante;
	//	while ((mensajeEntrante = in.readLine()) != null) {
		//	System.out.println(mensajeEntrante);
			// aca hacer algo para mostrar en ventana de cliente
	//	}
	
	
	/*
	// TODO ESTO NO SE USARIA
	
	public class InputUsuario implements Runnable {

		private BufferedReader input;
		
		@Override
		public void run() {
			try {
			input = new BufferedReader(new InputStreamReader(System.in));
			while (!listo) {
				String mensaje = input.readLine(); // esto hay q cerrarlo
				out.println(mensaje);
			}
			input.close(); // esto habria q fijarse , porq quiero ver si se cierra cuando sale del while
			} catch (IOException e) {
				cerrarConversacion();
			}
			
		}
		/* 
		public void cierraLocalInput() {
			input.close();
		}
		*/
	}
	
}
/*
