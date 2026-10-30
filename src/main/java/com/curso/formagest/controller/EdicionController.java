package com.curso.formagest.controller;

import com.curso.formagest.repository.CursoRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.curso.formagest.model.Edicion;
import com.curso.formagest.repository.EdicionRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController 
@RequestMapping("/api/ediciones")
@RequiredArgsConstructor 
@Tag(name = "Ediciones", description = "Creación, modificación y borrado de ediciones")
public class EdicionController {
	private final CursoRepository cursoRepository;
	private final EdicionRepository edicionRepository;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Edicion> getAllEdiciones() {
		return edicionRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Edicion> crear(@Valid @RequestBody Edicion edicion){
		edicion.setId(null); // Quitar ID del cliente (si ha puesto uno) para que se genere uno automaticamente

		Long cursoId =  edicion.getCurso().getId();
		if(!cursoRepository.existsById(cursoId)){
			return ResponseEntity.notFound().build();
		}

		edicion.setCurso(cursoRepository.findById(cursoId).get());

		Edicion guardada = edicionRepository.save(edicion);
		return ResponseEntity.created(URI.create("/api/ediciones/" + guardada.getId())).body(guardada);
	}

	@PutMapping ("/{id}") 
	@ResponseStatus(HttpStatus.OK)
	@Transactional 
	public ResponseEntity<Edicion> update(@Valid @RequestBody Edicion edicion, @PathVariable Long id){
		if(!edicionRepository.existsById(id)){ // Si no existe -> Not Found
			return ResponseEntity.notFound().build();
		}

		Long cursoId =  edicion.getCurso().getId();
		if(!cursoRepository.existsById(cursoId)){
			return ResponseEntity.notFound().build();
		}

		Edicion edicionOriginal = edicionRepository.findById(id).get(); // Actualizar campos
		edicionOriginal.setCurso(cursoRepository.findById(cursoId).get());
		edicionOriginal.setInicio(edicion.getInicio());
		edicionOriginal.setFin(edicion.getFin());
		edicionOriginal.setPlazas(edicion.getPlazas());
		edicionOriginal.setModalidad(edicion.getModalidad());

		return ResponseEntity.ok(edicionRepository.save(edicionOriginal));
	}

	@DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if(!edicionRepository.existsById(id)){ // Si no existe -> Not Found
			return ResponseEntity.notFound().build();
		}
		edicionRepository.deleteById(id);
		return ResponseEntity.noContent().build();
    }
}
