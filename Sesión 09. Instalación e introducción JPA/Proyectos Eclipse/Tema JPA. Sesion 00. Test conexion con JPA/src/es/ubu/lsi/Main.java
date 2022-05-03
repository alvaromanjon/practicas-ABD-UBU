package es.ubu.lsi;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * First simple example with JPA. 
 * 
 * Warning: we are not using any kind of data access design pattern (yet).
 * 
 * Similar to our examples with JDBC.
 * 
 * @author Jesús Maudes
 * @author Raúl Marticorena
 * @author Mario Martínez
 * @since 1.0
 */
public class Main {
	
	/** Logger. */
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * Root.
	 * 
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			logger.info("Iniciando el primer ejemplo de JPA\n"+ 
						"con una simple inserción/borrado de una entidad persistente");
			// Context
			logger.info("ANTES DE CREAR FACTORY");
			emf = Persistence.createEntityManagerFactory("Ejemplo");
			logger.info("Creando factory");
			// "Connection"
			em = emf.createEntityManager();
						
			em.getTransaction().begin(); // BEGIN TRANSACTION

			listarRegiones(em); // warning: flush in AUTO
 		
			em.getTransaction().commit(); // END TRANSACTION
		}
		catch(Exception ex) {
			logger.error("Exception");
			ex.printStackTrace();
			if (em.getTransaction().isActive()) {
				System.out.println("Commit rollback");
				em.getTransaction().rollback();
			}
			logger.error(ex.getLocalizedMessage());
		} finally {
			System.out.println("Closing...");
			// closing resources
			em.close();
			emf.close();
		}

	}

	/**
	 * Shows all the students.
	 * 
	 * @param em entity manager
	 */
	private static void listarRegiones(EntityManager em) {
		// Using JPQL to read students from database...
		// Not typed query as first example (in the future, use typed query)
		Query query = em.createQuery("SELECT r FROM Region r", Region.class);
		List<Region> regiones = (List<Region>) query.getResultList();
		System.out.println("------- Listado -----------");
		for (Region alumno : regiones) {
			System.out.println(alumno.toString());
		}
		System.out.println("------- Fin listado ----------");
	}
}
