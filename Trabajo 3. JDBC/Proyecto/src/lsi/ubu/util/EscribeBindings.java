package lsi.ubu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidad para reconfigurar el pool de conexiones a la bases de datos.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 */
public class EscribeBindings {
	/** Logger. */
	private static Logger logger = LoggerFactory.getLogger(EscribeBindings.class);

	/**
	 * Principal. 
	 * 
	 * @param args argumentos (se ignoran)
	 */
	public static void main(String[] args) {
		try{
			PoolDeConexiones.reconfigurarPool();
			logger.info("Pool reconfigurado con éxito.");
		}catch (Exception e){
			logger.error("Error reconfigurando el pool de conexiones.");
			logger.error(e.getMessage());
		}
	}

}
