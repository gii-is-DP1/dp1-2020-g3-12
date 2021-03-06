package org.springframework.gresur.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.gresur.model.Almacen;
import org.springframework.gresur.model.Categoria;
import org.springframework.gresur.model.Estanteria;
import org.springframework.gresur.repository.EstanteriaRepository;
import org.springframework.gresur.service.exceptions.CapacidadEstanteriaExcededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstanteriaService{
	
	@PersistenceContext
	private EntityManager em;
	
	private EstanteriaRepository estanteriaRepository;
	
	@Autowired
	public EstanteriaService(EstanteriaRepository estanteriaRepository) {
		this.estanteriaRepository = estanteriaRepository;
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	
	@Transactional(readOnly = true)
	public List<Estanteria> findAll() throws DataAccessException{
		return estanteriaRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Estanteria findById(Long id) throws DataAccessException{
		return estanteriaRepository.findById(id).orElse(null);
	}
	@Transactional(readOnly = true)
	public Estanteria findByCategoria(Categoria cat) throws DataAccessException{
		return estanteriaRepository.findByCategoria(cat).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Estanteria> findAllEstanteriaByAlmacen(Long id) throws DataAccessException{
		return estanteriaRepository.findByAlmacenId(id);
	}
	
	@Transactional(readOnly = true)
	public Double sumCapacidadEstanteriasAlmacenNotEstanteria(Almacen almacen, Long estanteriaId) throws DataAccessException{
		if(almacen == null || almacen.getId() == null) {
			return null;
		} else {
			return this.estanteriaRepository.sumCapacidadEstanteriasAlmacenNotEqualTo(almacen, estanteriaId).orElse(0.);
		}
	}
		
	@Transactional
	public Estanteria save(Estanteria estanteria) throws DataAccessException{
		em.clear();

		Almacen almacen = estanteria.getAlmacen();
		
		if(almacen.getCapacidad()<(sumCapacidadEstanteriasAlmacenNotEstanteria(almacen, estanteria.getId()) + estanteria.getCapacidad())) {
			throw new CapacidadEstanteriaExcededException("Las estanterias exceden la capacidad disponible del almacen");
		}
		
		Estanteria ret = estanteriaRepository.save(estanteria);
		em.flush();
		return ret;
	}
	
	@Transactional
	public void deleteById(Long id) throws DataAccessException{
		estanteriaRepository.deleteById(id);
	}
	
	@Transactional
	public void deleteAll() {
		estanteriaRepository.deleteAll();
	}
	
	@Transactional
	public Long count() {
		return estanteriaRepository.count();
	} 
	

}
