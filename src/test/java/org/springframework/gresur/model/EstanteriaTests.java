package org.springframework.gresur.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EstanteriaTests extends ValidatorTests{
	
	private Estanteria createSUT(String categoria, Double capacidad, Long almacen, Integer lista) {
		List<Producto> l = new ArrayList<Producto>();
		Almacen alm = null;
		
		if(lista != 0) {
			Producto p1 = new Producto();
			Producto p2 = new Producto();
			l.add(p1);
			l.add(p2);
		} if (almacen != null) {
			alm = new Almacen();
			alm.setId(almacen);
		}
				
		Estanteria estanteria = new Estanteria();
		estanteria.setCategoria(categoria == null ? null : Categoria.valueOf(categoria));
		estanteria.setCapacidad(capacidad);	
		estanteria.setAlmacen(alm);
		estanteria.setProductos(l);
		
		return estanteria;
	}
	
	@ParameterizedTest
	@CsvSource({
		"PINTURAS, 10000.00, 1, 0",
		"CEMENTOS, 1230.19, 2, 1"
	})
	/* EL ATRIBUTO DE LA LISTA ES 0 SI ESTA VACIA, 1 SI TIENE ELEMENTOS*/
	void validateEstanteriaNoErrorsTest(String categoria, Double capacidad, Long almacen, Integer lista) {
		Estanteria estanteria = this.createSUT(categoria, capacidad, almacen, lista);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Estanteria>> constraintViolations = validator.validate(estanteria);
		assertThat(constraintViolations.size()).isEqualTo(0);
	}
	
	@ParameterizedTest
	@CsvSource({
		", 10000.00, 1, 0",
		" , 1230.19, 2, 1"
	})
	void validateEstanteriaCategoriaNotNullTest(String categoria, Double capacidad, Long almacen, Integer lista) {
		Estanteria estanteria = this.createSUT(categoria, capacidad, almacen, lista);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Estanteria>> constraintViolations = validator.validate(estanteria);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"CEMENTOS, , 1, 0",
		"PINTURAS, , 2, 1"
	})
	void validateEstanteriaCapacidadNotNullTest(String categoria, Double capacidad, Long almacen, Integer lista) {
		Estanteria estanteria = this.createSUT(categoria, capacidad, almacen, lista);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Estanteria>> constraintViolations = validator.validate(estanteria);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"CEMENTOS, -1.20, 1, 0",
		"PINTURAS, -20.1039, 2, 1"
	})
	void validateEstanteriaCapacidadMinTest(String categoria, Double capacidad, Long almacen, Integer lista) {
		Estanteria estanteria = this.createSUT(categoria, capacidad, almacen, lista);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Estanteria>> constraintViolations = validator.validate(estanteria);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"CEMENTOS, 101029.2030, , 0",
		"PINTURAS, 10303.239, , 1"
	})
	void validateEstanteriaAlmacenNotOptional(String categoria, Double capacidad, Long almacen, Integer lista) {
		Estanteria estanteria = this.createSUT(categoria, capacidad, almacen, lista);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Estanteria>> constraintViolations = validator.validate(estanteria);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}

}