package com.clique.retire.service.drogatel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.clique.retire.client.rest.CaptacaoDrogatelClient;
import com.clique.retire.dto.AuditoriaDTO;
import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ClienteCaptacaoDTO;
import com.clique.retire.dto.DadosCaptacaoConferenciaDTO;
import com.clique.retire.dto.EnderecoClienteDTO;
import com.clique.retire.dto.EntradaDTO;
import com.clique.retire.dto.EtiquetaDTO;
import com.clique.retire.dto.FilialDTO;
import com.clique.retire.dto.LoteBipadoDTO;
import com.clique.retire.dto.MedicoDTO;
import com.clique.retire.dto.PacienteDTO;
import com.clique.retire.dto.PrescritorDTO;
import com.clique.retire.dto.ProdutoDTO;
import com.clique.retire.dto.ProdutoEntradaDTO;
import com.clique.retire.dto.ReceitaDTO;
import com.clique.retire.dto.ReceitaSkuDTO;
import com.clique.retire.dto.SolicitacaoCaptacaoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.drogatel.ClienteRepositoryImpl;
import com.clique.retire.repository.drogatel.UsuarioRepository;
import com.clique.retire.service.cosmos.UsuarioCosmosService;
import com.clique.retire.util.FeignUtil;
import com.clique.retire.util.WebUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CaptacaoPedidoService {
	
	private static final int TIPO_REGISTRO_PRESCRITOR = 1;
	private static final int TIPO_DOCUMENTO_IDENTIDADE = 2;

	@Autowired
	private ReceitaProdutoControladoService receitaProdutoControladoService;
	
	@Autowired
	private FilialService filialService;

	@Autowired
	private EmitirEtiquetaService emitirEtiquetaService;
	
	@Autowired
	private LoteBipadoService loteBipadoService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ClienteRepositoryImpl clienteRepositoryImpl;
	
	@Autowired
	private PendenciaPedidoService pendenciaPedidoService;

	@Autowired
	private PrescritorService prescritorService;

	@Autowired
	private UsuarioCosmosService usuarioCosmosService;
	
	@Autowired
	private PedidoService pedidoService;

	@Value("${url.captacao}")
	private String urlCaptacao;
	
	@Value("${etiqueta.url}")
	private String urlEtiqueta;

	public ReceitaSkuDTO consultaCaptacaoPedido(Long idPedido, boolean refazerCaptacao) {
		if (refazerCaptacao) {
			receitaProdutoControladoService.removerProdutoReceitaControlado(idPedido.intValue(), true);
		}
		ReceitaSkuDTO response = receitaProdutoControladoService.buscaDadosCaptacao(idPedido);
		response.setProximaReceita(receitaProdutoControladoService.validarSeExisteCaptacaoPendente(idPedido));
		return response;
	}
	
	public boolean permitirRefazerCaptacao(Long numeroPedido) {
		Pedido pedido = pedidoService.findById(numeroPedido);
		return Arrays.asList(FasePedidoEnum.AGUARDANDO_REGISTRO, FasePedidoEnum.AGUARDANDO_RECEITA).contains(pedido.getFasePedido());
	}

	public boolean registrarCaptacao(ReceitaSkuDTO request) {
		clienteRepositoryImpl.atualizarDocumentoCliente(
			request.getNumeroPedido(), request.getDocumentoCliente(), request.getOrgaoExpedidor(), request.getUfCliente()
		);
		receitaProdutoControladoService.salvarReceitaProduto(request);

		if (request.hasTodasReceitasCaptadas()) {
			gerarCaptacaoReceitaEEmitirEtiqueta(request.getNumeroPedido(), false);
			return false;
		}

		return true;
	}

	public void gerarCaptacaoReceitaEEmitirEtiqueta(Long numeroPedido, boolean reimpressao) {
		Integer idFilial = filialService.buscarFilial(WebUtils.getClientIp());
		FilialDTO filialDTO = filialService.findFilialByIDEtiqueta(idFilial);

		List<LoteBipadoDTO> lotes = loteBipadoService.buscarLotesPorPedido(numeroPedido);
		List<ReceitaSkuDTO> receitas = receitaProdutoControladoService.buscaDadosParaEnvioDeCaptacaoEEmissaoDeEtiqueta(numeroPedido);
		Usuario usuarioCaptacao = usuarioRepository.buscarPorCodigoUsuario(receitas.get(0).getCodigoUsuarioCaptacao());
		Usuario usuarioConferente = usuarioRepository.buscarPorCodigoUsuario(receitas.get(0).getCodigoUsuarioConferente());
		boolean isPedidoCupomFiscal = pedidoService.isPedidoCupomFiscal(numeroPedido.intValue());

		receitas.forEach(receita -> {
			List<ProdutoEntradaDTO> produtosEntrada = new ArrayList<>();
			for (ProdutoDTO produto : receita.getProdutos()) {
				for (LoteBipadoDTO lote: lotes) {
					if (produto.getId().intValue() == lote.getIdItemPedido() && lote.getQuantidade() > 0) {
						if (produto.getNumeroCaixas().intValue() == lote.getQuantidade().intValue()) {
							produtosEntrada.add(obterProdutoEntradaDTO(produto, produto.getNumeroCaixas(), lote.getLote(), isPedidoCupomFiscal));
							lote.setQuantidade(0);
							break;
						}

						if (produto.getNumeroCaixas() > lote.getQuantidade()) {
							produto.setNumeroCaixas(produto.getNumeroCaixas() - lote.getQuantidade());
							produtosEntrada.add(obterProdutoEntradaDTO(produto, lote.getQuantidade(), lote.getLote(), isPedidoCupomFiscal));
							lote.setQuantidade(0);
							continue;
						}

						if (produto.getNumeroCaixas() < lote.getQuantidade()) {
							produtosEntrada.add(obterProdutoEntradaDTO(produto,produto.getNumeroCaixas(), lote.getLote(), isPedidoCupomFiscal));
							lote.setQuantidade(lote.getQuantidade() - produto.getNumeroCaixas());
							break;
						}
					}
				}
			}

			if (!produtosEntrada.isEmpty()) {
				String numeroAutorizacao = receita.getNumeroAutorizacao();
				if (StringUtils.isEmpty(numeroAutorizacao)) {
					numeroAutorizacao = realizarCaptacaoEGerarAutorizacao(receita, produtosEntrada);
					receitaProdutoControladoService.atualizarAutorizacaoPeloNumeroPedidoEReceita(
							numeroPedido, receita.getNumeroReceita(), numeroAutorizacao
					);
				} else if (!reimpressao) {
					realizarCaptacaoEGerarAutorizacao(receita,produtosEntrada);
				}

				if (reimpressao) {
					EtiquetaDTO dto = new EtiquetaDTO();
					dto.setIdPedido(numeroPedido);
					dto.setVendedor(usuarioCaptacao);
					dto.setVendedorConferente(usuarioConferente);
					dto.setNumeroAutorizacao(Long.parseLong(numeroAutorizacao));

					int produtosPorEtiqueta = 6;
					for (int counter = 0; counter < produtosEntrada.size(); counter += produtosPorEtiqueta) {
						int lastIndex = Math.min(counter + produtosPorEtiqueta, produtosEntrada.size());
						List<ProdutoEntradaDTO> produtos = produtosEntrada.subList(counter, lastIndex);

						String conteudo = emitirEtiquetaService.gerarEtiqueta(dto, receita, filialDTO, produtos);

						BaseResponseDTO baseResponseDto = new BaseResponseDTO();
						baseResponseDto.setData(conteudo);

						String response = FeignUtil.getGerarEtiquetaClient(this.urlEtiqueta).gerarEtiqueta(idFilial, baseResponseDto);
						log.info((response == null ? "Imprimindo na loja " : "Erro na impressão, loja ") + idFilial );
					}
				}
			}
		});

		this.pendenciaPedidoService.removerPendenciaPedidoControladoNaoEntregueNoPrazo(numeroPedido);
	}
	
	private String realizarCaptacaoEGerarAutorizacao(ReceitaSkuDTO receita, List<ProdutoEntradaDTO> produtosEntrada) {
		SolicitacaoCaptacaoDTO solicitacaoCaptacaoDTO = new SolicitacaoCaptacaoDTO();

		EntradaDTO entradaDTO = new EntradaDTO();
		entradaDTO.setCodigoFilial(receita.getCodigoPolo());

		ReceitaDTO receitaDTO = new ReceitaDTO();
		receitaDTO.setIdTipoReceita(receita.getTipoReceita());
		receitaDTO.setDataEmissao(receita.getDataReceitaCaptacao());
		if (!StringUtils.isEmpty(receita.getNumeroAutorizacao())) {
			receitaDTO.setNumeroSerie(receita.getNumeroAutorizacao());
		}

		PrescritorDTO prescritorDTO = new PrescritorDTO();
		if(receita.getNumeroRegistro() != null) {
			String uf = receita.getNumeroRegistro().substring(receita.getNumeroRegistro().length() - 2);
			String crm = receita.getNumeroRegistro().replaceAll("[^0-9	]+", "");
			
			prescritorDTO.setNumeroRegistro(Integer.parseInt(crm));
			prescritorDTO.setEstadoRegistro(uf);
		}
		
		prescritorDTO.setTipoRegistro(TIPO_REGISTRO_PRESCRITOR);
		prescritorDTO.setNome(receita.getNomeProfissional());
		receitaDTO.setPrescritorDTO(prescritorDTO);
		entradaDTO.setReceitaDTO(receitaDTO);

		ClienteCaptacaoDTO clienteCaptacaoDTO = new ClienteCaptacaoDTO();
		clienteCaptacaoDTO.setDocumento(receita.getDocumentoCliente());
		clienteCaptacaoDTO.setTipoDocumento(TIPO_DOCUMENTO_IDENTIDADE);
		clienteCaptacaoDTO.setOrgaoDocumento(receita.getOrgaoExpedidor());
		clienteCaptacaoDTO.setEstadoDocumento(receita.getUfCliente());
		clienteCaptacaoDTO.setNome(receita.getNomeCliente());
		clienteCaptacaoDTO.setSexo(receita.getSexoCliente().equals("N") ? "M" : receita.getSexoCliente());
		clienteCaptacaoDTO.setTelefone1(receita.getCelular());
		clienteCaptacaoDTO.setTelefone2(receita.getCelular());
		clienteCaptacaoDTO.setEmail(receita.getEmailCliente());
		clienteCaptacaoDTO.setDataNascimento(receita.getDataNascimentoClienteCaptacao());
		clienteCaptacaoDTO.setCpf(receita.getCpfCnpjCliente());
		
		EnderecoClienteDTO enderecoClienteDTO = new EnderecoClienteDTO();
		enderecoClienteDTO.setTipoLogradouro("Rua");
		enderecoClienteDTO.setCep(receita.getCep());
		enderecoClienteDTO.setLogradouro(receita.getLogradouro());
		enderecoClienteDTO.setNumero(receita.getNumero());
		enderecoClienteDTO.setComplemento(receita.getComplemento());
		enderecoClienteDTO.setBairro(receita.getBairro());
		enderecoClienteDTO.setCidade(receita.getCidade());
		enderecoClienteDTO.setEstado(receita.getUf());
		
		clienteCaptacaoDTO.setEnderecoClienteDTO(enderecoClienteDTO);
		entradaDTO.setClienteDTO(clienteCaptacaoDTO);

		if (receita.getNomePaciente() != null && !receita.getNomePaciente().trim().isEmpty()) {
			PacienteDTO pacienteDTO = new PacienteDTO();
			pacienteDTO.setNome(receita.getNomePaciente());
			pacienteDTO.setSexo(receita.getSexo());
			pacienteDTO.setDataNascimento(receita.getDataNascimentoPacienteCaptacao());
			pacienteDTO.setIdade(receita.getIdadePaciente());
			pacienteDTO.setTipoIdade(receita.getTipoIdadePaciente());
			
			entradaDTO.setPacienteDTO(pacienteDTO);
		}

		entradaDTO.setProdutoEntradaDTO(produtosEntrada);
		solicitacaoCaptacaoDTO.setEntradaDTO(entradaDTO);

		AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
		auditoriaDTO.setCodigoEstacao("252");
		auditoriaDTO.setCodigoUsuario("51");
		solicitacaoCaptacaoDTO.setAuditoriaDTO(auditoriaDTO);

		log.info("json captacao " + new Gson().toJson(solicitacaoCaptacaoDTO));

		CaptacaoDrogatelClient client = FeignUtil.getCaptacaoDrogatelClient(urlCaptacao);
		JsonObject response = null;
		try {
			response = client.salvarCaptacao(solicitacaoCaptacaoDTO);
		} catch (FeignException e) {
			throw new BusinessException("Não foi possível obter a autorização para a captação da receita.");
		}

		return response.get("Data").getAsJsonObject()
			.get("Receita").getAsJsonObject()
			.get("NumeroCaptacaoCsmMovLoja").getAsString();
	}

	private ProdutoEntradaDTO obterProdutoEntradaDTO(ProdutoDTO produtoDTO, Integer nrCaixa, String lote, boolean indicRec) {
		ProdutoEntradaDTO dto = 
				ProdutoEntradaDTO.builder()
								 .codigo(produtoDTO.getIdProduto().intValue())
								 .codigosBarra(Arrays.asList(produtoDTO.getEans()))
								 .descricaoResumida(produtoDTO.getDescricaoProduto())
								 .digito(produtoDTO.getDvProduto())
								 .numeroLote(lote)
								 .quantidade(nrCaixa)
								 .precoDe(produtoDTO.getPrecoDe())
								 .precoPor(produtoDTO.getPrecoPor())
								 .build();
		if (indicRec)
			dto.setIndicREC(true);
		return dto;
	}

	public DadosCaptacaoConferenciaDTO buscarDadosCaptacaoRealizada(Long numeroPedido) {
		DadosCaptacaoConferenciaDTO captacoes =
				receitaProdutoControladoService.buscarDadosCaptacaoParaConferencia(numeroPedido);

		List<LoteBipadoDTO> lotes = loteBipadoService.buscarLotesPorPedido(numeroPedido);

		captacoes.getReceitas().forEach(receita -> {
			MedicoDTO prescritor = receita.getPrescritor();
			MedicoDTO medico = prescritorService.consultaPrescritor(
				prescritor.getConselho(), prescritor.getUfRegistro(), prescritor.getNumeroRegistro()
			);
			prescritor.setNome(medico.getNome());

			receita.getProdutos().forEach(produto -> {
				int produtosComLote = 0;

				for (LoteBipadoDTO loteBipado: lotes) {
					if (loteBipado.getIdItemPedido().equals(produto.getCodigo()) && loteBipado.getQuantidade() > 0) {
						int quantidadePedida = produto.getQuantidadePedida() - produtosComLote;
						int quantidadeLoteAtual = Math.min(produto.getQuantidadePedida(), loteBipado.getQuantidade());
						IntStream.range(0, quantidadeLoteAtual).forEach(x -> produto.getLotes().add(loteBipado.getLote()));

						produtosComLote += quantidadeLoteAtual;
						if (loteBipado.getQuantidade().equals(quantidadePedida)) {
							loteBipado.setQuantidade(0);
							break;
						}

						if (quantidadePedida < loteBipado.getQuantidade()) {
							loteBipado.setQuantidade(loteBipado.getQuantidade() - quantidadePedida);
							break;
						}

						if (quantidadePedida > loteBipado.getQuantidade()) {
							loteBipado.setQuantidade(0);
						}
					}
				}
			});
		});

		return captacoes;
	}

  public void validarCaptacao(Long numeroPedido, String matriculaConferente) {
		Integer codigoUsuarioConferente = usuarioCosmosService.buscarUsuarioParaConferenciaCaptacao(matriculaConferente)
			.orElseThrow(() -> new BusinessException("Usuário não existe ou não está apto para realizar conferência."));

		receitaProdutoControladoService.salvarUsuarioConferente(numeroPedido, codigoUsuarioConferente);
  }

}
