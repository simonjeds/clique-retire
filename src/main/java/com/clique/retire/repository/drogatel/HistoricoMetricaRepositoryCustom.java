package com.clique.retire.repository.drogatel;

import java.time.LocalDate;
import java.util.List;

import com.clique.retire.dto.HistoricoMetricaDTO;
import com.clique.retire.model.drogatel.HistoricoMetrica;

public interface HistoricoMetricaRepositoryCustom {
	
	
	/**
	 * Consulta o histórico de metricas para enviar ao firebise
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public List<HistoricoMetricaDTO> consultarMetricas(LocalDate dataInicial, LocalDate dataFinal);
	
	/**
	 * Método que salva as métricas no banco de dados DROGATEL.
	 * 
	 * @param metricas
	 */
	public void salvarEmBatch(List<HistoricoMetrica> metricas);
}
