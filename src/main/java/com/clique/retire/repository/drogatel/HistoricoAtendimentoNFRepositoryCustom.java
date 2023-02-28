package com.clique.retire.repository.drogatel;

import java.text.ParseException;
import java.util.List;

import com.clique.retire.dto.NotaFiscalDTO;
import com.clique.retire.dto.RelatorioHistoricoNFDTO;

public interface HistoricoAtendimentoNFRepositoryCustom{

	/**
	 * Busca uma nota fiscal pela chave.
	 * 
	 * @param chaveNF
	 * @return NotaFiscalDTO
	 * @throws ParseException
	 */
	public NotaFiscalDTO buscarNotaFiscal(String chaveNF) throws ParseException;

	/**
	 * Busca o histórico de atendimento de uma Nota Fiscal para impressão.
	 * 
	 * @param notaFiscal
	 * @return List<RelatorioHistoricoNFDTO>
	 */
	public List<RelatorioHistoricoNFDTO> buscarHistoricoPorNotaParaImpressao(NotaFiscalDTO notaFiscal);
	
}