package es.ubu.lsi.service.multas;

import java.util.Date;
import java.util.List;

import es.ubu.lsi.model.multas.Vehiculo;
import es.ubu.lsi.model.multas.TipoIncidencia;
import es.ubu.lsi.service.PersistenceException;

/**
 * Transaction service.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 *
 */
public interface Service {
	
	/** Máximo de puntos con el que se inicia el carné por puntos. */
	public static final int MAXIMO_PUNTOS = 12;
		
	/**
	 * Alta de una nueva incidencia sobre un conductor.
	 * 
	 * @param fecha fecha
	 * @param nif nif
	 * @param tipo tipo de incidencia
	 * @throws PersistenceException si se produce un error
	 * @see es.ubu.lsi.service.autoescula.IncidentError
	 */
	public void insertarIncidencia(Date fecha, String nif, long tipo) throws PersistenceException;

	/**
	 * Elimina todas las incidencias de un conductor poniendo sus puntos de nuevo a máximo.
	 * 
	 * @param nif nif
	 * @throws PersistenceException si se produce un error
	 * @see es.ubu.lsi.service.multas.IncidentError
	 */
	public void indultar(String nif) throws PersistenceException;
	
	/**
	 * Consulta vehiculos. En este caso en particular es importante recuperar 
	 * toda la información vinculada a las pistas de sus reservas y jugadores 
	 * para su visualización posterior.
	 * 
	 * @return vehiculos
	 * @throws PersistenceException si se produce un error
	 */
	public List<Vehiculo> consultarVehiculos() throws PersistenceException;

}
