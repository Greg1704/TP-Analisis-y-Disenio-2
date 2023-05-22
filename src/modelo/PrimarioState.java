package modelo;

public class PrimarioState extends State {

	public PrimarioState(Server server) {
		super(server);
		System.out.println("server modo primario");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cambiaEstado() {
		this.server.setEstado(new SecundarioState(this.server));
		this.server.init();
	}

	@Override
	public String getEstado() {
		return "Primario";
	}
	
	

}
