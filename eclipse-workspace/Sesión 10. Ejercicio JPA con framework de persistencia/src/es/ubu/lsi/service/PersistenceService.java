package es.ubu.lsi.service;

import javax.persistence.EntityManager;

/**
 * Persistence service. Define the basic methods in its lifecycle.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @since 1.0
 */
public class PersistenceService {
	
	/** Creates session. */
	public EntityManager createSession(){
		return PersistenceFactorySingleton.getEntityManager();
	}

	/**
	 * Rollbacks transaction.
	 * 
	 * @param em entity manager
	 */
	protected void rollbackTransaction(EntityManager em) {
		em.getTransaction().rollback();
	}

	/**
	 * Commits transaction.
	 * 
	 * @param em entity manager
	 */
	protected void commitTransaction(EntityManager em) {
		em.getTransaction().commit();
	}

	/**
	 * Begins transaction.
	 * 
	 * @param em entity manager
	 */
	protected void beginTransaction(EntityManager em) {
		em.getTransaction().begin();
	}
	
	/**
	 * Closes resources.
	 * 
	 * @param em entity manager
	 */
	protected void close(EntityManager em) {
		if (em.isOpen()) em.close();
	}
}