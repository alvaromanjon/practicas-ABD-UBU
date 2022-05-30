package es.ubu.lsi.dao.multas;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Conductor;
import es.ubu.lsi.model.multas.Vehiculo;

public class VehiculoDAO extends JpaDAO<Vehiculo, String> {

	public VehiculoDAO(EntityManager em) {
		super(em);
	}

	@Override
	public List<Vehiculo> findAll() {
		return getEntityManager().createNamedQuery("Vehiculo.findAll", Vehiculo.class).getResultList();
	}

}
