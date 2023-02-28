package com.clique.retire.service.drogatel;

import com.clique.retire.dto.ModalidadePagamentoDTO;
import com.clique.retire.infra.exception.ModalidadePagamentoInaptaException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessaValoresPedidoService {

    private final PedidoService pedidoService;

    public double calculaValorTotalItensPedido(List<ItemPedido> itens) {
        double valor = itens.stream().mapToDouble(item -> item.getQuantidadePedida() * item.getPrecoUnitario()).sum();
        return NumberUtil.round(valor, 2);
    }

    @Transactional
    public void atualizarValoresDoPedido(Pedido pedido) {
        double valorTotalItensPedido = calculaValorTotalItensPedido(pedido.getItensPedido());

        pedido.setValorTotalItens(valorTotalItensPedido);
        pedido.setUltimaAlteracao(new Date());

        ModalidadePagamentoDTO modalidadePagamento = obterNovosValoresModalidadePagamento(pedido);
        pedidoService.atualizaValoresModalidadePagamento(modalidadePagamento);

        pedido.setValorTotal(modalidadePagamento.getValorPago());
        pedidoService.atualizarPedido(pedido);
    }

    private ModalidadePagamentoDTO obterNovosValoresModalidadePagamento(Pedido pedido) {
        ModalidadePagamentoDTO modalidadePedido = pedidoService
                .buscarModalidadePagamentoCartaoPedido(pedido.getNumeroPedido())
                .orElseThrow(() -> new ModalidadePagamentoInaptaException("O pedido não possui pagamento no cartão."));

        Double taxaEntrega = pedido.getValorTaxaEntrega();       
        modalidadePedido.setValorPago(pedido.getValorTotalItens() + (Objects.isNull(taxaEntrega) ? 0 : taxaEntrega.doubleValue()));
        return modalidadePedido;
    }

}
