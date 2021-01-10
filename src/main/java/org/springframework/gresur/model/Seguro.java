package org.springframework.gresur.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "seguros")
public class Seguro extends BaseEntity implements Comparable<Seguro>{
	
	@NotBlank
	@Column(name = "compania")
	private String compania;
	
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "tipo_seguro")
	private TipoSeguro tipoSeguro;
	
	@NotNull
	@PastOrPresent
	@Column(name = "fecha_contrato")
	private LocalDate fechaContrato;
	
	@NotNull
	@Column(name = "fecha_expiracion")
	private LocalDate fechaExpiracion;
	
	@JsonIgnore
	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(name = "factura_recibida_id")
	private FacturaRecibida recibidas;
	
	@JsonIgnore
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "vehiculo_id")
	private Vehiculo vehiculo;

	@Override
	public int compareTo(Seguro o) {
		return this.fechaContrato.compareTo(o.fechaContrato);
	}
}
