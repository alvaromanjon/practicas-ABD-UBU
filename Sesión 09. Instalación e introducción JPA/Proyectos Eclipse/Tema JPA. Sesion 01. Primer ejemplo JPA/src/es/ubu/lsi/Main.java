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

			// Future persistent object
			Alumno juan = new Alumno();

			juan.setNIF("13113113A");
			juan.setNombre("Juan");
			juan.setApellido("Rondan");
			juan.setEdad(20);

			// Make persistent with an insert (using the entity manager)
			em.persist(juan);
						
			listarAlumnos(em); // warning: flush in AUTO

			// Example with an update (using a simple setter)
			juan.setEdad(21);  // warning: flush in AUTO		
			
			listarAlumnos(em);
			
			// Example with a delete (using the entity manager)
			em.remove(juan);	 		
			em.getTransaction().commit(); // END TRANSACTION
			
			listarAlumnos(em);
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
	private static void listarAlumnos(EntityManager em) {
		// Using JPQL to read students from database...
		// Not typed query as first simple example (in the future, we will use "typed query")
		Query query = em.createQuery("SELECT a FROM Alumno a", Alumno.class);
		List<Alumno> alumnos = (List<Alumno>) query.getResultList(); // warning by not using typed query...
		System.out.println("------- Listado de alumnos -----------");
		for (Alumno alumno : alumnos) {
			System.out.println(alumno.toString());
		}
		System.out.println("------- Fin listado alumnos ----------");
	}
}
