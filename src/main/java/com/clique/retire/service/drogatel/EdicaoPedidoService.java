package com.clique.retire.service.drogatel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.clique.retire.dto.CancelamentoPedidoDrogatelDTO;
import com.clique.retire.dto.ItemFaltaDTO;
import com.clique.retire.dto.ItemNotaFiscalDTO;
import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.dto.ProdutoKitDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoEdicaoPedido;
import com.clique.retire.enums.TipoPagamentoEnum;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.ModalidadePagamento;
import com.clique.retire.model.drogatel.MotivoDrogatel;
import com.clique.retire.model.drogatel.MovimentoProdutoPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.service.cosmos.UsuarioCosmosService;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.DatabaseHelper;
import com.clique.retire.util.SecurityUtils;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class EdicaoPedidoService {

    private final Gson gson;
    private final PBMService pbmService;
    private final PedidoService pedidoService;
    private final DrogatelService drogatelService;
    private final NotaFiscalService notaFiscalService;
    private final ItemPedidoService itemPedidoService;
    private final ComunicacaoService comunicacaoService;
    private final UsuarioCosmosService usuarioCosmosService;
    private final MotivoDrogatelService motivoDrogatelService;
    private final VencimentoCurtoService vencimentoCurtoService;
    private final ConvenioDrogatelService convenioDrogatelService;
    private final ProcessaValoresPedidoService processaValoresPedidoService;
    private final MovimentoProdutoPedidoService movimentoProdutoPedidoService;
    private final ModalidadePagamentoService modalidadePagamentoService;
    private final DrogatelParametroService drogatelParametroService;
    private final DatabaseHelper databaseHelper;

    public boolean isPedidoAptoParaEdicao(Pedido pedido) {
    	
    	// Permite edição se houver apenas 1 modalidade de pagamento
        List<TipoPagamentoEnum> tiposPagamentoPedido = pedidoService.buscarTiposPagamentoPedido(pedido.getNumeroPedido());
        if (tiposPagamentoPedido.size() == 1) {
        	
        	if (TipoPagamentoEnum.isPagamentosComConvenio(tiposPagamentoPedido)) {
        		String listaConvenioPermitido = drogatelParametroService.obterValorParametro(ParametroEnum.PERMITE_EDICAO_CONVENIO_MARKETPLACE);
        		 
        		if (listaConvenioPermitido.isEmpty())
        			return true;
        		
        		return listaConvenioPermitido.contains(pedido.getCodigoVTEX().substring(0,3));
        	}
        	
        	return TipoPagamentoEnum.isPagamentosNoCredito(tiposPagamentoPedido);
        }
        return false;
    }

    public TipoEdicaoPedido editarOuCancelarPedido(Pedido pedido, List<ItemFaltaDTO> itensFaltantes, boolean contemPBM) {
        
        Integer codUsuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
        convenioDrogatelService.removerDadosSobreAutorizacaoConvenioEmExcesso(pedido.getNumeroPedido());

        List<ItemFaltaDTO> itensFaltantesAposRegrasDeRemocao = aplicarRegrasDeRemocao(itensFaltantes, pedido);

        boolean isTodosProdutosEmFalta = isTodosProdutosEmFalta(pedido, itensFaltantesAposRegrasDeRemocao);
        if (isTodosProdutosEmFalta) {
            log.info("Pedido [{}] apto para cancelamento após aplicar as regras de remoção de itens. Usuário: [{}]",
                    pedido.getNumeroPedido(), codUsuarioLogado);
            cancelarPedido(pedido);
            return TipoEdicaoPedido.PEDIDO_CANCELADO;
        }
        
        if (contemPBM) return null;
        
        ModalidadePagamento modalidadePagamento = modalidadePagamentoService.obterPeloPedido(pedido);
        Integer qtdeParcelas = modalidadePagamento.getQtdeParcelas(); 
        if (Objects.nonNull(qtdeParcelas) && qtdeParcelas.intValue() > 1) 
        	return null;

        log.info("Pedido [{}] apto para remoção de itens. Usuário: [{}]: Itens faltantes: {}", pedido.getNumeroPedido(),
                codUsuarioLogado, gson.toJson(itensFaltantesAposRegrasDeRemocao));

        return databaseHelper.executeInTransaction(() -> {
        	//atualizarDadosVencimentoCurto(itensFaltantesAposRegrasDeRemocao)
            atualizarMovimentoProdutoPedido(itensFaltantesAposRegrasDeRemocao, pedido);
            //refazerPbmCasoNecessario(itensFaltantesAposRegrasDeRemocao, pedido.getNumeroPedido())
            atualizarDadosPedido(itensFaltantesAposRegrasDeRemocao, pedido);
            refazerConvenioCasoNecessario(pedido);

            log.info("Pedido [{}] - Atualizando itens com novas quantidades após ajuste", pedido.getNumeroPedido());
            processaValoresPedidoService.atualizarValoresDoPedido(pedido);

            atualizarDadosDaNotaFiscal(itensFaltantesAposRegrasDeRemocao, pedido);
            
    		if(pedido.getMarketplace().equals(SimNaoEnum.N))
            	comunicacaoService.enviarEmailPedidoComItensRemovidos(pedido.getNumeroPedido(), itensFaltantesAposRegrasDeRemocao);
            return TipoEdicaoPedido.ITENS_REMOVIDOS;
        });
        
    }

    private List<ItemFaltaDTO> aplicarRegrasDeRemocao(List<ItemFaltaDTO> itensFaltantes, Pedido pedido) {
        Map<Integer, ItemPedido> itensPedidoPorCodigo = pedido.getItensPedido().stream()
                .collect(Collectors.toMap(ItemPedido::getCodigo, Function.identity()));

        boolean hasItemControladoFaltante = itensFaltantes.stream().anyMatch(item -> {
            Integer codigoItem = item.getCodigoItem();
            return itensPedidoPorCodigo.get(codigoItem).getProdutoControlado().booleanValue();
        });

        List<Integer> idsItensFaltantes = itensFaltantes.stream()
                .map(ItemFaltaDTO::getCodigoItem).collect(Collectors.toList());
        List<ProdutoKitDTO> kitsDeItensFaltantes = pedido.getItensPedido().stream()
                .filter(item -> Objects.nonNull(item.getKit()) && Objects.nonNull(item.getSequencialKit()))
                .filter(item -> idsItensFaltantes.contains(item.getCodigo()))
                .map(ProdutoKitDTO::fromItemPedido)
                .collect(Collectors.toList());

        Predicate<ItemPedido> isAptoParaRemocaoDeControlados = itemPedido -> hasItemControladoFaltante
                && itemPedido.getProdutoControlado().booleanValue();
        Predicate<ItemPedido> isAptoParaRemocaoDeKitVirtual = itemPedido -> kitsDeItensFaltantes.stream()
                .anyMatch(kit -> kit.isPresentItemPedido(itemPedido));
        Predicate<ItemPedido> isAptoParaRemocaoDeTodasUnidades = isAptoParaRemocaoDeControlados
                .or(isAptoParaRemocaoDeKitVirtual);

        List<ItemFaltaDTO> itensAdicionaisConformeRegrasDeRemocao = pedido.getItensPedido().stream()
                .filter(isAptoParaRemocaoDeTodasUnidades)
                .map(item -> {
                    ItemFaltaDTO itemFaltaDTO = new ItemFaltaDTO();
                    itemFaltaDTO.setCodigoItem(item.getCodigo());
                    itemFaltaDTO.setQuantidadeFalta(item.getQuantidadePedida());
                    return itemFaltaDTO;
                }).collect(Collectors.toList());

        return Stream.concat(itensFaltantes.stream(), itensAdicionaisConformeRegrasDeRemocao.stream()).distinct()
                .map(itemFaltante -> {
                    ItemPedido itemPedido = itensPedidoPorCodigo.get(itemFaltante.getCodigoItem());

                    ItemFaltaDTO itemFalta = itemFaltante.deepClone();
                    itemFalta.setQuantidadePedido(itemPedido.getQuantidadePedida());
                    itemFalta.setCodigoProduto(itemPedido.getProduto().getCodigo());
                    itemFalta.setPbm(Objects.nonNull(itemPedido.getItemPedidoPBM()));
                    itemFalta.setItemPedidoRelacionado(itemPedido);

                    if (isAptoParaRemocaoDeTodasUnidades.test(itemPedido)) {
                        itemFalta.setQuantidadeFalta(itemPedido.getQuantidadePedida());
                    }

                    return itemFalta;
                })
                .collect(Collectors.toList());
    }

    private void atualizarDadosVencimentoCurto(List<ItemFaltaDTO> itensFaltantes) {
        List<Integer> idsItensPedidoParaRemocao = itensFaltantes.stream()
                .filter(this.todasUnidadesFaltantes())
                .map(ItemFaltaDTO::getCodigoItem)
                .collect(Collectors.toList());
        log.info("Removendo vencimento curto dos itens {}", idsItensPedidoParaRemocao);
        vencimentoCurtoService.removerVencimentoCurtoPorItensPedidos(idsItensPedidoParaRemocao);
    }

    private void atualizarMovimentoProdutoPedido(List<ItemFaltaDTO> itensFaltantes, Pedido pedido) {
        MotivoDrogatel motivoDrogatel = motivoDrogatelService
                .getMotivoByDescricaoCache(Constantes.MOTIVO_DROGATEL_PRODUTO_EM_FALTA);
        List<MovimentoProdutoPedido> movimentosProdutosPedido = itensFaltantes.stream()
                .map(item -> {
                    MovimentoProdutoPedido movimento = new MovimentoProdutoPedido(SecurityUtils.getCodigoUsuarioLogado());
                    movimento.setPedido(pedido);
                    movimento.setFasePedido(pedido.getFasePedido());
                    movimento.setQuantidade(-item.getQuantidadeFalta());
                    movimento.setCodigoProduto(item.getCodigoProduto());
                    movimento.setPoloPedido(pedido.getPolo());
                    movimento.setMotivoDrogatel(motivoDrogatel);
                    return movimento;
                }).collect(Collectors.toList());
        log.info("Pedido [{}] - Incluindo MovimentoProdutoPedido.", pedido.getNumeroPedido());
        movimentoProdutoPedidoService.salvarTodos(movimentosProdutosPedido);
    }

    private void atualizarDadosDaNotaFiscal(List<ItemFaltaDTO> itensFaltantes, Pedido pedido) {
        Map<Integer, ItemPedido> itensPedidoPorCodigo = pedido.getItensPedido().stream()
                .collect(Collectors.toMap(ItemPedido::getCodigo, Function.identity()));

        List<ItemNotaFiscalDTO> itensParaCorrigirQuantidade = itensFaltantes.stream()
                .filter(this.todasUnidadesFaltantes().negate())
                .map(item -> {
                    ItemPedido itemPedido = itensPedidoPorCodigo.get(item.getCodigoItem());

                    ItemNotaFiscalDTO itemNotaFiscalDTO = new ItemNotaFiscalDTO();
                    itemNotaFiscalDTO.setQuantidade(item.getQuantidadePedido() - item.getQuantidadeFalta());
                    itemNotaFiscalDTO.setCodigoItemPedido(item.getCodigoItem());
                    itemNotaFiscalDTO.setPreco(itemPedido.getPrecoUnitario());
                    return itemNotaFiscalDTO;
                }).collect(Collectors.toList());
        log.info("Atualizando a quantidade nos seguintes itens de nota fiscal: {}",
                gson.toJson(itensParaCorrigirQuantidade));
        notaFiscalService.atualizarValorNotaEItens(itensParaCorrigirQuantidade, pedido);
    }

    private void refazerPbmCasoNecessario(List<ItemFaltaDTO> itensFaltantes, Long numeroPedido) {
        boolean deveRegerarPbm = itensFaltantes.stream()
                .filter(todasUnidadesFaltantes().negate())
                .anyMatch(ItemFaltaDTO::isPbm);

        if (deveRegerarPbm) {
            log.info("Pedido [{}] - Refazendo autorização PBM após ajuste de itens", numeroPedido);
            pbmService.regerarAutorizacaoPedido(numeroPedido);
        }
    }

    private void refazerConvenioCasoNecessario(Pedido pedido) {
        Long numeroPedido = pedido.getNumeroPedido();
        boolean isPedidoComConvenio = pedidoService.isPedidoComConvenio(numeroPedido);

        if (isPedidoComConvenio) {
            log.info("Pedido [{}] - Regerando autorização de convênio após ajuste de itens", numeroPedido);
            convenioDrogatelService.refazerConvenioPedido(pedido);
        }
    }

    private void atualizarDadosPedido(List<ItemFaltaDTO> itensFaltantes, Pedido pedido) {
        log.info("Pedido [{}] - Atualizando conforme itens faltantes", pedido.getNumeroPedido());
        Map<Integer, ItemPedido> itensPedidoPorCodigo = pedido.getItensPedido().stream()
                .collect(Collectors.toMap(ItemPedido::getCodigo, Function.identity()));

        List<ItemPedido> itensPedidoParaRemocao = new ArrayList<>();

        itensFaltantes.forEach(itemFaltante -> {
            ItemPedido itemPedido = itensPedidoPorCodigo.get(itemFaltante.getCodigoItem());

            boolean deveRemoverItem = todasUnidadesFaltantes().test(itemFaltante);
            if (deveRemoverItem) {
                itensPedidoParaRemocao.add(itemPedido);
            } else {
                itemPedido.setQuantidadePedida(itemFaltante.getQuantidadePedido() - itemFaltante.getQuantidadeFalta());
            }
        });

        List<Integer> idsItensPedidoParaRemocao = itensPedidoParaRemocao.stream()
                .map(ItemPedido::getCodigo).collect(Collectors.toList());
        itemPedidoService.removerItensPedido(idsItensPedidoParaRemocao);
        pedido.getItensPedido().removeAll(itensPedidoParaRemocao);
    }

    private Predicate<ItemFaltaDTO> todasUnidadesFaltantes() {
        return item -> Objects.equals(item.getQuantidadeFalta(), item.getQuantidadePedido());
    }

    private boolean isTodosProdutosEmFalta(Pedido pedido, List<ItemFaltaDTO> itensEmFalta) {
        Stream<ItemPedidoDTO> itensAgrupadosPorProduto = pedido.getItensPedido().stream()
                .collect(Collectors.groupingBy(ItemPedido::getCodigoProduto))
                .values().stream()
                .map(itens -> {
                    ItemPedidoDTO itemPedidoDTO = new ItemPedidoDTO();
                    itemPedidoDTO.setCodigoProduto(itens.get(0).getCodigoProduto().intValue());
                    itemPedidoDTO.setQuantidadePedida(itens.stream().mapToInt(ItemPedido::getQuantidadePedida).sum());
                    return itemPedidoDTO;
                });


        Map<Integer, ItemFaltaDTO> produtosEmFaltaPorCodigo = itensEmFalta.stream()
                .collect(Collectors.groupingBy(ItemFaltaDTO::getCodigoProduto))
                .values().stream()
                .map(itens -> {
                    ItemFaltaDTO itemPedidoDTO = itens.get(0);
                    itemPedidoDTO.setQuantidadeFaltaTotal(itens.stream().mapToInt(ItemFaltaDTO::getQuantidadeFalta).sum());
                    return itemPedidoDTO;
                })
                .collect(Collectors.toMap(ItemFaltaDTO::getCodigoProduto, Function.identity()));

        return itensAgrupadosPorProduto.allMatch(itemPedido -> {
            ItemFaltaDTO produtoFaltante = produtosEmFaltaPorCodigo.get(itemPedido.getCodigoProduto());
            return Objects.nonNull(produtoFaltante)
                    &&  Objects.equals(itemPedido.getQuantidadePedida(), produtoFaltante.getQuantidadeFaltaTotal());
        });
    }

    public void cancelarPedido(Pedido pedido) {
        log.info("Pedido [{}] - Cancelando pedido no drogatel.", pedido.getNumeroPedido());
        MotivoDrogatel motivoCancelamento = motivoDrogatelService.buscarMotivoParaCancelamentoDePedidoNoDrogatel();
        Usuario usuarioResponsavel = usuarioCosmosService.obterPeloId(SecurityUtils.getCodigoUsuarioLogado());
        CancelamentoPedidoDrogatelDTO cancelamentoPedidoDrogatelDTO = CancelamentoPedidoDrogatelDTO.builder()
                .codigoMotivoCancelamento(motivoCancelamento.getId())
                .descricaoMotivoCancelamento("Pedido cancelado devido apontamento de falta.")
                .numeroPedido(pedido.getNumeroPedido().intValue())
                .matriculaResponsavel(Integer.parseInt(usuarioResponsavel.getMatricula()))
                .build();
        drogatelService.cancelarPedido(cancelamentoPedidoDrogatelDTO);
    	if(pedido.getMarketplace().equals(SimNaoEnum.N))
    		comunicacaoService.enviarEmailPedidoCancelado(pedido.getNumeroPedido());
    }

}
