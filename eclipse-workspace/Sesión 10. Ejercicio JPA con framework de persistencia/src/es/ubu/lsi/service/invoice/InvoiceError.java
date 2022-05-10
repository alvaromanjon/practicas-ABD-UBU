package es.ubu.lsi.service.invoice;

/**
 * Error code.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @since 1.0
 *
 */
public enum InvoiceError {
	// If we have more error messages, add new values to the enum type...
	NOT_EXIST_INVOICE_LINE("No existe esa línea de factura");
	
	/** Text. */
	private String text;
	
	/** Constructor. */
	private InvoiceError(String text) {
		this.text = text;
	}

	/**
	 * Gets text.
	 * 
	 * @return text
	 */
	public String getText() {
		return text;
	}
}
