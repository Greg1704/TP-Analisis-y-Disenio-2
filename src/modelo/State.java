package modelo;

public abstract class State implements IState { 
	protected Server server;
	
	public State(Server server) {
		this.server = server;
	}
}
