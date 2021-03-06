package org.springframework.gresur.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "emitidas")
public class FacturaEmitida extends Factura{
		
	@JsonView
	@NotNull(message = "No puede ser nulo")
	@ManyToOne(optional = false)
	private Dependiente dependiente;
	
	@NotNull(message = "No puede ser nulo")
	@ManyToOne(optional = false)
	private Cliente cliente;

}
