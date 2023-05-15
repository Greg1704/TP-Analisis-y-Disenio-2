package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import controlador.ControladorServer;

public class Server implements Runnable, ConsultaEstado {

	private ServerSocket server;
	private Socket cliente;
	private ArrayList<ManejaConexiones> conexiones;
	private ArrayList<Chat> chats = new ArrayList();
	private int puertoServer;
	private boolean listo = false;
	private boolean modoEscucha;
	private ExecutorService pool;
	private ControladorServer cs;
	
	public Server(int port) {
		conexiones = new ArrayList(); // arrayList de 2 max por ahora
		this.puertoServer = port;
		this.modoEscucha = true;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(puertoServer);
			pool = Executors.newCachedThreadPool();
			while (!listo) {
				cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m); // agrego igual al cliente a la lista de conexiones del servidor, para avisarle
									// que su pedido de conexion es rechazado
				this.cs.cambioCantConectados(1);
				System.out.println("se conecto una persona");
				System.out.println(conexiones.size());
				pool.execute(m);
			}
		} catch (IOException e) {

		}
	}
	
	public int buscaIndicePropio(Mensaje mensaje) {
		int i = 0;
		while (i < conexiones.size() && conexiones.get(i).getPuerto() != mensaje.getPuertoEmisor()) {
			i++;
		}
		if (i < conexiones.size() && conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) {
			return i;
		} else
			return -1; // esto es imposible que pase
	}
	
	public int buscaIndiceSolicitado(int puerto) {
		int i = 0;
		while (i < conexiones.size() && conexiones.get(i).getPuerto() != puerto) {
			i++;
		}
		if (i < conexiones.size() && conexiones.get(i).getPuerto() == puerto) {
			return i;
		} else
			return -1;
	}
	
	public void nuevoChat(Socket cliente1, Socket cliente2) {
		Chat chat = new Chat(cliente1, cliente2);
		chats.add(chat);
	}
	
	public void agregarAlChat(Mensaje mensaje) {
		int i = 0;
		while (i < conexiones.size() && (chats.get(i).getPuerto1() != mensaje.getPuertoEmisor() || chats.get(i).getPuerto2() != mensaje.getPuertoEmisor())) {
			i++;
		}
		if (i < conexiones.size() && (chats.get(i).getPuerto1() == mensaje.getPuertoEmisor() || chats.get(i).getPuerto2() == mensaje.getPuertoEmisor())) {
			
		}
	}
	
	public void eliminarChat() {
		
	}
	
	@Override // metodo de interfaz
	public void consultaDisponibilidad(Mensaje mensaje, int puerto) {
		int indiceSolicitado = this.buscaIndiceSolicitado(puerto);
		int indicePropio = buscaIndicePropio(mensaje);
		if (indiceSolicitado != -1) { // se encontró a la persona
			if (!conexiones.get(indiceSolicitado).isHablando()) { // no está hablando
				Mensaje respuesta = new Mensaje("/solicitud/", mensaje.getIpEmisor(), mensaje.getPuertoEmisor());
				conexiones.get(indiceSolicitado).setHablando(true);
				conexiones.get(indiceSolicitado).setPuertoOtroUsuario(mensaje.getPuertoEmisor());
				conexiones.get(indicePropio).setHablando(true);
				conexiones.get(indicePropio).setPuertoOtroUsuario(puerto);
				try {
					conexiones.get(indiceSolicitado).mandarMensaje(respuesta);
				} catch (IOException e) {
					System.out.println("aca no deberia entrar nunca, mandar mensaje");
				}
			} else { // está hablando
				Mensaje respuesta = new Mensaje("/enCharla/", this.server.getInetAddress().getHostAddress(), this.puertoServer);
				try {
					conexiones.get(indicePropio).mandarMensaje(respuesta);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else { // no se encontró a la persona
			Mensaje respuesta = new Mensaje("/erroneo/", this.server.getInetAddress().getHostAddress(), this.puertoServer);
			try {
				conexiones.get(indicePropio).mandarMensaje(respuesta);
			} catch (IOException e) {
				System.out.println("error en erroneo");
			}
		}
	}

	public void cierraChat(Mensaje mensaje) {
		int i=0;
		int cerrados = 0;
		while (i < conexiones.size() && cerrados < 2) { 
			if (conexiones.get(i).getPuertoOtroUsuario() == mensaje.getPuertoEmisor() || (conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor() && 
					conexiones.get(i).getPuertoOtroUsuario() !=-10)) { // si esa persona estaba hablando con alguien
				conexiones.get(i).setPuertoOtroUsuario(0);
				conexiones.get(i).setHablando(false);
				conexiones.remove(i);
				cerrados++;
				this.cs.cambioCantConectados(-1);
			} else if (conexiones.get(i).getPuertoOtroUsuario() == -10 && conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) { // si no estaba hablando con nadie
				conexiones.get(i).setPuertoOtroUsuario(0);
				conexiones.get(i).setHablando(false);
				conexiones.remove(i);
				cerrados = 2;
				this.cs.cambioCantConectados(-1);
			} else {
				i++;
			}
		}
	}

	public void reparte(Mensaje mensaje) {
		for (ManejaConexiones cliente: conexiones) {
			if (cliente != null) {
				try {
					if (cliente.getPuerto() == mensaje.getPuertoEmisor() || cliente.puertoOtroUsuario == mensaje.getPuertoEmisor()) {
						cliente.mandarMensaje(mensaje);
					}
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage() + "mandando mensaje");
				}
			}
		}
	}
	
	public void setModoEscucha(boolean modoEscucha) {
		this.modoEscucha = modoEscucha;
	}
	
	public void cerrarServidor() {
		listo = true;
		modoEscucha = true;
		for (ManejaConexiones cliente : conexiones) {
			Mensaje mensaje = new Mensaje("/cerrar/", server.getInetAddress().getHostAddress(), this.puertoServer);
			try {
				cliente.mandarMensaje(mensaje);
				cliente.cerrarCliente();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("esto no deberia pasar nunca");
			}
		}
		int i = 0;
		while (i < conexiones.size()) {
			conexiones.remove(i);
		}
	}
	
	private class ManejaConexiones implements Runnable {
		private Socket cliente;
		private int puerto = 0;
		private boolean hablando;
		private int puertoOtroUsuario = -10;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		
		public ManejaConexiones(Socket cliente) {
			this.cliente = cliente;
		}
		
		@Override
		public void run() {
			try {
		        os = new ObjectOutputStream(cliente.getOutputStream());
				is = new ObjectInputStream(cliente.getInputStream());
				Mensaje mensaje;
				while ((mensaje = (Mensaje) is.readObject()) != null) {
					if (mensaje.getMensaje().equals("/cerrar/")) { 
						reparte(mensaje);
						cierraChat(mensaje);
					} else if (mensaje.getMensaje().contains("/puerto/")) {
						String[] cadena = mensaje.getMensaje().split(" ");
						this.puerto = Integer.parseInt(cadena[1]);
					} else if (mensaje.getMensaje().contains("/intentoConexion/")) {
						String[] cadena = mensaje.getMensaje().split(" ");
						int puertoAConectar = Integer.parseInt(cadena[1]);
						consultaDisponibilidad(mensaje, puertoAConectar);
					} else if (mensaje.getMensaje().contains("/aceptar/")) {
						reparte(mensaje);
					} else if (mensaje.getMensaje().contains("/rechazar/")) {
						cierraChat(mensaje);
					} else {
						reparte(mensaje);
						//chat.agregarMensajes(mensaje);
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getLocalizedMessage() + "run de server manejaconex");
			}
		}

		public void mandarMensaje(Mensaje mensaje) throws IOException {
			os.writeObject(mensaje);
		}
		
		public void cerrarCliente() {
			try {
				is.close();
				os.close();
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
				is.close();
				os.close();
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
		
		public int getPuerto() {
			return this.puerto;
		}

		public boolean isHablando() {
			return hablando;
		}

		public int getPuertoOtroUsuario() {
			return puertoOtroUsuario;
		}

		public Socket getCliente() {
			return cliente;
		}

		public void setHablando(boolean hablando) {
			this.hablando = hablando;
		}

		public void setPuertoOtroUsuario(int puertoOtroUsuario) {
			this.puertoOtroUsuario = puertoOtroUsuario;
		}
		
	}
	
	public String getIpSolicitante() {
		InetAddress aux = this.conexiones.get(0).cliente.getLocalAddress();
		return (aux.toString());
	}

	public void setCs(ControladorServer cs) {
		this.cs = cs;
	}
	
 }
