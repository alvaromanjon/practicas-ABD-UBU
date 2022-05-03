package lsi.ubu.util;

/**
 * Errores en un SGBD.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public enum SGBDError {
	FK_VIOLATED, // Violacion de clave foranea
	PK_VIOLATED, // Violacion de clave primaria
	NOT_EXISTS_SEQUENCE, // No existe la secuencia utilizada
	// A�adir despues de esta linea si fuera necesario
	CONFLICTO_DE_VALORES,
	
	UNKNOWN; // No determinado.
}
