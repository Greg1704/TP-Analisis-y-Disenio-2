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
	private static int puertoLatidosPrimario = 65535;
	private static int puertoEsperaSecundario = 65534;
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
					if (activo) {
						Monitor.getInstance().setActivo(false);
					} else {
						try {
							Socket socket = new Socket("localhost", puertoEsperaSecundario);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(true);
							out.close();
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
					ServerSocket servSocket = new ServerSocket(puertoLatidosPrimario);
					while (true) {
						Socket socket = servSocket.accept();
						Monitor.getInstance().setActivo(true);
						System.out.println("El primario sigue funcionando");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void setActivo(Boolean b) {
		this.activo = b;
	}
}

