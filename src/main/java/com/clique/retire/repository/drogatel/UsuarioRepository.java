package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Usuario;

import java.util.Optional;

/**
 * @author Framework
 *
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	/**
	 * Método responsável por consultar um do drogatel pelo código.
	 * 
	 * @param codigoUsuario
	 * @return Usuario
	 */
	@Query(value = "SELECT u FROM Usuario u WHERE u.codigoUsuario = :codigoUsuario")
	Usuario buscarPorCodigoUsuario(@Param("codigoUsuario") Integer codigoUsuario);

	Optional<Usuario> findByMatricula(String matricula);
	
}
