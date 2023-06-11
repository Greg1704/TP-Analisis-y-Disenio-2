package modelo;

import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import modelo.interfaces.IComunicacionMonitor;

public class Monitor implements IComunicacionMonitor {
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
		this.t.scheduleAtFixedRate(new TimerTask() { 
				
				@Override
				public void run() {
					if(activo) {
						Monitor.getInstance().setActivo(false);
					} else {
						try {
							Socket socket = new Socket("localhost", puertoEsperaSecundario);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(true);
							out.close();
							socket.close();
						} catch (ConnectException e) {
						} catch (SocketException e) {
						} catch (Exception e) {
							e.getStackTrace();
						}
					}
				}
			}, 500, 3000);
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
					}
				} catch (Exception e) {
					System.exit(0);
				}
			}
		}.start();
	}
	
	public void setActivo(Boolean b) {
		this.activo = b;
	}
}

