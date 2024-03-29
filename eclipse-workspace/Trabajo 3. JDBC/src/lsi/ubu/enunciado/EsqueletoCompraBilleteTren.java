package lsi.ubu.enunciado;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lsi.ubu.util.ExecuteScript;
import lsi.ubu.util.PoolDeConexiones;

/**
 * CompraBilleteTren:
 * Implementa el la compra de un billete de Tren
 * controlando si quedan plazas libres y si existe el viaje
 * segun PDF de la carpeta enunciado
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jes�s Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Ra�l Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */
public class EsqueletoCompraBilleteTren {

	private static Logger logger = LoggerFactory.getLogger(EsqueletoCompraBilleteTren.class);

	private static final String script_path = "sql/";


	public static void main(String[] args) {

		pruebaCompraBilletes();

		System.out.println("FIN.............");
	}



	static public void creaTablas() {
		ExecuteScript.run(script_path + "CompraBilleteTren");
	}

	private static java.util.Date toDate(String miString){ //convierte una cadena en fecha
		try{    
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Las M en mayusculas porque sino interpreta minutos!!
			java.util.Date fecha = sdf.parse(miString);
			return fecha;	    
		} catch (ParseException e){
			logger.error(e.getMessage());			
			return null;
		}
	}

	static void pruebaCompraBilletes() {
		creaTablas();	

		PoolDeConexiones pool = PoolDeConexiones.getInstance();

		Connection con=null;
		ResultSet rs=null;
		Statement st=null;

		//comprarBillete( Time p_hora, java.util.Date p_fecha, String p_origen, String p_destino, int p_nroPlazas)
		//Prueba caso no existe el viaje
		try{
			java.util.Date fecha = toDate("15/04/2010");
			Time hora = Time.valueOf("12:00:00");
			String origen="Burgos";
			String destino="Madrid";
			int nroPlazas=3; 

			comprarBillete( hora, fecha, origen, destino, nroPlazas);
			System.out.println("NO se da cuenta de que no existe el viaje MAL");
		} catch (SQLException e) {
			if (e.getErrorCode()==CompraBilleteTrenException.NO_EXISTE_VIAJE){
				System.out.println("Se da cuenta de que no existe el viaje OK");
			}
		}


		//Prueba caso si existe pero no hay plazas
		try{
			java.util.Date fecha=toDate("20/04/2022");
			Time hora = Time.valueOf("8:30:00");
			String origen="Burgos";
			String destino="Madrid";
			int nroPlazas=50;         
			comprarBillete( hora, fecha, origen, destino, nroPlazas);
			System.out.println("NO se da cuenta de que no hay plazas MAL");
		} catch (SQLException e) {
			if (e.getErrorCode()==CompraBilleteTrenException.NO_PLAZAS){
				System.out.println("Se da cuenta de que no hay plazas OK");
			}
		}

		//Prueba caso si existe y si hay plazas
		try{
			java.util.Date fecha=toDate("20/04/2022");
			Time hora = Time.valueOf("8:30:00");
			String origen="Burgos";
			String destino="Madrid";
			int nroPlazas=5;
			comprarBillete( hora, fecha, origen, destino, nroPlazas);

			String resultadoEsperado="11120/04/2225113550"; 
			String resultadoReal="";

			con = pool.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT IDVIAJE||IDTREN||IDRECORRIDO||FECHA||NPLAZASLIBRES||REALIZADO||IDCONDUCTOR" +
					"||IDTICKET||CANTIDAD||PRECIO" +
					" FROM VIAJES natural join tickets" +
					" where idticket=3" +
					" and trunc(fechacompra) = trunc(current_date)");        	 
			while (rs.next()){
				resultadoReal+=rs.getString(1);
			}

			//System.out.println("R"+resultadoReal);
			//System.out.println("E"+resultadoEsperado);
			if (resultadoReal.equals(resultadoEsperado)){
				System.out.println("Compra ticket OK");
			} else {
				System.out.println("Compra ticket MAL");
			}

		} catch (SQLException e) {
			System.out.println("Error inesperado MAL");  			
		}

		//Prueba borrar billete
		try{
			int id = 100;
			anularBillete(id);
			System.out.println("NO se da cuenta de que el ticket no existe MAL");
		} catch (SQLException e) {
			if (e.getErrorCode()==CompraBilleteTrenException.NO_EXISTE_TICKET) {
				System.out.println("Se da cuenta de que no existe el ticket OK");
			} else {
				System.out.println("Error inesperado MAL");
			}
		}	
	}

	public static int comprarBillete( Time p_hora, java.util.Date p_fecha, String p_origen, String p_destino, int p_nroPlazas)
			throws SQLException{

		/*Conversiones de fechas y horas*/
		java.sql.Date 			v_fecha = new java.sql.Date(p_fecha.getTime());
		java.sql.Timestamp 		v_hora =  new java.sql.Timestamp( p_hora.getTime() );

		PoolDeConexiones pool = PoolDeConexiones.getInstance();

		Connection con = null;
		PreparedStatement SEL_viajes = null;
		PreparedStatement UPD_viajes = null;
		PreparedStatement INS_ticket = null;
		PreparedStatement SEL_seq = null;

		ResultSet rs = null;   
		int idTicket = 0;

		try {
			con = pool.getConnection();

			SEL_viajes = con.prepareStatement("select * from viajes "
					+ "inner join recorridos on viajes.idRecorrido = recorridos.idRecorrido "
					+ "where (fecha = ? and estacionOrigen = ? and estacionDestino = ? "
					+ "and horaSalida-trunc(horaSalida) = ?-trunc(?))");

			SEL_viajes.setDate(1, v_fecha);
			SEL_viajes.setString(2, p_origen);
			SEL_viajes.setString(3, p_destino);
			SEL_viajes.setTimestamp(4, v_hora);
			SEL_viajes.setTimestamp(5, v_hora);

			rs = SEL_viajes.executeQuery();

			if (!rs.next()) {
				throw new CompraBilleteTrenException(2);
			}

			BigDecimal plazasLibres = rs.getBigDecimal("nPlazasLibres").subtract(new BigDecimal(p_nroPlazas));

			if (plazasLibres.compareTo(BigDecimal.ZERO) < 0) {
				throw new CompraBilleteTrenException(1);
			}

			UPD_viajes = con.prepareStatement("update (select viajes.nPlazasLibres as plazas "
					+ "from viajes inner join recorridos "
					+ "on viajes.idRecorrido = recorridos.idRecorrido "
					+ "where fecha = ? and estacionOrigen = ? and estacionDestino = ? "
					+ "and horaSalida-trunc(horaSalida) = ?-trunc(?)"
					+ ") t "
					+ "set t.plazas = t.plazas - ?");

			UPD_viajes.setDate(1, v_fecha);
			UPD_viajes.setString(2, p_origen);
			UPD_viajes.setString(3, p_destino);
			UPD_viajes.setTimestamp(4, v_hora);
			UPD_viajes.setTimestamp(5, v_hora);
			UPD_viajes.setInt(6, p_nroPlazas);

			UPD_viajes.executeUpdate();

			INS_ticket = con.prepareStatement("insert into tickets (idTicket, idViaje, fechaCompra,	cantidad, precio) "
					+ "values (seq_tickets.nextval, ?, CURRENT_DATE, ?, ?)");

			INS_ticket.setInt(1, rs.getInt("idViaje"));
			INS_ticket.setInt(2, p_nroPlazas);
			INS_ticket.setBigDecimal(3, (rs.getBigDecimal("precio")).multiply(new BigDecimal(p_nroPlazas)));

			INS_ticket.executeUpdate();

			SEL_seq = con.prepareStatement("select seq_tickets.currval from dual");
			rs = SEL_seq.executeQuery();

			if (rs.next()) {
				idTicket = rs.getInt(1);
			}

			con.commit();

		} catch(SQLException e) {
			logger.debug(e.getMessage());
			con.rollback();
			throw e;
		}
		finally {
			if (SEL_viajes != null) {
				SEL_viajes.close();
			}

			if (UPD_viajes != null) {
				UPD_viajes.close();
			}

			if (INS_ticket != null) {
				INS_ticket.close();
			}

			if (SEL_seq != null) {
				SEL_seq.close();
			}

			if (con != null) {
				con.close();
			}
		}

		return idTicket;
		/*Como comparar en Oracle campos TIME si no tiene TIMEs
		 * trunc(timestamp) te devuelve la fecha pero con la hora 00:00
		 * Si a un timestamp le restas trunc(timestamp) te queda el tiempo transcurrido desde las 00:00
		 * o lo que es lo mismo, la hora
		 * 
		 * Por ello el where tendra una condicion de esta pinta
		 *  	
		 *	    						  AND horasalida-trunc(horaSalida) = ?-trunc(?)
		 *
		 * La interrogacion sustituirla por
		 *  pst.setTimestamp(x, v_hora);
		 *  pst.setTimestamp(x+1, v_hora);
		 *  
		 *  donde x es el numero de parametro de la primera de las interrogaciones
		 *  y pst la sentencia preparada correspondiente
		 */	

		/*  	
            En el insert el valor de precio seria de la seguiente forma
            INS_ticket.setBigDecimal(3, (rs.getBigDecimal("precio")).multiply(new BigDecimal(p_nroPlazas)));


		 */
	}

	public static void anularBillete(int p_idTicket) throws SQLException {
		PoolDeConexiones pool = PoolDeConexiones.getInstance();
		Connection con = null;
		PreparedStatement SEL_ticket = null;
		PreparedStatement DEL_ticket = null;
		ResultSet rs = null; 
		
		try {
			con = pool.getConnection();
			
			SEL_ticket = con.prepareStatement("select * from tickets where idTicket = ?");
			SEL_ticket.setInt(1, p_idTicket);
			rs = SEL_ticket.executeQuery();
			
			if (!rs.next()) {
				throw new CompraBilleteTrenException(3);
			}
			
			DEL_ticket = con.prepareStatement("delete from tickets where idTicket = ?");
			DEL_ticket.setInt(1, p_idTicket);
			DEL_ticket.executeUpdate();
			
			con.commit();
		} catch (SQLException e) {
			logger.debug(e.getMessage());
			con.rollback();
			throw e;
		} 
		finally {
			if (SEL_ticket != null) {
				SEL_ticket.close();
			}
			
			if (DEL_ticket != null) {
				DEL_ticket.close();
			}

			if (con != null) {
				con.close();
			}
		}
	}
}
