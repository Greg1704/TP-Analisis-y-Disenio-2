package modelo;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class Encriptacion {
	
	public static Mensaje encriptadoMensaje(Mensaje mensaje) {
		try {
			byte[] textoEncriptado = encriptarABytes("12345678", mensaje.getMensaje(), "DES");
			String textoEncriptadoBase64 = Base64.getEncoder().encodeToString(textoEncriptado);
			mensaje.setMensaje(textoEncriptadoBase64);
			return mensaje;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}
	
	public static Mensaje desencriptadoMensaje(Mensaje mensaje) {
		try {
			byte[] textoEncriptado = Base64.getDecoder().decode(mensaje.getMensaje());
			String textoOriginal = desencriptarDeBytes("12345678",	textoEncriptado, "DES");
			mensaje.setMensaje(textoOriginal);
			return mensaje;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}
	
	public static byte[] encriptarABytes(String pass, String texto, String algoritmo) throws Exception {
		java.security.Key key = new SecretKeySpec(pass.getBytes(), algoritmo);
		Cipher cipher = Cipher.getInstance(algoritmo);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(texto.getBytes());
	}

	public static String desencriptarDeBytes(String pass, byte[] encriptado, String algoritmo) throws Exception {
		java.security.Key key = new SecretKeySpec(pass.getBytes(), algoritmo);
		Cipher cipher = Cipher.getInstance(algoritmo);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(encriptado);
		return new String(bytes);
	}

}
