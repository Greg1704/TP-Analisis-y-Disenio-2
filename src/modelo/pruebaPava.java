package modelo;

import controlador.ControladorServer;

public class pruebaPava {
	
	public static void prueba() {
		String texto = "/intentoDeConexion/1234/gregozap";
		String[] arrayNuevo = texto.split("/");
		System.out.println(arrayNuevo[3]);
	}
	public static void main(String[] args) {
		prueba();
		String texto = Encriptacion.encriptadoMensaje("hola como estas", "aaaaaaaa");
		System.out.println(texto);
	}
	
}
