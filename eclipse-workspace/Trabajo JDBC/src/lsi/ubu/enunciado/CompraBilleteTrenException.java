package lsi.ubu.enunciado;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompraBilleteTrenException:
 * Implementa las excepciones contextualizadas de la transaccion
 * de CompraBilleteTren
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jes�s Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Ra�l Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */
public class CompraBilleteTrenException extends SQLException {	
	
	private static final long serialVersionUID = 1L;
	
	public static final int NO_PLAZAS = 1;
	public static final int NO_EXISTE_VIAJE = 2;
		
	private int codigo; // = -1;
	private String mensaje;

	private static Logger l = LoggerFactory.getLogger(CompraBilleteTrenException.class);	

	public CompraBilleteTrenException(int code) {
		this.codigo = code;
		
		switch(codigo) {
			case 1:
				this.mensaje = "No hay plazas suficientes.";
				break;
			case 2:
				this.mensaje = "No existe ese viaje para esa fecha, hora, origen y destino.";
				break;
		}
		
		l.error(mensaje);

		// Traza_de_pila
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			l.info(ste.toString());		}
		}
	

	@Override
	public String getMessage() { // Redefinicion del metodo de la clase
									// Exception
		return mensaje;
	}

	@Override
	public int getErrorCode() { // Redefinicion del metodo de la clase
								// SQLException
		return codigo;
	}

}
