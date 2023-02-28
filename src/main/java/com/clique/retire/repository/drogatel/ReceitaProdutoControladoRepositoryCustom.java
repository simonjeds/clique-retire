package com.clique.retire.repository.drogatel;

import java.util.List;

import com.clique.retire.dto.DadosCaptacaoConferenciaDTO;
import com.clique.retire.dto.PacienteDTO;
import com.clique.retire.dto.ReceitaSkuDTO;

/**
 * @author Framework
 */
public interface ReceitaProdutoControladoRepositoryCustom {

  ReceitaSkuDTO buscaDadosCaptacao(Long numeroPedido);

  List<ReceitaSkuDTO> buscaDadosParaEnvioDeCaptacaoEEmissaoDeEtiqueta(Long numeroPedido);

  void removerProdutoReceitaControlado(Integer numeroPedido);

  boolean isEntregaViaMotociclistaEContemControlado(Integer numeroPedido);
  
  boolean existeDadosDaReceita(Long numeroPedido);
  
  void atualizarAutorizacaoPeloNumeroPedidoEReceita(Long numeroPedido, String numeroReceita, String numeroAutorizacao);
  
  void removerAutorizacaoPeloNumeroPedido(Long numeroPedido);

  DadosCaptacaoConferenciaDTO buscarDadosCaptacaoParaConferencia(Long numeroPedido);

  public List<PacienteDTO> buscaPacientesComandaSeparacao(Long numeroPedido);

}
