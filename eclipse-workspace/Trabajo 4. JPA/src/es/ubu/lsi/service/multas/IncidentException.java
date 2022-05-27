package es.ubu.lsi.service.multas;

import es.ubu.lsi.service.PersistenceException;

/**
 * Reservation exception.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 *
 */
public class IncidentException extends PersistenceException {

	/** Error code. */
	private IncidentError error;

	/** Default. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            text
	 */
	public IncidentException(String text) {
		super(text);
	}

	/**
	 * Constructor.
	 * 
	 * @param error
	 *            error code
	 */
	public IncidentException(IncidentError error) {
		super(error.getText());
		setError(error);
	}

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            text
	 * @param ex
	 *            exception
	 */
	public IncidentException(String text, Exception ex) {
		super(text, ex);
	}

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            text
	 * @param error
	 *            error code
	 * @param ex
	 *            exception
	 */
	public IncidentException(String text, IncidentError error, Exception ex) {
		super(text, ex);
		setError(error);
	}

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            text
	 * @param error
	 *            error code
	 */
	public IncidentException(String text, IncidentError error) {
		super(text);
		setError(error);
	}

	/**
	 * Constructor.
	 * 
	 * @param error
	 *            error code
	 * @param ex
	 *            exception
	 */
	public IncidentException(IncidentError error, Exception ex) {
		super(ex);
		setError(error);
	}

	/**
	 * Gets error code.
	 * 
	 * @return error code
	 * @return
	 */
	public IncidentError getError() {
		return error;
	}

	/**
	 * Sets error code.
	 * 
	 * @param error
	 *            error
	 */
	private void setError(IncidentError error) {
		this.error = error;
	}
}
