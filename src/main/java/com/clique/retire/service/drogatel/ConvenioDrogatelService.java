package com.clique.retire.service.drogatel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.clique.retire.client.soap.ConvenioDrogatelClient;
import com.clique.retire.dto.ItemAutorizacaoConvenioDTO;
import com.clique.retire.dto.ItemSolicitacaoAutorizacaoConvenioDTO;
import com.clique.retire.dto.PagamentoEmConvenioDTO;
import com.clique.retire.dto.SolicitacaoAutorizacaoConvenioDTO;
import com.clique.retire.dto.ValidarRegrasAutorizacaoNormalResponseDTO.ConvenioResponse;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.ConvenioRepositoryCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConvenioDrogatelService {

    private final FilialService filialService;
    private final PedidoService pedidoService;
    private final ConvenioRepositoryCustom convenioRepository;
    private final ConvenioDrogatelClient convenioDrogatelClient;

    /**
     * Este método refaz a autorização de convênio para o pedido e atualiza o valor dos itens do pedido, mas não
     * altera os itens da nota fiscal nem os valores da modalidade de pagamento
     * @param pedido pedido para regeração de autorização de convênio
     */
    public void refazerConvenioPedido(Pedido pedido) {
        Long numeroPedido = pedido.getNumeroPedido();
        log.info("Refazendo autorização de convênio para o pedido [{}]", numeroPedido);
        Integer codigoLojaSolicitacao = filialService.obterIdFilialParaGerarAutorizacaoConvenio(numeroPedido);
        
        List<Integer> codigosItem = pedido.getItensPedido().stream().map(ItemPedido::getCodigo).collect(Collectors.toList());
        
        SolicitacaoAutorizacaoConvenioDTO solicitacao =
                convenioRepository.obterDadosParaRegerarAutorizacaoConvenio(codigosItem, codigoLojaSolicitacao);
        List<ItemSolicitacaoAutorizacaoConvenioDTO> itensSolicitacao = solicitacao.getItensSolicitacaoAutorizacao();

        ConvenioResponse response = convenioDrogatelClient.validarRegrasAutorizacaoNormal(solicitacao);
        if (Objects.isNull(response.getItensAutorizacao())) 
        	throw new BusinessException("Nenhum item do pedido foi autorizado novamente pelo convênio");
        List<ItemAutorizacaoConvenioDTO> itensAutorizados = response.getItensAutorizacao().stream()
                .map(itemAutorizacao -> {
                    ItemSolicitacaoAutorizacaoConvenioDTO itemSolicitacao = itensSolicitacao.stream()
                            .filter(item -> item.getCodigoProduto().equals(itemAutorizacao.getCodigoProduto())
                                    && item.getQuantidadeSolicitada().equals(itemAutorizacao.getQuantidadeAprovada())
                                    && !item.isItemUsado()
                            ).findFirst()
                            .orElseThrow(() -> new BusinessException("Não foi possível identificar o item autorizado"));

                    itemSolicitacao.setItemUsado(true);

                    return ItemAutorizacaoConvenioDTO.builder()
                            .codigoItem(itemSolicitacao.getCodigoItem())
                            .quantidadeSolicitada(itemSolicitacao.getQuantidadeSolicitada())
                            .quantidadeAutorizada(itemAutorizacao.getQuantidadeAprovada())
                            .codigoProduto(itemAutorizacao.getCodigoProduto())
                            .valorPagoConvenio(itemAutorizacao.getPrecoUnitario() - itemAutorizacao.getPrecoUnitarioPagoAVista())
                            .precoVenda(itemAutorizacao.getPrecoVenda())
                            .precoUnitario(itemAutorizacao.getPrecoUnitario())
                            .valorPagoAVista(itemAutorizacao.getPrecoUnitarioPagoAVista())
                            .build();
                }).collect(Collectors.toList());

        Double valorPagoConvenio = response.getValorTotalPedido() - response.getValorTotalPagoAVista();
        if (SimNaoEnum.S.equals(pedido.getMarketplace())) 
        	valorPagoConvenio += pedido.getValorTaxaEntrega();

        PagamentoEmConvenioDTO pagamentoEmConvenioDTO = PagamentoEmConvenioDTO.builder()
                .numeroAutorizacao(Integer.parseInt(response.getNumeroAutorizacao()))
                .valorPagoConvenio(valorPagoConvenio)
                .itensAutorizacao(itensAutorizados)
                .build();

        convenioRepository.atualizarDadosPagamentoEmConvenio(numeroPedido, pagamentoEmConvenioDTO);
        pedido.getItensPedido().forEach(itemPedido -> {
            ItemAutorizacaoConvenioDTO itemAutorizadoConvenio = itensAutorizados.stream()
                    .filter(itemAutorizado -> itemAutorizado.getCodigoItem().equals(itemPedido.getCodigo()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Item do pedido não encontrado na autorização de convênio"));
            if (itemAutorizadoConvenio.getQuantidadeAutorizada().intValue() > 0)
            	itemPedido.setPrecoUnitario(itemAutorizadoConvenio.getPrecoUnitario());
        });
        pedidoService.atualizarPedido(pedido);
    }
    
    public void removerDadosSobreAutorizacaoConvenioEmExcesso(Long numeroPedido) {
    	convenioRepository.removerDadosSobreAutorizacaoConvenioEmExcesso(numeroPedido);
    }	
}
