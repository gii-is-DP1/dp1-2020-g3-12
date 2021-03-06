package org.springframework.gresur.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.omg.CORBA.portable.UnknownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.gresur.model.Administrador;
import org.springframework.gresur.model.ITV;
import org.springframework.gresur.model.Notificacion;
import org.springframework.gresur.model.Pedido;
import org.springframework.gresur.model.Personal;
import org.springframework.gresur.model.Reparacion;
import org.springframework.gresur.model.Seguro;
import org.springframework.gresur.model.TipoNotificacion;
import org.springframework.gresur.model.TipoVehiculo;
import org.springframework.gresur.model.Transportista;
import org.springframework.gresur.model.Vehiculo;
import org.springframework.gresur.repository.VehiculoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.gresur.service.exceptions.MatriculaUnsupportedPatternException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VehiculoService {

	@PersistenceContext
	private EntityManager em;
		
	@Autowired
	private NotificacionService notificacionService;
	
	@Autowired
	private ITVService ITVService;
	
	@Autowired
	private SeguroService seguroService;
	
	@Autowired
	private ReparacionService reparacionService;
	
	@Autowired
	private AdministradorService adminService;
	
	@Autowired
	private PedidoService pedidoService;
	
	private VehiculoRepository vehiculoRepository;
	
	@Autowired
	public VehiculoService(VehiculoRepository vehiculoRepository) {
		this.vehiculoRepository = vehiculoRepository;	
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	
	@Transactional(readOnly = true)
	public Iterable<Vehiculo> findAll() throws DataAccessException{
		return vehiculoRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Boolean existsByMatricula(String matricula) throws DataAccessException{
		return vehiculoRepository.existsByMatricula(matricula);
	}
	
	@Transactional(readOnly = true)
	public Page<Vehiculo> findAll(Pageable pageable) throws DataAccessException{
		return vehiculoRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public Vehiculo findByMatricula(String matricula) throws DataAccessException{
		return vehiculoRepository.findByMatricula(matricula).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public Vehiculo findById(Long id) throws DataAccessException{
		return vehiculoRepository.findById(id).get();
	}

		@Transactional
	public Vehiculo save(Vehiculo vehiculo) throws DataAccessException{
		em.clear();

		 TipoVehiculo tipo = vehiculo.getTipoVehiculo();
		 switch(tipo) {
		case CAMION:
			if(!Pattern.matches("[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}", vehiculo.getMatricula())) throw new MatriculaUnsupportedPatternException("Formato de matricula no valido para camion");
			break;
		case CARRETILLA_ELEVADORA:
			if(!Pattern.matches("E[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}", vehiculo.getMatricula())) throw new MatriculaUnsupportedPatternException("Formato de matricula no valido para carretilla elevadora");
			break;
		case FURGONETA:
			if(!Pattern.matches("[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}", vehiculo.getMatricula())) throw new MatriculaUnsupportedPatternException("Formato de matricula no valido para furgoneta");
			break;
		case GRUA:
			if(!Pattern.matches("[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}", vehiculo.getMatricula())) throw new MatriculaUnsupportedPatternException("Formato de matricula no valido para grua");
			break;
		default:
			throw new NullPointerException();
		 }
		 	 		 
		Vehiculo ret = vehiculoRepository.save(vehiculo);
		em.flush();
		return ret;
	}
	
	@Transactional(rollbackFor = DataAccessException.class)
	public void deleteById(Long id) throws DataAccessException{
		reparacionService.deleteByVehiculoId(id);
		ITVService.deleteByVehiculoId(id);
		seguroService.deleteByVehiculoId(id);
		vehiculoRepository.deleteById(id);
	}
	
	@Transactional
	public void deleteByMatricula(String matricula) throws DataAccessException{
		reparacionService.deleteByVehiculoMatricula(matricula);
		ITVService.deleteByVehiculoMatricula(matricula);
		seguroService.deleteByVehiculoMatricula(matricula);
		vehiculoRepository.deleteByMatricula(matricula);
	}
	
	@Transactional
	public void deleteAll() throws DataAccessException{
		reparacionService.deleteAll();
		seguroService.deleteAll();
		ITVService.deleteAll();
		vehiculoRepository.deleteAll();
	}
	
	/*	PROPIEDAD DERIVADA - DISPONIBILIDAD	 */
	public Boolean getDisponibilidad(String matricula) {
		Vehiculo vehiculo = this.findByMatricula(matricula);
		if(vehiculo == null) {
			return false;
		} else {
			ITV ultimaITVFavorable = ITVService.findLastITVFavorableByVehiculo(vehiculo.getMatricula());
			Seguro ultimoSeguro = seguroService.findLastSeguroByVehiculo(vehiculo.getMatricula());
			Reparacion ultimaReparacion = reparacionService.findLastReparacionByVehiculo(vehiculo.getMatricula());
			
			return (ultimaITVFavorable != null && ultimoSeguro != null && !ultimoSeguro.getFechaExpiracion().isBefore(LocalDate.now()) &&
					(ultimaReparacion == null || ultimaReparacion.getFechaSalidaTaller() != null && ultimaReparacion.getFechaSalidaTaller().isBefore(LocalDate.now())));
		}
	}
	public Boolean getDisponibilidad(String matricula, Transportista t) {
		Boolean disponibilidadVehiculo = this.getDisponibilidad(matricula);
		List<Pedido> pedidosEnRepartoVehiculo = pedidoService.findPedidosEnRepartoByMatricula(matricula);
		
		return disponibilidadVehiculo && (pedidosEnRepartoVehiculo.size() == 0 || pedidosEnRepartoVehiculo.stream().allMatch(x->x.getTransportista().equals(t)));
	}
	
	@EventListener(ApplicationReadyEvent.class)
	@Scheduled(cron = "0 0 7 * * *")
	@Transactional
	public void ITVSeguroReparacionvalidation() throws UnknownException{
		Iterator<Vehiculo> vehiculos = vehiculoRepository.findAll().iterator();
			
		// Receptores de la notificacion
		List<Personal> lPer = new ArrayList<>();
		for (Personal per : adminService.findAllPersonal()) {
			if(per.getClass() == Administrador.class || per.getClass() == Transportista.class)
				lPer.add(per);
		}
			
		while (vehiculos.hasNext()) {
			
			Vehiculo v = vehiculos.next();
			ITV ultimaITVFavorable = ITVService.findLastITVFavorableByVehiculo(v.getMatricula());
			Seguro ultimoSeguro = seguroService.findLastSeguroByVehiculo(v.getMatricula());
			Reparacion ultimaReparacion = reparacionService.findLastReparacionByVehiculo(v.getMatricula());
			
			try {
				if(ultimaITVFavorable == null) {
					Notificacion warning = new Notificacion();
					warning.setTipoNotificacion(TipoNotificacion.SISTEMA);
					warning.setCuerpo("El vehículo con matrícula:" + v.getMatricula() + " ha dejado de estar disponible debido a la invalidez o caducidad de su ITV");
					notificacionService.save(warning,lPer);
				} if(ultimoSeguro == null || ultimoSeguro.getFechaExpiracion().isBefore(LocalDate.now())) {		
					Notificacion warning = new Notificacion();
					warning.setTipoNotificacion(TipoNotificacion.SISTEMA);
					warning.setCuerpo("El vehículo con matrícula: " + v.getMatricula() + " ha dejado de estar disponible debido a la invalidez o caducidad de su Seguro");		
					notificacionService.save(warning,lPer);
				} if(ultimaReparacion != null && (ultimaReparacion.getFechaSalidaTaller() == null || ultimaReparacion.getFechaSalidaTaller().isAfter(LocalDate.now()))) {	
					Notificacion warning = new Notificacion();
					warning.setTipoNotificacion(TipoNotificacion.SISTEMA);
					warning.setCuerpo("El vehículo con matrícula: " + v.getMatricula() + " ha dejado de estar disponible debido a que esta en reparacion");	
					notificacionService.save(warning,lPer);
				}
			} catch (Exception e) {
				log.error("No se ha podido revisar el vehiculo " + v.getMatricula() + ": " + e.getMessage());
			}
		}
		log.info("Vehiculos revisados");
	}
	
	@Transactional
	public Long count() {
		return vehiculoRepository.count();
	}
	
}
