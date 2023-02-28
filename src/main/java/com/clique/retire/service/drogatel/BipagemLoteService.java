package com.clique.retire.service.drogatel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.dto.ItensProdutoDTO;
import com.clique.retire.dto.PedidoDTO;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.ItemPedidoResumido;
import com.clique.retire.model.drogatel.LoteBipado;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PedidoResumido;
import com.clique.retire.model.drogatel.SeparacaoPedido;
import com.clique.retire.repository.drogatel.LoteBipadoRepository;
import com.clique.retire.repository.drogatel.PedidoRepository;
import com.clique.retire.repository.drogatel.PedidoResumidoRepository;
import com.clique.retire.repository.drogatel.SeparacaoRepository;
import com.clique.retire.util.SecurityUtils;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BipagemLoteService {

	private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado.";
	private static final String FALHA_BIPAGEM = "Ocorreu um erro, favor realizar novamente a bipagem dos produtos.";
	private static final String FALHA_DEFINIR_PRODUTO = "Problema na identificação do produto.";

	@Autowired
	private SeparacaoRepository repository;

	@Autowired
	private LoteBipadoRepository loteRepository;

	@Autowired
	private PedidoResumidoRepository pedidoResumidoRepository;

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private SeparacaoService separacaoService;

	public PedidoDTO separarItem(Long idPedido, ItensProdutoDTO itens) {

		Optional<SeparacaoPedido> separacao = repository.obterSeparacaoEmAbertoPorIdPedido(idPedido);

		if (!separacao.isPresent()) {
			throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
		}

		Pedido pedido = separacaoService.validarSeparacaoPedido(idPedido);
		
		validarLotesDuplicados(itens, pedido);
		
		itens.getItens().stream().forEach(item ->
			produtoService.validarBipagemExigeReceita(item)
		);

		Optional<PedidoResumido> pedidoResumido = pedidoResumidoRepository.findById(idPedido);
		if (!pedidoResumido.isPresent()) {
			throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
		}
		
		Integer codigoProduto = itens.getItens().get(0).getCodigoProduto();
		
		List<ItemPedidoResumido> listItem = pedidoResumido.get().getItensPedido()
												.stream()
												.filter(p -> p.getProduto().getCodigo().equals(codigoProduto))
												.collect(Collectors.toList());
		
		if (listItem.isEmpty()) {
			throw new EntidadeNaoEncontradaException(FALHA_DEFINIR_PRODUTO);
		}
		
		itens.getItens().stream().forEach(item -> {
			log.info("[BIPAGEM] Salvando lote - pedido: " + idPedido + " - " + new Gson().toJson(item));
			
			Optional<ItemPedidoResumido> itemPedidoOpt = listItem.stream()
																 .filter(i ->
																	  i.getProduto().getCodigo().equals(item.getCodigoProduto()) &&
																			i.getQuantidadeBipada() < i.getQuantidadePedida() )
																 .findFirst(); 
			
			if (!itemPedidoOpt.isPresent()) {
				throw new BusinessException(FALHA_DEFINIR_PRODUTO);
			}
			
			ItemPedidoResumido itemPedidoResumido = itemPedidoOpt.get();
			itemPedidoResumido.setQuantidadeBipada(itemPedidoResumido.getQuantidadeBipada() + 1);
			
			salvarLoteBipado(item, itemPedidoResumido);
			ratearSeparacao(pedido, item.getCodigoProduto(), item.getQuantidadeSeparada());

		});

		pedidoRepository.save(pedido);

		
		return pedidoService.buscarPedidoPorId(pedido.getNumeroPedido());
	}

	private void validarLotesDuplicados(ItensProdutoDTO itens, Pedido pedido) {
		List<ItemPedido> itensControladoPedido = pedido.getItensPedido().stream()
				.filter(p -> p.getProdutoControlado().equals(SimNaoEnum.S)).collect(Collectors.toList());

		ItemPedidoDTO dto = new ItemPedidoDTO();
		Optional<ItemPedidoDTO> value = itens.getItens().stream().findFirst();
		if(value.isPresent()){
			dto = value.get();
		}
		ItemPedidoDTO finalDto = dto;
		Integer qtdTotalPedida = itensControladoPedido.stream()
				.filter(it -> it.getCodigoProduto() == finalDto.getCodigoProduto().intValue())
				.map(ItemPedido::getQuantidadePedida)
				.reduce(0,(p1, p2) -> p1 + p2);
		
		if (qtdTotalPedida != itens.getItens().size()) {
			throw new BusinessException(FALHA_BIPAGEM);			
		}
	}

	public void salvarLoteBipado(ItemPedidoDTO item, ItemPedidoResumido itemPedido) {
		LoteBipado loteBipado = new LoteBipado(SecurityUtils.getCodigoUsuarioLogado());
		loteBipado.setItemPedido(itemPedido);
		loteBipado.setLote(item.getLote());
		loteBipado.setQuantidade(1);
		loteBipado.setCodigoItemNotaFiscal(pedidoRepository.buscarCodigoItemNotaFiscal(itemPedido.getCodigo()));
		loteBipado.setTipoBipagem("R");
		loteRepository.save(loteBipado);
	}
	
	public void ratearSeparacao(Pedido pedido, Integer idProduto, Integer quantidadeSeparada) {
		
		for (ItemPedido itemPedido : pedido.getItensPedido()) {

			if (itemPedido.getProduto().getCodigo().equals(idProduto) && !itemPedido.isSeparacaoCompleta()) {
				if (quantidadeSeparada > itemPedido.getQuantidadePedida()) {
					quantidadeSeparada -= itemPedido.getQuantidadePedida();
					itemPedido.setQuantidadeSeparada(itemPedido.getQuantidadePedida());
				} else {
					itemPedido.setQuantidadeSeparada(quantidadeSeparada);
					quantidadeSeparada = 0;
				}
			}
		}
	}	
}