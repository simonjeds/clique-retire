package com.clique.retire.repository.drogatel;

import java.util.List;

import com.clique.retire.dto.PagamentoEmConvenioDTO;
import com.clique.retire.dto.SolicitacaoAutorizacaoConvenioDTO;

public interface ConvenioRepositoryCustom {
	
	/**
	 * Remove as autorizações geradas em excesso para o convênio. (OBS: ESTA REMOÇÃO É TEMPORARIA - EQUIPE DO DROGATE IRÁ CORRIGIR O PROCESSO) 
	 * @param numeroPedido
	 */
	void removerDadosSobreAutorizacaoConvenioEmExcesso(Long numeroPedido);

	/**
	 * Consulta os dados necessários para regeração de autorização de convênio para o pedido
	 * @param codigosItem - código dos itens
	 * @param idLoja - identificador da loja solicitante
	 * @return DTO com os dados da solicitação de convênio
	 */
    SolicitacaoAutorizacaoConvenioDTO obterDadosParaRegerarAutorizacaoConvenio(List<Integer> codigosItem, Integer idLoja);

    /**
     * Atualiza os dados da tabela PAGAMENTO_EM_CONVENIO e ITEM_AUTORIZACAO_CONVENIO com base na nova autorização
     * @param numeroPedido numero do pedido
     * @param pagamentoEmConvenioDTO dados para serem atualizados
     */
    void atualizarDadosPagamentoEmConvenio(Long numeroPedido, PagamentoEmConvenioDTO pagamentoEmConvenioDTO);

}
