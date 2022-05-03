import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Prueba {
	private static Logger l = null;
	
	public static void main(String[] args) {
		
		l =	LoggerFactory.getLogger(Prueba.class);
		
		
		l.trace("Esta es una notificacion trace, Log4j la ignora, "
				+ " pero otros loggers no, es la de menor importancia"
				+ " En la practica no la usaremos");
		
		l.debug("Esta es una notificacion debug, la usaremos a modo de siembra de printfs");
		l.info("Esta es una notificacion info, la usaremos para corraborar que el flujo del programa es el esperado");
		l.warn("Esta es una notificacion warn, la usaremos para los warnings");
		l.error("Esta es una notificacion error, la usaremos cuando se lance una excepcion");
		
		/*
		 * Cambia en el fichero src\log4j.properties la linea
		 *  log4j.appender.theFileAppender.Threshold= ... 
		 *  	con el nivel que creas oportuno para lo que se registre en res/log4.log
		 *  
		 *  log4j.appender.theConsoleAppender.Threshold= ... 
		 *  	con el nivel que creas oportuno para lo que se muestre por consola
		 * 
		 * Threshold=OFF   => se desactiva ese dispositivo
		 * Threshold=ERROR => se ve solo error
		 * Threshold=WARN => se ve warn y error
		 * Threshold=INFO => se ve info, warn y error 
		 * Threshold=DEBUG => se ven todos
		 * 
		 */
		
		
		
				

	}

}
