package com.clique.retire.service.drogatel;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DrogatelParametroService {

	@Autowired
    private DrogatelParametroRepository repository;

    public DrogatelParametro obterParametro(ParametroEnum parametroEnum) {
    	DrogatelParametro parametro = repository.findByNome(parametroEnum.getDescricao());
    	if (Objects.isNull(parametro)) 
    		throw new EntidadeNaoEncontradaException(String.format("Parâmetro s%, não encontrado!!", parametroEnum.getDescricao()));
        return parametro;
	}
    
    public String obterValorParametro(ParametroEnum parametroEnum) {
    	return obterParametro(parametroEnum).getValor().trim();
    }
    
    public String[] obterArrayParametro(ParametroEnum parametroEnum, String separador) {
    	return obterValorParametro(parametroEnum).split(separador);
    }
}
