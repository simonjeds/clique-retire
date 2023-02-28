package com.clique.retire.service.drogatel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.DadosCaptacaoConferenciaDTO;
import com.clique.retire.dto.ProdutoDTO;
import com.clique.retire.dto.ReceitaSkuDTO;
import com.clique.retire.enums.SexoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoConselhoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.ReceitaProdutoControlado;
import com.clique.retire.repository.drogatel.PedidoRepository;
import com.clique.retire.repository.drogatel.ReceitaProdutoControladoRepository;
import com.clique.retire.repository.drogatel.ReceitaProdutoControladoRepositoryCustom;
import com.clique.retire.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReceitaProdutoControladoService {

	private final PedidoRepository pedidoRepository;
	private final ReceitaProdutoControladoRepository receitaProdutoControladoRepository;
	private final ReceitaProdutoControladoRepositoryCustom receitaProdutoControladoRepositoryCustom;
	private final IMGService imgService;

	public void salvarReceitaProduto(ReceitaSkuDTO receitaSkuDTO) {
		Pedido pedido = pedidoRepository.findById(receitaSkuDTO.getNumeroPedido())
			.orElseThrow(() -> new BusinessException("Pedido não encontrado."));

		receitaSkuDTO.getProdutos().forEach(produto -> {
			Integer qtdBipada = produto.getQuantidadeReceita();
			for (ItemPedido itemPedido : pedido.getItensPedido()) {
				if (
					qtdBipada > 0 &&
					itemPedido.getCodigo() == produto.getId().intValue() &&
					itemPedido.getProdutoControlado().equals(SimNaoEnum.S)
				) {
					ReceitaProdutoControlado receitaProdutoControlado = this.montarReceitaProdutoControlado(receitaSkuDTO, produto);

					int saldo = Math.min(produto.getQuantidadePedida(), qtdBipada);
					qtdBipada -= saldo;
					receitaProdutoControlado.setRcpc_nr_caixa(saldo);
					receitaProdutoControlado.setItemPedido(itemPedido.getCodigo().longValue());
					receitaProdutoControlado.setProduto(itemPedido.getProduto().getCodigo().longValue());
					receitaProdutoControladoRepository.saveAndFlush(receitaProdutoControlado);
				}
			}
		});
	}

	private ReceitaProdutoControlado montarReceitaProdutoControlado(ReceitaSkuDTO receitaSkuDTO, ProdutoDTO produtoDTO) {
		ReceitaProdutoControlado receitaProdutoControlado = new ReceitaProdutoControlado(
			SecurityUtils.getCodigoUsuarioLogado().toString()
		);

		// Chave médico
		StringBuilder chaveMedico = new StringBuilder()
			.append(TipoConselhoEnum.buscarPorName(receitaSkuDTO.getConselho()).getChave())
			.append(StringUtils.leftPad(receitaSkuDTO.getNumeroRegistro(), 10, "0"))
			.append(receitaSkuDTO.getUf().toUpperCase());
		receitaProdutoControlado.setChave_medico(chaveMedico.toString());

		// Dados da receita
		if (StringUtils.isNotEmpty(receitaSkuDTO.getDataReceita())) {
			try {
				Date date = new SimpleDateFormat("dd/MM/yyyy").parse(receitaSkuDTO.getDataReceita());
				receitaProdutoControlado.setRcpc_dt_emissao_receita(date);
			} catch (Exception e) {
				throw new BusinessException("Data da receita inválida");
			}
		}
		receitaProdutoControlado.setRcpc_nr_numero_receita(receitaSkuDTO.getNumeroReceita());
		receitaProdutoControlado.setTire_sq_receita(produtoDTO.getIdTipoReceita());
		receitaProdutoControlado.setRcpc_fl_receita_assinada(SimNaoEnum.S);
		receitaProdutoControlado.setRcpc_fl_receita_sem_rasura(SimNaoEnum.S);
		receitaProdutoControlado.setRcpc_fl_receita_original(SimNaoEnum.S);
		receitaProdutoControlado.setRcpc_fl_envio_digital(imgService.isContemReceitaDigital(receitaSkuDTO.getNumeroPedido()) ? SimNaoEnum.S : SimNaoEnum.N);
		receitaProdutoControlado.setRcpc_fl_uso_prolongado(SimNaoEnum.getValueByBoolean(produtoDTO.isAntibioticoUsoProlongado()));
		receitaProdutoControlado.setRcpc_cd_autorizacao(receitaSkuDTO.getNumeroNotificacao());

		// Dados do cliente
		receitaProdutoControlado.setRcpc_ds_identidade_cliente(receitaSkuDTO.getDocumentoCliente());
		receitaProdutoControlado.setRcpc_ds_orgao_emissor_doc_comprador(receitaSkuDTO.getOrgaoExpedidor());
		receitaProdutoControlado.setRcpc_ds_uf_emissao_doc_comprador(receitaSkuDTO.getUfCliente());
		receitaProdutoControlado.setRcpc_tx_cid(
			receitaSkuDTO.getCidade().length() > 20
				? receitaSkuDTO.getCidade().substring(0, 17) + "..."
				: receitaSkuDTO.getCidade()
		);

		// Dados do paciente
		if (receitaSkuDTO.isAntibiotico()) {
			receitaProdutoControlado.setRcpc_nm_paciente(receitaSkuDTO.getNomePaciente());
			receitaProdutoControlado.setRcpc_nr_idade_paciente(receitaSkuDTO.getIdadePaciente());
			receitaProdutoControlado.setRcpc_tp_idade_paciente("A");
			receitaProdutoControlado.setRcpc_fl_sexo_paciente(SexoEnum.getValorPorSigla(receitaSkuDTO.getSexo()));
		}

		// Dados complementares
		receitaProdutoControlado.setUltimaAlteracao(new Date());
		receitaProdutoControlado.setEtiquetaEmitida(SimNaoEnum.N);

		return receitaProdutoControlado;
	}

	public ReceitaSkuDTO buscaDadosCaptacao(Long numeroPedido) {
		return receitaProdutoControladoRepositoryCustom.buscaDadosCaptacao(numeroPedido);
	}

	public List<ReceitaSkuDTO> buscaDadosParaEnvioDeCaptacaoEEmissaoDeEtiqueta(Long numeroPedido) {
		return receitaProdutoControladoRepositoryCustom.buscaDadosParaEnvioDeCaptacaoEEmissaoDeEtiqueta(numeroPedido);
	}
	
	public boolean isEntregaViaMotociclistaEContemControlado(Integer numeroPedido) {
		return receitaProdutoControladoRepositoryCustom.isEntregaViaMotociclistaEContemControlado(numeroPedido);
	}

	@Transactional("drogatelTransactionManager")
	public void removerProdutoReceitaControlado(Integer numeroPedido, boolean refazerCapatacao) {
		if (refazerCapatacao) {
			log.info("Removendo os dados de receita do pedido {}.",numeroPedido);
			receitaProdutoControladoRepositoryCustom.removerProdutoReceitaControlado(numeroPedido);
		}
	}

	@Transactional("drogatelTransactionManager")
	public void atualizarAutorizacaoPeloNumeroPedidoEReceita(
		Long numeroPedido, String numeroReceita,String numeroAutorizacao
	) {
		receitaProdutoControladoRepositoryCustom.atualizarAutorizacaoPeloNumeroPedidoEReceita(
			numeroPedido, numeroReceita, numeroAutorizacao
		);
	}

	@Transactional("drogatelTransactionManager")
	public void salvarUsuarioConferente(Long numeroPedido, Integer codigoUsuarioConferente) {
		receitaProdutoControladoRepository.salvarUsuarioConferente(numeroPedido, codigoUsuarioConferente);
	}

	@Transactional
    public void removerPorItensPedido(List<Integer> idsItensPedido) {
		if (CollectionUtils.isNotEmpty(idsItensPedido)) {
			log.info("Removendo ReceitaProdutoControlado para os itensPedido: {}", idsItensPedido);
			List<Long> idsItensPedidoLong = idsItensPedido.stream().map(Integer::longValue).collect(Collectors.toList());
			receitaProdutoControladoRepository.deleteDocumentoEsperadoByIdsItensPedido(idsItensPedidoLong);
			receitaProdutoControladoRepository.deletePosologiaByIdsItensPedido(idsItensPedidoLong);
			receitaProdutoControladoRepository.deleteReceitaLoteProdutoByIdsItensPedido(idsItensPedidoLong);
			receitaProdutoControladoRepository.deleteAllByItemPedidoCodigo(idsItensPedidoLong);
		}
    }
	
	public DadosCaptacaoConferenciaDTO buscarDadosCaptacaoParaConferencia(Long numeroPedido) {
		DadosCaptacaoConferenciaDTO captacoes = receitaProdutoControladoRepositoryCustom.buscarDadosCaptacaoParaConferencia(numeroPedido);
		captacoes.setReceitaDigital(imgService.isContemReceitaDigital(numeroPedido));
		return captacoes;
	}
	
	public boolean validarSeExisteCaptacaoPendente(Long numeroPedido) {
		int aCaptar = receitaProdutoControladoRepository.aCaptar(numeroPedido);
		return aCaptar > 0;
	}
	
	public boolean validarSeExisteReceitaComCaptacaoPendente(Long numeroPedido) {
		return receitaProdutoControladoRepositoryCustom.existeDadosDaReceita(numeroPedido) && validarSeExisteCaptacaoPendente(numeroPedido);
	}
	
}
