package modelo;

public interface IState {
	public void init();
	public void cambiaEstado();
	public String getEstado();
}
