package org.springframework.gresur.model;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity 
@Table(name="vehiculos")
public class ITV extends BaseEntity{
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	@PastOrPresent
	protected LocalDate fecha;

	@DateTimeFormat(pattern="dd/MM/yyyy")
	protected LocalDate expiracion;
	
	@NotNull
	@Enumerated(value = EnumType.STRING)
	protected ResultadoITV resultado;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "facturas_recibidas")
	private FacturaRecibida recibidas;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Vehiculo vehiculo;
}
