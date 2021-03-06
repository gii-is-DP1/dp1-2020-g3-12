package org.springframework.gresur.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.gresur.model.Administrador;
import org.springframework.gresur.model.Dependiente;
import org.springframework.gresur.model.EncargadoDeAlmacen;
import org.springframework.gresur.model.Personal;
import org.springframework.gresur.model.Transportista;
import org.springframework.gresur.repository.AdministradorRepository;
import org.springframework.gresur.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministradorService extends PersonalService<Administrador, AdministradorRepository>{

	private UserRepository userRepo;
	@Autowired
	public AdministradorService(AdministradorRepository admRepository,UserRepository userRepo) {
		super.personalRepo = admRepository;
		this.userRepo = userRepo;
	}	
	
	@Transactional
	public Dependiente saveDependiente(Dependiente p){
		Dependiente empleado = personalGRepo.save(p);
		 return empleado;
	}
	@Transactional
	public EncargadoDeAlmacen saveEncargadoDeAlmacen(EncargadoDeAlmacen p){
		EncargadoDeAlmacen empleado = personalGRepo.save(p);
		 return empleado;
	}
	@Transactional
	public Transportista saveTransportista(Transportista p){
		Transportista empleado = personalGRepo.save(p);
		 return empleado;
	}
	@Transactional
	public Personal findByNIFPersonal(String nif){
		Personal p = personalGRepo.findByNIF(nif);
		 return p;
	}
	@Transactional
	public void deleteByNIFPersonal(String nif){
		userRepo.deleteByPersonalNIF(nif);
		personalGRepo.deleteByNIF(nif);
		 
	}
	


}
