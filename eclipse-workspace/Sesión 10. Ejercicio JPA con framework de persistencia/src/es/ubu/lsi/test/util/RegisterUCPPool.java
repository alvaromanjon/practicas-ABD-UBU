package es.ubu.lsi.test.util;

import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Reconfigurar UCP Pool con JNDI. 
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @version 1.0
 * @since 1.0 
 */

public class RegisterUCPPool {
	
	/** Name of UCP Pool. */
	public static final String JDBC_UCP_POOL = "jdbc/UCP_pool";

	public static void main(String[] args) throws NamingException, SQLException {
		reconfigurarPool();		
		/*
		 * Examina tu mismo si ha añadido las propiedades de la
		 * conexión con el nombre "jdbc/UCP_pool" al fichero res/.bindings en Windows
		 * o "UCP_pool" al fichero res/jdbc/.bindings en Linux
		 * 
		 * Puedes borrar el fichero res/.bindings (Win) o res/jdbc/.bindings (Linux)
		 * antes de ejecutar el main para comprobar si lo crea.	  
		 */
		System.out.println("UCP for testing OK");
	}
	
	public static void reconfigurarPool() 
            throws NamingException, SQLException {
		
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.fscontext.RefFSContextFactory");
		
		properties.setProperty(Context.PROVIDER_URL, "file:./res");
		
		Context context = new InitialContext(properties);
		
		PoolDataSource  pds = 
			PoolDataSourceFactory.getPoolDataSource();
		pds.setConnectionFactoryClassName(
			"oracle.jdbc.pool.OracleDataSource");
		pds.setURL("jdbc:oracle:thin:HR/hr@localhost:1521:xe");
			
		//// Configuración del pool   
		pds.setMinPoolSize(3);
		pds.setMaxPoolSize(10);
		pds.setInitialPoolSize(5);
		pds.setTimeToLiveConnectionTimeout(18000);
		
		context.rebind(JDBC_UCP_POOL, pds);
	}

}
