package org.springframework.gresur.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Personal extends Entidad{
	
	@Column(unique = true)
	@NotBlank
	@Pattern(regexp = "^[0-9]{2}\\s?[0-9]{10}$")
	protected String NSS;
	
	protected String image;
	
	@JsonIgnore
	@OneToMany(mappedBy = "emisor")
	protected List<Notificacion> noti_enviadas;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "receptores")
	protected List<Notificacion> noti_recibidas;
	
	@JsonIgnore
	@OneToMany(mappedBy = "personal", cascade = CascadeType.REMOVE)
	protected List<Contrato> contratos;
}
