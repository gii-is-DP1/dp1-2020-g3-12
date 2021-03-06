package org.springframework.gresur.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.gresur.model.Personal;
import org.springframework.gresur.repository.PersonalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalService<T extends Personal, E extends PersonalRepository<T>> {

	@PersistenceContext
	private EntityManager em;
	
	protected E personalRepo;
	
	@Autowired
	protected ContratoService contratoService;
	
	@Autowired
	protected PersonalRepository<Personal> personalGRepo;
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	
	@Transactional(readOnly = true)
	public List<T> findAll() throws DataAccessException{
		return personalRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public Boolean existByNif(String nif) throws DataAccessException{
		return personalGRepo.existsByNIF(nif);
	}
	
	@Transactional(readOnly = true)
	public Boolean existByNSS(String nss) throws DataAccessException{
		return personalGRepo.existsByNSS(nss);
	}
	
	@Transactional(readOnly = true)
	public T findByNIF(String NIF) throws DataAccessException{
		return personalRepo.findByNIF(NIF);
	}
	
	@Transactional
	public T save(T personal) throws DataAccessException{
		em.clear();

		T ret = personalRepo.save(personal);
		em.flush();
		return ret;
	}
	
	@Transactional
	public Personal savePersonal(Personal personal) throws DataAccessException{
		em.clear();

		Personal ret = personalGRepo.save(personal);
		em.flush();
		return ret;
	}
	
	@Transactional
	public void deleteByNIF(String NIF) throws DataAccessException{
		contratoService.deleteByPersonalNIF(NIF);
		personalRepo.deleteByNIF(NIF);
	}
	
	@Transactional
	public void deleteAll() throws DataAccessException{
		contratoService.deleteAll();
		personalRepo.deleteAll();
	}
	
	@Transactional
	public long count() throws DataAccessException{
		return personalRepo.count();
	}
	
	/* METODOS GENERALES PARA EL PERSONAL AL COMPLETO (superclase)*/
	@Transactional(readOnly = true)
	public List<Personal> findAllPersonal() throws DataAccessException{
		return personalGRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public Personal findPersonal(Long id) throws DataAccessException{
		return personalGRepo.findById(id).orElse(null);
	}
	
}
