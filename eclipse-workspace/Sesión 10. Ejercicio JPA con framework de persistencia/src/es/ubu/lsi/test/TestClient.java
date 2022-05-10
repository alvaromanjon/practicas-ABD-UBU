package es.ubu.lsi.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.service.invoice.InvoiceError;
import es.ubu.lsi.service.invoice.InvoiceException;
import es.ubu.lsi.service.invoice.Service;
import es.ubu.lsi.service.invoice.ServiceImpl;
import es.ubu.lsi.test.util.ExecuteScript;
import es.ubu.lsi.test.util.PoolDeConexiones;

/**
 * Test client. Testing JPA transactions using JDBC code.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @since 1.0
 */
public class TestClient {

	/** Logger. */
	private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

	/** Connection pool. */
	private static PoolDeConexiones pool;

	/** Path. */
	private static final String SCRIPT_PATH = "sql/";

	/**
	 * Main.
	 * 
	 * @param args
	 *            arguments.
	 */
	public static void main(String[] args) {
		try {
			System.out.println("INICIANDO TEST...");
			init();
			System.out.println("Probando el servicio...");
			testService();
			System.out.println("FIN TEST.............");
		}
		catch(Exception ex) {
			ex.printStackTrace(); 
			logger.error("ERROR GRAVE en la aplicación {}", ex.getMessage());
		}
	}

	/**
	 * Init pool.
	 */
	static public void init() {
		try {
			// Acuerdate de q la primera vez tienes que crear el .bindings con:
			// PoolDeConexiones.reconfigurarPool();

			// Inicializacion de Pool
			pool = PoolDeConexiones.getInstance();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Create tables.
	 */
	static public void createTables() {
		ExecuteScript.run(SCRIPT_PATH + "script.sql");
	}

	/**
	 * Test service using JDBC and JPA.
	 */
	static void testService() throws Exception {
		createTables();

		Connection connectionTest = null;
		ResultSet rs = null;
		Statement st = null;

		Service implService = null;

		try {
			// JPA Service
			implService = new ServiceImpl(); // DARA ERROR DE COMPILACIÓN PORQUE HAY QUE IMPLEMENTAR LA CLASE
			System.out.println("OK Framework y servicio iniciado");
			implService.borrarLinea(2, 1); // borrar linea 2 de la factura 1
			System.out.println("OK Transacción de borrado realizada");

			// Begin first test...
			connectionTest = pool.getConnection();

			// Comprobar si el join de facturas con lineas tiene todos los datos
			// menos la linea borrada, y ademas el importe esta restado
			st = connectionTest.createStatement();
			rs = st.executeQuery("SELECT f.nro||cliente||fecha||total||linea||descripcion||unidades||importe "
					+ "FROM facturas f INNER JOIN lineasFactura lf ON f.nro=lf.nro");

			StringBuilder resultado = new StringBuilder();
			while (rs.next()) {
				resultado.append(rs.getString(1));
			}

			logger.debug(resultado.toString());
			String cadenaEsperada = "1Pepe03/04/13101Articulo15102Ana03/02/13251Articulo15102Ana03/02/13252Articulo21515";
			logger.debug(cadenaEsperada);

			if (cadenaEsperada.equals(resultado.toString())) {
				System.out.println("OK Borrado");
			} else {
				System.out.println("ERROR Borrado");
			}

			// Comprobamos lanza excepcion

			// Begin second test...
			try {
				implService.borrarLinea(2, 1); // borrar linea 2 de la factura 1
												// otra vez
				System.out.println("ERROR NO se da cuenta de que no existe la linea");
			} catch (InvoiceException e) {
				if (e.getError() == InvoiceError.NOT_EXIST_INVOICE_LINE) {
					System.out.println("OK Se da cuenta de que no existe la linea de factura");
				} else {
					System.err.println("ERROR No ha clasificado correctamente el error, no existe la linea de factura");
				}
			} // catch
			connectionTest.commit();
			logger.debug("COMMIT de la transacción de testing");
		} catch (Exception e) { // for testing code...
			e.printStackTrace();
			if (connectionTest != null) {
				connectionTest.rollback();
				logger.debug("ROLLBACK de la transacción de testing");
			}
			logger.error(e.getMessage());		
		} finally {
			if (rs!=null) rs.close();
			if (st!=null) st.close();
			if (connectionTest != null) connectionTest.close();			
		} // finally
	} // testClient

} // TestClient
