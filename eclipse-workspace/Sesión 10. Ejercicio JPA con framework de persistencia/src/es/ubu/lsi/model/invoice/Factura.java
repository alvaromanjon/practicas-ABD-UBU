package es.ubu.lsi.model.invoice;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;


/**
 * The persistent class for the FACTURAS database table.
 * 
 */
@Entity
@Table(name="FACTURAS")
@NamedQuery(name="Factura.findAll", query="SELECT f FROM Factura f")
public class Factura implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long nrofactura;

	private String cliente;

	private BigDecimal importe;

	//bi-directional many-to-one association to LineaFactura
	@OneToMany(mappedBy="factura")
	private Set<LineaFactura> lineasFacturas;

	public Factura() {
	}

	public long getNrofactura() {
		return this.nrofactura;
	}

	public void setNrofactura(long nrofactura) {
		this.nrofactura = nrofactura;
	}

	public String getCliente() {
		return this.cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Set<LineaFactura> getLineasFacturas() {
		return this.lineasFacturas;
	}

	public void setLineasFacturas(Set<LineaFactura> lineasFacturas) {
		this.lineasFacturas = lineasFacturas;
	}

	public LineaFactura addLineasFactura(LineaFactura lineasFactura) {
		getLineasFacturas().add(lineasFactura);
		lineasFactura.setFactura(this);

		return lineasFactura;
	}

	public LineaFactura removeLineasFactura(LineaFactura lineasFactura) {
		getLineasFacturas().remove(lineasFactura);
		lineasFactura.setFactura(null);

		return lineasFactura;
	}

}