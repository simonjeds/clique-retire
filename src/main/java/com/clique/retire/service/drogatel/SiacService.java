package com.clique.retire.service.drogatel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.dto.OrigemPrePedidoEcommerceResponseDTO;
import com.clique.retire.dto.PrePedidoDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.ItemPedidoPBM;
import com.clique.retire.model.drogatel.ItemPrePedidoSiac;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.model.enums.AtivoInativoEnum;
import com.clique.retire.repository.drogatel.ItemPedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.SiacRepository;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.FeignUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SiacService {
	
	@Autowired
	ItemPedidoService itemPedidoService;
	
	@Autowired
	private ItemPedidoRepositoryCustom itemPedidoRepositoryCustom;
	
	@Autowired
	private SiacRepository siacRepository;
	
	@Autowired
	private ParametroService parametroService;
	
	@Transactional(value = "drogatelTransactionManager")
	public Collection<PrePedidoSiac> criarPrePedidoSiac(Pedido pedido, Integer codigoUsuarioResponsavel) {
		
		log.info("Processo de criação do(s) pre-pedido(s) SIAC referente ao pedido {} iniciado.", pedido.getNumeroPedido());
		List<PrePedidoSiac> listaRegistroPedido = gerarPrePedidoApp(pedido, codigoUsuarioResponsavel);
		
		for(PrePedidoSiac prePedido: listaRegistroPedido) {
			String numeroPrePedido = obterPrepedido(pedido.getPolo().getCodigo(), prePedido.getCodigo(), Constantes.TIPO_PEDIDO_DROGATEL);
			log.info("Pre-pedido SIAC número {} foi gerado com sucesso. Pedido {}.", numeroPrePedido, pedido.getNumeroPedido());
			prePedido.setNumeroPrePedido(Integer.valueOf(numeroPrePedido));
			prePedido.setAtivoInativo(AtivoInativoEnum.A);
			siacRepository.atualizarNumeroPrePeddio(prePedido);
		}
		return listaRegistroPedido;
	}
	
	/**
	 * Gera o(s) prepedido(s) SIAC a partir de um pedido.
	 * @param numeroPedido
	 * @param codigoUsuarioResponsavel
	 * @return Lista com os pre-pedidos gerados
	 */
	private List<PrePedidoSiac> gerarPrePedidoApp(Pedido pedido, Integer codigoUsuarioResponsavel) {
		Map  <String, Set<ItemPedido>> mapaPrePedidos = obterChavePrePedido(pedido);
		final Set <String> setChaves = mapaPrePedidos.keySet();
		final List<PrePedidoSiac> retorno = new ArrayList<>();
		
		for (String mapPre : setChaves) {
			Set<ItemPedido> itens = mapaPrePedidos.get(mapPre);
			PrePedidoSiac prePedido = montarPrePedidoSIAC(pedido.getNumeroPedido().intValue(), pedido.getPolo().getCodigo(), itens,
					codigoUsuarioResponsavel);
			siacRepository.save(prePedido);
			retorno.add(prePedido);
		}
		return retorno;
	}
	
	
	/**
	 * Separa os itens do pedido de acordo com o prepedido que cada um deve estar
	 * @param Pedido
	 * @return Map com os itens
	 */
	@Transactional
	public Map <String, Set<ItemPedido>> obterChavePrePedido(Pedido pedido) {
		final Set<ItemPedido> listaItensPedidoControlado = new HashSet<>();
		final Set<ItemPedido> listaItensPedidoNormal = new HashSet<>();
		final Map<String, Set<ItemPedido>> mapaPrePedidos = new HashMap<>();
		
		for (ItemPedido item : pedido.getItensPedido()) {

			if(SimNaoEnum.S.equals(item.getItemRegistrado())) {
				continue;
			}

			final String autorizacaoPBM = itemPedidoRepositoryCustom.obterNumeroAutorizacaoPBM(item.getCodigo());

			if (autorizacaoPBM != null) {
				final Set <ItemPedido> setItens;
				if (mapaPrePedidos.containsKey(autorizacaoPBM)) {
					setItens = mapaPrePedidos.get(autorizacaoPBM);
				} else {
					setItens = new HashSet<>();
					mapaPrePedidos.put(autorizacaoPBM, setItens);
				}
				setItens.add(item);

			} else if (SimNaoEnum.S.equals(item.getProdutoControlado())) {
				listaItensPedidoControlado.add(item);
			} else {
				listaItensPedidoNormal.add(item);
			}
		}

		if (!listaItensPedidoNormal.isEmpty()) {
			mapaPrePedidos.put("NORMAL", listaItensPedidoNormal);
		}
		if (!listaItensPedidoControlado.isEmpty()) {
			mapaPrePedidos.put("CONTROLADO", listaItensPedidoControlado);
		}

		return mapaPrePedidos;
	}
	
	@SuppressWarnings("rawtypes")
	public PrePedidoSiac montarPrePedidoSIAC(Integer numeroPedido, Integer codigoFilial, Set itens,
			Integer codigoResponsavel){
		PrePedidoSiac prePedido = new PrePedidoSiac(codigoResponsavel.toString());
		prePedido.setNumeroPedido(numeroPedido);
		prePedido.setCodigoFilial(codigoFilial);
		List <ItemPrePedidoSiac>itensPedido = new ArrayList<>();
		prePedido.setAtivoInativo(AtivoInativoEnum.A);
		
		 for(Object item : itens){
			 ItemPrePedidoSiac itemPrepedidoSIAC =  new ItemPrePedidoSiac(codigoResponsavel.toString());
			 itemPrepedidoSIAC.setAtivoInativo(AtivoInativoEnum.A);
			 itemPrepedidoSIAC.setPrePedido(prePedido);
			 
			 if(item instanceof ItemPedidoPBM) {
				 ItemPedidoPBM itemPbm = (ItemPedidoPBM) item;				 
				 itemPrepedidoSIAC.setCodigoItemPedido(itemPbm.getItemPedido().getCodigo());
			 }else if(item instanceof ItemPedido){
				 ItemPedido itemPedido = (ItemPedido) item;
				 itemPrepedidoSIAC.setCodigoItemPedido(itemPedido.getCodigo());
			 }
			 
			 itensPedido.add(itemPrepedidoSIAC);
		 }
		 prePedido.setItens(itensPedido);
		return prePedido;
	}
	
	private String obterPrepedido(Integer codigoLoja, Integer codigoOrigem, String tipoPedido) {
		String url = parametroService.buscarPorChave(ParametroEnum.URL_WSDL_SIAC.getDescricao()).getValor();
		OrigemPrePedidoEcommerceResponseDTO responseDTO  = 
				FeignUtil.getIntegradorSiacClient(url)
						 .gerarPrePedido(PrePedidoDTO.builder()
							  					     .codigoLoja(codigoLoja)
													 .codigoOrigem(codigoOrigem)
													 .tipoPrePedido(tipoPedido)
													 .build());
		
		if (!"success".equals(responseDTO.getStatus())) 
			throw new BusinessException("Não foi possível gerar o PRÉ-PEDIDO SIAC!!");
		
		return responseDTO.getOrigemPrePedido().getCodigoPreVenda().toString();
	}
	
	public List<ItemPedidoSIACDTO> obterItensValorMaiorQueSIAC(Integer numeroPedido) {
		return siacRepository.obterItensValorMaiorQueSIAC(numeroPedido);
	}
	
}