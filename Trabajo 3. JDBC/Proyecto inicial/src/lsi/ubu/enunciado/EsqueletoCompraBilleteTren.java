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
          
			
	}
	
	public static void comprarBillete( Time p_hora, java.util.Date p_fecha, String p_origen, String p_destino, int p_nroPlazas)
			throws SQLException{
		
		PoolDeConexiones pool = PoolDeConexiones.getInstance();
		
		/*Conversiones de fechas y horas*/
		java.sql.Date 			v_fecha = new java.sql.Date(p_fecha.getTime());
		java.sql.Timestamp 		v_hora =  new java.sql.Timestamp( p_hora.getTime() );
		
		Connection con = null;
    	PreparedStatement SEL_viajes = null;
    	PreparedStatement UPD_viajes = null;
    	PreparedStatement INS_ticket = null;
    	
    	ResultSet rs = null;   
		
		//A completar por el alumno
		
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
}
