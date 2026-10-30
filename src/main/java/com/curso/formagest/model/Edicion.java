package com.curso.formagest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

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
	@NotNull
	private Curso curso;

	private Date fecha_inicio;

	private Date fecha_fin;

	@Min(1) // Plazas tienen que ser > 0
	private int plazas;

	private Modalidad modalidad;

	@AssertTrue public boolean isValidRange() { // @AssertTrue para que el @Validate falle si el final no es posterior al inicio
		return fecha_fin.compareTo(fecha_inicio) >= 0;
	}
}
