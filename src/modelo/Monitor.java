package modelo;

import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class Monitor {
	private boolean activo = true;
	private Timer t = new Timer();
	private static int puertoLatidosPrimario = 10000;
	private static int puertoEsperaSecundario = 8000;
	private static Monitor instance = null;
	
	public static Monitor getInstance() {
		if (instance == null) 
			instance = new Monitor();
		return instance;
	}
	
	
	private Monitor() {
		this.t.scheduleAtFixedRate(new TimerTask() { // el task se ejecutará cada 1000ms (1s) y la ejecucion de latidos dura 5 segundos, cuando no se recibe mensaje, se pasa a modo secundario
				
				@Override
				public void run() {
					if(activo) {
						Monitor.getInstance().setActivo(false);
						System.out.println("Pasando a false");
					} else {
						try {
							Socket socket = new Socket("localhost", puertoEsperaSecundario);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(true);
							out.close();
							socket.close();
							System.out.println("se ejecuta la parte de paso de secundario a primario");
						} catch (ConnectException e) {
							System.out.println("El secundario no existe todavía");
						} catch (SocketException e) {
							//
						} catch (Exception e) {
							System.out.println(e.getLocalizedMessage());
						}
					}
				}
			}, 1000, 5000);
		this.comienzaEsperaLatidos();
	}
	
	public void comienzaEsperaLatidos() {
		new Thread() {
			public void run() {
				try {
					System.out.println("se ejecuta comienzaEsperaLatidos");
					ServerSocket servSocket = new ServerSocket(puertoLatidosPrimario); // PUERTO MONITOR ES 12000 // PUERTO PRIMARIO ES 11000
					while (true) {
						Socket socket = servSocket.accept();
						Monitor.getInstance().setActivo(true);
						System.out.println("El primario sigue funcionando");
					}

				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
			}
		}.start();
	}
	
	public void setActivo(Boolean b) {
		this.activo = b;
	}
}

