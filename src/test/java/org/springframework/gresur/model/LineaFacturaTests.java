package org.springframework.gresur.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class LineaFacturaTests extends ValidatorTests {
	
	private LineaFactura createSUT(Integer cantidad,Long factura, Long producto, Double precio) {
		
		LineaFactura lineaFactura = new LineaFactura();
		Factura fac = null;
		Producto prod = null;
		
		lineaFactura.setCantidad(cantidad);
		lineaFactura.setPrecio(precio);
		
		if(factura!=null && factura>0) {
			fac = new Factura();
			lineaFactura.setFactura(fac);
		}
		
		if(producto!=null && producto>0) {
			prod = new Producto();
			lineaFactura.setProducto(prod);
		}
		
		return lineaFactura;
	}
	/*LOS ATRIBUTOS FACTURA Y PRODUCTO SI NO APARECEN O BIEN VALEN 0, SIGNIFICAN QUE SON NULOS */
	@ParameterizedTest
	@CsvSource({
		"15,1,1,20",
		"8,1,1,20",
		"1,1,1,20"
	})
	void validateLineaFacturaNoErrorsTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(0);
	}
	
	@ParameterizedTest
	@CsvSource({
		",1,1,20",
		",1,1,20",
		",2,2,40"
	})
	void validateLineaFacturaCantidadNotNullTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"-1,1,1,20"
	})
	void validateLineaFacturaCantidadNotMinTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	@ParameterizedTest
	@CsvSource({
		"15,,1,20",
		"8,0,1,20",
		"1,,1,20"
	})
	void validateLineaFacturaFacturaNotNullTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	@ParameterizedTest
	@CsvSource({
		"15,1,,20",
		"8,1,,20",
		"1,1,0,20"
	})
	void validateLineaFacturaProductoNotNullTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"15,1,1,",
		"8,1,1,-5",
		"1,1,1,"
	})
	void validateLineaFacturaNotNullPrecioTest(Integer cantidad,Long factura, Long producto, Double precio) {
		LineaFactura lineaFactura = this.createSUT(cantidad, factura, producto, precio);
		Validator validator = createValidator();
		Set<ConstraintViolation<LineaFactura>> constraintViolations = validator.validate(lineaFactura);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	

}
