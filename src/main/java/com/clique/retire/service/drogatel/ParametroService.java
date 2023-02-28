package com.clique.retire.service.drogatel;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParametroService {

    private final DrogatelParametroRepository repository;

    public DrogatelParametro buscarPorChave(String nomeParametro) {
        return repository.findByNome(nomeParametro);
    }

	public String buscarPorChave(ParametroEnum parametro) {
		String nomeParametro = parametro.getDescricao();
		return Optional.ofNullable(repository.findByNome(nomeParametro))
				.map(DrogatelParametro::getValor)
				.orElseThrow(() -> new IllegalArgumentException("Parãmetro não encontrado com nome: "+ nomeParametro));
	}
	
}
