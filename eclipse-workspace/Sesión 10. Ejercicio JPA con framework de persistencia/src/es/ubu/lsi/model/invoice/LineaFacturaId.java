package es.ubu.lsi.model.invoice;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the LINEAS_FACTURA database table.
 * 
 */
@Embeddable
public class LineaFacturaId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false)
	private long nrofactura;

	private String concepto;

	public LineaFacturaId() {
	}
	public long getNrofactura() {
		return this.nrofactura;
	}
	public void setNrofactura(long nrofactura) {
		this.nrofactura = nrofactura;
	}
	public String getConcepto() {
		return this.concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof LineaFacturaId)) {
			return false;
		}
		LineaFacturaId castOther = (LineaFacturaId)other;
		return 
			(this.nrofactura == castOther.nrofactura)
			&& this.concepto.equals(castOther.concepto);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.nrofactura ^ (this.nrofactura >>> 32)));
		hash = hash * prime + this.concepto.hashCode();
		
		return hash;
	}
}