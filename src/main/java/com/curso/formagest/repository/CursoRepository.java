package com.curso.formagest.repository;

import com.curso.formagest.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long>{
	
}
