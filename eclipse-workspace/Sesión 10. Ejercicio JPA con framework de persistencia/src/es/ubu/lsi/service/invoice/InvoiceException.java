package es.ubu.lsi.service.invoice;

import es.ubu.lsi.service.PersistenceException;

/**
 * Invoice exception.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @since 1.0
 *
 */
public class InvoiceException extends PersistenceException {
	
	/** Error code. */
	private InvoiceError error;

	/** Default. */
	private static final long serialVersionUID = 1L;

	/** 
	 * Constructor. 
	 * 
	 * @param text text
	 */
	public InvoiceException(String text) {
		super(text);
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param error error code
	 */
	public InvoiceException(InvoiceError error) {
		super(error.getText());
		this.error = error;
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param error error code
	 */
	public InvoiceException(String text, Exception ex) {
		super(text, ex);
	}
	
	
	
	/**
	 * Gets error code.
	 * 
	 * @return error code
	 * @return
	 */
	public InvoiceError getError(){
		return error;
	}
}
