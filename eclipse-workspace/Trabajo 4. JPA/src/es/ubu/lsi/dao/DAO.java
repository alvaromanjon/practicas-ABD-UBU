package es.ubu.lsi.dao;

import java.util.List;

/**
 * DAO.
 * 
 * @param <E> entity type
 * @param <K> key type
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 */
public interface DAO<E,K> {
	/** 
	 * Persist. 
	 *  
	 * @param entity entity
	 */
	void persist(E entity);

	/**
	 * Remove.
	 * 
	 * @param entity entity
	 */
	void remove(E entity);
	
	/**
	 * Find by primary key.
	 * 
	 * @param id value
	 * @return entity
	 */
	E findById(K id);
	
	/**
	 * Find all entities.
	 * 
	 * @return all entities
	 */
	List<E> findAll();
}