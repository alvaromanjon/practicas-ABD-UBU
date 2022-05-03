package es.ubu.lsi;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Alumno
 *
 */
@Entity
@Table(name="REGIONS")
public class Region implements Serializable {

	@Id
	@Column(name="REGION_ID")
	private String regionId;
	
	@Column(name="REGION_NAME")
	private String regionName;
	private static final long serialVersionUID = 1L;

	public Region() {
		super();
	}   
	
	public String getRegionId() {
		return this.regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}   
	

	public String getRegionName() {
		return this.regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}   
	
	@Override
	public String toString() {
		return "Region [regionId=" + regionId + ", regionName=" + regionName + "]";
	}
  
}
