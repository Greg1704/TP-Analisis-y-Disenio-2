package modelo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente implements Runnable {
	
	private Socket cliente;
	private BufferedReader in;
	private PrintWriter out;

	@Override
	public void run() {

		try {
			cliente = new Socket("localhost", 1234);
			out = new PrintWriter(cliente.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
		} catch (IOException e) {
			
		}

	}
}
