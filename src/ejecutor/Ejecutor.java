package ejecutor;

import controlador.Controlador;
import controlador.ControladorServer;

public class Ejecutor {

	public static void main(String[] args) {
		//ControladorServer cServer = new ControladorServer();
		Controlador c=Controlador.getInstancia();
	}
}