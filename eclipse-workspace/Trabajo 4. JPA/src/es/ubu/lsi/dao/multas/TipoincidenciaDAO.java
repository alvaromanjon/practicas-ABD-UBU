package es.ubu.lsi.dao.multas;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Conductor;
import es.ubu.lsi.model.multas.Tipoincidencia;

public class TipoincidenciaDAO extends JpaDAO<Tipoincidencia, Long> {

	public TipoincidenciaDAO(EntityManager em) {
		super(em);
	}

	@Override
	public List<Tipoincidencia> findAll() {
		return getEntityManager().createNamedQuery("Tipoincidencia.findAll", Tipoincidencia.class).getResultList();
	}

}
