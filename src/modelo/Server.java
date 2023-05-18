package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import controlador.ControladorServer;
import controlador.IComunicacion;
import controlador.IConectados;

public class Server implements Runnable, IConsultaEstado,IConectados, IChat {

	private ServerSocket server;
	private Socket cliente;
	private ArrayList<ManejaConexiones> conexiones;
	private ArrayList<Chat> chats = new ArrayList<Chat>();
	private int puertoServer;
	private boolean listo = false;
	private ExecutorService pool;
	private IConectados cs;
	
	public Server(int port,IConectados cs) {
		conexiones = new ArrayList<ManejaConexiones>(); 
		this.puertoServer = port;
		this.cs = cs;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(puertoServer);
			pool = Executors.newCachedThreadPool();
			while (!listo) {
				cliente = server.accept();
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m);
				this.cambioCantConectados(conexiones.size());
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
			return -1; // no se encontro
	}
	
	@Override
	public void nuevoChat(String ip1, int puerto1, int puerto2) {
		Chat chat = new Chat(ip1, puerto1, puerto2);
		chats.add(chat);
	}
	
	@Override
	public void agregarAlChat(Mensaje mensaje) {
		int i = 0;
		while (i < chats.size() && (chats.get(i).getPuerto1() != mensaje.getPuertoEmisor() || chats.get(i).getPuerto2() != mensaje.getPuertoEmisor())) {
			i++;
		}
		if (i < chats.size() && (chats.get(i).getPuerto1() == mensaje.getPuertoEmisor() || chats.get(i).getPuerto2() == mensaje.getPuertoEmisor())) {
			chats.get(i).agregarMensajes(mensaje);
		}
	}
	
	@Override
	public void eliminarChat(Mensaje mensaje) {
		int i = 0;
		while (i < chats.size() && (chats.get(i).getPuerto1() != mensaje.getPuertoEmisor() && chats.get(i).getPuerto2() != mensaje.getPuertoEmisor())) {
			i++;
		}
		if (i < conexiones.size() && (chats.get(i).getPuerto1() == mensaje.getPuertoEmisor() || chats.get(i).getPuerto2() == mensaje.getPuertoEmisor())) {
			chats.remove(i);
		}
	}
	
	@Override
	public void desconectaChat(Mensaje mensaje) {
		int i=0;
		int cerrados = 0;
		while (i < conexiones.size() && cerrados < 2) { 
			if (conexiones.get(i).getPuertoOtroUsuario() == mensaje.getPuertoEmisor() || (conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor() && 
					conexiones.get(i).getPuertoOtroUsuario() !=-10)) { // si esa persona estaba hablando con alguien
				conexiones.get(i).setPuertoOtroUsuario(-10);
				conexiones.get(i).setHablando(false);
				cerrados++;
				if (mensaje.getMensaje().contains("/cerrar/")) {
					conexiones.remove(i);
					this.cambioCantConectados(conexiones.size());
				}
			} else if (conexiones.get(i).getPuertoOtroUsuario() == -10 && conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) { // si no estaba hablando con nadie
				conexiones.get(i).setPuertoOtroUsuario(-10);
				conexiones.get(i).setHablando(false);
				cerrados = 2;
				if (mensaje.getMensaje().contains("/cerrar/")) {
					conexiones.remove(i);
					this.cambioCantConectados(conexiones.size());
				}
			} else {
				i++;
			}
		}
	}
	
	@Override // metodo de interfaz
	public void consultaDisponibilidad(Mensaje mensaje, int puerto) {
		int indiceSolicitado = this.buscaIndiceSolicitado(puerto);
		int indicePropio = buscaIndicePropio(mensaje);
		if (indiceSolicitado != -1) { // se encontró a la persona
			if (!conexiones.get(indiceSolicitado).isHablando()) { // no está hablando
				conexiones.get(indiceSolicitado).setHablando(true);
				conexiones.get(indiceSolicitado).setPuertoOtroUsuario(mensaje.getPuertoEmisor());
				conexiones.get(indicePropio).setHablando(true);
				conexiones.get(indicePropio).setPuertoOtroUsuario(puerto);
				this.nuevoChat(conexiones.get(indicePropio).getCliente().getInetAddress().getHostAddress(),
						conexiones.get(indicePropio).getPuerto(), conexiones.get(indicePropio).getPuertoOtroUsuario());
				System.out.println(conexiones.size());
				conexiones.get(indiceSolicitado).mandarMensaje(mensaje);
			} else { // está hablando
				Mensaje respuesta = new Mensaje("/enCharla/", this.server.getInetAddress().getHostAddress(),
						this.puertoServer);
				conexiones.get(indicePropio).mandarMensaje(respuesta);
			}
		} else { // no se encontró a la persona
			Mensaje respuesta = new Mensaje("/erroneo/", this.server.getInetAddress().getHostAddress(),
					this.puertoServer);
			conexiones.get(indicePropio).mandarMensaje(respuesta);
		}
	}
	
	public boolean disponibilidadPuerto(int puerto) {
		int i = 0;
		while (i < conexiones.size() - 1 && conexiones.get(i).getPuerto() != puerto) {
			i++;
		}
		if (i < conexiones.size() - 1 && conexiones.get(i).getPuerto() == puerto) { // se encontro que alguien usaba el puerto
			return false;
		} else {
			return true;
		}
	}

	
	@Override
	public void cambioCantConectados(int sumaOresta) {
		this.cs.cambioCantConectados(sumaOresta);
		
	}

	public void reparte(Mensaje mensaje) {
		for (ManejaConexiones cliente : conexiones) {
			if (cliente != null) {
				if (cliente.getPuerto() == mensaje.getPuertoEmisor()
						|| cliente.puertoOtroUsuario == mensaje.getPuertoEmisor()) {
					cliente.mandarMensaje(mensaje);
				}
			}
		}
	}

	public void rechaza(Mensaje mensaje) {
		int i = 0;
		while (i < conexiones.size() && (conexiones.get(i).getPuertoOtroUsuario() != mensaje.getPuertoEmisor())) {
			i++;
		}
		if (conexiones.get(i).getPuertoOtroUsuario() == mensaje.getPuertoEmisor()) {
			conexiones.get(i).mandarMensaje(mensaje);
		}
	}

	public void puertoErroneo(Mensaje mensaje) {
		int ultimoIndice = conexiones.size() - 1;
			conexiones.get(ultimoIndice).mandarMensaje(mensaje);
	}
	
	public void cerrarServidor() {
		listo = true;
		for (ManejaConexiones cliente : conexiones) {
			Mensaje mensaje = new Mensaje("/cerrar/", server.getInetAddress().getHostAddress(), this.puertoServer);
			cliente.mandarMensaje(mensaje);
			cliente.cerrarCliente();
		}
		int i = 0;
		while (i < conexiones.size()) {
			conexiones.remove(i);
		}
	}
	
	private class ManejaConexiones implements Runnable, IComunicacion {
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
						desconectaChat(mensaje);
					} else if (mensaje.getMensaje().contains("/puerto/")) {
						String[] cadena = mensaje.getMensaje().split(" ");
						this.puerto = Integer.parseInt(cadena[1]);
						if (!disponibilidadPuerto(this.puerto)) {
							Mensaje mensaje2 = new Mensaje("/sinDisponibilidad/", cliente.getInetAddress().getHostAddress(), this.puerto);
							puertoErroneo(mensaje2);
							desconectaChat(mensaje2);
						}
					} else if (mensaje.getMensaje().contains("/intentoConexion/")) {
						String[] cadena = mensaje.getMensaje().split("/");
						int puertoAConectar = Integer.parseInt(cadena[2]);
						consultaDisponibilidad(mensaje, puertoAConectar);
					} else if (mensaje.getMensaje().contains("/aceptar/")) {
						reparte(mensaje);
					} else if (mensaje.getMensaje().contains("/rechazar/")) {
						rechaza(mensaje);
						eliminarChat(mensaje);
						desconectaChat(mensaje);
					} else {
						agregarAlChat(mensaje);
						reparte(mensaje);
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getLocalizedMessage() + "run de server manejaconex");
			}
		}

		public void mandarMensaje(Mensaje mensaje) {
			try {
				os.writeObject(mensaje);
			} catch (IOException e) {
				System.out.println("esto no deberia pasar nunca, mandar mensaje desde el servidor");
			}
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

		public int getPuerto() {
			return this.puerto;
		}

		public boolean isHablando() {
			return hablando;
		}

		public int getPuertoOtroUsuario() {
			return puertoOtroUsuario;
		}

		public void setHablando(boolean hablando) {
			this.hablando = hablando;
		}

		public void setPuertoOtroUsuario(int puertoOtroUsuario) {
			this.puertoOtroUsuario = puertoOtroUsuario;
		}
		
		public Socket getCliente() {
			return this.cliente;
		}

		@Override
		public void mostrarIntentoDeConexion(String ip, int puerto) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarMensajeTextArea(Mensaje mensaje) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarUsuarioOcupado() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarUsuarioNoDisponible() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarCierreSesion() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarConexionErronea() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarConexxionErroneaServer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarPuertoErroneo() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mostrarPuertoEnUso() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cerrarInstancia() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aceptaInicioSesion() {
			// TODO Auto-generated method stub
			
		}
		
	}

	public void setCs(ControladorServer cs) {
		this.cs = cs;
	}
	
 }
