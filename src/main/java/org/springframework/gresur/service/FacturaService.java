package org.springframework.gresur.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.gresur.model.Factura;
import org.springframework.gresur.model.LineaFactura;
import org.springframework.gresur.repository.FacturaRepository;
import org.springframework.transaction.annotation.Transactional;

public class FacturaService<T extends Factura, E extends FacturaRepository<T>> {
	
	protected E facturaRepo;
	
	@Autowired
	protected FacturaRepository<Factura> facturaGRepo;
	
	@Autowired
	protected LineasFacturaService lfService;
	
	@Transactional(readOnly = true)
	public List<T> findAll() throws DataAccessException {
		return facturaRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public T findByNumFactura(Long numFactura) throws DataAccessException{
		return facturaRepo.findById(numFactura).orElse(null);
	}
		
	@Transactional(rollbackFor = DataAccessException.class)
	public void deleteByNumFactura(Long numFactura) throws DataAccessException{
		lfService.deleteByFacturaId(numFactura);
		facturaRepo.deleteById(numFactura);
	}
	
	@Transactional
	public void deleteAll() throws DataAccessException{
		lfService.deleteAll(this.findLineasFactura());
		facturaRepo.deleteAll();
	}
	
	@Transactional(readOnly = true)
	public List<LineaFactura> findLineasFactura(){
		try {
			return facturaRepo.findAll().stream().map(x->x.getLineasFacturas()).flatMap(List::stream).collect(Collectors.toList()); //TODO Revisar este stream
		} catch (NullPointerException e) {
			return new ArrayList<LineaFactura>();
		}
	}
	
	@Transactional(readOnly = true)
	public Long count() {
		return facturaRepo.count();
	}
	
	/*METODOS GENERALES PARA TODAS LAS FACTURAS (superclase)*/
	@Transactional(readOnly = true)
	public Iterable<Factura> findAllFacturas() throws DataAccessException{
		return facturaGRepo.findAll();
	}
		
	@Transactional(readOnly = true)
	public Long countAllFacturas() {
		return facturaGRepo.count();
	}
	
}
