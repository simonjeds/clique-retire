package com.clique.retire.service.drogatel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.clique.retire.client.rest.ComunicacaoClient;
import com.clique.retire.dto.ItemEmailDTO;
import com.clique.retire.dto.ItemFaltaDTO;
import com.clique.retire.dto.PedidoEditadoEmailDTO;
import com.clique.retire.enums.TipoEdicaoPedido;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Produto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComunicacaoService {

    private final ComunicacaoClient client;
    private final PedidoService pedidoService;

    public void enviarEmailPedidoComItensRemovidos(Long numeroPedido, List<ItemFaltaDTO> itensRemovidos) {
        enviarEmailComunicacaoPedidoEditado(numeroPedido, TipoEdicaoPedido.ITENS_REMOVIDOS, itensRemovidos);
    }

    public void enviarEmailPedidoCancelado(Long numeroPedido) {
        enviarEmailComunicacaoPedidoEditado(numeroPedido, TipoEdicaoPedido.PEDIDO_CANCELADO, null);
    }

    private void enviarEmailComunicacaoPedidoEditado(Long numeroPedido, TipoEdicaoPedido tipoEdicao,
            List<ItemFaltaDTO> itensEditados) {
        Pedido pedido = pedidoService.findById(numeroPedido);
        
		if (Objects.nonNull(pedido.getCodigoVTEX()) && isStartRppOrIfd(pedido))
			return;

        PedidoEditadoEmailDTO pedidoEmailDTO = pedidoService.buscarDadosPedidoParaEnvioEmail(numeroPedido);
        pedidoEmailDTO.setEmailCliente(pedidoEmailDTO.getEmailCliente());
        pedidoEmailDTO.setTipoNotificacao(tipoEdicao);
        List<ItemEmailDTO> itensPedido = pedido.getItensPedido().stream()
                .map(itemPedido -> ItemEmailDTO.builder()
                        .codigoProduto(itemPedido.getProduto().getCodigo().longValue())
                        .descricao(itemPedido.getProduto().getDescricao())
                        .preco(itemPedido.getPrecoUnitario())
                        .quantidade(itemPedido.getQuantidadePedida())
                        .build()
                ).collect(Collectors.toList());
        pedidoEmailDTO.setItensPedido(itensPedido);

        if (TipoEdicaoPedido.ITENS_REMOVIDOS == tipoEdicao) {
            if (CollectionUtils.isEmpty(itensEditados)) {
                throw new BusinessException("Não foram identificados itens editados no pedido para envio do email.");
            }

            List<ItemEmailDTO> itensEditadosEmailDTO = itensEditados.stream()
                    .map(itemFalta -> {
                        ItemPedido itemPedido = itemFalta.getItemPedidoRelacionado();
                        Produto produto = itemPedido.getProduto();

                        return ItemEmailDTO.builder()
                                .codigoProduto(produto.getCodigo().longValue())
                                .descricao(produto.getDescricao())
                                .preco(itemPedido.getPrecoUnitario())
                                .quantidade(itemFalta.getQuantidadeFalta())
                                .build();
                    })
                    .collect(Collectors.toList());
            pedidoEmailDTO.setItensRemovidos(itensEditadosEmailDTO);
        }

        client.enviarEmailPedidoEditadoCliqueRetire(pedidoEmailDTO);
    }

	private boolean isStartRppOrIfd(Pedido pedido) {
		return pedido.getCodigoVTEX().startsWith("RPP") || pedido.getCodigoVTEX().startsWith("IFD");
	}

}
