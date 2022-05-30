package es.ubu.lsi.model.multas;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: DireccionPostal
 *
 */
@Embeddable

public class DireccionPostal implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String direccion;
	
	private String cp;
	
	private String ciudad;
	
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String getCodigoPostal() {
		return cp;
	}
	
	public void setCodigoPostal(String cp) {
		this.cp = cp;
	}
	
	public String getCiudad() {
		return ciudad;
	}
	
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	
	@Override
	public String toString() {
		return "DireccionPostal: Dirección - " + direccion + ", código postal - " 
				+ cp + ", ciudad - " + ciudad;
	}
}
