package org.springframework.gresur.service;


import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.gresur.model.Cliente;
import org.springframework.gresur.model.FacturaEmitida;
import org.springframework.gresur.repository.FacturaEmitidaRepository;
import org.springframework.gresur.service.exceptions.ClienteDefaulterException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaEmitidaService extends FacturaService<FacturaEmitida, FacturaEmitidaRepository>{
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ConfiguracionService configService;
	
	@Autowired
	public FacturaEmitidaService(FacturaEmitidaRepository feRepo) {
		super.facturaRepo = feRepo;
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	@Transactional
	public List<FacturaEmitida> findByClienteIdAndEstaPagadaFalse(Long id){
		return facturaRepo.findByClienteIdAndEstaPagadaFalse(id);
	}
	
	@Transactional
	public FacturaEmitida save(FacturaEmitida emitida) throws DataAccessException {
		em.clear();
		FacturaEmitida emitidaDB = emitida.getId() == null ? null : facturaRepo.findById(emitida.getId()).orElse(null);
		
		Cliente cliente = emitida.getCliente();
		if(!facturaRepo.findByClienteIdAndEstaPagadaFalse(cliente.getId()).isEmpty() && emitidaDB==null && !emitida.esRectificativa())
			throw new ClienteDefaulterException("El cliente tiene facturas pendientes");
		
		if(emitida.getId() == null && emitida.esRectificativa()) {
			emitida.setFechaEmision(emitida.getOriginal().getFechaEmision());
			emitida.setNumFactura(configService.nextValEmitidasRectificada());
			FacturaEmitida anterior = (FacturaEmitida) emitida.getOriginal();
			anterior.setEstaPagada(true);
			facturaRepo.save(anterior);
			em.flush();
		}
		FacturaEmitida ret = facturaRepo.save(emitida);
		em.flush();
		return ret;
	}
	
	@Transactional
	public List<FacturaEmitida> findFacturasByCliente(Long id){
		return facturaRepo.findByClienteId(id);
	}
	
	@Transactional
	public List<FacturaEmitida> findFacturasByClienteAndFecha(Long id,LocalDate fecha){
		return facturaRepo.findByClienteIdAndFechaEmision(id, fecha);
	}
	
	@Transactional
	public List<FacturaEmitida> findByFechaEmisionBeforeAndFechaEmisionAfter(LocalDate dateBefore, LocalDate dateAfter){
		return facturaRepo.findByFechaEmisionBeforeAndFechaEmisionAfter(dateBefore, dateAfter);
	}
	
	@Transactional
	public FacturaEmitida findByNumFactura(String numFactura) {
		return facturaRepo.findByNumFactura(numFactura);
	}
	
	@Transactional
	public List<FacturaEmitida> findAll(Pageable pageable) {
		return facturaRepo.findAll(pageable);
	}
	
}