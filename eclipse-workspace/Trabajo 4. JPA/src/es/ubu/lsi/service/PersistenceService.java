package es.ubu.lsi.service;

import javax.persistence.EntityManager;

/**
 * Persistence service. Define the basic methods in its lifecycle.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 */
public class PersistenceService {

	/** Creates session. */
	public EntityManager createSession() {
		return PersistenceFactorySingleton.getEntityManager();
	}

	/**
	 * Rollbacks transaction.
	 * 
	 * @param em
	 *            entity manager
	 */
	protected void rollbackTransaction(EntityManager em) {
		if (em != null) {
			em.getTransaction().rollback();
		} else {
			throw new IllegalArgumentException("Entity manager with null value.");
		}
	}

	/**
	 * Commits transaction.
	 * 
	 * @param em
	 *            entity manager
	 */
	protected void commitTransaction(EntityManager em) {
		if (em != null) {
			em.getTransaction().commit();
		} else {
			throw new IllegalArgumentException("Entity manager with null value.");
		}
	}

	/**
	 * Begins transaction.
	 * 
	 * @param em
	 *            entity manager
	 */
	protected void beginTransaction(EntityManager em) {
		if (em != null) {
			em.getTransaction().begin();
		} else {
			throw new IllegalArgumentException("Entity manager with null value.");
		}
	}

	/**
	 * Closes resources.
	 * 
	 * @param em
	 *            entity manager
	 */
	protected void close(EntityManager em) {
		if (em != null && em.isOpen()) {
			em.close();
		} else {
			throw new IllegalArgumentException("Entity manager with null value or closed.");
		}
		
	}
}