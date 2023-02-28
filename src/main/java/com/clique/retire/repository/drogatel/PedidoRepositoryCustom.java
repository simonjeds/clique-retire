package com.clique.retire.repository.drogatel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.clique.retire.dto.CaptacaoLoteDTO;
import com.clique.retire.dto.ItemPendenteNegociarDTO;
import com.clique.retire.dto.LocalizarPedidoFiltroDTO;
import com.clique.retire.dto.ModalidadePagamentoDTO;
import com.clique.retire.dto.PagamentoDinheiroDTO;
import com.clique.retire.dto.PedidoDTO;
import com.clique.retire.dto.PedidoDataMetricasDTO;
import com.clique.retire.dto.PedidoEditadoEmailDTO;
import com.clique.retire.dto.PedidoEntregaDTO;
import com.clique.retire.dto.PedidoFaltaDTO;
import com.clique.retire.dto.PedidoNotaFiscalDTO;
import com.clique.retire.dto.PedidoPendente25DiasDTO;
import com.clique.retire.dto.PedidoRetornoMotociclistaDTO;
import com.clique.retire.dto.ProdutoDTO;
import com.clique.retire.dto.RelatorioPedidoDevolucaoAraujoTemDTO;
import com.clique.retire.dto.TipoPedidoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.TipoPagamentoEnum;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.wrapper.PageWrapper;

/**
 * @author Framework
 *
 */
public interface PedidoRepositoryCustom {


	/**
	 * Busca os produtos por um pedido
	 *
	 * @param idPedido
	 * @param urlBaseImagem
	 * @return List<ProdutoDTO>
	 */
	public List<ProdutoDTO> buscarProdutosPorPedido(Long idPedido, String urlBaseImagem, boolean captacao, String nrReceita);

	/**
	 * Atualiza um pedido utilizando JPA
	 * @return retorna um objeto Pedido
	 */
	public Pedido atualizarPedido(Pedido pedido);

	/**
	 * Método que verifica se o produto tem exigência de receita.
	 *
	 * @param idProduto
	 */
	public boolean isProdutoExigeReceita(Integer idProduto);


	/**
	 * Busca um pedido pelo codigo do pedido
	 * @param codigoPedido
	 * @return retorna um objeto Pedido
	 */
	Pedido buscarPedidoPorCodigoPedido(Long codigoPedido);

	/**
	 * Método que retorna uma lista de pedidos, conforme filtro e filial. <br/>
	 * Também considera paginação pelo parâmetro maxResults.
	 *
	 * @param filtro filtro de busca
	 * @return lista de pedidos
	 */
	PageWrapper<PedidoEntregaDTO> buscarPedidosLojaPorFiltro(LocalizarPedidoFiltroDTO filtro);

	public Integer obterPedidoEmSeparacaoPorUsuario(Integer codigoUsuario, Integer codigoLoja);

	public void alterarFasePedido(Long numeroPedido, FasePedidoEnum novaFase);

	public void atualizaItensPedidoSeparacao(Integer numeroPedido);

	/**
	 * Método que atualiza a quantidade expedida dos itens de um pedido.
	 *
	 * @param numeroPedido
	 * @param dataAlteracao
	 */
	public void atualizarQuantExpedidaDosItensDoPedido(Integer numeroPedido, Date dataAlteracao);

	/**
	 * Atualiza o tipo de retirada efetiva do pedido, definindo-o como o próprio tipo de retirada selecionado pelo cliente.
	 * <br /> <br />
	 * TRLSEQEFETIVA = TRLSEQ
	 *
	 * @param numeroPedido
	 */
	public void atualizarTipoRetiradaEfetiva(Integer numeroPedido);

	/**
	 * Método para verificar se a loja possui pedido atendido.
	 *
	 * @param codigoFilial
	 * @return true se houver pedidos na fase atendido e false caso não haja.
	 */
	public boolean isExistePedidosAgSeparacaoPorFilial(Integer codigoFilial) ;

	/**
	 * Retorna um pedido para apontamento de falta de mercadoria.
	 *
	 * @param numeroPedido
	 * @return
	 */
	public Pedido buscarPedidoParaRegistrarFalta(Long numeroPedido);

	/**
	 * Retorna um pedido para inicialização do registro de falta.
	 * @param numeroPedido código do pedido
	 * @return PedidoFaltaDTO
	 */
	PedidoFaltaDTO buscarPedidoParaInicioFalta(Integer numeroPedido);
	
	/**
	 * Retorna um pedido para emissão de nota fiscal
	 * @param numeroPedido
	 * @return PedidoDTO
	 */
	public PedidoDTO buscarPedidoParaEmitirNotaFiscalSap(Integer numeroPedido);

	/**
	 * Retorna true ou false para verificação do tipo de pagamento
	 * @param numeroPedido
	 * @return Boolean
	 */
	public Boolean isPagamentoSAP(Integer numeroPedido, Integer numeroTipoPagamento);


	/**
	 * Retorna os eans de umproduto
	 * @return PedidoDTO
	 */
	public Map<Integer, String> buscarCodigoBarraPorProduto(List<Integer> listProduto);

	/**
	 * Retorna um pedido para entrega.
	 *
	 * @param filtro
	 * @return PedidoEntregaDTO
	 */
	List<PedidoEntregaDTO> buscarPedidoParaEntrega(String filtro, String numeroPedidoDrogatel, boolean isParceiro);

	/**
	 * Busca todos os pedidos que estão na fase
	 * @return
	 */
	public List<PedidoDataMetricasDTO> buscarPedidosParaCalculoMetricas();

	/**
	 * Método que atualiza o pedido colocando o último número de registro de pedido.
	 *
	 * @param numeroPedido
	 */
	public void atualizarUltimoCodigoRegistro(Integer codigoUltimoRegistro, Integer numeroPedido);

	/**
	 * Método que verifica se o pedido possui itens aguardando mercadoria.
	 *
	 * @param numeroPedido
	 */
	public boolean isPedidoComItensAgMercadoria(Long numeroPedido);

	/**
	 * Método que verifica se pedido com box possui o codigo de abertura preenchido
	 *
	 * @param numeroPedido
	 */
	public boolean isPedidoBoxSemCodigoAberturaPreenchido(Integer numeroPedido);

	/**
	 * Método para obter o numero do pedido ecommerce cliente.
	 *
	 * @param numeroPedido
	 * @return numero do pedido
	 */
	public String obterPedidoEcommerceCliente(Integer numeroPedido);

	/**
	 * Busca um número de pedido de forma aleatória para testes nas POC do coletor.
	 * @return Integer <b>Numero do Pedido</b>
	 */
	public Integer buscarNumeroPedidoRandomico();


	/**
	 * Consulta um Pedido com Historico de Pendencias de Notas Fiscais. <br/>
	 *
	 * @param filtro parêmtro de busca
	 * @param filial filial logada
	 * @param codStatus 1 - Pendentes, 2 - Emitidas, 3 - Todas
	 * @return lista de pedidos e respectivos status de NF
	 */
	List<PedidoNotaFiscalDTO> buscarPedidosNotasFiscaisPorFiltro(String filtro, Integer codStatus, Integer filial);

	/**
	 * Busca o tipo de entrega do pedido
	 * @return Tipo Entrega
	 */
	public Object[] buscarTipoEntrega(Integer codigoPedido);

	/**
	 * Buscar ExpedicaoPedido por numero pedido
	 * @param codPedido
	 * @return ExpedicaoPedido
	 */
	public ExpedicaoPedido buscarExpedicaoPedidoPorNumeroPedido(Long codPedido);


	/**
	 * obter itens do pedido que estão pendentes para negociaçãor
	 * @return lista com os itens
	 */
	public List<ItemPendenteNegociarDTO> obterItensPendentesANegociar(Long numeroPedido);

	/**
	 * Busca os tipos de pedido
	 * @return List TipoPedidoDTO
	 */
	public List<TipoPedidoDTO> buscarTiposPedido();

	/**
	 * Método obtém todos os pedidos pendente (Emissão NF).
	 *
	 * @param idFilial código da filial que está fazendo a requisição
	 */
	List<PedidoDTO> obterPedidosPendente(Integer idFilial);
	
	/**
	 * Método obtém pedidos pendente com mais de 25 dias da Emissão da NF.
	 *
	 * @param idFilial código da filial que está fazendo a requisição
	 * @param pagina 
	 */
	PageWrapper<PedidoPendente25DiasDTO> obterPedidosPendente25Dias(Integer idFilial, int pagina);

	/**
	 * Método para gravar a hora que o pedido foi para negociacao.
	 * @author Marcus Vinícius
	 * @param  statusPedido, numeroPedido
	 */
	public void updateStatusIntegrationPedido(String coluna, String statusPedido, Integer numeroPedido);
	/**
	 * Busca todos os lotes bipados para um medicamento utilizando o numero do pedido como filtro
	 * @param numeroPedido
	 * @return
	 */
	public List<CaptacaoLoteDTO> buscaLoteBipadoPorNumeroPedido(Long numeroPedido);

	/**
	 * Método que verifica se o pedido possui itens com exigência de receita.
	 *
	 * @param idPedido
	 */
	public boolean isPedidoExigeReceita(Long idPedido);

	/**
	 * Ajusta a quantidade separada para ficar igual a quantidade pedida
	 * @param numeroPedido numero do pedido
	 */
	void ajustarQuantidadeSeparadaItemPedido(Integer numeroPedido);

	/**
	 * Busca o pedido para gravação de fluxo de retorno do motociclista d 4.0
	 * @param filtro numero do pedido ou a chave da nota fiscal
	 * @return PedidoRetornoMotociclistaDTO dados do pedido
	 */
	PedidoRetornoMotociclistaDTO consultarPedidoRetornoMotociclista(String filtro);

	/**
	 * Método que obtem as informações para montar o relatório de impressão do pedido
     * de devolução Araujo Tem.
	 * @param numeroPedido numero do pedido
	 * @return dados para relatório
	 */
	RelatorioPedidoDevolucaoAraujoTemDTO obterPedidoFaltaAraujoTemDevolucao(Long numeroPedido);

	/**
	 * Obtem a quantiade de pedidos pendentes para a filial
	 * @param filial código da filial
	 * @return quantidade de pedidos
	 */
	int obterQuantidadePedidosPendente(Integer filial);

	/**
	 * Obtem o pedido para cancelamento de pedido controlado
	 */
	String obterPedidoControladoParaCancelamento(Integer numeroPedido);

	/**
	 * Busca os tipos de pagamento do pedido
	 * @param numeroPedido numero do pedido
	 * @return enums com os tipos do pagamento
	 */
	List<TipoPagamentoEnum> buscarTiposPagamentoPedido(Long numeroPedido);

	/**
	 * Retorna a modalidade de pagamento para cartão do pedido
	 * @param numeroPedido numero do pedido
	 * @return dto com os dados da modalidade de pagamento
	 */
	Optional<ModalidadePagamentoDTO> buscarModalidadePagamentoPedido(Long numeroPedido);

	/**
	 * Atualiza os valores da modalidade de pagamento do pedido
	 * @param modalidadePagamento dto com os novos dados da modalidade de pagamento
	 */
	void atualizaValoresModalidadePagamento(ModalidadePagamentoDTO modalidadePagamento);

	/**
	 * Verifica se o pedido possui algum convênio vinculado
	 * @param numeroPedido numero do pedido
	 * @return booleano indicando a existência do convênio
	 */
    boolean isPedidoComConvenio(Long numeroPedido);

	/**
	 * Busca os dados do pedido para enviar o email de edição do pedido ou cancelamento
	 * @param numeroPedido numero do pedido
	 * @return DTO com dados necessários para envio de email
	 */
	PedidoEditadoEmailDTO buscarDadosPedidoParaEnvioEmail(Long numeroPedido);
	
	/**
	 * Busca a quantidade de vezes que o pedido foi apontado falta em lojas diferentes
	 * @param numeroPedido numero do pedido
	 * @return quantidade de vezes
	 */
	int buscarQuantidadeDeApontamentoDeFaltaEmLojaDiferente(Long numeroPedido);
	
	/**
	 * Busca os dados do pre pedido
	 * @param numeroPedido numero do pedido
	 * @return PrePedidoSiac
	 */
	List<PrePedidoSiac> buscarPrePedidoSiac(Integer numeroPedido);
	
	/**
	 * Atualiza a fase do pre pedido para EM SEPARACAO no siac
	 * @param codigoPrePedidoOrigem codigo da pedido de origem do siac
	 */
	public void atualizarFasePrePedido(String codigoOrigemPrePedido);

	/**
	 * Verifica se o pagamento do pedido é em dinheiro
	 * @param numeroPedido numero do pedido
	 */
	public boolean isPagamentoEmDinheiro4ponto0Drogatel(Long numeroPedido);
	
	public PedidoDTO obtemPedidoParaNormalizarPreco(Integer numeroPedido);
	
	public PagamentoDinheiroDTO obterModalidadePagamentoDinheiro(Integer numeroPedido);
	
	public void atualizaPrecoPedidoSIAC(PagamentoDinheiroDTO dinheiroDTO, PedidoDTO pedidoDTO);

	/**
	 * Verifica se o produto é geladeira
	 * @param numeroPedido numero do pedido
	 */
	public boolean isProdutoGeladeira(Long numeroPedido);

	public String obterEmailClientePedido(Long numeroPedido);
}