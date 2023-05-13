package modelo;

public final class Encriptacion {
	
	public static String Encriptar(String mensaje) {
		char[] chars = mensaje.toCharArray();
		int i = 1;
		String mensajeEncriptado="";
		for(char c:chars) {
			c +=i;
			i++;
			if(i==4)
				i=1;
			mensajeEncriptado+= c;
		}
		System.out.println(mensajeEncriptado);
		return mensajeEncriptado;
	}
	
	public static String Desencriptar(String mensaje) {
		char[] chars = mensaje.toCharArray();
		int i = 1;
		String mensajeDesencriptado="";
		for(char c:chars) {
			c-=i;
			i++;
			if(i==4)
				i=1;
			mensajeDesencriptado+=c;
		}
		System.out.println(mensajeDesencriptado);
		return mensajeDesencriptado;
	}
	
}
