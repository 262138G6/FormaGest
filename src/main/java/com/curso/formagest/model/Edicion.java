package com.curso.formagest.model;

import com.curso.formagest.enums.Modalidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Setter
@Getter 
public class Edicion {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne // ManyToOne porque una edición solo puede tener un curso pero un curso puede formar parte de varias ediciones (1-N)
	@NotNull(message = "Debes seleccionar un curso.")
	private Curso curso;

	//hacemos fechas coherentes con la columna date, sin horas

	@NotNull(message = "La fecha de inicio es obligatoria.")
	private LocalDate fecha_inicio;

	@NotNull(message = "La fecha de fin es obligatoria.")
	private LocalDate fecha_fin;

	@Min(value = 1, message = "Debe haber al menos una plaza.") // Plazas tienen que ser > 0
	private int plazas;

	@Enumerated(EnumType.STRING)
	private Modalidad modalidad;

	@AssertTrue(message = "La fecha de fin debe ser igual o posterior a la fecha de inicio")
	public boolean isValidRange() {
		if (fecha_inicio == null || fecha_fin == null) {
			return true;
		}
		return !fecha_fin.isBefore(fecha_inicio);
	}

}
