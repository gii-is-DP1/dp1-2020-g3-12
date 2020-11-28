package org.springframework.gresur.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "contratos")
public class Contrato extends BaseEntity{
	
	//TODO @SalarioMinimo Revisar implementacion en configuracion
	private Double nomina;
	
	@NotBlank
	@Column(name = "entidad_bancaria")
	private String entidadBancaria;
	
	@NotNull
	@Column(name = "fecha_inicio")
	@PastOrPresent
	private LocalDate fechaInicio;
	
	@NotNull
	@FutureOrPresent
	private LocalDate fechaFin;
	
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name ="tipo_jornada")
	private TipoJornada tipoJornada;

	@JsonIgnore
	@NotNull
	@ManyToOne(optional = false)
	private Personal personal;
}
