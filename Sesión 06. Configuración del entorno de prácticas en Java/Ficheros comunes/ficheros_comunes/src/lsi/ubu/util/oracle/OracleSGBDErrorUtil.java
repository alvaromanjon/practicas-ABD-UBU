package lsi.ubu.util.oracle;

import java.sql.SQLException;

import lsi.ubu.util.SGBDError;
import lsi.ubu.util.SGBDErrorUtil;

/**
 * Errores codificados para Oracle. Referencia:
 * https://docs.oracle.com/cd/B28359_01/server.111/b28278/toc.htm.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public class OracleSGBDErrorUtil implements SGBDErrorUtil {

	// Codigos de error en Oracle
	private static final int PK_VIOLATED = 1; 		//ORA-0001
	private static final int FK_VIOLATED = 2291;	//ORA-2291
	private static final int NOT_EXISTS_SEQUENCE = 2289;
	private static final int CONFLICTO_DE_VALORES = 17085;
	// A�adir segun sea necesario, por parte del alumno...

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
			// Se copia el texto integro, como ejemplo, de la referencia dada
			// pero no se hara con el resto.
			/*
			 * ORA-02291: integrity constraint (string.string) violated - parent
			 * key not found Cause: A foreign key value has no matching primary
			 * key value. Action: Delete the foreign key or add a matching
			 * primary key. 
			 */
			return SGBDError.FK_VIOLATED;
		case PK_VIOLATED:
			return SGBDError.PK_VIOLATED;
		case NOT_EXISTS_SEQUENCE:
			return SGBDError.NOT_EXISTS_SEQUENCE;
		case CONFLICTO_DE_VALORES:
			return SGBDError.CONFLICTO_DE_VALORES;
		}
		return SGBDError.UNKNOWN;
	}

	/**
	 * Comprueba si la excepcion contiene un codigo de error buscado.
	 * 
	 * @param ex
	 *            excepcion con codigo interno de la base de datos
	 * @param error
	 *            error en la base de datos
	 * @return true si coinciden, false en caso contrario
	 */
	@Override
	public boolean checkExceptionToCode(SQLException ex, SGBDError error) {
		return new OracleSGBDErrorUtil().translate(ex.getErrorCode()) == error;
	}
}