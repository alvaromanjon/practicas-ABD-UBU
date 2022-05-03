package lsi.ubu.util.exceptions.oracle;

import java.sql.SQLException;

import lsi.ubu.util.exceptions.SGBDError;
import lsi.ubu.util.exceptions.SGBDErrorUtil;

/**
 * Errores codificados para Oracle. Referencia:
 * https://docs.oracle.com/cd/B28359_01/server.111/b28278/toc.htm.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public class OracleSGBDErrorUtil implements SGBDErrorUtil {

	// Códigos de error en Oracle
	private static final int PK_VIOLATED = 1;
	private static final int FK_VIOLATED = 2291; //ORA-02291: integrity constraint (la que sea) violated - parent key not found
	private static final int NOT_EXISTS_SEQUENCE = 2289;
	private static final int FK_VIOLATED_DELETE = 2292; //ORA-02292: integrity constraint (la que sea) violated - child record found
	// Añadir según sea necesario, por parte del alumno...

	/**
	 * {@inheritDoc}.
	 * 
	 * @param errorSGBD
	 *            {@inheritDoc
	 * @return {@inheritDoc}
	 */
	@Override
	public SGBDError translate(int errorSGBD) {
		switch (errorSGBD) {
		case FK_VIOLATED:
			// Se copia el texto íntegro, como ejemplo, de la referencia dada
			// pero no se hará con el resto.
			/*
			 * ORA-02291: integrity constraint (string.string) violated - parent
			 * key not found Cause: A foreign key value has no matching primary
			 * key value. Action: Delete the foreign key or add a matching
			 * primary key. 
			 */
			return SGBDError.FK_VIOLATED;
		case PK_VIOLATED:
			return SGBDError.PK_VIOLATED;
		case FK_VIOLATED_DELETE:
			return SGBDError.FK_VIOLATED_DELETE;
		case NOT_EXISTS_SEQUENCE:
			return SGBDError.NOT_EXISTS_SEQUENCE;
		}
		return SGBDError.UNKNOWN;
	}

	/**
	 * Comprueba si la excepción contiene un código de error buscado.
	 * 
	 * @param ex
	 *            excepción con código interno de la base de datos
	 * @param error
	 *            error en la base de datos
	 * @return true si coinciden, false en caso contrario
	 */
	@Override
	public boolean checkExceptionToCode(SQLException ex, SGBDError error) {
		return new OracleSGBDErrorUtil().translate(ex.getErrorCode()) == error;
	}
}