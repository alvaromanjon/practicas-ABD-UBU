package es.ubu.lsi.model.multas;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the INCIDENCIA database table.
 * 
 */
@Entity
@NamedQuery(name="Incidencia.findAll", query="SELECT i FROM Incidencia i")
public class Incidencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private IncidenciaPK id;

	@Lob
	private String anotacion;

	//bi-directional many-to-one association to Conductor
	@ManyToOne
	@JoinColumn(name="NIF")
	private Conductor conductor;

	//bi-directional many-to-one association to Tipoincidencia
	@ManyToOne
	@JoinColumn(name="IDTIPO")
	private Tipoincidencia tipoincidencia;

	public Incidencia() {
	}

	public IncidenciaPK getId() {
		return this.id;
	}

	public void setId(IncidenciaPK id) {
		this.id = id;
	}

	public String getAnotacion() {
		return this.anotacion;
	}

	public void setAnotacion(String anotacion) {
		this.anotacion = anotacion;
	}

	public Conductor getConductor() {
		return this.conductor;
	}

	public void setConductor(Conductor conductor) {
		this.conductor = conductor;
	}

	public Tipoincidencia getTipoincidencia() {
		return this.tipoincidencia;
	}

	public void setTipoincidencia(Tipoincidencia tipoincidencia) {
		this.tipoincidencia = tipoincidencia;
	}

	@Override
	public String toString() {
		return "Incidencia: id - " + id + ", anotación - " + anotacion 
				+ ", conductor - " + conductor + ", tipo de incidencia - " 
				+ tipoincidencia;
	}
}