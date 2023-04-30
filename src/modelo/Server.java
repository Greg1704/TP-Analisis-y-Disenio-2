package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import controlador.IObservador;

public class Server implements Runnable {

	private ServerSocket server;
	private ArrayList<ManejaConexiones> conexiones;
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
				Socket cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m); // agrego igual al cliente a la lista de conexiones del servidor, para avisarle que su pedido de conexion es rechazado
				pool.execute(m);
				if (this.modoEscucha == true) {
					if (conexiones.size() < 2) { // redundante xq se verifica bien el modo escucha
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
						cerrarServidor();
					} else {
						reparte(mensaje);
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
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
				// try x obligacion, no deberia pasar
			}
		}
		
		public void cerrarServidor() {
			try {
				listo = true;
				modoEscucha = true;
				in.close();
				out.close();
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
