package com.clique.retire.repository.drogatel;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.clique.retire.dto.AvaliacaoDTO;
import com.clique.retire.model.drogatel.Avaliacao;

@Repository
public interface AvaliacaoCustom{

	/**
	 * Método para buscar os dados complementares para salvar uma Avaliação
	 * 
	 * @return ParametroCliqueRetire
	 */
	public AvaliacaoDTO buscarDadosComplementares(Integer codUsuario);
	
	
	/**
	 * Método utilizado para salvar uma Avaliação do Usuário
	 * 
	 */
	public void salvarAvaliacao(Avaliacao avaliacao);


	/**
	 * Método utilizado validar se o usuario ja realizou alguma validacao no dia
	 * 
	 */
	public Avaliacao recuperarAvaliacaoPorDia(Integer codUsuario, Date dataAtual);
}
