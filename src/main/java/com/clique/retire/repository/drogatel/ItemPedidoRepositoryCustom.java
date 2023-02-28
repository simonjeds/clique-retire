package com.clique.retire.repository.drogatel;

import java.util.List;

import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.dto.ProdutoDevolucaoAraujoTemDTO;
import com.clique.retire.dto.RelatorioPedidoSeparacaoDTO;
import com.clique.retire.dto.RelatorioProdutoSeparacaoDTO;
import com.clique.retire.dto.RelatorioTermoCompromissoDTO;
import com.clique.retire.model.drogatel.ItemPedido;

public interface ItemPedidoRepositoryCustom {

	/**
	 * Recupera os itens do pedido para impressão ao iniciar separação
	 * @param numeroPedido
	 * @return List<RelatorioProdutoSeparacaoDTO>
	 */
	public List<RelatorioProdutoSeparacaoDTO> obterItensPedidoParaImpressao(Long numeroPedido, RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO);

	/**
	 * Recupera um pedido para impressão ao iniciar separação
	 * @param numeroPedido
	 * @return RelatorioPedidoSeparacaoDTO
	 */
	public RelatorioPedidoSeparacaoDTO obterPedidoParaImpressao(Long numeroPedido);

	/**
	 * Recupera atributos para gerar o termo de entrega
	 * @param numeroPedido
	 * @return RelatorioTermoCompromissoDTO
	 */
	public RelatorioTermoCompromissoDTO obterRelatorioTermoEntrega(Long numeroPedido);

	/**
	 * Obtem o numero de autorizacao de PBM
	 * @param primary key da tabela DRGTBLIPPITEMPEDIDOPBM
	 */
	public String obterNumeroAutorizacaoPBM(Integer codigo);

	/**
	 * Método que consulta os itens do pedido durante a separação
	 *
	 * @param numeroPedido
	 * @return retorna list com os itens do pedido.
	 */
	public List<ItemPedido> obterItensPedidoParaSeparacao(Long numeroPedido);

	/**
	 * Atualizar a quantidade dos itens do pedido.
	 * @param itemPedido
	 */
	public void atualizarItemPedido(ItemPedido itemPedido);

	/**
	 * Método para ajustar a situação do pedido para a realização de uma devolução total.
	 * @param numeroPedido
	 */
	public void ajustarPedidoParaDevolucaoTotal(Long numeroPedido);

	/**
	 * Obtem os itens do pedido para devolução
	 * @param numeroPedido numero do pedido
	 * @return lista de itens do pedido para devolução araujo tem
	 */
	List<ProdutoDevolucaoAraujoTemDTO> obterItensPedidoParaDevolucaoAraujoTem(Long numeroPedido);

	/**
	 * Obtem os itens que sofreram edição durante separação
	 * @param numeroPedido numero do pedido
	 * @return lista de itens do pedido que sofreram edição
	 */
    public List<String> obterItensEditadosComandaSeparacao(Long numeroPedido);
    
    /**
	 * Método que atualiza o preço de um item_pedido que está com preço maior que o da loja.
	 * @param codigoItem, novoPreco
	 * @return 
	 */
	public void atualizarPrecoItemPedidoSIAC(Integer codigoItem, Double novoPreco);
	
	public void registrarHistoricoAltPreco(ItemPedidoSIACDTO item);

}