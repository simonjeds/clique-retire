package com.clique.retire.service.cosmos;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.cosmos.UsuarioCosmosRepositoryCustom;
import com.clique.retire.repository.drogatel.UsuarioRepository;

@Service
public class UsuarioCosmosService {
	
	@Autowired
	private UsuarioCosmosRepositoryCustom usuarioRepositoryCustom;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional("cosmosTransactionManager")
	public Integer validarMatricula(String matricula) {
		matricula = matricula.replaceAll("[^0-9]", "");
		
		if (StringUtils.isNotBlank(matricula)) {
			return usuarioRepositoryCustom.buscarCodigoUsuarioPorMatricula(matricula);
		}
		
		return null;
	}
	
	public Optional<Integer> buscarUsuarioParaConferenciaCaptacao(String matriculaUsuario) {
		return Optional.ofNullable(usuarioRepositoryCustom.buscarUsuarioParaConferenciaCaptacao(matriculaUsuario));
	}
	
	public Usuario obterPeloId(Integer idUsuario) {
		return usuarioRepository.findById(idUsuario).orElseThrow(() -> new BusinessException("Usuário não encontrado"));
	}

	public Usuario buscarPelaMatricula(String matricula) {
		return usuarioRepository.findByMatricula(matricula)
				.orElseThrow(() -> new BusinessException("Usuário não encontrado para matrícula informada"));
	}

}
