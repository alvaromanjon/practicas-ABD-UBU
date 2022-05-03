package lsi.ubu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Pool de conexiones.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */

public class PoolDeConexiones {
	
	/** Pool de conexiones. */
	private static PoolDeConexiones poolDeConexiones;
	
	/** DataSource. */
	private DataSource ds;

	public static void main(String[] args) {
		
		PoolDeConexiones pool = null;
		Connection conn = null;
				
		try {
				pool = PoolDeConexiones.getInstance();
				conn = pool.getConnection();
				
				System.out.println("Conexion creada.");				
				System.out.println("La conexion es valida: "+conn.isValid(0));
				
		} catch ( SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			
			try {
				conn.close();
				System.out.println("Conexion cerrada.");
				System.out.println("La conexion es valida: "+conn.isValid(0));
			} catch ( SQLException e ) {
				System.out.println("Error al cierre de la conexion.");
				System.out.println(e.getMessage());
			}
		}

	}
	
	/**
	 * Constructor del pool de conexiones, siguiendo el patron de dise�o Singleton.
	 * 
	 * @throws RuntimeException
	 *             si no encuentra el el recurso JNDI, y por tanto no logra instanciar el pool 
	 */
	private PoolDeConexiones() {
		
		try {
			Properties properties = new Properties();
			properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,	"com.sun.jndi.fscontext.RefFSContextFactory");
			properties.setProperty(Context.PROVIDER_URL, "file:./res");
			Context context = new InitialContext(properties);

			ds = (DataSource) context.lookup("jdbc/testdb_pooled");			
		
		} catch (NamingException e) {
			System.out.println("Problema no se encuentra el nombre del recurso en el contexto JNDI");
			
			e.printStackTrace();
			//La aplicacion ya no puede seguir si no se instancia un pool
			throw new RuntimeException();
		}

			return;
		}
	
	/**
	 * Obtiene la instancia del pool de conexiones si no existia.
	 * 
	 */	
	public static PoolDeConexiones getInstance() {		
		
		if (poolDeConexiones == null) {
				poolDeConexiones = new PoolDeConexiones();
		}	
	
		return poolDeConexiones;
	}
	
	/**
	 * Obtiene una conexion.
	 * 
	 * @return conexion
	 * @throws SQLException
	 *             si hay un error con la base de datos
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = ds.getConnection();

		conn.setAutoCommit(false);
		conn.setTransactionIsolation(
			Connection.TRANSACTION_READ_COMMITTED);
			//Connection.TRANSACTION_SERIALIZABLE);

		return conn;
	}

}
