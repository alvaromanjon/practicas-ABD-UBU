package lsi.ubu.util;

import java.sql.SQLException;

/**
 * Utilidad para el tratamiento de errores en bases de datos.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public interface SGBDErrorUtil {
	
	/**
	 * Traduce el codigo de error numurico al valor de error correspondiente
	 * en el SGBD que se estu utilizando.
	 * 
	 * @param errorSGBD numero devuelto por el SGBD
	 * @return el tipo de error correspondiente
	 */
	SGBDError translate(int errorSGBD);
	
	/**
	 * Comprueba si la excepcion contiene un cierto codigo de error.
	 * 
	 * @param ex excepcion con codigo interno de la base de datos
	 * @param error error en la base de datos
	 * @return true si coinciden, false en caso contrario
	 */
	boolean checkExceptionToCode(SQLException ex, SGBDError error);	
} 