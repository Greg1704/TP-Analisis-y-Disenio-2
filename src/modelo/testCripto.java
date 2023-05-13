package modelo;

public class testCripto {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String mensaje= "caca de toro, es lo mejor del mundo.";
		mensaje = Encriptacion.Encriptar(mensaje);
		mensaje = Encriptacion.Desencriptar(mensaje);
	}

}
