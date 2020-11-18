package org.springframework.gresur.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "contratos")
public class Contrato extends BaseEntity{
	
	//TODO validacion de configuracion
	private Double nomina;
	
	@NotBlank
	@Column(name = "entidad_bancaria")
	private String entidadBancaria;
	
	@Column(name = "fecha_inicio")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@PastOrPresent
	private LocalDate fechaInicio;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@FutureOrPresent
	private LocalDate fechaFin;
	
	@ManyToOne(optional = false)
	private Personal personal;
}
