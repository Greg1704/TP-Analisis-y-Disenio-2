package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import controlador.ControladorServer;
import controlador.interfaces.IComunicacion;
import controlador.interfaces.IConectados;
import modelo.chat.Chat;
import modelo.chat.Mensaje;
import modelo.interfaces.IChat;
import modelo.interfaces.IConsultaEstado;
import modelo.interfaces.IReconectar;

public class Server implements IConsultaEstado, IConectados, IChat, IReconectar, Serializable {

	private int puertoHeartBeat = 10000; // puerto para conectarse al monitor siendo el servidor original
	private int puertoServidorOriginal = 11000; // puerto original donde habla la gente
	private int puertoSecundario = 8000; // puerto en el que escucha el servidor secundaria para que el monitor le avise cuando tiene que usarse
	private int puertoTransferenciaDatos = 7000; // puerto para mandarse informacion entre servidor original y el secundario
	private String ipServer;
	private ArrayList<ManejaConexiones> conexiones;
	private ArrayList<Chat> chats = new ArrayList<Chat>();
	private IConectados cs;
	private boolean primario = true;
	
	public Server(IConectados cs) {
		conexiones = new ArrayList<ManejaConexiones>(); 
		this.cs = cs;
		this.correrServer();
		InetAddress localHost;
		try {
			localHost = InetAddress.getLocalHost();
			this.ipServer = localHost.getHostAddress();
		} catch (UnknownHostException e) {
		}

	}
	
	public void correrServer() {
		new Thread() {
			public void run() {
				try {
					ServerSocket server = new ServerSocket(puertoServidorOriginal);
					heartBeat();
					mandarActualizacionInformacion();
					while (true) {
						Socket cliente = server.accept();
						identificador(cliente);
					}
				} catch (BindException e) {
					setPrimario(false);
					esperaFalloHeartBeat();
					recibirActualizacionInformacion();
				} catch (IOException e) {
				}
			}
		}.start();
	}
	
	@Override
	public void reconecta() {
		correrServer();
	}
	
	public void heartBeat() { // esto es para PRIMARIO Y SECUNDARIO: hay que mandar latidos O estar conectados	// con el monitor asi el monitor avisa si se cae el primero
		new Thread() {
			public void run() {
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
							try {
								Socket socket = new Socket("localhost", puertoHeartBeat); // soy servidor
																									// primario
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
								out.writeObject(true);
								out.close();
								socket.close();
								cambioCantConectados(conexiones.size());
							} catch (Exception e) {
								
							}
					}
				}, 0, 3500);
			}
		}.start();
	}
	
	public void esperaFalloHeartBeat() { // metodo que usa el servidor secundario para esperar notificacion del monitor
		new Thread() {
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(puertoSecundario);
					Socket monitor = serverSocket.accept(); // linea en la que espera conexion del monitor para avisarle
					serverSocket.close();
					setPrimario(true);
					reconecta();
					
				} catch (IOException e) {
					System.exit(0);
				}
			}
		}.start();
	}
	
	public void mandarActualizacionInformacion() { // mandarle la informacion actualizada al servidor secundario
		new Thread() {
					public void run() {
						try {
							ServerSocket serverSocket = new ServerSocket(puertoTransferenciaDatos); // soy servidor
							while (true) { // primario
								Socket socket = serverSocket.accept();
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

								out.writeObject(conexiones);
								out.flush();
								out.writeObject(chats);
								out.flush();
							}
						} catch (IOException e) {
							System.out.println(e.getLocalizedMessage());
						}
					}
			}.start();
	}

	public void recibirActualizacionInformacion() { // metodo que ejecuta el servidor secundario
		new Thread() {
			public void run() {
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						try {
							if (!primario) {
								Socket socket = new Socket("localhost", puertoTransferenciaDatos); // soy servidor
																									// secundario
								ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
								ArrayList<ManejaConexiones> conexiones = (ArrayList<ManejaConexiones>) in.readObject();
								setConexiones(conexiones);
								cambioCantConectados(conexiones.size());
								ArrayList<Chat> chats = (ArrayList<Chat>) in.readObject();
								setChats(chats);
								socket.close();
							} else {
								t.cancel();
							}
						} catch (Exception e) {
							System.out.println(e.getLocalizedMessage());
						}
					}
				}, 0, 1000);
			}
		}.start();
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
				} else {
					i++;
				}
			} else if (conexiones.get(i).getPuertoOtroUsuario() == -10 && conexiones.get(i).getPuerto() == mensaje.getPuertoEmisor()) { // si no estaba hablando con nadie
				conexiones.get(i).setPuertoOtroUsuario(-10);
				conexiones.get(i).setHablando(false);
				cerrados = 2;
				if (mensaje.getMensaje().contains("/cerrar/")) {
					conexiones.remove(i);
					this.cambioCantConectados(conexiones.size());
				} else {
					i++;
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
				this.nuevoChat(ipServer,
						conexiones.get(indicePropio).getPuerto(), conexiones.get(indicePropio).getPuertoOtroUsuario());
				conexiones.get(indiceSolicitado).mandarMensaje(mensaje, puerto);
			} else { // está hablando
				Mensaje respuesta = new Mensaje("/enCharla/", ipServer,
						this.puertoServidorOriginal);
				conexiones.get(indicePropio).mandarMensaje(respuesta, mensaje.getPuertoEmisor());
			}
		} else { // no se encontró a la persona
			Mensaje respuesta = new Mensaje("/erroneo/", ipServer,
					this.puertoServidorOriginal);
			conexiones.get(indicePropio).mandarMensaje(respuesta, mensaje.getPuertoEmisor());
		}
	}

	public boolean disponibilidadPuerto(int puerto) {
		int i = 0;
		while (i < conexiones.size() && conexiones.get(i).getPuerto() != puerto) {
			i++;
		}
		if (i < conexiones.size()) { // se encontro que alguien usaba
																					// el puerto
			return false;
		} else {
			return true;
		}
	}
	
	public void identificador(Socket cliente) {
		try {
			ObjectInputStream is = new ObjectInputStream(cliente.getInputStream());
			Mensaje mensaje = (Mensaje) is.readObject();
			if (disponibilidadPuerto(mensaje.getPuertoEmisor())) { // es una nueva conexion ? 
				ManejaConexiones m = new ManejaConexiones(cliente, mensaje);
				conexiones.add(m);
				this.cambioCantConectados(conexiones.size());
			} else {
				if (!mensaje.getMensaje().contains("/puerto/")) { // entra un mensaje en el que existe un cliente con ese puerto, me fijo si es alguien queriendo registrarse con ese mismo puerto
					int indice = buscaIndicePropio(mensaje);
					if (indice!=-1) {
						conexiones.get(indice).maneja(cliente, mensaje);
					}
				} else {
					// se resuelve desde el Cliente, si se quiere hacer un serverSocket con mismo puerto te fleta
				}
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cambioCantConectados(int sumaOresta) {
		this.cs.cambioCantConectados(sumaOresta);
		Mensaje mensaje = new Mensaje(conexiones, ipServer, puertoServidorOriginal);
		for (ManejaConexiones cliente : conexiones) {
			cliente.mandarMensaje(mensaje, cliente.getPuerto());
		}
	}

	public void reparte(Mensaje mensaje) {
		for (ManejaConexiones cliente : conexiones) {
			if (cliente.getPuerto() == mensaje.getPuertoEmisor()) { // mandarle el mensaje a la persona que mando el mensaje
				cliente.mandarMensaje(mensaje, mensaje.getPuertoEmisor());
			} else if (cliente.puertoOtroUsuario == mensaje.getPuertoEmisor()) { // mandarle el mensaje a la persona destino
				cliente.mandarMensaje(mensaje, cliente.getPuerto());
			}
		}
	}
	
	public void rechaza(Mensaje mensaje) {
		int i = 0;
		while (i < conexiones.size() && (conexiones.get(i).getPuertoOtroUsuario() != mensaje.getPuertoEmisor())) {
			i++;
		}
		if (conexiones.get(i).getPuertoOtroUsuario() == mensaje.getPuertoEmisor()) {
			conexiones.get(i).mandarMensaje(mensaje, conexiones.get(i).getPuertoOtroUsuario());
		}
	}

	public void puertoErroneo(Mensaje mensaje) {
		int ultimoIndice = conexiones.size() - 1;
			int puerto = conexiones.get(ultimoIndice).getPuerto();
			conexiones.get(ultimoIndice).mandarMensaje(mensaje, puerto);
			conexiones.remove(ultimoIndice);
			cambioCantConectados(conexiones.size());
	}
	
	public void cerrarServidor() {
		for (ManejaConexiones cliente : conexiones) {
			Mensaje mensaje = new Mensaje("/cerrar/", ipServer, this.puertoServidorOriginal);
			cliente.mandarMensaje(mensaje, cliente.getPuerto());
		}
		int i = 0;
		while (i < conexiones.size()) {
			conexiones.remove(i);
		}
	}
	
	public class ManejaConexiones implements IComunicacion,Serializable {
		private String ip;
		private String nombre;
		private int puerto = 0;
		private boolean hablando;
		private int puertoOtroUsuario = -10;
		
		public ManejaConexiones(Socket cliente, Mensaje mensaje) {
			InetAddress localHost;
			try {
				localHost = InetAddress.getLocalHost();
				this.ip = localHost.getHostAddress();
			} catch (UnknownHostException e) {
				System.out.println("no se q pasa al obtener la ip");
			}
			maneja(cliente, mensaje);
		}

		public void maneja(Socket cliente, Mensaje mensaje) {
			if (mensaje != null) {
				if (mensaje.getMensaje().equals("/cerrar/")) {
					reparte(mensaje);
					desconectaChat(mensaje);
				} else if (mensaje.getMensaje().contains("/puerto/")) {
					String[] cadena = mensaje.getMensaje().split("/");
					int puertoAux = Integer.parseInt(cadena[2]);
					this.nombre = cadena[3];
					if (!disponibilidadPuerto(puertoAux)) {
						Mensaje mensaje2 = new Mensaje("/sinDisponibilidad/", cliente.getInetAddress().getHostAddress(),
								puertoAux);
						puertoErroneo(mensaje2); // VER ESTO FALTA AVISAR QUE NO HAY DISPONIBILIDAD
						desconectaChat(mensaje2);
					} else {
						puerto = puertoAux;
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
		}

		public void mandarMensaje(Mensaje mensaje, int puerto) {
			try {
				Socket socketEnvioMensaje = new Socket("localhost", puerto);
				ObjectOutputStream os = new ObjectOutputStream(socketEnvioMensaje.getOutputStream());
				os.writeObject(mensaje);
				os.flush();
				socketEnvioMensaje.close();
			} catch (IOException e) {
			//	System.out.println("esto no deberia pasar nunca, mandar mensaje desde el servidor");
			//	System.out.println(e.getLocalizedMessage());
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
		
		public String getIp() {
			return ip;
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

		@Override
		public void mandarMensaje(Mensaje mensaje) {
			
			}

		@Override
		public String toString() {
			return "nombre = " + this.nombre + " ip = " + this.ip + " puerto = " + this.puerto;
		}

		@Override
		public void mostrarMensajeNoEnviado(Mensaje mensaje) {
			// TODO Auto-generated method stub
			
		}
		
		
		/*
		@Override
		public String toString() {
			return "nombre: " + this.nombre + " ip: " + this.ip + " puerto: " + this.puerto;
		}
		*/
	}

	public int getPuertoServer() {
		return puertoServidorOriginal;
	}
	
	public int getPuertoSecundario() {
		return puertoSecundario;
	}

	public ArrayList<ManejaConexiones> getConexiones() {
		return conexiones;
	}

	public void setConexiones(ArrayList<ManejaConexiones> conexiones) {
		this.conexiones = conexiones;
	}

	public boolean isPrimario() {
		return primario;
	}

	public void setPrimario(boolean primario) {
		this.primario = primario;
	}

	public ArrayList<Chat> getChats() {
		return chats;
	}

	public void setChats(ArrayList<Chat> chats) {
		this.chats = chats;
	}

	public void setCs(ControladorServer cs) {
		this.cs = cs;
	}
	
 }
