package es.ubu.lsi.model.invoice;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the LINEAS_FACTURA database table.
 * 
 */
@Entity
@Table(name="LINEAS_FACTURA")
@NamedQuery(name="LineaFactura.findAll", query="SELECT l FROM LineaFactura l")
public class LineaFactura implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LineaFacturaId id;
	
	@Column(name = "DESCRIPCION", length = 10)
	private String descripcion;
	
	@Column(name = "IMPORTE", precision = 7, scale = 2)
	private BigDecimal importe;

	@Column(name = "UNIDADES", precision = 22, scale = 0)
	private BigDecimal unidades;

	//bi-directional many-to-one association to Factura
	@ManyToOne
	@JoinColumn(name="NROFACTURA")
	private Factura factura;

	public LineaFactura() {
	}

	public LineaFacturaId getId() {
		return this.id;
	}

	public void setId(LineaFacturaId id) {
		this.id = id;
	}

	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Factura getFactura() {
		return this.factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

}