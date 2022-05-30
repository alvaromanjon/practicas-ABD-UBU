package es.ubu.lsi.model.multas;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the INCIDENCIA database table.
 * 
 */
@Embeddable
public class IncidenciaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date fecha;

	@Column(insertable=false, updatable=false)
	private String nif;

	public IncidenciaPK() {
	}
	
	public java.util.Date getFecha() {
		return this.fecha;
	}
	public void setFecha(java.util.Date fecha) {
		this.fecha = fecha;
	}
	public String getNif() {
		return this.nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof IncidenciaPK)) {
			return false;
		}
		IncidenciaPK castOther = (IncidenciaPK)other;
		return 
			this.fecha.equals(castOther.fecha)
			&& this.nif.equals(castOther.nif);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.fecha.hashCode();
		hash = hash * prime + this.nif.hashCode();
		
		return hash;
	}
	
	@Override
	public String toString() {
		return "IncidenciaPK: NIF - " + nif + ", fecha - " + fecha;
	}
}