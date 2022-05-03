/*
 Ilustracion 3: Registrando en un contexto JNDI la configuraci�n de un pool.
*/

package lsi.ubu;

import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Reconfigurar Pool con JNDI. 
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */

public class ReconfigurarPool {

	public static void main(String[] args) throws NamingException, SQLException {
		reconfigurarPool();
		
		/*
		 * Examina tu mismo si ha a�adido las propiedades de la
		 * conexion con el nombre "jdbc/testdb_pooled" a el
		 * fichero res/.bindings
		 * 
		 * Puedes borrar el fichero res/.bindings antes de ejecutar el main
		 * para ver si lo crea		  
		 */

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
		pds.setURL("jdbc:oracle:thin:hr/hr@LVAROMANJNVB8FC.local:1521:xe");
			
		//// Configuracion del pool   
		pds.setMinPoolSize(3);
		pds.setMaxPoolSize(10);
		pds.setInitialPoolSize(5);
		pds.setTimeToLiveConnectionTimeout(18000);
		
		context.rebind("jdbc/testdb_pooled", pds);
	}

}
