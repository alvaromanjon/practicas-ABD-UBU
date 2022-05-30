package es.ubu.lsi.model.multas;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the VEHICULO database table.
 * 
 */
@Entity
@NamedQuery(name="Vehiculo.findAll", query="SELECT v FROM Vehiculo v")
public class Vehiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="VEHICULO_IDAUTO_GENERATOR", sequenceName="VEHICULOSEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VEHICULO_IDAUTO_GENERATOR")
	private String idauto;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="direccion", column = @Column(name="direccion")),
		@AttributeOverride(name="cp", column = @Column(name="cp")),
		@AttributeOverride(name="ciudad", column = @Column(name="ciudad")),
	})
	private DireccionPostal direccion;

	private String nombre;

	//bi-directional many-to-one association to Conductor
	@OneToMany(mappedBy="vehiculo")
	private Set<Conductor> conductores;

	public Vehiculo() {
	}

	public String getIdauto() {
		return this.idauto;
	}

	public void setIdauto(String idauto) {
		this.idauto = idauto;
	}

	public DireccionPostal getDireccion() {
		return this.direccion;
	}

	public void setDireccion(DireccionPostal direccion) {
		this.direccion = direccion;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<Conductor> getConductores() {
		return this.conductores;
	}

	public void setConductores(Set<Conductor> conductores) {
		this.conductores = conductores;
	}

	public Conductor addConductores(Conductor conductor) {
		getConductores().add(conductor);
		conductor.setVehiculo(this);

		return conductor;
	}

	public Conductor removeConductores(Conductor conductor) {
		getConductores().remove(conductor);
		conductor.setVehiculo(null);

		return conductor;
	}

	@Override
	public String toString() {
		return "Vehículo: idAuto - " + idauto + ", nombre - " 
				+ nombre + ", dirección postal - " + direccion;
	}
}