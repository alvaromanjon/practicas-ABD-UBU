package es.ubu.lsi.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.model.multas.Vehiculo;
import es.ubu.lsi.model.multas.Conductor;
import es.ubu.lsi.model.multas.Incidencia;
import es.ubu.lsi.model.multas.TipoIncidencia;
import es.ubu.lsi.service.PersistenceException;
import es.ubu.lsi.service.PersistenceFactorySingleton;
import es.ubu.lsi.service.multas.IncidentError;
import es.ubu.lsi.service.multas.IncidentException;
import es.ubu.lsi.service.multas.Service;
import es.ubu.lsi.service.multas.ServiceImpl;
import es.ubu.lsi.test.util.ExecuteScript;
import es.ubu.lsi.test.util.PoolDeConexiones;

/**
 * Test client.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 */
public class TestClient {

	/** Logger. */
	private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

	/** Connection pool. */
	private static PoolDeConexiones pool;

	/** Path. */
	private static final String SCRIPT_PATH = "sql/";

	/** Simple date format. */
	private static SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * Main.
	 * 
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Iniciando...");
			init();
			System.out.println("Probando el servicio...");
			testService();
			System.out.println("FIN.............");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error grave en la aplicación {}", ex.getMessage());
		}
	}

	/**
	 * Init pool.
	 */
	static public void init() {
		try {
			// Acuerdate de q la primera vez tienes que crear el .bindings con:
			//PoolDeConexiones.reconfigurarPool();
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
		Service implService = null;
		try {
			// JPA Service
			implService = new ServiceImpl();
			System.out.println("Framework y servicio iniciado...");

			// insertar incidencia para el conductor 10000000A con 3 puntos de penalización
			insertarIncidenciaCorrecta(implService);
			
			// intenta insertar con tipo de incidencia que no existe
			insertarIncidenciaConTipoIncidenciaErroneo(implService);

			// indultar a Juana con nif 10000000C, borrando sus dos incidencias y con 12
			// puntos
			indultarConductorConDosIncidencias(implService);
			
			// intenta indultar a un conductor que no existe
			indultarAUnConductorQueNoExiste(implService);

					
			// comprueba que la consulta de pistas carga todos los datos
			consultarVehiculosUsandoGrafo(implService);

		} catch (Exception e) { // for testing code...
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			pool = null;
		}
	} // testClient

	/**
	 * Indulta a conductor con dos incidencias.
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void indultarConductorConDosIncidencias(Service implService) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.print("Indulto del conductor...\n");
			implService.indultar("10000000C");

			con = pool.getConnection();
			// Comprobar si la incidencia se ha añadido
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM INCIDENCIA WHERE NIF='10000000C'");
			if (!rs.next()) {
				System.out.println("\tOK todas las incidencias borradas del conductor indultado");
			} else {
				System.out.println("\tERROR alguna incidencia no borrada del conductor indultado");
			}
			rs.close();
			rs = st.executeQuery("SELECT puntos FROM CONDUCTOR WHERE NIF='10000000C'");
			int puntos = -1;
			if (rs.next()) {
				puntos = rs.getInt(1);
			}
			if (puntos == Service.MAXIMO_PUNTOS) {
				System.out.println("\tOK puntos bien iniciados con indulto ");
			} else {
				System.out.println("\tERROR puntos mal iniciados con indulto, tiene " + puntos + " puntos");
			}
			rs.close();
			rs = st.executeQuery("SELECT count(*) FROM INCIDENCIA WHERE NIF<>'10000000C'");
			rs.next();
			int contador = rs.getInt(1);
			if (contador == 8) {
				System.out.println("\tOK el número de incidencias de otros conductores es correcto");
			} else {
				System.out.println("\tERROR el número de incidencias de otros conductores no es correcto");
			}
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			con.rollback();
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}
	
	/**
	 * Intenta indultar a un conductor que no existe.
	 * 
	 * @param implService servicio
	 */
	private static void indultarAUnConductorQueNoExiste(Service implService) {
		try {
			System.out.println("Indultar a un conductor que no existe");
			implService.indultar("00000000Z");
			System.out.println("\tERROR NO detecta que NO existe el conductor y finaliza la transacción");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_DRIVER) {
				System.out.println("\tOK detecta correctamente que NO existe ese conductor");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de indultar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en indultos en vehiculoss", ex);
		} catch(Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción de indultar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave en indultos en vehiculos", ex);
		}
	}

	/**
	 * Inserta una incidencia correcta.
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarIncidenciaCorrecta(Service implService) throws Exception {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.println("Insertar incidencia correcta");
			implService.insertarIncidencia(dateformat.parse("15/05/2019 16:00"), "10000000A", 3); // 3 es moderada con 3
																									// puntos
			// insertamos incidencia descontando 3 puntos al conductor 10000000A que tenía 9
			// inicialmente

			con = pool.getConnection();

			// Comprobar si la incidencia se ha añadido
			st = con.createStatement();
			rs = st.executeQuery("SELECT fecha||'-'||nif||'-'||idtipo FROM INCIDENCIA ORDER BY fecha, nif, idtipo");

			StringBuilder resultado = new StringBuilder();
			while (rs.next()) {
				resultado.append(rs.getString(1));
				resultado.append("\n");
			}
			logger.debug(resultado.toString());
			String cadenaEsperada =
			// @formatter:off
			"11/04/19 12:00:00,000000-10000000A-2\n" +
			"12/04/19 11:00:00,000000-10000000B-2\n" +
			"12/04/19 12:00:00,000000-10000000C-2\n" +
			"12/04/19 12:00:00,000000-20000000C-2\n" +
			"12/04/19 13:00:00,000000-10000000C-3\n" +
			"12/04/19 13:00:00,000000-20000000C-3\n" +
			"13/04/19 14:00:00,000000-30000000A-3\n" +			
			"13/04/19 15:00:00,000000-30000000B-2\n" +
			"13/04/19 16:00:00,000000-30000000C-1\n" +
			"15/05/19 16:00:00,000000-10000000A-3\n"; // nueva fila
			// @formatter:on
	
			if (cadenaEsperada.equals(resultado.toString())) {
				System.out.println("\tOK incidencia bien insertada");
			} else {
				System.out.println("\tERROR incidencia mal insertada");
			}
			rs.close();
			rs = st.executeQuery("SELECT puntos FROM conductor WHERE NIF='10000000A'");
			StringBuilder resultadoEsperadoPuntos = new StringBuilder();
			while (rs.next()) {
				resultadoEsperadoPuntos.append(rs.getString(1));
			}
			String puntosEsperados = "3"; // le deberíamos descontar 3 puntos quendado 6-3 = 3 puntos.
			if (puntosEsperados.equals(resultadoEsperadoPuntos.toString())) {
				System.out.println("\tOK actualiza bien los puntos del conductor");
			} else {
				System.out.println("\tERROR no descuenta bien los puntos de la incidencia del conductor");
			}
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			con.rollback();
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}

	/**
	 * Intenta insertar una incidencia cuyo tipo no existe.
	 * 
	 * @param implService servicio
	 */
	private static void insertarIncidenciaConTipoIncidenciaErroneo(Service implService) {
		try {
			System.out.println("Insertar incidencia con tipo erróneo");
			// fecha y usuario correcto
			implService.insertarIncidencia(dateformat.parse("15/05/2019 17:00"), "10000000A", 5); // 5 NO EXISTE
			System.out.println("\tERROR NO detecta que NO existe el tipo de incidencia y finaliza la transacción");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_INCIDENT_TYPE) {
				System.out.println("\tOK detecta correctamente que NO existe ese tipo de incidencia");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de inserción de incidencia con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en inserción de incidencia en vehiculos", ex);
		} catch(Exception ex) {
			logger.error("ERROR grave de programación en transacción de inserción de incicencia con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave en inserción de incidencia en vehiculos", ex);
		}
	}

	/**
	 * Prueba consulta de vehiculos, cargando datos completos desde un grafo de
	 * entidades.
	 * 
	 * @param implService implementación del servicio
	 */
	private static void consultarVehiculosUsandoGrafo(Service implService) {
		try {
			System.out.println("Información completa con grafos de entidades...");
			List<Vehiculo> vehiculos = implService.consultarVehiculos();		
			for (Vehiculo vehiclulo : vehiculos) {
				System.out.println(vehiclulo.toString());
				Set<Conductor> conductores = vehiclulo.getConductores();
				for (Conductor conductor : conductores) {
					System.out.println("\t" + conductor.toString());
					Set<Incidencia> incidencias = conductor.getIncidencias();
					for (Incidencia incidencia : incidencias) {
						System.out.println("\t\t" + incidencia.toString());
					}
				}
			}
			System.out.println("OK Sin excepciones en la consulta completa y acceso posterior");
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de consultas de vehiculos con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en consulta de vehiculos", ex);
		}
	}

	/**
	 * Cierra recursos de la transacción.
	 * 
	 * @param con conexión
	 * @param st  sentencia
	 * @param rs  conjunto de datos
	 * @throws SQLException si se produce cualquier error SQL
	 */
	private static void cerrarRecursos(Connection con, Statement st, ResultSet rs) throws SQLException {
		if (rs != null && !rs.isClosed())
			rs.close();
		if (st != null && !st.isClosed())
			st.close();
		if (con != null && !con.isClosed())
			con.close();
	}

} // TestClient
