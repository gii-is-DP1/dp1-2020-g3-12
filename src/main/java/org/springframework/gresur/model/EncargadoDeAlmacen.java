package org.springframework.gresur.model;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encargadosAlmacen")
public class EncargadoDeAlmacen extends Personal{
	
	@OneToOne(optional = false)
	private Almacen almacen;
}
