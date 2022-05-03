package lsi.ubu.util.exceptions;

import java.sql.SQLException;

/**
 * Utilidad para el tratamiento de errores en bases de datos.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public interface SGBDErrorUtil {
	
	/**
	 * Traduce el código de error numérico al valor de error correspondiente
	 * en el SGBD que se esté utilizando.
	 * 
	 * @param errorSGBD número devuelto por el SGBD
	 * @return el tipo de error correspondiente
	 */
	SGBDError translate(int errorSGBD);
	
	/**
	 * Comprueba si la excepción contiene un cierto código de error.
	 * 
	 * @param ex excepción con código interno de la base de datos
	 * @param error error en la base de datos
	 * @return true si coinciden, false en caso contrario
	 */
	boolean checkExceptionToCode(SQLException ex, SGBDError error);	
} 