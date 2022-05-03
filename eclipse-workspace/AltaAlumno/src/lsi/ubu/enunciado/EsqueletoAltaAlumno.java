package lsi.ubu.enunciado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lsi.ubu.util.ExecuteScript;
import lsi.ubu.util.PoolDeConexiones;

/**
 * AltaAlumno:
 * Implementa el alta de un alumno en un grupo de una asignatura
 * segun PDF de la carpeta enunciado
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @version 1.0
 * @since 1.0 
 */
public class EsqueletoAltaAlumno {
	
	private static Logger logger = LoggerFactory.getLogger(EsqueletoAltaAlumno.class);

	private static final String script_path = "sql/";
	

	public static void main(String[] args) throws SQLException{		

		ExecuteScript.run(script_path + "AltaAlumno.sql");
		
		PoolDeConexiones p = PoolDeConexiones.getInstance();
		
		Connection con=null;
		ResultSet rs=null;
		Statement st=null;
		
		//public static void matricular(String p_alumno, String p_asig, int p_grupo) throws SQLExceptionProblema6{
		
		try{	
			//Matriculamos en el grupo lleno			
			try{
				matricular("Julian","OFIM",1); //sale mal porque el grupo esta lleno
				System.out.println("NO se da cuenta de que el grupo esta lleno MAL");
			} catch (AltaAlumnoException e) {
				if (e.getErrorCode()==AltaAlumnoException.SIN_PLAZAS){
					System.out.println("Se da cuenta de que el grupo esta lleno OK");
				}
			}
			
			try{
				matricular("Pedro","OFIM",3); //sale mal porque no existe el grupo
				System.out.println("NO se da cuenta de que el grupo no existe MAL");
			} catch (AltaAlumnoException e) {
				if (e.getErrorCode()==AltaAlumnoException.NO_EXISTE_ASIG_O_GRUPO){
					System.out.println("Se da cuenta de que el grupo no existe OK");
				}
			}
			
			try{
				matricular("Pedro","ALGEBRA",1); //sale mal porque no existe la asignatura
				System.out.println("NO se da cuenta de que la asignatura no existe MAL");
			} catch (AltaAlumnoException e) {
				if (e.getErrorCode()==AltaAlumnoException.NO_EXISTE_ASIG_O_GRUPO){
					System.out.println("Se da cuenta de que la asignatura no existe OK");
				}
			}
			
			con = p.getConnection();
			matricular("Pedro","FPROG",1);
			
			st = con.createStatement();
			rs = st.executeQuery("select idgrupo||grupos.asignatura||plazaslibres||idmatricula||alumno" +
					" from grupos left join matriculas on(matriculas.grupo=grupos.idGrupo)");
			String resultado="";
			while (rs.next()){
				resultado+=rs.getString(1);				
			}
			//System.out.println(resultado);
			String teniaQueDar="1OFIM01PEPE1FPROG31PEPE1OFIM02ANA1FPROG32ANA1OFIM03JUAN1FPROG33JUAN1OFIM04LUIS1FPROG34LUIS2OFIM15ANTONIO2FPROG45ANTONIO2OFIM16MERCEDES2FPROG46MERCEDES2OFIM17JESUS2FPROG47JESUS1OFIM011Pedro1FPROG311Pedro10OFIM4";
			
			if (resultado.equals(teniaQueDar)){
				System.out.println("Matricula OK");
			} else {
				System.out.println("Matricula MAL");
			}
						
			
		} catch (SQLException e){
			if (con!=null) con.rollback();
			logger.error(e.getMessage());
		} finally {
			if (rs!=null) rs.close();
			if (st!=null) st.close();
			if (con!=null) con.close();			
		}		
		
	

		System.out.println("FIN.............");
	}

	
	public static void matricular(String arg_alumno, String arg_asig, int arg_grupo) throws SQLException{
    	
    	PoolDeConexiones p = PoolDeConexiones.getInstance();
    	
    	Connection con = null;
    	PreparedStatement ins_matricula = null;
    	PreparedStatement upd_plazas_libres = null;
    }
    
	
	
}
