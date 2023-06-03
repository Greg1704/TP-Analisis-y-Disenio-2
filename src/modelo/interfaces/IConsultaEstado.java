package modelo.interfaces;

import modelo.chat.Mensaje;

public interface IConsultaEstado {
	public void consultaDisponibilidad(Mensaje mensaje, int puerto);
}
