package com.clique.retire.service.drogatel;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.Status;
import com.clique.retire.dto.StatusPedidoEcommerceDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.StatusPedidoEcommerce;
import com.clique.retire.repository.drogatel.StatusPedidoEcommerceRepository;
import com.clique.retire.util.XmlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class StatusPedidoEcommerceService {

	@Autowired
	private StatusPedidoEcommerceRepository repository;

	/**
	 * Atualiza a tabela do integrador VTEX, definindo as alterações no pedido, de
	 * acordo com a mudança de fase.
	 * 
	 * @param pedido
	 * @param codigoUsuario 
	 * @throws JsonProcessingException
	 */
	@Transactional("drogatelTransactionManager")
	public void atualizarStatusIntegradorVtex(Pedido pedido, FasePedidoEnum fase, Integer codigoUsuario) throws JsonProcessingException {
		// Monta o DTO para a integração com VTEX.
		StatusPedidoEcommerceDTO status = new StatusPedidoEcommerceDTO();
		status.setNumeroPedido(pedido.getNumeroPedido().intValue());
		status.setNumeroPedidoEcommerce(pedido.getCodigoVTEXSomenteNumeros());
		status.setNumeroPedidoEcommerceCliente(pedido.getCodigoVTEX());
		status.setDataAtualizacao(new Date());
		status.setValorNotaFiscal(pedido.getValorTotal());
		status.setCodigoFilial(pedido.getPolo().getCodigo());
		status.setNomeCliente(pedido.getCliente().getNome());
		status.setFase(fase.getChave());

		// Converte o DTO em XML.
		String xmlStatus = XmlUtil.serializarObjeto(status);

		// Cria um registro na tabela de status ecommerce (que é lida pela integração
		// com VTEX)
		StatusPedidoEcommerce statusEcommerce = new StatusPedidoEcommerce(codigoUsuario);
		statusEcommerce.setCodigoPedidoDrogatel(status.getNumeroPedido());
		statusEcommerce.setXml(xmlStatus);
		statusEcommerce.setEnviada(SimNaoEnum.N);
		statusEcommerce.setCodigoUsuarioAlteracao(String.valueOf(codigoUsuario));
		repository.save(statusEcommerce);
	}
	
	public void atualizarStatusIntegracaoEcommerce(Pedido pedido, Integer codigoUsuario) throws JsonProcessingException {
		String xmlStatus = XmlUtil.serializarObjeto(Status.builder()
														  .numeroPedido(pedido.getNumeroPedido().intValue())
														  .numeroPedidoEcommerce(pedido.getCodigoVTEXSomenteNumeros())
														  .numeroPedidoEcommerceCliente(pedido.getCodigoVTEX())
														  .fase(FasePedidoEnum.AGUARDANDO_RECEITA.getChave())
														  .build());
		
		StatusPedidoEcommerce statusEcommerce = new StatusPedidoEcommerce(codigoUsuario);
		statusEcommerce.setCodigoPedidoDrogatel(pedido.getNumeroPedido().intValue());
		statusEcommerce.setXml(xmlStatus);
		statusEcommerce.setEnviada(SimNaoEnum.N);
		statusEcommerce.setCodigoUsuarioAlteracao(String.valueOf(codigoUsuario));
		repository.save(statusEcommerce);
	}
}
