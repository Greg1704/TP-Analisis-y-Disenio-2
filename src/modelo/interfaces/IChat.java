package modelo.interfaces;

import modelo.chat.Mensaje;

public interface IChat {
	public void nuevoChat(String ip1, int puerto1, int puerto2);
	public void agregarAlChat(Mensaje mensaje);
	public void eliminarChat(Mensaje mensaje);
	public void desconectaChat(Mensaje mensaje);
}
