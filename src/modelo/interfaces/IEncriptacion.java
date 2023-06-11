package modelo.interfaces;

public interface IEncriptacion {
	public String encriptadoMensaje(String mensaje, String clave);
	public String desencriptadoMensaje(String mensaje, String clave);
	public byte[] encriptarABytes(String pass, String texto, String algoritmo) throws Exception;
	public String desencriptarDeBytes(String pass, byte[] encriptado, String algoritmo) throws Exception;
}
