package es.ubu.lsi;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Alumno
 *
 * @author Jesús Maudes
 * @author Raúl Marticorena
 * @author Mario Martínez
 * @since 1.0
 */
@Entity
public class Alumno implements Serializable {

	@Id
	private String NIF;
	private String nombre;
	private String apellido;
	private int edad;
	private static final long serialVersionUID = 1L;

	public Alumno() {
		super();
	}   
	public String getNIF() {
		return this.NIF;
	}

	public void setNIF(String NIF) {
		this.NIF = NIF;
	}   
	public String getName() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}   
	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}   
	public int getEdad() {
		return this.edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Alumno [NIF=").append(NIF).append(", nombre=")
				.append(nombre).append(", apellido=").append(apellido)
				.append(", edad=").append(edad).append("]");
		return builder.toString();
	}
   
}
