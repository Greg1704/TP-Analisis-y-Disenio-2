package modelo;

import java.io.Serializable;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import modelo.interfaces.IEncriptacion;

public class Encriptacion implements IEncriptacion, Serializable {
	
	private static Encriptacion instance = null;
	
	public static Encriptacion getInstancia() {
		if (instance == null) {
			instance = new Encriptacion();
		}
		return instance;
	}
	
	public String encriptadoMensaje(String mensaje, String clave) {
		try {
			byte[] textoEncriptado = encriptarABytes(clave, mensaje, "DES");
			String textoEncriptadoBase64 = Base64.getEncoder().encodeToString(textoEncriptado);
			return textoEncriptadoBase64;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}
	
	public String desencriptadoMensaje(String mensaje, String clave) {
		try {
			byte[] textoEncriptado = Base64.getDecoder().decode(mensaje);
			String textoOriginal = desencriptarDeBytes(clave,	textoEncriptado, "DES");
			return textoOriginal;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}
	
	public byte[] encriptarABytes(String pass, String texto, String algoritmo) throws Exception {
		java.security.Key key = new SecretKeySpec(pass.getBytes(), algoritmo);
		Cipher cipher = Cipher.getInstance(algoritmo);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(texto.getBytes());
	}

	public String desencriptarDeBytes(String pass, byte[] encriptado, String algoritmo) throws Exception {
		java.security.Key key = new SecretKeySpec(pass.getBytes(), algoritmo);
		Cipher cipher = Cipher.getInstance(algoritmo);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(encriptado);
		return new String(bytes);
	}

}
