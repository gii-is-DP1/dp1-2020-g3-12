package org.springframework.gresur.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.gresur.model.Administrador;
import org.springframework.gresur.model.Categoria;
import org.springframework.gresur.model.EstadoPedido;
import org.springframework.gresur.model.Estanteria;
import org.springframework.gresur.model.LineaFactura;
import org.springframework.gresur.model.Notificacion;
import org.springframework.gresur.model.Personal;
import org.springframework.gresur.model.Producto;
import org.springframework.gresur.model.TipoNotificacion;
import org.springframework.gresur.repository.ProductoRepository;
import org.springframework.gresur.service.exceptions.CapacidadProductoExcededException;
import org.springframework.gresur.service.exceptions.StockWithoutEstanteriaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

	@PersistenceContext
	private EntityManager em;
		
	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private NotificacionService notificacionService;
	
	@Autowired
	private AdministradorService adminService;
	
	@Autowired
	private FacturaEmitidaService facturaService;
	
	private ProductoRepository productoRepository;
	
	@Autowired
	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	
	/* CRUD METHODS */
	
	@Transactional(readOnly = true)
	public List<Producto> findAll() throws DataAccessException{
		return productoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Producto findById(Long id) throws DataAccessException{
		return productoRepository.findById(id).orElse(null);
	}

	@Transactional
	public Producto save(Producto producto) throws DataAccessException {
		em.clear();

		Estanteria estanteria = producto.getEstanteria();
		if(estanteria != null) {
			Double capacidadE = estanteria.getCapacidad();
			
			Double volumenProductos = this.productoRepository.sumStockProductosEstanteriaNotNombre(estanteria.getId(), producto.getNombre()).orElse(0.0)
									  + producto.getAlto()*producto.getAncho()*producto.getProfundo()*producto.getStock();
			
			if(capacidadE < volumenProductos) {
				throw new CapacidadProductoExcededException("El volumen de los productos es mayor a la capacidad de la estanteria");
			}
		}
		else {
			if(producto.getStock()>0) {
				throw new StockWithoutEstanteriaException("No se puede añadir stock a un producto sin estanteria asociada");	
			} else if(producto.getStock()==0) {
				return productoRepository.save(producto);
			}
		}
		
		if(producto.getStock() <= producto.getStockSeguridad() && producto.getId() != null) {
			Notificacion noti = new Notificacion();
			noti.setTipoNotificacion(TipoNotificacion.SISTEMA);
			noti.setCuerpo("El producto: '("+producto.getId()+")-"+producto.getNombre()+"' esta a punto de agotarse, considere reponerlo");
			noti.setFechaHora(LocalDateTime.now());
			
			List<Personal> adminReceptores = new ArrayList<>();
			for (Administrador adm : adminService.findAll()) {
				adminReceptores.add(adm);
			}
			notificacionService.save(noti, adminReceptores);
		}
		
		Producto ret = productoRepository.save(producto);
		em.flush();
		return ret;
	}
	
	@Transactional
	public void deleteById(Long id) throws DataAccessException{
		productoRepository.deleteById(id);
	}
	
	@Transactional
	public void deleteAll() throws DataAccessException{
		productoRepository.deleteAll();
	}
	
	/* USER STORIES */
	
	@Transactional(readOnly = true)
	public Double getDemanda(Producto p, LocalDate fromDate) throws DataAccessException{
		
		if(fromDate == null) {
			fromDate = LocalDate.now().minusMonths(1);
		}
		LocalDate tmp = fromDate;	
		List<LineaFactura> lf = facturaService.findLineasFactura().stream()
																	 .filter(x->x.getFactura().esDefinitiva() && x.getFactura().getFechaEmision().isAfter(tmp))
																	 .collect(Collectors.toList());
		
		Long totalVentas = lf.stream()
							 .mapToLong(x->x.getCantidad())
							 .sum();
		System.out.println("TOMAAS TOTAL VENTAS" + totalVentas);
		Double ventasProducto = lf.stream()
								  .filter(x->x.getProducto().equals(p))
								  .mapToDouble(x->x.getCantidad())
								  .sum();
		System.out.println("TOMAAS VENTAS PRODUCTO" + totalVentas);
		return ventasProducto/totalVentas;
	}
	
	public Integer stockRequerido(Producto p) {
		List<LineaFactura> lf = this.pedidoService.findByProductoAndEstadoIn(EstadoPedido.EN_ESPERA, p);
		Integer stockDemandado = lf.stream().mapToInt(x->x.getCantidad()).sum();
		return p.getStock() - stockDemandado;
	}
	
	@Transactional(readOnly = true)
	public List<Producto> findAllProductosByName(String s){
		return productoRepository.findByNombreContainingIgnoreCase(s.trim());
	}
	
	@Transactional(readOnly = true)
	public List<Producto> findByEstanteria(Categoria c){
		return productoRepository.findByEstanteriaCategoria(c);
	}
	
	@Transactional(readOnly = true)
	public Long count() {
		return productoRepository.count();
	}
	
	@Transactional(readOnly = true)
	public Page<Producto> findAllPageable(Pageable pageable) throws DataAccessException{
		return productoRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<Producto> findByEstanteriaPageable(Categoria c , Pageable pageable){
		return productoRepository.findByEstanteriaCategoria(c,pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<Producto> findByProductosByNamePageable(String s , Pageable pageable){
		return productoRepository.findByNombreContainingIgnoreCase(s, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Producto> findAllOrderedPageable(Pageable pageable) throws DataAccessException{
		return productoRepository.findAllOrderStock(pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<Producto> findByEstanteriaOrderedPageable(Categoria c , Pageable pageable){
		return productoRepository.findByEstanteriaCategoriaOrderStock(c,pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<Producto> findByProductosByNameOrderedPageable(String s , Pageable pageable){
		return productoRepository.findByNombreContainingIgnoreCaseOrderStock(s, pageable);
	}
}
