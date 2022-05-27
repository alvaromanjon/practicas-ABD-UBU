package es.ubu.lsi.service;

/**
 * Persistence exception.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 *
 */
public class PersistenceException extends Exception {

	/** Default. */
	private static final long serialVersionUID = 1L;

	/** 
	 * Constructor. 
	 */
	public PersistenceException() {
		super();
	}
	
	/** 
	 * Constructor. 
	 * 
	 * @param text text 
	 */
	public PersistenceException(String text) {
		super(text);
	}
	
	/** 
	 * Constructor. 
	 * 
	 * @param ex cause
	 */
	public PersistenceException(Throwable ex) {
		super(ex);
	}
	
	/** 
	 * Constructor.
	 * 
	 * @param text text
	 * @param ex cause
	 */
	public PersistenceException(String text, Throwable ex) {
		super(text, ex);
	}
	
}
