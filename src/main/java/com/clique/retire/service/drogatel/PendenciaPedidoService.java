package com.clique.retire.service.drogatel;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPendenciaPedidoEnum;
import com.clique.retire.model.drogatel.MotivoDrogatel;
import com.clique.retire.model.drogatel.PendenciaPedidoDrogatel;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.drogatel.PendenciaPedidoRepository;
import com.clique.retire.util.Constantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PendenciaPedidoService {

  private final PendenciaPedidoRepository repository;
  private final MotivoDrogatelService motivoDrogatelService;

  @Transactional
  public void removerPendenciaPedidoControladoNaoEntregueNoPrazo(Long numeroPedido) {
    this.repository.removerPendenciaPedidoControladoNaoEntregueNoPrazo(numeroPedido);
  }
  
  public void gerarPendencia(Integer numeroPedido, String titulo, String motivo) {
	  
	  MotivoDrogatel filaPedidoNaoEntregue = motivoDrogatelService.getMotivoByDescricaoCache(titulo);
	  String descricaoPendencia = String.format("%s: %s", titulo, motivo);
	  
	  PendenciaPedidoDrogatel pendenciaPedido = new PendenciaPedidoDrogatel(Constantes.USUARIO_ADMINISTRADOR.toString());
	  pendenciaPedido.setNumeroPedido(numeroPedido);
	  pendenciaPedido.setDescricaoPendencia(descricaoPendencia);
	  pendenciaPedido.setCodigoMotivoDrogatel(filaPedidoNaoEntregue.getId().intValue());
	  pendenciaPedido.setPendenciaResolvida(SimNaoEnum.N);
	  pendenciaPedido.setDataCriacao(new Date());
	  pendenciaPedido.setTipoPendencia(TipoPendenciaPedidoEnum.DIVERSAS.getChave());
	  pendenciaPedido.setResponsavelPendencia(new Usuario(Constantes.USUARIO_ADMINISTRADOR));
	  
	  repository.save(pendenciaPedido);
  }

}