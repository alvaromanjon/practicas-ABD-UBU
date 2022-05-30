package es.ubu.lsi.model.multas;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the TIPOINCIDENCIA database table.
 * 
 */
@Entity
@NamedQuery(name="Tipoincidencia.findAll", query="SELECT t FROM Tipoincidencia t")
public class Tipoincidencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPOINCIDENCIA_ID_GENERATOR", sequenceName="TIPOINCIDENCIASEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPOINCIDENCIA_ID_GENERATOR")
	private long id;

	private String descripcion;

	private BigDecimal valor;

	//bi-directional many-to-one association to Incidencia
	@OneToMany(mappedBy="tipoincidencia")
	private List<Incidencia> incidencias;

	public Tipoincidencia() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public List<Incidencia> getIncidencias() {
		return this.incidencias;
	}

	public void setIncidencias(List<Incidencia> incidencias) {
		this.incidencias = incidencias;
	}

	public Incidencia addIncidencia(Incidencia incidencia) {
		getIncidencias().add(incidencia);
		incidencia.setTipoincidencia(this);

		return incidencia;
	}

	public Incidencia removeIncidencia(Incidencia incidencia) {
		getIncidencias().remove(incidencia);
		incidencia.setTipoincidencia(null);

		return incidencia;
	}
	
	@Override
	public String toString() {
		return "Tipo de incidencia: id - " + id + ", descripción - " + descripcion 
				+ ", valor - " + valor + ", incidencias - " + incidencias;
	}
}