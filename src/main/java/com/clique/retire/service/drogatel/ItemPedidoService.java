package com.clique.retire.service.drogatel;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.repository.drogatel.ItemPedidoRepository;
import com.clique.retire.repository.drogatel.ItemPedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemPedidoService {

    private final ItemPedidoRepository repository;
    private final LoteBipadoService loteBipadoService;
    private final NotaFiscalService notaFiscalService;
    private final PedidoRepositoryCustom pedidoRepositoryCustom;
    private final ItemCupomFiscalService itemCupomFiscalService;
    private final ItemPedidoRepositoryCustom itemPedidoRepositoryCustom;
    private final ReceitaProdutoControladoService receitaProdutoControladoService;
    private final PedidoMercadoriaService pedidoMercadoriaService;

    public List<ItemPedido> obterItensPedido(Long numeroPedido) {
        return itemPedidoRepositoryCustom.obterItensPedidoParaSeparacao(numeroPedido);
    }

    public void atualizarItensPedidoSeparacao(Integer numeroPedido) {
        pedidoRepositoryCustom.atualizaItensPedidoSeparacao(numeroPedido);
    }

    @Transactional
    public void removerItensPedido(List<Integer> idsItensPedido) {
        if (CollectionUtils.isEmpty(idsItensPedido)) {
            return;
        }
        log.info("Removendo ItemPedido com ids {}", idsItensPedido);

        notaFiscalService.removerItensNotaPelosItensPedidos(idsItensPedido);
        loteBipadoService.removerPorItensPedido(idsItensPedido);
        receitaProdutoControladoService.removerPorItensPedido(idsItensPedido);
        itemCupomFiscalService.removerPorItensPedido(idsItensPedido);
        pedidoMercadoriaService.removerReferenciaPorItensPedido(idsItensPedido);
        repository.deleteItemPrePedidoSiacByItensPedido(idsItensPedido);
        repository.deleteItemAutorizacaoConvenioByItensPedido(idsItensPedido);
        repository.deleteAllByCodigoIn(idsItensPedido);
    }
    
    public void atualizarPrecoItemPedidoSIAC(Integer codigoItem, Double novoPreco) {
    	itemPedidoRepositoryCustom.atualizarPrecoItemPedidoSIAC(codigoItem, novoPreco);
    }
    
    public void registrarHistoricoAltPreco(ItemPedidoSIACDTO item) {
    	itemPedidoRepositoryCustom.registrarHistoricoAltPreco(item);
    }
}