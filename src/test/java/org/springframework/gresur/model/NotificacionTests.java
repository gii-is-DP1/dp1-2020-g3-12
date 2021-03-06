package org.springframework.gresur.model;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NotificacionTests extends ValidatorTests{
	
	private Notificacion createSUT(String tipo, String cuerpo, String fecha , Integer emisor, Integer receptores) {
		Personal e = null;
		List<LineaEnviado> lr = new ArrayList<LineaEnviado>();
		
		Notificacion notificacion = new Notificacion();
		
		if(receptores != null && receptores > 0) {
			Dependiente d1 = new Dependiente();
			Administrador a1 = new Administrador();

			LineaEnviado ln1 = new LineaEnviado(notificacion, d1);
			LineaEnviado ln2 = new LineaEnviado(notificacion, a1);
			
			lr.add(ln1);
			lr.add(ln2);
			
		} if(emisor != null && emisor > 0) {
			e = new Administrador();
		}
		

		notificacion.setTipoNotificacion(tipo == null ? null:TipoNotificacion.valueOf(tipo));
		notificacion.setCuerpo(cuerpo);
		notificacion.setFechaHora(fecha == null ? null : LocalDateTime.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss")));
		notificacion.setEmisor(e);
		notificacion.setLineasEnviado(lr);
		
		return notificacion;
	}
	
	@ParameterizedTest
	@CsvSource({
		"NORMAL, Esto es una pequeña notificacion, 12/11/2020-23:52:31, 1, 1",
		"SISTEMA, Esto es una notificacion super larga para demostrar que pueden "
		+ "ser todo lo largas que haga falta asi que hola que tal todo el mundo yo aqui "
		+ "escribiendo codigo a las 3 de la mañana :D, 22/11/2020-03:54:39, ,1"
	})
	void validateNotificacionNoErrorsTest(String tipo, String cuerpo, String fecha, Integer emisor, Integer receptores) {
		Notificacion notificacion = this.createSUT(tipo, cuerpo, fecha, emisor, receptores);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Notificacion>> constraintViolations = validator.validate(notificacion);
		assertThat(constraintViolations.size()).isEqualTo(0);
	}
	
	@ParameterizedTest
	@CsvSource({
		", patata, 12/11/2020-23:52:31, 1, 1",
		", asljds, 12/11/2020-23:52:31, 0 ,1"
	})
	void validateNotificacionTipoNotificacionNotNullTest(String tipo, String cuerpo, String fecha, Integer emisor, Integer receptores) {
		Notificacion notificacion = this.createSUT(tipo, cuerpo, fecha, emisor, receptores);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Notificacion>> constraintViolations = validator.validate(notificacion);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"URGENTE, '', 12/11/2020-23:52:31, 1, 1",
		"SISTEMA, '   ', 12/11/2020-23:52:31, 0 ,1",
		"NORMAL, , 12/11/2020-23:52:31, 1, 1"
	})
	void validateNotificacionCuerpoNotBlankTest(String tipo, String cuerpo, String fecha , Integer emisor, Integer receptores) {
		Notificacion notificacion = this.createSUT(tipo, cuerpo, fecha, emisor, receptores);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Notificacion>> constraintViolations = validator.validate(notificacion);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
	@ParameterizedTest
	@CsvSource({
		"URGENTE, urgencia!!!!, , 1, 1",
		"SISTEMA, urgencia mas urgente!!!, , , 1"
	})
	void validateNotificacionFechaHoraNotNullTest(String tipo, String cuerpo, String fecha , Integer emisor, Integer receptores) {
		Notificacion notificacion = this.createSUT(tipo, cuerpo, fecha, emisor, receptores);
		
		Validator validator = createValidator();
		Set<ConstraintViolation<Notificacion>> constraintViolations = validator.validate(notificacion);
		assertThat(constraintViolations.size()).isEqualTo(1);
	}
	
}
