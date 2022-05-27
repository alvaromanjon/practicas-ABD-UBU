package es.ubu.lsi.test.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilidad para ejecutar el script sql de borrado y creacion de datos. Permite
 * la ejecucion de los "tests" con independencia.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesus Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raul Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @version 1.0
 * @since 1.0 
 */
public class ExecuteScript {
	/** Logger. */
	private static Logger l = LoggerFactory.getLogger(ExecuteScript.class);;

	/**
	 * Principal.
	 * 
	 * @param args
	 *            el primer argumento incluye el nombre del script a ejecutar
	 */
	public static void main(String[] args) {
		run(args[0]);
		//run();
	}

	/**
	 * Ejecuta el script sql.
	 * 
	 * @param file_name
	 *            nombre del script .sql a ejecutar
	 */
	public static void run(String file_name) {
		String OS = System.getProperty("os.name").toLowerCase();
	
		try {
			
			String line;
			Process p=null;
			
			//if is linux
			if (OS.indexOf("nux") >= 0) {
				
				String sqlLauncher ="sql/lanza_sqlplus.sh";	
				p = Runtime.getRuntime().exec(new String[]{sqlLauncher,"hr/hr","@"+file_name});
				
			} else if (OS.indexOf("win") >= 0) { //if is windows
				p = Runtime.getRuntime().exec("sqlplus hr/hr @" + file_name);
			} else
				throw (new Exception("lsi.ubu.util.ExecuteScript.java"
						+ " Sistema operativo incompatible"));
			
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				l.info(line);
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
