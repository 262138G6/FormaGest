package com.curso.formagest.controller;

import com.curso.formagest.model.Curso;
import com.curso.formagest.repository.CursoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController 
@RequestMapping("/api/cursos")
@RequiredArgsConstructor 
public class CursoController {
	
	private final CursoRepository cursoRepository;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Curso> getAllCursos() {
		return cursoRepository.findAll();
	}

	@GetMapping("/{id}")
    public ResponseEntity<Curso> getCurso(@PathVariable Long id) {
        // findById() devuelve un Optional: map() lo convierte en 200 con el contacto si existe
        // y orElse() devuelve 404 si la caja está vacía.
        return cursoRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
    }

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Curso crear(@Valid @RequestBody Curso curso){
		curso.setId(null); // Quitar ID del cliente (si ha puesto uno) para que se genere uno automaticamente
		return cursoRepository.save(curso);
	}

	@PutMapping ("/{id}") 
	@ResponseStatus(HttpStatus.OK)
	@Transactional 
	public ResponseEntity<Curso> update(@Valid @RequestBody Curso curso, @PathVariable Long id){
		if(!cursoRepository.existsById(id)){ // Si no existe -> Not Found
			return ResponseEntity.notFound().build();
		}

		Curso cursoOriginal = cursoRepository.findById(id).get(); // Actualizar campos
		cursoOriginal.setActivo(curso.isActivo());
		cursoOriginal.setCodigo(curso.getCodigo());
		cursoOriginal.setDescripcion(curso.getDescripcion());
		cursoOriginal.setHoras(curso.getHoras());
		cursoOriginal.setNombre(curso.getNombre());

		return ResponseEntity.ok(cursoRepository.save(cursoOriginal));
	}

	@DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCurso(@PathVariable Long id) {
        if(!cursoRepository.existsById(id)){ // Si no existe -> Not Found
			return ResponseEntity.notFound().build();
		}
		if(!cursoRepository.findById(id).get().getEdiciones().isEmpty()){ // Si existe pero no se cumple la precondición -> Conflict
			return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body("No se puede eliminar un curso asociado a una edición");
		}
		cursoRepository.deleteById(id);
		return ResponseEntity.noContent().build(); // Si si existe -> borrar y respuesta 2xx
    }
}