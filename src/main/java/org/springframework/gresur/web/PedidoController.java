package org.springframework.gresur.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.gresur.configuration.services.UserDetailsImpl;
import org.springframework.gresur.model.Administrador;
import org.springframework.gresur.model.Cliente;
import org.springframework.gresur.model.Dependiente;
import org.springframework.gresur.model.EstadoPedido;
import org.springframework.gresur.model.FacturaEmitida;
import org.springframework.gresur.model.LineaFactura;
import org.springframework.gresur.model.Notificacion;
import org.springframework.gresur.model.Pedido;
import org.springframework.gresur.model.Personal;
import org.springframework.gresur.model.TipoNotificacion;
import org.springframework.gresur.model.TipoVehiculo;
import org.springframework.gresur.model.Transportista;
import org.springframework.gresur.model.Vehiculo;
import org.springframework.gresur.repository.TransportistaRepository;
import org.springframework.gresur.repository.UserRepository;
import org.springframework.gresur.service.AdministradorService;
import org.springframework.gresur.service.ClienteService;
import org.springframework.gresur.service.FacturaEmitidaService;
import org.springframework.gresur.service.LineasFacturaService;
import org.springframework.gresur.service.NotificacionService;
import org.springframework.gresur.service.PedidoService;
import org.springframework.gresur.service.PersonalService;
import org.springframework.gresur.service.ProductoService;
import org.springframework.gresur.service.VehiculoService;
import org.springframework.gresur.util.Tuple2;
import org.springframework.gresur.util.Tuple3;
import org.springframework.gresur.util.Tuple4;
import org.springframework.gresur.util.Tuple5;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("api/pedido")
@RestController
@Slf4j
public class PedidoController {
	
	private final PedidoService pedidoService;

	@Autowired
	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}
	
	@Autowired
	public VehiculoService vehiculoService;
	
	@Autowired
	public ClienteService clienteService;
	
	@Autowired
	public ProductoService productoService;
	
	@Autowired
	public FacturaEmitidaService facturaEmitidaService;
	
	@Autowired
	public LineasFacturaService lineaFacturaService;
	
	@Autowired
	public NotificacionService notificacionService;
	
	@Autowired
	public AdministradorService adminService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	protected PersonalService<Transportista, TransportistaRepository> personalService;
	
	
	@GetMapping("/page={pageNo}&size={pageSize}&order={orden}")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
	public Page<Pedido> findAll(@PathVariable("pageNo") Integer pageNo , @PathVariable("pageSize") Integer pageSize ,@PathVariable("orden") String orden) {
		Pageable paging = null;
		
		if(!orden.equals("")) paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.valueOf(orden), "fechaRealizacion"));
		else paging = PageRequest.of(pageNo, pageSize);
		
		return pedidoService.findAll(paging);
	}
	
	@GetMapping("/id/{id}")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
	public Pedido findById(@PathVariable("id") Long id) {
		return pedidoService.findByID(id);
	}
	
	@GetMapping("/{estado}/page={pageNo}&size={pageSize}&order={orden}")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('ADMIN')")
	public Page<Pedido> findAllByEstadoOrden(@PathVariable("estado") String estado,@PathVariable("pageNo") Integer pageNo , @PathVariable("pageSize") Integer pageSize ,@PathVariable("orden") String orden) {
		
		Pageable paging = null;
		
		if(!orden.equals("")) paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.valueOf(orden), "fechaRealizacion"));
		else paging = PageRequest.of(pageNo, pageSize);
		

		return pedidoService.findByEstado(EstadoPedido.valueOf(estado) , paging);
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasRole('DEPENDIENTE')")
	@Transactional
	public ResponseEntity<?> add(@RequestBody Tuple4<String, String, LocalDate, Tuple4<Double, Boolean, String, List<Tuple2<Long, Integer>>>> pedido) {
		Authentication user = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) user.getPrincipal();
		
		Dependiente dependiente = (Dependiente) userRepository.findByUsername(userDetails.getUsername()).orElse(null).getPersonal();
		
		try {
			String direccionEnvio = pedido.getE1();
			EstadoPedido estado = EstadoPedido.valueOf(pedido.getE2());
			LocalDate fechaEnvio = pedido.getE3();
			
			if(!fechaEnvio.isAfter(LocalDate.now())) {
				log.warn("/pedido/add Constrain violation in params");
				return ResponseEntity.badRequest().body("La fecha de envío debe ser futura");
			}
			
			else {
				Double importeFactura = pedido.getE4().getE1();
				Boolean estaPagada = pedido.getE4().getE2();
				Cliente cliente = clienteService.findByNIF(pedido.getE4().getE3());
				
				FacturaEmitida factura = new FacturaEmitida();
				factura.setCliente(cliente);
				factura.setDependiente(dependiente);
				factura.setEstaPagada(true);
				factura.setImporte(importeFactura);
				factura = facturaEmitidaService.save(factura);
				
				List<LineaFactura> lf = new ArrayList<LineaFactura>();
				for(Tuple2<Long, Integer> pair : pedido.getE4().getE4()) {
					LineaFactura linea = new LineaFactura();
					linea.setCantidad(pair.getE2());
					linea.setFactura(factura);
					linea.setProducto(productoService.findById(pair.getE1()));
					linea.setPrecio(pair.getE2()*productoService.findById(pair.getE1()).getPrecioVenta());
					linea = lineaFacturaService.save(linea);
					lf.add(linea);
				}
				factura.setLineasFacturas(lf);
				factura.setEstaPagada(estaPagada);
				factura = facturaEmitidaService.save(factura);
				
				Pedido pedidoRes = new Pedido();
				pedidoRes.setDireccionEnvio(direccionEnvio);
				pedidoRes.setEstado(estado);
				pedidoRes.setFacturaEmitida(factura);
				pedidoRes.setFechaEnvio(fechaEnvio);
				pedidoRes.setFechaRealizacion(LocalDate.now());
				pedidoRes = pedidoService.save(pedidoRes);
				
				// Receptores de la notificacion
				List<Personal> lPer = new ArrayList<>();
				for (Personal per : adminService.findAllPersonal()) {
					if(per.getClass() == Administrador.class)
						lPer.add(per);
				}
				
				for(LineaFactura linea : lf) {
					if(linea.getCantidad() > linea.getProducto().getStock()) {
						Notificacion aviso = new Notificacion();
						aviso.setCuerpo("Se necesita reposición del producto: " + linea.getProducto().getNombre() + " para poder completar el pedido con id " + pedidoRes.getId());
						aviso.setTipoNotificacion(TipoNotificacion.SISTEMA);
						aviso.setFechaHora(LocalDateTime.now());
						aviso = notificacionService.save(aviso, lPer);
					}
				}
				log.info("/pedido/add Entity Pedido with id: "+ pedidoRes.getId() +" was created successfully");
				return ResponseEntity.ok(pedidoRes);
			}
		}catch(Exception e) {
			log.error("/pedido/add "+e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
	}
	
	@PostMapping("/{id}")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('ADMIN')")
	public ResponseEntity<?> cancelarPedido(@PathVariable("id") Long id) {
		try {
			
			Pedido pedido = pedidoService.findByID(id);
			if(pedido != null) {
				pedido.setEstado(EstadoPedido.CANCELADO);
			
				pedido = pedidoService.save(pedido);
				log.info("/pedido/{id} Entity Pedido with id: "+pedido.getId()+" was edited successfully");
				return ResponseEntity.ok(pedido);				
			}
			else {
				return ResponseEntity.badRequest().body("Error: Pedido not found");
			}
		} catch (Exception e) {
			log.error("/pedido/{id} "+e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/ocupacion")
	@PreAuthorize("hasRole('TRANSPORTISTA')")
	public List<Tuple3<Vehiculo, Double, Double>> ocupacionPedidosDelDia() {
		
		Iterable<Vehiculo> iterableVehiculos = vehiculoService.findAll();
		List<Vehiculo> listaVehiculos = new ArrayList<Vehiculo>();
		iterableVehiculos.forEach(listaVehiculos::add);
		
		List<Vehiculo> vehiculosFiltrado = listaVehiculos.stream()
				.filter(x->x.getTipoVehiculo()==TipoVehiculo.CAMION || x.getTipoVehiculo()==TipoVehiculo.FURGONETA).collect(Collectors.toList());
		
		
		List<Pedido> lp = pedidoService.findPedidosEnRepartoByFecha(LocalDate.now());
		
		Map<Vehiculo,List<FacturaEmitida>> diccVehiculoDimPedidos = new HashMap<Vehiculo, List<FacturaEmitida>>();
		
		for (int i = 0; i < lp.size(); i++) {
			Vehiculo v = lp.get(i).getVehiculo(); //clave
			FacturaEmitida f = lp.get(i).getFacturaEmitida(); //valor
			
			if(diccVehiculoDimPedidos.containsKey(v)) {
				List<FacturaEmitida> lf = diccVehiculoDimPedidos.get(v);
				lf.add(f);
				diccVehiculoDimPedidos.put(v, lf);
			}
			else {
				List<FacturaEmitida> l = new ArrayList<FacturaEmitida>();
				l.add(f);
				diccVehiculoDimPedidos.put(v, l);
			}
		}
		
		
		List<Tuple3<Vehiculo, Double, Double>> listaDef = new ArrayList<Tuple3<Vehiculo,Double,Double>>();
		
		for (Vehiculo v : vehiculosFiltrado) {
			
			if(diccVehiculoDimPedidos.get(v)!=null) {
				Tuple3<Vehiculo, Double, Double> tp = new Tuple3<Vehiculo, Double, Double>();
				tp.setE1(v);
				
				
				List<LineaFactura> l = diccVehiculoDimPedidos.get(v).stream().map(x->x.getLineasFacturas()).flatMap(List::stream).collect(Collectors.toList());
				
				
				Double dimProductos = l.stream().mapToDouble(x->x.getProducto().getAlto()*x.getProducto().getAncho()*x.getProducto().getProfundo()).sum();
				tp.setE2(dimProductos);
				
				
				Double dimTotal = v.getCapacidad();
				tp.setE3(dimTotal);
				
				tp.name1="vehiculo";
				tp.name2="dimProductos";
				tp.name3="dimTotales";
				
				listaDef.add(tp);
			}
			
		}
	
		return listaDef;
	}
	
	@GetMapping("/hoy")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('TRANSPORTISTA') ")
	public List<Tuple5<Long, String, EstadoPedido, String, String>> findAllHoy() {
				
		List<Tuple5<Long, String, EstadoPedido, String, String>> ls = new ArrayList<Tuple5<Long,String,EstadoPedido,String,String>>();
		
		Authentication user = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) user.getPrincipal();
		Personal per = userRepository.findByUsername(userDetails.getUsername()).orElse(null).getPersonal();
		
		Iterable<Pedido> pedidosHoy = pedidoService.findAll();
		List<Pedido> listaPedidosHoy = new ArrayList<>();
		pedidosHoy.forEach(listaPedidosHoy::add);
		
		List<Pedido> pedidosRepartoPreparado = listaPedidosHoy
				.stream()
				.filter(x->x.getEstado().equals(EstadoPedido.PREPARADO) 
								|| x.getEstado().equals(EstadoPedido.EN_REPARTO))
				.filter(x->x.getTransportista().getNIF().equals(per.getNIF()))
				.collect(Collectors.toList());
		
		
		String s = "";
		
		for(Pedido p: pedidosRepartoPreparado) {
			
			String cliente = p.getFacturaEmitida().getCliente().getName();			
			String tipoVehiculo = p.getVehiculo()!=null?p.getVehiculo().getTipoVehiculo().toString():s;
			String vehiculoT = (p.getVehiculo()!=null&&tipoVehiculo!=null)?tipoVehiculo+" (" + p.getVehiculo().getMatricula().toString() +")":s;
			Tuple5<Long, String, EstadoPedido, String, String> t5 = new Tuple5<Long, String, EstadoPedido, String, String>();
			
			t5.setE1(p.getId());
			t5.setE2(p.getDireccionEnvio());
			t5.setE3(p.getEstado());
			t5.setE4(cliente);
			t5.setE5(vehiculoT);
			ls.add(t5);				
		}
		return ls;
	}
	
	@GetMapping("/factura/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TRANSPORTISTA') or hasRole('DEPENDIENTE')")
	public Pedido findFacturaPedido(@PathVariable("id") Long id) {
		return pedidoService.findByID(id);
	}
	
	@PutMapping("/reparto/{id}")
	@PreAuthorize("hasRole('TRANSPORTISTA')")
	public ResponseEntity<?> setEnReparto(@PathVariable("id") Long id,@RequestBody @Valid Vehiculo veh, BindingResult result) {
		try {
			if(result.hasErrors()) {
				List<FieldError> le = result.getFieldErrors();
				log.warn("/pedido/reparto/{id} Constrain violation in params");
				return ResponseEntity.badRequest().body(le.get(0).getDefaultMessage() + (le.size()>1? " (Total de errores: " + le.size() + ")" : ""));
			}
			
			Pedido p = pedidoService.findByID(id);
			
			Authentication user = SecurityContextHolder.getContext().getAuthentication();
			UserDetailsImpl userDetails = (UserDetailsImpl) user.getPrincipal();
			Personal per = userRepository.findByUsername(userDetails.getUsername()).orElse(null).getPersonal();
			
			if(p!=null) {
				p.setTransportista(personalService.findByNIF(per.getNIF()));
				p.setVehiculo(veh);
				p.setEstado(EstadoPedido.EN_REPARTO);
				p.setFechaEnvio(LocalDate.now());
				
				pedidoService.save(p);
				log.info("/pedido/reparto/{id} Entity Pedido with id: "+p.getId()+" was edited successfully");
				return ResponseEntity.ok(p);
			}else {
				return ResponseEntity.badRequest().body("Error: Pedido not found");
			}
		}catch(Exception e) {
			log.error("/pedido/reparto/{id} " + e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/entregado/{id}")
	@PreAuthorize("hasRole('TRANSPORTISTA')")
	public ResponseEntity<?> setEntregado(@PathVariable("id") Long id) {
		try {
			Pedido p = pedidoService.findByID(id);
			Authentication user = SecurityContextHolder.getContext().getAuthentication();
			UserDetailsImpl userDetails = (UserDetailsImpl) user.getPrincipal();
			Personal per = userRepository.findByUsername(userDetails.getUsername()).orElse(null).getPersonal();
			
			
			if(p!=null&&p.getEstado().equals(EstadoPedido.EN_REPARTO)&&p.getTransportista().getNIF().equals((per.getNIF()))) {
				p.setEstado(EstadoPedido.ENTREGADO);
			
				pedidoService.save(p);
				log.info("/pedido/reparto/{id} Entity Pedido with id: "+p.getId()+" was edited successfully");
				return ResponseEntity.ok(p);
			
			}else {
				return ResponseEntity.badRequest().body("Error: Pedido not found or Pedido not Preparado");
			}
		}catch(Exception e) {
			log.error("/pedido/entregado/{id} " + e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("/transportista/{estado}")
	@PreAuthorize("hasRole('TRANSPORTISTA')")
	public List<Tuple5<Long, String, EstadoPedido, String, String>> findAllByEstadoTransportista(@PathVariable("estado") String estado) {
		
		List<Tuple5<Long, String, EstadoPedido, String, String>> ls = new ArrayList<Tuple5<Long,String,EstadoPedido,String,String>>();

		Authentication user = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) user.getPrincipal();
		Personal per = userRepository.findByUsername(userDetails.getUsername()).orElse(null).getPersonal();
		
		Iterable<Pedido> pedidosHoy = pedidoService.findByEstado(EstadoPedido.valueOf(estado));
		List<Pedido> listaPedidosHoy = new ArrayList<>();
		pedidosHoy.forEach(listaPedidosHoy::add);
		
		List<Pedido> pedidosRepartoPreparado = listaPedidosHoy
				.stream()
				.filter(x->x.getTransportista().getNIF().equals(per.getNIF()))
				.collect(Collectors.toList());
		
		String s = "";
		
		for(Pedido p: pedidosRepartoPreparado) {
			
			String cliente = p.getFacturaEmitida().getCliente().getName();			
			String tipoVehiculo = p.getVehiculo()!=null?p.getVehiculo().getTipoVehiculo().toString():s;
			String vehiculoT = (p.getVehiculo()!=null&&tipoVehiculo!=null)?tipoVehiculo+" (" + p.getVehiculo().getMatricula().toString() +")":s;
			Tuple5<Long, String, EstadoPedido, String, String> t5 = new Tuple5<Long, String, EstadoPedido, String, String>();
			
			t5.setE1(p.getId());
			t5.setE2(p.getDireccionEnvio());
			t5.setE3(p.getEstado());
			t5.setE4(cliente);
			t5.setE5(vehiculoT);
			ls.add(t5);
		}
		return ls;
	}
	@Transactional
	@PutMapping("/pagado/{id}")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('ADMIN')")
	public ResponseEntity<?> setEstaPagado(@PathVariable("id") Long id) {
		try {
			Pedido p = pedidoService.findByID(id);
			
			if(p!=null) {
	
					FacturaEmitida f = p.getFacturaEmitida();
					f.setEstaPagada(f.getEstaPagada()? false : true);
					f = facturaEmitidaService.save(f);
					
					log.info("/pedido/reparto/{id} Entity Factura with id: "+f.getId()+" was edited successfully");
					return ResponseEntity.ok(p);		
			}else {
				return ResponseEntity.badRequest().body("Error al intentar cambiar el estado del pago del pedido");
			}
		}catch(Exception e) {
			log.error("/pedido/pagado/{id} " + e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/update")
	@PreAuthorize("hasRole('DEPENDIENTE') or hasRole('ADMIN')")
	public ResponseEntity<?> updatePedido(@Valid @RequestBody Pedido pedido, BindingResult result) {
		try {
			Pedido p = pedidoService.findByID(pedido.getId());
			
			if(p==null) {
				return ResponseEntity.badRequest().body("Pedido no existente");
			}
			
			if(result.hasErrors()) {
				List<FieldError> le = result.getFieldErrors();
				log.warn("/pedido/update Constrain violation in params");
				return ResponseEntity.badRequest().body(le.get(0).getDefaultMessage() + (le.size()>1? " (Total de errores: " + le.size() + ")" : ""));
			}
			if(p.getVersion() != pedido.getVersion()) {
				log.error("/pedido/update Concurrent modification");
				return ResponseEntity.badRequest().body("Concurrent modification");
			}
			
			clienteService.save(pedido.getFacturaEmitida().getCliente());
			Pedido pDef = pedidoService.save(pedido);
			log.info("/pedido/update Entity Pedido with id: "+pDef.getId()+" was edited successfully");
			return ResponseEntity.ok(pDef);
		}catch(Exception e) {
			log.error("/pedido/update " + e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}