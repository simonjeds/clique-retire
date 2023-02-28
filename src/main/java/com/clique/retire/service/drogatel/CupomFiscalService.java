package com.clique.retire.service.drogatel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.SituacaoCupomEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.CupomFiscal;
import com.clique.retire.model.drogatel.ItemCupomFiscal;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.CupomFiscalRepository;
import com.clique.retire.repository.drogatel.PedidoRepository;

@Service
public class CupomFiscalService {
	
	private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado - ";

	private static final Logger LOGGER = LoggerFactory.getLogger(CupomFiscalService.class);
	
	@Autowired
	private CupomFiscalRepository cupomFiscalRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PedidoService pedidoService;
	
	@Transactional("drogatelTransactionManager")
	public void cadastrarAtualizarCupomFiscal(Set<ItemPedido> itensPedido, Integer codigoRegistroPedido,
			Integer codigoUsuario) {

		CupomFiscal cupomFiscal = cupomFiscalRepository.findCupomFiscalByCodigoRegistro(codigoRegistroPedido);
		if (cupomFiscal != null) {
			cupomFiscal.setCodigoRegistro(codigoRegistroPedido);
		}else {
			cupomFiscal = new CupomFiscal(codigoUsuario.toString());
			cupomFiscal.setSituacaoCupom(SituacaoCupomEnum.E);		 
			cupomFiscal.setItensCuponsFiscais(montarItensCupom(itensPedido, cupomFiscal,codigoUsuario));
			cupomFiscal.setCodigoRegistro(codigoRegistroPedido);
			
		 }
		cupomFiscal = cupomFiscalRepository.save(cupomFiscal);
		 
		 LOGGER.info("Cupom fiscal inserido para o registro: {} - qtd itens: {}",
			 codigoRegistroPedido, cupomFiscal.getItensCuponsFiscais().size()
		 );
	}
	
	private List<ItemCupomFiscal> montarItensCupom(Set<ItemPedido> itensPedido,CupomFiscal cupomFiscal,
			Integer codigoUsuario) {
		List<ItemCupomFiscal> result = new ArrayList<>();

		for(ItemPedido  itemPedido: itensPedido) {

			if (itemPedido.getItemRegistrado().equals(SimNaoEnum.N)) {
				ItemCupomFiscal itemCupomFiscal = new ItemCupomFiscal(codigoUsuario.toString());
				itemCupomFiscal.setItemPedido(itemPedido);
				itemCupomFiscal.setCupom(cupomFiscal);
				itemCupomFiscal.setQuantidadeDevolvida(Integer.valueOf(0));
				result.add(itemCupomFiscal);
			}
		}
		return result;
	}
	
	@Transactional("drogatelTransactionManager")
	public void confirmarEmissaoCupom(Long numeroPedido, Integer codigoUsuario) {

		// Busca o pedido pelo número de pedido.
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);
		
		if (pedido == null) {
			throw new BusinessException(PEDIDO_NAO_ENCONTRADO + numeroPedido);
		}
		
		// Altera a fase do pedido para Aguardando expedição (código 19)
		pedidoService.registrarNovaFasePedido(pedido,
				FasePedidoEnum.AGUARDANDO_EXPEDICAO,
				pedido.getFasePedido(), codigoUsuario, pedido.getFilial().getId());
	}
}
