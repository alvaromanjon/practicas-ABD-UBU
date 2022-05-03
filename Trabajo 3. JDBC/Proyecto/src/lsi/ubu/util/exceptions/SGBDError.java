package lsi.ubu.util.exceptions;

/**
 * Errores en un SGBD.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @version 1.0
 * @since 1.0
 */
public enum SGBDError {
	FK_VIOLATED, // Violación de clave foránea por padre inexistente => con insert/udate
	FK_VIOLATED_DELETE, // Violación de clave foránea por hijo existente => con delete/udate
	PK_VIOLATED, // Violación de clave primaria
	NOT_EXISTS_SEQUENCE, // No existe la secuencia utilizada
	// Añadir antes de esta línea si fuera necesario
	
	UNKNOWN; // No determinado.
}