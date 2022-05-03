package lsi.ubu.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pool de conexiones.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jes�s Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Ra�l Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */

public class PoolDeConexiones {
	// Configuraci�n de JNDI
	private static final String FILE_SYSTEM_CONTEXT_FACTORY = "com.sun.jndi.fscontext.RefFSContextFactory";
	private static final String FILE_RES = "file:./res";
	private static final String JDBC_TESTDB_DS = "jdbc/testdb_pooled";

	// Constantes de conexi�n
	private static final String DRIVER_TYPE = "thin";
	private static final int PORT = 1521;
	private static final String SID = "xe";
	private static final String HOST = "localhost";
	private static final String USER = "hr";
	
	//
	private static final String CONNECTION_FACTORY = "oracle.jdbc.pool.OracleDataSource";
	
	// Tip: si hay problemas de conexi�n, comprueba que en la BD el password est� en min�sculas,
	// prueba desde SQL*Plus o SQLDeveloper que efectivamente puedes conectarte con hr/hr.
	private static final String PASSWORD = "hr"; 
	
	/** Pool de conexiones. */
	private static PoolDeConexiones poolDeConexiones;
	
	/** DataSource. */
	private DataSource ds;
	
	/** Logger. */
	private static Logger logger = LoggerFactory.getLogger(PoolDeConexiones.class);
	
		
	/**
	 * Constructor del pool de conexiones, siguiendo el patr�n de dise�o Singleton.
	 * 
	 * @throws RuntimeException
	 *             si no encuentra el el recurso JNDI, y por tanto no logra instanciar el pool 
	 */
	private PoolDeConexiones() {	
		
		try {
			Properties properties = new Properties();
			properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,	FILE_SYSTEM_CONTEXT_FACTORY);
			properties.setProperty(Context.PROVIDER_URL, FILE_RES);
			Context context = new InitialContext(properties);

			ds = (DataSource) context.lookup(JDBC_TESTDB_DS);			
		
		} catch (NamingException e) {
			logger.error("Problema: no se encuentra el nombre del recurso en el contexto JNDI");
			logger.error(e.getMessage());
			
			//La aplicaci�n ya no puede seguir si no se instancia un pool
			throw new RuntimeException();
		}

			return;
		}
	
	/**
	 * Obtiene la instancia del pool de conexiones si no exist�a.
	 * 
	 */	
	public static PoolDeConexiones getInstance() {		
		
		if (poolDeConexiones == null) {
				poolDeConexiones = new PoolDeConexiones();
		}	
	
		return poolDeConexiones;
	}
	
	/**
	 * Obtiene una conexi�n.
	 * 
	 * @return conexi�n
	 * @throws SQLException
	 *             si hay un error con la base de datos
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = ds.getConnection();

		conn.setAutoCommit(false);
		logger.info("Activacion de Autocommit={}", conn.getAutoCommit());
		conn.setTransactionIsolation(
			Connection.TRANSACTION_READ_COMMITTED);
			//Connection.TRANSACTION_SERIALIZABLE);
		    //No v�lidos en Oracle:
				// conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				// conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		logger.info(traceConnectionSettings(conn));

		return conn;
	}
	
	/**
	 * Reconfigura el pool de conexiones volviendo a publicar la nueva
	 * configuraci�n.
	 * 
	 * @throws NamingException
	 *             si el nombre del recurso JNDI genera errores
	 * @throws SQLException
	 *             si hay un error con la base de datos
	 */
	public static void reconfigurarPool() 
            throws NamingException, SQLException {
		
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,	FILE_SYSTEM_CONTEXT_FACTORY);
		
		properties.setProperty(Context.PROVIDER_URL, FILE_RES);
		
		Context context = new InitialContext(properties);
		
		PoolDataSource  pds = 
			PoolDataSourceFactory.getPoolDataSource();
		pds.setConnectionFactoryClassName(CONNECTION_FACTORY);
		
		String url = "jdbc:oracle:" + DRIVER_TYPE + ":" + USER + "/" + PASSWORD + "@" + HOST + ":" + PORT + ":" + SID;
		pds.setURL(url);
			
		//// Configuraci�n del pool   
		pds.setMinPoolSize(3);
		pds.setMaxPoolSize(10);
		pds.setInitialPoolSize(5);
		pds.setTimeToLiveConnectionTimeout(18000);
		
		////Activaci�n de la cach� de sentencias prepradas (6 sentencias)
		pds.setMaxStatements(6);
		
		context.rebind(JDBC_TESTDB_DS, pds);
		
		logger.info("Contexto JNDI para el nombre "+JDBC_TESTDB_DS+" registrado OK en "+FILE_RES);
		return;
	}

	/**
	 * Consulta la configuraci�n de la conexi�n.
	 * 
	 * @param conn
	 *            conexi�n
	 * @return texto con el modo de autocommit y nivel de aislamiento actual
	 * @throws SQLException
	 *             si hay un error con la base de datos
	 */
	public String traceConnectionSettings(Connection conn) throws SQLException {
		String retorno = "Activacion de Autocommit=" + conn.getAutoCommit() + "\n";
		retorno += "Nivel de Aislamiento=";
		switch (conn.getTransactionIsolation()) {
		case Connection.TRANSACTION_NONE:
			retorno += "TRANSACTION_NONE";
			break;
		case Connection.TRANSACTION_READ_COMMITTED:
			retorno += "TRANSACTION_READ_COMMITTED";
			break;
		case Connection.TRANSACTION_READ_UNCOMMITTED:
			retorno += "TRANSACTION_READ_UNCOMMITTED";
			break;
		case Connection.TRANSACTION_REPEATABLE_READ:
			retorno += "TRANSACTION_REPEATABLE_READ";
			break;
		case Connection.TRANSACTION_SERIALIZABLE:
			retorno += "TRANSACTION_REPEATABLE_READ";
			break;
		default:
			throw new RuntimeException("Nivel de aislamiento no detectado. Revisar configuraci�n.");
		}
		return retorno;
	}

	/**
	 * Redimensiona el pool.
	 * 
	 * @param initialLimit
	 *            tama�o inicial
	 * @param minLimit
	 *            tama�o m�nimo
	 * @param maxLimit
	 *            tama�o m�ximo
	 * @throws SQLException
	 *             si hay un error con la cach� de conexiones
	 */	
	public void resizePool(int initialLimit, int minLimit, int maxLimit) throws SQLException {

		((PoolDataSource) ds).setMinPoolSize(minLimit);
		((PoolDataSource) ds).setMaxPoolSize(maxLimit);
		((PoolDataSource) ds).setInitialPoolSize(initialLimit);
		
		return;
	}
	
	/**
	 * Consulta la configuraci�n del data source de Oracle.
	 * 
	 * @param conn
	 *            conexi�n
	 * @return texto con las caracter�sticas actuales
	 * @throws SQLException
	 *             si hay un error con la base de datos
	 */
	public String traceOracleSettings() throws SQLException {
		PoolDataSource pds = (PoolDataSource) ds;
		String retorno = "trabajando con OracleDataSource\n";
		retorno += "Activacion de Cache de Sentencias Preparadas con " + pds.getMaxStatements()+" sentencias \n";
		
		retorno += "Tama�o Inicial Pool de Conexiones=" + pds.getInitialPoolSize() + "\n";
		retorno += "Tama�o Minimo Pool de Conexiones="  + pds.getMinPoolSize() + "\n";
		retorno += "Tama�o Maximo Pool de Conexiones="  + pds.getMaxPoolSize() + "\n";

		retorno += "Tiempo m�ximo de conexi�n viva=" + pds.getTimeToLiveConnectionTimeout() + "\n";

		return retorno;
	}

}
