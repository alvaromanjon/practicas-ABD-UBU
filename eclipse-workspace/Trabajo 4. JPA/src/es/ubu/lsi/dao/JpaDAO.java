package es.ubu.lsi.dao;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

/**
 * JPA DAO generic implementation. All JPA DAO should inherit from this class.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * 
 * @param <E> element type (entity)
 * @param <K> key type
 */
public abstract class JpaDAO<E,K> implements DAO<E,K> {
	
	/** Index of entity formal par. */
	private static final int ENTITY_FORMAL_PAR = 0;

	/**
	 * Entity class.
	 */
	protected Class<?> entityClass;

	/**
	 * Entity manager.
	 */
	protected EntityManager entityManager;

	/**
	 * Constructor.
	 */
	public JpaDAO(EntityManager em) {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<?>) genericSuperclass.getActualTypeArguments()[ENTITY_FORMAL_PAR];
		this.setEntityManager(em);
	}
	
	/** 
	 * Sets entity manager. 
	 * 
	 * @param em entity manager.
	 */
	private void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}
	
	/**
	 * Gets entity manager.
	 * 
	 * @return entity manager
	 */
	protected EntityManager getEntityManager() {
		return this.entityManager;
	}

	/** 
	 * Persists entity.
	 * 
	 * @param entity entity
	 */
	public void persist(E entity) { entityManager.persist(entity); }

	/**
	 * Removes entity.
	 * 
	 * @param entity entity
	 */
	public void remove(E entity) { entityManager.remove(entity); }

	/**
	 * Finds entity by primary key.
	 * 
	 * @param id key
	 * @return found entity with that primary key
	 */
	@SuppressWarnings("unchecked")
	public E findById(K id) { return (E) entityManager.find(entityClass, id); }
}
