package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controlador.ControladorServer;
import controlador.IComunicacion;
import controlador.IConectados;

public class Server implements Runnable, IConsultaEstado, IConectados, IChat, IReconectar {

	private int puertoMonitor = 10000; // puerto para conectarse al monitor siendo el servidor original
	private int puertoServer = 11000; // puerto original donde habla la gente
	private int puertoSecundario = 8000; // puerto en el que escucha el servidor secundaria para que el monitor le avise cuando tiene que usarse
	private int puertoSincronizacion = 7000; // puerto para mandarse informacion entre servidor original y el secundario
	private ServerSocket server;
	private ArrayList<ManejaConexiones> conexiones;
	private ArrayList<Chat> chats = new ArrayList<Chat>();
	private boolean listo = false;
	private ExecutorService pool;
	private IConectados cs;
	private boolean primario = true;
	
	public Server(IConectados cs) {
		conexiones = new ArrayList<ManejaConexiones>(); 
		this.cs = cs;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(puertoServer);
			conectarseAMonitor();
			mandarActualizacionInformacion();
			System.out.println("ORIGINAL");
			System.out.println("ORIGINAL");
		//	pool = Executors.newCachedThreadPool();
			while (!listo) {
				Socket cliente = server.accept();
				System.out.println("se ejecuta el while listo");
				ManejaConexiones m = new ManejaConexiones(cliente);
				conexiones.add(m);
				this.cambioCantConectados(conexiones.size());
			//	pool.execute(m);
			}
		} catch (BindException e) {
			this.primario = false;
			conectarseAMonitor();
			recibirActualizacionInformacion();
			System.out.println("el serverSocket tenia el puerto ocupado"); 
		} catch (IOException e) {
			
		}
	}
	
	@Override
	public void reconecta() {
		this.run();
	}
	
	public void conectarseAMonitor() { // esto es para PRIMARIO Y SECUNDARIO: hay que mandar latidos O estar conectados
										// con el monitor asi el monitor avisa si se cae el primero
		new Thread() {
			public void run() {
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (primario) {
							try {
								Socket socket = new Socket("localhost", puertoMonitor); // soy servidor
																									// primario
								mandarActualizacionInformacion();
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
								out.writeObject(true);

								out.close();
								socket.close();

							} catch (Exception e) {
								System.out.println(e.getLocalizedMessage());
							
							}
						} else {
							try {
								ServerSocket serverSocket = new ServerSocket(puertoSecundario);
								while (true) {
									Socket socket = serverSocket.accept(); // una vez que esto se acepta , baja de linea
									System.out.println("el servidor secundario se hizo primario");
									serverSocket.close();
									socket.close();
									setPrimario();
									reconecta();
								
								}
							} catch (Exception e) {
								System.out.println(e.getLocalizedMessage());
							}
						}
					}
				}, 1000, 5000);
			}
		}.start();
	}
	
	public void mandarActualizacionInformacion() { // mandarle la informacion actualizada al secundario
		new Thread() {
			public void run() {
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (primario) {
							try {
								Socket socket = new Socket("localhost", puertoSincronizacion); // soy servidor
																									// primario
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
								System.out.println();
								out.writeObject(conexiones);
								out.writeObject(chats);
								//out.writeObject(pool);
								out.close();
								socket.close();
							} catch (IOException e) {
								System.out.println("No hay servidor secundario en mandar actualizacion");
							}
						}
					}
				}, 0, 5000);
			}
		}.start();
	}
	
	public void recibirActualizacionInformacion() {
		new Thread() {
			public void run() {
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						if (!primario) {
							try {
								ServerSocket serverSocket = new ServerSocket(puertoSincronizacion); // soy servidor	 secundario
								while (true) {
									Socket socket = serverSocket.accept();
									ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
									
									ArrayList<ManejaConexiones> conexiones = (ArrayList<ManejaConexiones>) in.readObject();
								//	setConexiones(conexiones);
									System.out.println(conexiones.size());
									ArrayList<Chat> chats = (ArrayList<Chat>) in.readObject();
									setChats(chats);
									System.out.println(conexiones.size());
							//		ExecutorService pool = (ExecutorService) in.readObject();
							//		setPool(pool);
								}
							} catch (Exception e) {
								System.out.println("No hay servidor secundario en recibir actualizacion");
								System.out.println(e.getLocalizedMessage());
							}
						}
						}
					}, 0, 5000);
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
				this.nuevoChat(conexiones.get(indicePropio).getCliente().getInetAddress().getHostAddress(),
						conexiones.get(indicePropio).getPuerto(), conexiones.get(indicePropio).getPuertoOtroUsuario());
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
		if (i < conexiones.size() - 1 && conexiones.get(i).getPuerto() == puerto) { // se encontro que alguien usaba
																					// el puerto
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
			conexiones.get(ultimoIndice).cerrarCliente();
			conexiones.remove(ultimoIndice);
			cambioCantConectados(conexiones.size());
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
	
	public class ManejaConexiones implements IComunicacion {
		private Socket cliente;
		private String nombre;
		private int puerto = 0;
		private boolean hablando;
		private int puertoOtroUsuario = -10;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		
		public ManejaConexiones(Socket cliente) {
			this.cliente = cliente;
			maneja();
		}
		
		public void maneja() {
			new Thread() {
				public void run() {
					try {
						os = new ObjectOutputStream(cliente.getOutputStream());
						is = new ObjectInputStream(cliente.getInputStream());
						Mensaje mensaje;
						while ((mensaje = (Mensaje) is.readObject()) != null) {
							if (mensaje.getMensaje().equals("/cerrar/")) {
								reparte(mensaje);
								cerrarCliente();
								desconectaChat(mensaje);
							} else if (mensaje.getMensaje().contains("/puerto/")) {
								String[] cadena = mensaje.getMensaje().split(" ");
								puerto = Integer.parseInt(cadena[1]);
								if (!disponibilidadPuerto(puerto)) {
									Mensaje mensaje2 = new Mensaje("/sinDisponibilidad/",
											cliente.getInetAddress().getHostAddress(), puerto);
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
						//
					} catch (ClassNotFoundException e) {

					}
				}
			}.start();
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

	public int getPuertoServer() {
		return puertoServer;
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

	public void setPrimario() {
		this.primario = true;
	}

	public ArrayList<Chat> getChats() {
		return chats;
	}

	public void setChats(ArrayList<Chat> chats) {
		this.chats = chats;
	}

	public ExecutorService getPool() {
		return pool;
	}
	
	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}

	public void setCs(ControladorServer cs) {
		this.cs = cs;
	}
	
 }
