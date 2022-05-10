package es.ubu.lsi.service.invoice;

import es.ubu.lsi.service.PersistenceException;

/**
 * Transaction service.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @since 1.0
 *
 */
public interface Service {

	/**
	 * Remove the invoice line.
	 * 
	 * @param line invoice line
	 * @param nro invoice nro
	 * 
	 * @throws PersistenceException if exists any error
	 */
	public  void borrarLinea(int line, int nro)
			throws PersistenceException;
}
