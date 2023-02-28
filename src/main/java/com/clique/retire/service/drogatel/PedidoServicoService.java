package com.clique.retire.service.drogatel;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PedidoServico;
import com.clique.retire.repository.drogatel.PedidoServicoRepository;
import com.clique.retire.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PedidoServicoService {

	@Autowired
	private PedidoServicoRepository repository;
	
	@Autowired
	private ExpedicaoPedidoService expedicaoPedidoService;
	
	@Autowired
	private PendenciaPedidoService pendenciaPedidoService;
	
	public PedidoServico obterPedidoServicoPeloID(Long numeroPedidoServico) {
		return repository.findById(numeroPedidoServico)
									  .orElseThrow(() -> new BusinessException("Pedido de serviço não encontrado"));
	}
	
	public Optional<PedidoServico> obterPedidoServicoPeloPedido(Pedido pedido) {
		return repository.findByPedido(pedido);
	}
	
	public void atualizarFasePedidoServico(Pedido pedido, FasePedidoEnum fase) {
		Optional<PedidoServico> optionalPS = this.obterPedidoServicoPeloPedido(pedido);
		if (optionalPS.isPresent()) {
			PedidoServico pedidoServico = optionalPS.get();
			
			log.info("Atualizando a fase do pedido de serviço {} para {}", pedidoServico.getCodigo(), fase);
			pedidoServico.setFasePedido(fase);
			repository.save(pedidoServico);
			
			if (fase.equals(FasePedidoEnum.ENTREGUE) || fase.equals(FasePedidoEnum.NAO_ENTREGUE)) {
				ExpedicaoPedido expedicaoPedido = expedicaoPedidoService.finalizarExpedicaoPedidoMaisRecente(pedidoServico);
				if (fase.equals(FasePedidoEnum.NAO_ENTREGUE)) {
					pendenciaPedidoService.gerarPendencia(pedidoServico.getPedido().getNumeroPedido().intValue(), 
														  Constantes.FILA_RECEITA_NAO_RECOLHIDA, 
														  Objects.nonNull(expedicaoPedido) ? expedicaoPedido.getMotivoNaoEntrega() : "sem motivo especificado");
				}
			}
		}
	}
}