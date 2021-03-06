package org.springframework.gresur.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "recibidas")
public class FacturaRecibida extends Factura{
		
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "No puede ser nulo")
	private Concepto concepto;
	
	@JsonView
	@ManyToOne
	private Proveedor proveedor;
}
