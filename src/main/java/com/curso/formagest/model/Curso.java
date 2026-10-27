package com.curso.formagest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Setter
@Getter 
public class Curso {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique=true)
	@NotBlank(message = "El código de curso es obligatorio")
	private String codigo;

	@NotBlank 
	private String nombre;

	@NotBlank 
	private String descripcion;

	private int horas;

	private boolean activo;

	@OneToMany(mappedBy = "curso") // Atributo vinulado al atributo "curso" de Edicion. Si una edicion pertenece a un curso se añade a esta lista. Usado para buscar si un curso pertenece a una edición rapidamente
	private List<Edicion> ediciones = new ArrayList<>();
}
