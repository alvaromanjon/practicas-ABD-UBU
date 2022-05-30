package es.ubu.lsi.dao.multas;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Conductor;
import es.ubu.lsi.model.multas.Incidencia;
import es.ubu.lsi.model.multas.IncidenciaPK;

public class IncidenciaDAO extends JpaDAO<Incidencia, IncidenciaPK> {

	public IncidenciaDAO(EntityManager em) {
		super(em);
	}

	@Override
	public List<Incidencia> findAll() {
		return getEntityManager().createNamedQuery("Incidencia.findAll", Incidencia.class).getResultList();
	}

}
