package com.clique.retire.service.drogatel;

import com.clique.retire.dto.*;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.RelatorioEnum;
import com.clique.retire.enums.SexoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.ImpressaoPedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.*;
import com.clique.retire.service.cosmos.ImagemService;
import com.clique.retire.service.cosmos.UsuarioCosmosService;
import com.clique.retire.util.*;
import com.google.zxing.WriterException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImpressaoPedidoService {

	private static final String TIPO_ENTREGA_VIA_MOTO = "E";
	private static final String NOTA_FISCAL_NAO_ENCONTRADA = "Nota fiscal não encontrada.";
	private static final String A_NOTA_FISCAL_NAO_FOI_RECEBIDA_AINDA = "A Nota Fiscal não foi recebida ainda.";

	private static final String DESCRICAO = "descricao";
	private static final String SECAO = "secao";

	private static final int HEIGHT = 90;
	private static final int WIDTH = 120;

	private static final String LOGO_ARAUJO = "LOGO_ARAUJO";

	private static final String QRCODE = "QRCODE";
	private static final String EMOJI_FELIZ = "EMOJI_FELIZ";
	private static final String EMOJI_TRISTE = "EMOJI_TRISTE";
	private static final String PALMAS = "PALMAS";
	private static final String PALMAS_DOIS = "PALMAS_DOIS";
	private static final String VIA_MOTO = "VIA_MOTO";
	private static final String CONTROLADO = "CONTROLADO";
	private static final String IS_IMPRIMIR_TUTORIAL = "IS_IMPRIMIR_TUTORIAL";
	private static final String EXCLAMACAO_ESQUERDA = "EXCLAMACAO_ESQUERDA";
	private static final String EXCLAMACAO_DIREITA = "EXCLAMACAO_DIREITA";
	private static final String CORTE_AQUI = "CORTE_AQUI";
	private static final String EXCLAMACAO_CINZA_ESQUERDA = "EXCLAMACAO_CINZA_ESQUERDA";
	private static final String EXCLAMACAO_CINZA_DIREITA = "EXCLAMACAO_CINZA_DIREITA";
	private static final String EXCLAMACAO_ESQUERDA_CINZA = "EXCLAMACAO_ESQUERDA_CINZA";
	private static final String EXCLAMACAO_DIREITA_CINZA = "EXCLAMACAO_DIREITA_CINZA";

	private static final String MSG_NOVA_COMANDA = "MSG_NOVA_COMANDA";
	private static final String HAS_CANHOTO = "HAS_CANHOTO";
	private static final String CONTAINS_ITEM_NORMAL = "CONTAINS_ITEM_NORMAL"; //contador_outro
	private static final String CONTAINS_ITEM_ESPECIAL = "CONTAINS_ITEM_ESPECIAL"; // contador_geral

	private static final String ISVACINA = "ISVACINA";
	private static final String ISCONTROLADO = "ISCONTROLADO";
	private static final String ISREFRIGERADO = "ISREFRIGERADO";
	private static final String ISANTIBIOTICO = "ISANTIBIOTICO";
	private static final String TESOURA = "TESOURA";
	private static final String SENHA_FRACIONADO = "SENHA_FRACIONADO";
	private static final String NUMVACINA = "NUMVACINA";
	private static final String NUMCONTROLADO = "NUMCONTROLADO";
	private static final String NUMREFRIGERADO = "NUMREFRIGERADO";
	private static final String NUMANTIBIOTICO = "NUMANTIBIOTICO";
	private static final String TOTALPAGE = "TOTALPAGE";
	private static final String ISRAPPIORIFOOD = "ISRAPPIORIFOOD";
	private static final String CAMINHO_RELATORIO = "caminhoRelatorio";

	private final Environment env;
	private final QrCodeUtil qrCodeUtil;
	private final RelatorioUtil relatorioUtil;
	private final ImagemService imagemService;
	private final PedidoService pedidoService;
	private final ParametroService parametroService;
	private final MarketingPlaceService marketingPlaceService;
	private final UsuarioCosmosService usuarioService;
	private final FilialRepositoryCustom filialRepositoryCustom;
	private final PedidoRepositoryCustom pedidoRepositoryCustom;
	private final ItemPedidoRepositoryCustom itemPedidoRepository;
	private final ImpressaoPedidoRepository impressaoPedidoRepository;
	private final ControleIntranetRepositoryCustom controleIntranetRepository;
	private final HistoricoAtendimentoNFRepositoryCustom historicoAtendimentoNFRepository;
	private final PedidoFracionadoService pedidoFracionadoService;
	private final IMGService imgService;
	private final ReceitaProdutoControladoRepositoryCustom receitaProdutoControladoRepositoryCustom;
	private final DrogatelService drogatelService;

	/**
	 * Gera um relatório de pedido ao iniciar uma separação.
	 *
	 * @param chaveNota chave da nota fiscal
	 * @param ipCliente ip do cliente logado
	 * @return byte[] - relatório de pedido em pdf
	 */
	@Transactional("drogatelTransactionManager")
	public byte[] imprimirHistoricoNF(String chaveNota, String ipCliente) throws Exception {
		NotaFiscalDTO notaFiscal = historicoAtendimentoNFRepository.buscarNotaFiscal(chaveNota);

		if (notaFiscal == null) {
			throw new BusinessException(NOTA_FISCAL_NAO_ENCONTRADA);
		} else if (!notaFiscal.isNotaFiscalJaRecebida()) {
			throw new BusinessException(A_NOTA_FISCAL_NAO_FOI_RECEBIDA_AINDA);
		}

		List<RelatorioHistoricoNFDTO> listRelatorio = historicoAtendimentoNFRepository
			.buscarHistoricoPorNotaParaImpressao(notaFiscal);

		if (listRelatorio == null || listRelatorio.isEmpty()) {
			throw new BusinessException(A_NOTA_FISCAL_NAO_FOI_RECEBIDA_AINDA);
		}

		// Monta a url de imagem para os produtos.
		String urlBase = parametroService.buscarPorChave(ParametroEnum.URL_SERVICO_IMAGENS.getDescricao()).getValor();
		listRelatorio.forEach(dto ->
				dto.getItens()
				   .forEach(item -> item.setUrlImagem(imagemService.montarURLImagens(item.getCodigo(), urlBase))
			)
		);

		Map<String, Object> parametros = new HashMap<>();
		parametros.put(LOGO_ARAUJO, relatorioUtil.getLogoAraujo());
		byte[] pdfFile = relatorioUtil.gerarRelatorio(
			RelatorioEnum.HISTORICO_NF.getJrxmlPath(), null, listRelatorio, parametros
		);

		Integer codigoFilial = controleIntranetRepository.findFilialByIp(ipCliente);
		enviarParaImpressora(pdfFile, codigoFilial);

		return pdfFile;
	}

	/**
	 * Gera um relatório de pedido ao iniciar uma separação.
	 *
	 * @param numeroPedido
	 * @return byte[] - relatório de pedido em pdf
	 */
	@Transactional(value = "drogatelTransactionManager", propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public ImpressaoDTO imprimirPedido(Long numeroPedido, Integer codigoUsuario, boolean enviarParaImpressora) throws Exception {
		pedidoService.obterNumeroAberturabox(numeroPedido, null);

		RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO = itemPedidoRepository.obterPedidoParaImpressao(numeroPedido);
		List<RelatorioProdutoSeparacaoDTO> listRelatorioProdutoSeparacaoDTO = itemPedidoRepository
			.obterItensPedidoParaImpressao(numeroPedido, relatorioPedidoSeparacaoDTO);
		List<byte[]> receitas = imgService.obterListaReceitaDigital(numeroPedido);
		relatorioPedidoSeparacaoDTO.setReceitaDigital(!receitas.isEmpty());
		relatorioPedidoSeparacaoDTO.setPacientes(pacientesQuatroPontoZeroControlado(numeroPedido));

		listRelatorioProdutoSeparacaoDTO = agruparItens(listRelatorioProdutoSeparacaoDTO);
		formatarMensagemControlado(relatorioPedidoSeparacaoDTO);

		List<String> itensModificados = pedidoService.obterItensEditadosComandaSeparacao(numeroPedido);
		definirItensModificadosDoPedido(listRelatorioProdutoSeparacaoDTO, itensModificados);

		relatorioPedidoSeparacaoDTO.setPedidoPossuiVencCurto(
			listRelatorioProdutoSeparacaoDTO.stream().anyMatch(item -> item.getDataValidadePedidoVencCurto() != null)
		);
		Object[] result = pedidoRepositoryCustom.buscarTipoEntrega(Integer.parseInt(relatorioPedidoSeparacaoDTO.getNumPedido()));
		String tipoEntrega = result.length > 0 ? result[0].toString() : StringUtils.EMPTY;
		boolean isEntregaViaMoto = tipoEntrega.equalsIgnoreCase(TIPO_ENTREGA_VIA_MOTO);

        Map<String, Object> parametros = parametrosImpressao(relatorioPedidoSeparacaoDTO, codigoUsuario, isEntregaViaMoto);

		String caminhoRelatorio = caminhoRelatorio(relatorioPedidoSeparacaoDTO, itensModificados, parametros, listRelatorioProdutoSeparacaoDTO);

		relatorioPedidoSeparacaoDTO.setListProduto(listRelatorioProdutoSeparacaoDTO);

		if (relatorioPedidoSeparacaoDTO.isSuperVendedor()) {
			relatorioPedidoSeparacaoDTO.setCanalVenda(TipoPedidoEnum.ARAUJOTEM.getValor().concat(" - SV"));
		}

		boolean containsEspecial = containsEspecial(listRelatorioProdutoSeparacaoDTO);

		String descricaoMarketplace = marketingPlaceService.obterDescricao(relatorioPedidoSeparacaoDTO.getIdPedidoVtex());
		boolean isDeliveryMarketplace = StringUtils.isNotEmpty(descricaoMarketplace);

		relatorioPedidoSeparacaoDTO.setPagamentoEmDinheiro(pedidoService.isPagamentoEmDinheiro(numeroPedido));
		relatorioPedidoSeparacaoDTO.setTroco(formatarDinheiro(relatorioPedidoSeparacaoDTO.getTroco()));
		relatorioPedidoSeparacaoDTO.setValorTotalPedido(formatarDinheiro(relatorioPedidoSeparacaoDTO.getValorTotalPedido()));

		if(relatorioPedidoSeparacaoDTO.isPagamentoEmDinheiro()){
			parametros.put(EXCLAMACAO_CINZA_ESQUERDA, relatorioUtil.getExclamacaoCinza());
			parametros.put(EXCLAMACAO_CINZA_DIREITA, relatorioUtil.getExclamacaoCinza());
		}

		Map<String, Object> parametrosGerarPdf = new HashMap<>();
		parametrosGerarPdf.put("containsEspecial", containsEspecial);
		parametrosGerarPdf.put("relatorioPedidoSeparacaoDTO", relatorioPedidoSeparacaoDTO);
		parametrosGerarPdf.put("descricaoMarketplace", descricaoMarketplace);
		parametrosGerarPdf.put("isDeliveryMarketplace", isDeliveryMarketplace);
		parametrosGerarPdf.put("itensModificados", itensModificados);
		parametrosGerarPdf.put("parametros", parametros);
		parametrosGerarPdf.put(CAMINHO_RELATORIO, caminhoRelatorio);
		parametrosGerarPdf.put("isEntregaViaMoto", isEntregaViaMoto);
		Map<String, Object> pdf = gerarPdfImpressao(parametrosGerarPdf);
		byte[] pdfFile = (byte[])pdf.get("pdfFile");
		caminhoRelatorio = (String)pdf.get(CAMINHO_RELATORIO);

		ImpressaoDTO impressaoDTO = new ImpressaoDTO();
		impressaoDTO.setDocumento(Base64.getEncoder().encodeToString(pdfFile));

		if (!receitas.isEmpty()) {
			List<String> receitasBase64 = new ArrayList<>();
			receitas.forEach(receita -> receitasBase64.add(Base64.getEncoder().encodeToString(receita)));

			impressaoDTO.setReceitaDigital(receitasBase64);
		}

		if (enviarParaImpressora) {
			log.info("Enviando pedido '{}' para impressora. Relatório: {}", numeroPedido, caminhoRelatorio);
			try {
				enviarParaImpressora(pdfFile, relatorioPedidoSeparacaoDTO.getCodFilial());

				if (!receitas.isEmpty())
					receitas.forEach(receita -> enviarParaImpressora(receita, relatorioPedidoSeparacaoDTO.getCodFilial()));

				new Thread(() -> salvarImpressaoPedido(new ImpressaoPedido(numeroPedido.intValue(), codigoUsuario, new Date()))).start();
			} catch (Exception exception) {
				log.error(exception.getMessage(), exception);
				impressaoDTO.setErroImpressao(true);
			}
		}

		return impressaoDTO;
	}

    private Map<String, Object> parametrosImpressao(RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO, Integer codigoUsuario, boolean isEntregaViaMoto) throws WriterException, IOException {
        InputStream qrCode = qrCodeUtil.generateQRCodeImage(relatorioPedidoSeparacaoDTO.getNumPedido(), WIDTH, HEIGHT);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(QRCODE, qrCode);
        parametros.put(EMOJI_FELIZ, relatorioUtil.getEmojiFeliz());
        parametros.put(EMOJI_TRISTE, relatorioUtil.getEmojiTriste());
        parametros.put(PALMAS, relatorioUtil.getPalmas());
        parametros.put(PALMAS_DOIS, relatorioUtil.getPalmas());
        parametros.put(CONTROLADO, relatorioPedidoSeparacaoDTO.isMedicamentoControlado());
        parametros.put(VIA_MOTO, isEntregaViaMoto);

        if(relatorioPedidoSeparacaoDTO.isPedidoQuatroPontoZero()){
            parametros.put(CORTE_AQUI, relatorioUtil.getCorteAqui());
        }

        if (
            !relatorioPedidoSeparacaoDTO.isSuperVendedor() &&
            relatorioPedidoSeparacaoDTO.isPedidoAraujoTem() &&
            StringUtils.isNotBlank(relatorioPedidoSeparacaoDTO.getTipoRetirada())
        ) {
            parametros.put(IS_IMPRIMIR_TUTORIAL, impressaoPedidoRepository.isImprimirTutorialAraujoTem(codigoUsuario));
        } else {
            parametros.put(IS_IMPRIMIR_TUTORIAL, impressaoPedidoRepository.isImprimirTutorial(codigoUsuario));
        }

        return parametros;
    }

    private String caminhoRelatorio( RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO,
        List<String> itensModificados, Map<String, Object> parametros, List<RelatorioProdutoSeparacaoDTO> listRelatorioProdutoSeparacaoDTO) {

    	String senhaPedidoFracionado = pedidoFracionadoService.obterSenhaPeloNumeroPedido(Integer.parseInt(relatorioPedidoSeparacaoDTO.getNumPedido()));
        if (StringUtils.isNotBlank(senhaPedidoFracionado)) {
        	parametros.put(TESOURA, relatorioUtil.getTesoura());
        	parametros.put(SENHA_FRACIONADO, senhaPedidoFracionado);
        	parametros.put(EXCLAMACAO_ESQUERDA_CINZA, relatorioUtil.getExclamacaoCinza());
			parametros.put(EXCLAMACAO_DIREITA_CINZA, relatorioUtil.getExclamacaoCinza());

			return RelatorioEnum.PEDIDO_FRACIONADO.getJrxmlPath();
        }

		String caminhoRelatorio = "";
        if(itensModificados.isEmpty()){
            caminhoRelatorio = relatorioPedidoSeparacaoDTO.isMedicamentoControlado()
            ? RelatorioEnum.PEDIDO_SEPARACAO_CONTROLADO.getJrxmlPath()
            : RelatorioEnum.PEDIDO_SEPARACAO.getJrxmlPath();
        } else {

            parametros.put(EXCLAMACAO_ESQUERDA, relatorioUtil.getExclamacao());
            parametros.put(EXCLAMACAO_DIREITA, relatorioUtil.getExclamacao());

            caminhoRelatorio = relatorioPedidoSeparacaoDTO.isMedicamentoControlado()
                    ? RelatorioEnum.PEDIDO_SEPARACAO_CONTROLADO_EDITADO.getJrxmlPath()
                    : RelatorioEnum.PEDIDO_SEPARACAO_EDITADO.getJrxmlPath();
        }

        String caminhoRelatorioAraujoTem = caminhoRelatorioAraujoTem(relatorioPedidoSeparacaoDTO,listRelatorioProdutoSeparacaoDTO);
        if( StringUtils.isNotBlank(caminhoRelatorioAraujoTem)){
            caminhoRelatorio = caminhoRelatorioAraujoTem;
        }
        return caminhoRelatorio;
    }

    private String caminhoRelatorioAraujoTem(
        RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO,
        List<RelatorioProdutoSeparacaoDTO> listRelatorioProdutoSeparacaoDTO){

        String caminhoRelatorio = null;
        if (
            !relatorioPedidoSeparacaoDTO.isSuperVendedor() &&
            relatorioPedidoSeparacaoDTO.isPedidoAraujoTem() &&
            relatorioPedidoSeparacaoDTO.getFilialOrigemAraujoTem().equals(relatorioPedidoSeparacaoDTO.getFilial())
        ) {
            DrogatelParametro parametro = parametroService
                .buscarPorChave(ParametroEnum.URL_BASE_BUSCAR_PEDIDO_PRODUTO_UZ.getDescricao());

            PedidoUzDTO pedidoUz = FeignUtil.getPedidoDiretoClient(parametro.getValor())
                .buscarPedidosProdutosUz(Integer.valueOf(relatorioPedidoSeparacaoDTO.getNumPedido()));

            if (pedidoUz.getCount() > 0) {
                caminhoRelatorio = RelatorioEnum.PEDIDO_SEPARACAO_ARAUJO_TEM.getJrxmlPath();

                for (RelatorioProdutoSeparacaoDTO produto : listRelatorioProdutoSeparacaoDTO) {
                    String secaoUz = "";
                    Optional<PedidoProdutoUzDTO> uz = pedidoUz.getData().stream()
                        .filter(p -> p.getCodigoProduto().equals(produto.getCodigoProduto()))
                        .findFirst();
                    if(uz.isPresent()){
                        secaoUz = uz.get().getUz().trim();
                    }
                    produto.setSecao(secaoUz);
                }
            }
        }
        return caminhoRelatorio;
    }

	//Rappi ainda nao esta sendo entrega pelo clique e retire ate a entrega desse codigo
	private byte[] gerarImpressaoComanda(
		Map<String, Object> parametros, String caminhoRelatorio, RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO,
		boolean isDeliveryMarketingPlace
	) throws JRException, IOException {
		parametros.put(HAS_CANHOTO, true);
		parametros.put(TESOURA, relatorioUtil.getTesoura());
		parametros.put(ISRAPPIORIFOOD, isDeliveryMarketingPlace);

		preecherParametrosDoCanhotoEMensagemNovaComanda(relatorioPedidoSeparacaoDTO.getListProduto(), parametros);
		separarProdutosEspecias(relatorioPedidoSeparacaoDTO);

		List<JasperPrint> jasperPrints = new ArrayList<>();

		List<RelatorioPedidoSeparacaoDTO> listRelatorio = Collections.singletonList(relatorioPedidoSeparacaoDTO);
		JasperPrint comanda = relatorioUtil.gerarJasper(caminhoRelatorio,  listRelatorio, parametros);
		jasperPrints.add(comanda);

		caminhoRelatorio = RelatorioEnum.PEDIDO_SEPARACAO_COMANDA_CANHOTO.getJrxmlPath();

		if(!isDeliveryMarketingPlace) {
			JasperPrint canhoto = relatorioUtil.gerarJasper(caminhoRelatorio,  listRelatorio, parametros);
			jasperPrints.add(canhoto);
		}

		return relatorioUtil.export(jasperPrints);
	}

	@Async
	private void salvarImpressaoPedido(ImpressaoPedido impressao) {
		impressaoPedidoRepository.save(impressao);
	}

	/**
	 * enviar um documento para impressora
	 *
	 * @param pdfFile  de bytes do documento
	 * @param codigoFilial da filial
	 */
	private void enviarParaImpressora(byte[] pdfFile, Integer codigoFilial) {
		String impressaoAtiva = env.getProperty("impressao.ativa");
		if (Boolean.parseBoolean(impressaoAtiva)) {
			String nomeImpressora = filialRepositoryCustom.obterNomeImpressora(codigoFilial);
			String paramCaminhoServidor = parametroService
				.buscarPorChave(ParametroEnum.SERVIDOR_IMPRESSAO_DROGATEL.getDescricao()).getValor();
			String urlApiImpressao = parametroService
				.buscarPorChave(ParametroEnum.URL_REST_API_IMPRESSAO_ARAUJO.getDescricao()).getValor();

			ImpressaoDTO dto = new ImpressaoDTO();
			dto.setServidor(paramCaminhoServidor);
			dto.setDocumento(Base64.getEncoder().encodeToString(pdfFile));
			dto.setNomeImpressora(nomeImpressora);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<ImpressaoDTO> request = new HttpEntity<>(dto, headers);

			restTemplate.exchange(urlApiImpressao, HttpMethod.POST, request, ImpressaoDTO.class);
		}
	}

	/**
	 * Gera um relatório de termo de entrega
	 *
	 * @param numeroPedido
	 * @return byte[] - relatório de pedido em pdf
	 */
	@Transactional("drogatelTransactionManager")
	public byte[] imprimirTermoEntrega(Long numeroPedido) throws Exception {
		RelatorioTermoCompromissoDTO relatorioTermoCompromissoDTO = itemPedidoRepository.obterRelatorioTermoEntrega(numeroPedido);

		Map<String, Object> parametros = new HashMap<>();
		parametros.put(LOGO_ARAUJO, relatorioUtil.getLogoAraujo());

		List<RelatorioTermoCompromissoDTO> listRelatorio = new ArrayList<>();
		listRelatorio.add(relatorioTermoCompromissoDTO);

		byte[] pdfFile = relatorioUtil.gerarRelatorio(
			RelatorioEnum.TERMO_COMPROMISSO.getJrxmlPath(), null, listRelatorio, parametros
		);

		enviarParaImpressora(pdfFile, relatorioTermoCompromissoDTO.getCodFilial());

		return pdfFile;
	}

	private List<RelatorioProdutoSeparacaoDTO> agruparItens(List<RelatorioProdutoSeparacaoDTO> itensPedido) {
		Map<String, RelatorioProdutoSeparacaoDTO> itensMap = new HashMap<>();
		List<RelatorioProdutoSeparacaoDTO> itensOrdenados = new ArrayList<>();

		for (RelatorioProdutoSeparacaoDTO item : itensPedido) {
			String pbm = StringUtils.defaultString(item.getProdutoPBM());
			String farmaciaPopular = StringUtils.defaultString(item.getProdutoFarmaciaPopular());
			String numPrePedido = StringUtils.defaultString(item.getNumPrePedido());

			String chave = MessageFormat.format(
				"{0}##{1}#{2}#{3}",
				item.getCodigo(), pbm, farmaciaPopular, numPrePedido
			);

			RelatorioProdutoSeparacaoDTO itemAux = itensMap.get(chave);

			if (itemAux != null) {
				item.setQuantidade(String.valueOf(
					Integer.parseInt(item.getQuantidade()) + Integer.parseInt(itemAux.getQuantidade())
				));
			}

			itensMap.put(chave, item);
		}

		itensMap.forEach((key, value) -> itensOrdenados.add(value));

		itensOrdenados.sort(new BeanComparator<>(DESCRICAO));
		itensOrdenados.sort(new BeanComparator<>(SECAO));

		return itensOrdenados;
	}

	private void formatarNomeCliente(RelatorioPedidoSeparacaoDTO pedido, int tamanho) {
		if (pedido != null) {
			pedido.setNomeCliente(limitaTamanhoString(pedido.getNomeCliente(), tamanho));
		}
	}

	private void formatarMensagemControlado(RelatorioPedidoSeparacaoDTO pedido) {
		if (pedido.isMedicamentoControlado()) {
			pedido.setMensagemTipoPassoDois("[Registrar Pedido]");
			pedido.setMensagemDescricaoPassoDois("Aguarde confirmação do registro");
			StringBuilder complemento = new StringBuilder()
				.append("1) Anexe a folha do pedido aos  medicamentos controlados e separe-os em local  apropriado no ARMARIO DE CONTROLADOS. ")
				.append("Destaque nesta comanda a existência de itens não controlados na  prateleira, caso existam. <br/><br/>")
				.append("2) Para demais itens não controlados do pedido: reimprima a comanda, grampeie a folha do pedido na  sacola, ")
				.append("vá até o local de armazenagem dos pedidos  do 'Clique e Retire' e guarde o pedido na");
			pedido.setComplemento(complemento.toString());
			pedido.setMensagemControlado("CONTÉM MEDICAMENTO(S) CONTROLADO(S): MANTER ESTES ITENS NO ARMÁRIO");
			pedido.setEtapaMensagem("4) Com a chegada do cliente, valide a receita apresentada e proceda com a captação no menu de entrega do pedido.");
		} else {
			pedido.setMensagemTipoPassoDois("[Emitir Nota Fiscal]");
			pedido.setMensagemDescricaoPassoDois("Aguarde o término da emissão da nota fiscal");
			pedido.setMensagemControlado("");
			pedido.setEtapaMensagem("");
			pedido.setComplemento("1) Grampeie a folha do pedido na sacola, vá até o local de armazenagem dos pedidos do 'Clique e Retire' e guarde <br/>o pedido na");
		}
	}

	private String limitaTamanhoString(String texto, int tamanhoMaximo) {
		if (texto != null && texto.length() > tamanhoMaximo) {
			texto = texto.substring(0, tamanhoMaximo);
		}

		return texto;
	}

	public Boolean containsEspecial(List<RelatorioProdutoSeparacaoDTO> listRelatorioProdutoSeparacaoDTO) {
		return listRelatorioProdutoSeparacaoDTO.stream()
			.anyMatch(relatorioProdutoSeparacaoDTO -> (
				relatorioProdutoSeparacaoDTO.isProdutoAntibiotico() ||
				relatorioProdutoSeparacaoDTO.isProdutoControlado() ||
				relatorioProdutoSeparacaoDTO.isProdutoGeladeira() ||
				relatorioProdutoSeparacaoDTO.isVacina()
			));
	}

	public void separarProdutosEspecias(RelatorioPedidoSeparacaoDTO dto) {
		List<RelatorioProdutoSeparacaoDTO> listProdutos = dto.getListProduto();
		List<RelatorioProdutoSeparacaoDTO> listVacinas = listProdutos.stream()
			.filter(RelatorioProdutoSeparacaoDTO::isVacina).collect(Collectors.toList());
		List<RelatorioProdutoSeparacaoDTO> listaControlados = listProdutos.stream()
			.filter(RelatorioProdutoSeparacaoDTO::isProdutoControlado).collect(Collectors.toList());
		List<RelatorioProdutoSeparacaoDTO> listAntibioticos = listProdutos.stream()
			.filter(RelatorioProdutoSeparacaoDTO::isProdutoAntibiotico).collect(Collectors.toList());
		List<RelatorioProdutoSeparacaoDTO> listRefrigerados = listProdutos.stream()
			.filter(RelatorioProdutoSeparacaoDTO::isProdutoGeladeira).collect(Collectors.toList());

		dto.setListVacinas(listVacinas);
		dto.setListaControlados(listaControlados);
		dto.setListAntibioticos(listAntibioticos);
		dto.setListRefrigerados(listRefrigerados);
	}

	/**
	 *
	 * Parametros do canhoto
	 * esta na ordem de impressao o contador
	 * @param listRelatorioProdutoSeparacaoDTO
	 * @param parametros
	 * @return
	 */
	public void preecherParametrosDoCanhotoEMensagemNovaComanda(
		List<RelatorioProdutoSeparacaoDTO> listRelatorioProdutoSeparacaoDTO, Map<String, Object> parametros
	) {
		int contador = 0;

		parametros.put(ISANTIBIOTICO, Boolean.FALSE);
		parametros.put(ISCONTROLADO, Boolean.FALSE);
		parametros.put(ISVACINA, Boolean.FALSE);
		parametros.put(ISREFRIGERADO, Boolean.FALSE);
		parametros.put(CONTAINS_ITEM_NORMAL, Boolean.FALSE);
		parametros.put(CONTAINS_ITEM_ESPECIAL, Boolean.FALSE);
//		Contador de exibicao do canhoto  1 do totalpage
		parametros.put(NUMVACINA, 0);
		parametros.put(NUMCONTROLADO, 0);
		parametros.put(NUMREFRIGERADO, 0);
		parametros.put(NUMANTIBIOTICO, 0);

		for (RelatorioProdutoSeparacaoDTO relatorioProdutoSeparacaoDTO : listRelatorioProdutoSeparacaoDTO) {
			if (relatorioProdutoSeparacaoDTO.isProdutoAntibiotico()) {
				parametros.put(CONTAINS_ITEM_ESPECIAL, Boolean.TRUE);
				parametros.put(ISANTIBIOTICO, Boolean.TRUE);
			} else if (relatorioProdutoSeparacaoDTO.isProdutoControlado()) {
				parametros.put(CONTAINS_ITEM_ESPECIAL, Boolean.TRUE);
				parametros.put(ISCONTROLADO, Boolean.TRUE);
			} else if (relatorioProdutoSeparacaoDTO.isVacina()) {
				parametros.put(CONTAINS_ITEM_ESPECIAL, Boolean.TRUE);
				parametros.put(ISVACINA, Boolean.TRUE);
			} else if (relatorioProdutoSeparacaoDTO.isProdutoGeladeira()) {
				parametros.put(CONTAINS_ITEM_ESPECIAL, Boolean.TRUE);
				parametros.put(ISREFRIGERADO, Boolean.TRUE);
			} else {
				parametros.put(CONTAINS_ITEM_NORMAL, Boolean.TRUE);
			}
		}

		Set<String> cabecalho = new HashSet<>();
		if (parametros.get(ISVACINA) != null && Boolean.parseBoolean(parametros.get(ISVACINA).toString())) {
			cabecalho.add("VACINA");
			parametros.put(NUMVACINA, ++contador);
		}

		if (parametros.get(ISCONTROLADO) != null &&  Boolean.parseBoolean(parametros.get(ISCONTROLADO).toString())) {
			cabecalho.add(CONTROLADO);
			parametros.put(NUMCONTROLADO, ++contador);
		}

		if (parametros.get(ISREFRIGERADO) != null && Boolean.parseBoolean(parametros.get(ISREFRIGERADO).toString())) {
			cabecalho.add("REFRIGERADO");
			parametros.put(NUMREFRIGERADO, ++contador);
		}

		if (parametros.get(ISANTIBIOTICO) != null && Boolean.parseBoolean(parametros.get(ISANTIBIOTICO).toString())) {
			cabecalho.add("ANTIBIÓTICO");
			parametros.put(NUMANTIBIOTICO, ++contador);
		}

		parametros.put(TOTALPAGE, contador);
		String mensagemNovaComanda = "PEDIDO CONTÉM : ".concat(String.join("/", cabecalho));
		parametros.put(MSG_NOVA_COMANDA, mensagemNovaComanda);
	}

	public byte[] imprimirDanfe(String chaveNota) {
		try {
			String url = parametroService.buscarPorChave(ParametroEnum.URL_BASE_DANFE_PRINTER.getDescricao()).getValor();
			Response response = FeignUtil.getDanfePrinterClient(url).gerarDanfePelaChaveNota(chaveNota);
			byte[] byteResponse = IOUtils.toByteArray(response.body().asInputStream());

			if (response.status() >= 200 && response.status() < 300) {
				return byteResponse;
			}

			throw new BusinessException(new String(byteResponse));
		} catch (Exception ex) {
			log.error("Ocorreu um erro ao tentar gerar a DANFE da nota [{}]", chaveNota, ex);
			throw new BusinessException("Ocorreu um erro ao tentar gerar a DANFE deste pedido.");
		}
	}

    /**
     * Gera um relatório de pedido ao iniciar uma separação.
     *
     * @param pedidoFaltaDTO dto com dados da devolução
     * @return byte[] - relatório de pedido em pdf
     */
    @Transactional("drogatelTransactionManager")
    public ImpressaoDTO imprimirDevolucaoPedidoFaltaAraujoTem(PedidoFaltaDTO pedidoFaltaDTO) {
        try {
            RelatorioPedidoDevolucaoAraujoTemDTO relatorioPedidoDevolucaoAraujoTemDTO =
				pedidoRepositoryCustom.obterPedidoFaltaAraujoTemDevolucao(pedidoFaltaDTO.getCodigo());
            List<ProdutoDevolucaoAraujoTemDTO> produtosPedido =
				itemPedidoRepository.obterItensPedidoParaDevolucaoAraujoTem(pedidoFaltaDTO.getCodigo());

			Map<Integer, Integer> faltaPorProduto = pedidoFaltaDTO.getProdutos().stream()
				.filter(produto -> Optional.ofNullable(produto.getQuantidadeFalta()).orElse(0) != 0)
				.collect(Collectors.toMap(ProdutoFaltaDTO::getCodigo, ProdutoFaltaDTO::getQuantidadeFalta));

			List<ProdutoDevolucaoAraujoTemDTO> produtosFaltantes = produtosPedido.stream()
				.filter(produto -> Optional.ofNullable(faltaPorProduto.get(produto.getCodigoProdutoSemDigito())).orElse(0) != 0)
				.collect(Collectors.toList());

			
			Integer codUsuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
			ImpressaoDTO dto = new ImpressaoDTO();
			produtosFaltantes.forEach(produto -> {
				produto.setEmFalta(faltaPorProduto.get(produto.getCodigoProdutoSemDigito()));
				if (!drogatelService.sinalizarApontamentoFaltaZeroBalcao(pedidoFaltaDTO.getCodigoFilial(), produto.getCodigoProdutoSemDigito(), codUsuarioLogado)) 
					dto.setFalhaSinalizacaoZeroBalcao(true);
			});

            Usuario usuario = usuarioService.obterPeloId(codUsuarioLogado);
            relatorioPedidoDevolucaoAraujoTemDTO.setNomeUsuario(usuario.getNome());
            relatorioPedidoDevolucaoAraujoTemDTO.setListProduto(produtosFaltantes);

            byte[] pdfFile = relatorioUtil.gerarRelatorio(
                RelatorioEnum.PEDIDO_DEVOLUCAO_ARAUJO_TEM.getJrxmlPath(), null,
                Collections.singletonList(relatorioPedidoDevolucaoAraujoTemDTO), null
			);

            enviarParaImpressora(pdfFile, pedidoFaltaDTO.getCodigoFilial());

			dto.setDocumento(Base64.getEncoder().encodeToString(pdfFile));
            return dto;
        } catch (Exception ex) {
            log.error("Ocorreu um erro ao tentar gerar o PDF de devolução do pedido {}", pedidoFaltaDTO.getCodigo(), ex);
            throw new BusinessException("Ocorreu um erro ao tentar gerar o PDF de devolução deste pedido.");
        }
    }

    public void definirItensModificadosDoPedido(List<RelatorioProdutoSeparacaoDTO> itens, List<String> itensModificados) {
    	itens.forEach(
			itemPedido ->
				itensModificados.forEach(itemModificado -> {
					if (itemPedido.getCodigo().equals(itemModificado))
						itemPedido.setModificado(true);
				})
    	);
    }

    private String pacientesQuatroPontoZeroControlado(Long numeroPedido){
        List<PacienteDTO> pacientes = receitaProdutoControladoRepositoryCustom.buscaPacientesComandaSeparacao(numeroPedido);
        if(pacientes.isEmpty()){
            return null;
        }else{
            StringBuilder infoPacientes = new StringBuilder();
            for (PacienteDTO paciente : pacientes) {
                infoPacientes
                    .append(paciente.getNome()).append(" - ")
                    .append(DateUtils.dateParaDataFormatada(paciente.getDataNascimento())).append(" - ")
                    .append(SexoEnum.getValorPorSigla(paciente.getSexo()).getDescricao()).append("<br/>");
            }
            return infoPacientes.toString();
        }
    }

	public String imprimirReceitaDigital(Long numeroPedido, String ipCliente) {
		List<byte[]> receitas = imgService.obterListaReceitaDigital(numeroPedido);
		if (!receitas.isEmpty()) {
			Integer codigoFilial = controleIntranetRepository.findFilialByIp(ipCliente);
			receitas.forEach(receita -> enviarParaImpressora(receita, codigoFilial));
		}

		return "Receita impressa.";
	}

	private String formatarDinheiro(String valor){
		if(valor.equals("0")){
			valor = "0.00";
		}
		Locale locale = new Locale("pt", "BR");
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
		return currencyFormatter.format(Double.valueOf(valor));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> gerarPdfImpressao(Map<String, Object> parametrosGerarPdf) throws Exception {
		byte[] pdfFile;
		String caminhoRelatorio = (String)parametrosGerarPdf.get(CAMINHO_RELATORIO);
		boolean containsEspecial = (Boolean)parametrosGerarPdf.get("containsEspecial");
		RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO = (RelatorioPedidoSeparacaoDTO)parametrosGerarPdf.get("relatorioPedidoSeparacaoDTO");
		boolean isDeliveryMarketplace = (Boolean)parametrosGerarPdf.get("isDeliveryMarketplace");
		String descricaoMarketplace = (String)parametrosGerarPdf.get("descricaoMarketplace");
		List<String> itensModificados = (List<String>)parametrosGerarPdf.get("itensModificados");
		Map<String, Object> parametros = (Map<String, Object>)parametrosGerarPdf.get("parametros");
		boolean isEntregaViaMoto = (Boolean)parametrosGerarPdf.get("isEntregaViaMoto");
		if (containsEspecial) { // Nova comanda
			formatarNomeCliente(relatorioPedidoSeparacaoDTO, 32);
			if (isDeliveryMarketplace) {
				relatorioPedidoSeparacaoDTO.setCanalVenda(descricaoMarketplace);
			}
			if(itensModificados.isEmpty()) {
				caminhoRelatorio = RelatorioEnum.PEDIDO_SEPARACAO_COMANDA.getJrxmlPath();
			}
			pdfFile = gerarImpressaoComanda(parametros, caminhoRelatorio, relatorioPedidoSeparacaoDTO, isDeliveryMarketplace);
		} else { // Antiga comanda
			formatarNomeCliente(relatorioPedidoSeparacaoDTO, 19);
			if (!isEntregaViaMoto && isDeliveryMarketplace) {
				parametros.put(VIA_MOTO, false);
				relatorioPedidoSeparacaoDTO.setTipoRetirada(descricaoMarketplace);
			}

			pdfFile = relatorioUtil.gerarRelatorio(
				caminhoRelatorio, null, Collections.singletonList(relatorioPedidoSeparacaoDTO), parametros
			);
		}

		Map<String, Object> arquivoECaminho = new HashMap<>();
		arquivoECaminho.put(CAMINHO_RELATORIO, caminhoRelatorio);
		arquivoECaminho.put("pdfFile", pdfFile);
		return arquivoECaminho;
	}
}