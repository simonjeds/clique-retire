package com.clique.retire.repository.drogatel;

import static com.clique.retire.util.Constantes.PEDIDO_NAO_ENCONTRADO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.DadosCaptacaoConferenciaDTO;
import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.dto.MedicoDTO;
import com.clique.retire.dto.PacienteDTO;
import com.clique.retire.dto.ReceitaConferenciaDTO;
import com.clique.retire.dto.ReceitaSkuDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SexoEnum;
import com.clique.retire.enums.TipoRegistroPrescritorEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.service.drogatel.ParametroService;

@Repository
public class ReceitaProdutoControladoRepositoryImpl implements ReceitaProdutoControladoRepositoryCustom {

	private static final String PEDIDO_SEM_MEDICAMENTO_CONTROLADO = "O pedido informado não possui medicamento controlado.";
	private DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private static final String NUMERO_PEDIDO = "numeroPedido";

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private PedidoRepositoryImpl pedidoRepositoryImpl;

	@Override
	public ReceitaSkuDTO buscaDadosCaptacao(Long numeroPedido) {
		return this.buscaDadosCaptacao(numeroPedido, false).stream()
			.findFirst()
			.orElseThrow(() -> new EntidadeNaoEncontradaException(PEDIDO_SEM_MEDICAMENTO_CONTROLADO));
	}

	@Override
	public List<ReceitaSkuDTO> buscaDadosParaEnvioDeCaptacaoEEmissaoDeEtiqueta(Long numeroPedido) {
		return this.buscaDadosCaptacao(numeroPedido, true);
	}

	@SuppressWarnings("unchecked")
	private List<ReceitaSkuDTO> buscaDadosCaptacao(Long numeroPedido, boolean captacao) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT ")
			.append("p.polo_cd_polo,  ")// 0
			.append("c.clnt_ds_nome,  ")// 1
			.append("rpc.RCPC_NR_NUMERO_RECEITA,  ")// 2
			.append("rpc.RCPC_DT_EMISSAO_RECEITA,  ")// 3
			.append("rpc.RCPC_DS_IDENTIDADE_CLIENTE,  ")// 4
			.append("rpc.CHAVE_MEDICO,  ")// 5
			.append("tlp.TIRE_SQ_RECEITA,  ")// 6
			.append("rpc.RCPC_NM_PACIENTE,  ")// 7
			.append("rpc.RCPC_FL_SEXO_PACIENTE,  ")// 8
			.append("rpc.RCPC_NR_IDADE_PACIENTE,  ")// 9
			.append("rpc.RCPC_TP_IDADE_PACIENTE,  ")// 10
			.append("p.pedi_tn_telefone_entrega, ")// 11
			.append("c.CLNT_TN_CI, ")// 12
			.append("c.CLNT_DH_ANIVERSARIO,  ")// 13
			.append("c.CLNT_DS_SEXO, ")// 14
			.append("m.MEDI_NM_MEDICO, ")// 15
			.append("de.EDRC_DS_OUTROS,  ")// 16
			.append("eae.ENAX_DS_LOGRADOURO, ")// 17
			.append("de.EDRC_NR_NUMERO,  ")// 18
			.append("eae.ENAX_DS_BAIRRO, ")// 19
			.append("loc.NOME_LOCAL,  ")// 20
			.append("loc.UF_LOCAL, ")// 21
			.append("eae.ENAX_CD_CEP, ")// 22
			.append("ip.ITPD_FL_PRODUTO_ANTIBIOTICO, ")// 23
			.append("c.CLNT_CD_CLIENTE, ")// 24
			.append("c.CLNT_TN_TEL1,  ")// 25
			.append("tlp.TPLP_SQ_TPLISTASPS, ") //26
			.append("c.CLNT_DS_ORGAO_EXPEDIDOR_CI, ") // 27
			.append("c.CLNT_CD_UF_CI, ") // 28
			.append("c.CLNT_TN_CPF_CNPJ, ") // 29
			.append("rpc.rcpc_cd_autorizacao, ") // 30
			.append("ct.NOME_TIPO + ' ' + NOME_LOG as logradouro, ") // 31
			.append("cb.EXTENSO_BAI as bairro, ") // 32
			.append("llo.NOME_LOCAL as cidade, ") // 33
			.append("uf.SIGLA_UF as uf, ") // 34
			.append("cl.CEP8_LOG as cep, ") // 35
			.append("c.CLNT_DS_EMAIL as email_cliente, ") // 36
			.append("rpc.xxxx_cd_usualt AS usuario_captacao, ") // 37
			.append("rpc.xxxx_dh_alt AS data_captacao, ") // 38
			.append("rpc.usua_cd_usuario AS usuario_conferente, ") // 39
			.append(" p.TPFR_CD_TIPO_FRETE as tipo_frete ") //40
			.append("FROM pedido p (nolock)  ")
			.append("INNER JOIN item_pedido ip (nolock) ON p.pedi_nr_pedido = ip.pedi_nr_pedido  ")
			.append("INNER JOIN produto_mestre pm (nolock) ON ip.prme_cd_produto = pm.prme_cd_produto  ")
			.append("INNER JOIN cliente c (nolock) ON c.clnt_cd_cliente = p.clnt_cd_cliente  ")
			.append("LEFT JOIN PROD_MESTRE_PS_VAL pmpv (nolock) ON pm.PRME_CD_PRODUTO = pmpv.PRME_CD_PRODUTO AND PMPV_DT_FIMVAL is null ")
			.append("LEFT JOIN TP_LISTA_PSICOTROP tlp (nolock) ON pmpv.TPLP_SQ_TPLISTASPS = tlp.TPLP_SQ_TPLISTASPS  ")
			.append("LEFT JOIN TIPO_RECEITA tr (nolock) ON tlp.TIRE_SQ_RECEITA = tr.TIRE_SQ_RECEITA  ")
			.append("LEFT JOIN RECEITA_PRODUTO_CONTROLADO rpc (nolock) ON rpc.ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO  ") ///  ATENÇÃO DOBRADA AQUI
			.append("LEFT JOIN MEDICO m ON m.MEDI_NV_CHAVE = rpc.CHAVE_MEDICO ")
			.append("LEFT JOIN DGTL_ENDERECO de ON (p.EDRC_CD_ENDERECO = de.EDRC_CD_ENDERECO) ")
			.append("LEFT JOIN ENDERECO_ARAUJO_EXPRESS eae ON eae.ENAX_CD_ENDERECO = de.ENAX_CD_ENDERECO ")
			.append("LEFT JOIN CEP_LOC loc ON loc.CHAVE_LOCAL = eae.CHAVE_LOCAL ")
			.append("LEFT JOIN CEP_LOG (nolock) cl ON cl.CHVLOCAL_LOG = de.CHVLOCAL_LOG and cl.CHAVE_LOG = de.CHAVE_LOG ")
			.append("LEFT JOIN CEP_BAI (nolock) cb ON cb.CHVLOC_BAI = cl.CHVLOCAL_LOG and cb.CHAVE_BAI = cl.CHVBAI1_LOG ")
			.append("LEFT JOIN CEP_TIT (nolock) ct ON ct.CHAVE_TIPO = cl.CHVTIPO_LOG ")
			.append("LEFT JOIN CEP_LOC (nolock) llo ON llo.CHAVE_LOCAL = cl.CHVLOCAL_LOG ")
			.append("LEFT JOIN CEP_UFS (nolock) uf ON uf.SIGLA_UF = llo.UF_LOCAL ")
			.append("WHERE ip.pedi_nr_pedido = :numeroPedido AND ip.itpd_fl_produto_controlado = 'S' ");

		if (!captacao) {
			sql.append("ORDER BY rpc.rcpc_nr_numero_receita DESC");
		}

		List<Object[]> resultado = em.createNativeQuery(sql.toString())
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.getResultList();

		if (CollectionUtils.isEmpty(resultado)) {
			throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
		}

		return resultado.stream().map(receita -> {
			ReceitaSkuDTO dto = new ReceitaSkuDTO();

			dto.setNumeroPedido(numeroPedido);
			dto.setCodigoPolo((Integer) receita[0]);
			dto.setNumeroAutorizacao((String) receita[30]);
			dto.setCodigoUsuarioCaptacao((Integer) receita[37]);
			dto.setDataCaptacao((Date) receita[38]);
			dto.setCodigoUsuarioConferente((Integer) receita[39]);

			// Dados do médico
			dto.setNumeroRegistro((String) receita[5]);
			dto.setNomeProfissional((String) receita[15]);

			// Dados da receita
			dto.setNumeroReceita((String) receita[2]);
			dto.setTipoReceita(Integer.valueOf((Short) receita[6]));

			if (receita[3] != null) {
				dto.setDataReceita(dateFormatter.format((Date) receita[3]));
				dto.setDataReceitaCaptacao((Date) receita[3]);
			}

			if (receita[23] != null) {
				String antibiotico = (String) receita[23];
				dto.setAntibiotico(antibiotico.equals("S"));
			}

			// Dados do cliente
			dto.setNomeCliente((String) receita[1]);
			dto.setDocumentoCliente((String) receita[12]);
			dto.setIdCliente((Integer) receita[24]);
			dto.setOrgaoExpedidor((String) receita[27]);
			dto.setUfCliente(Objects.nonNull(receita[28]) ? receita[28].toString().trim() : null);
			dto.setCpfCnpjCliente((String) receita[29]);

			if (receita[13] != null) {
				dto.setDataNascimentoCliente(dateFormatter.format((Date) receita[13]));
				dto.setDataNascimentoClienteCaptacao((Date) receita[13]);
			}

			if (receita[36] != null) {
				dto.setEmailCliente((String) receita[36]);
			}

			if (receita[14] != null) {
				dto.setSexoCliente(String.valueOf(receita[14]));
			}

			if (receita[11] != null) {
				dto.setCelular((String) receita[11]);
			} else if (receita[25] != null) {
				dto.setCelular((String) receita[25]);
			} else {
				dto.setCelular("");
			}

			// Endereço do cliente
			dto.setLogradouro(StringUtils.firstNonBlank((String) receita[17], (String) receita[31]));
			dto.setNumero((Integer) receita[18]);
			dto.setBairro(StringUtils.firstNonBlank((String) receita[19], (String) receita[32]));
			dto.setCidade(StringUtils.firstNonBlank((String) receita[20], (String) receita[33]));
			dto.setUf(StringUtils.firstNonBlank((String) receita[21], (String) receita[34]));
			dto.setCep(StringUtils.firstNonBlank((String) receita[22], (String) receita[35]));
			dto.setComplemento((String) receita[16]);

			// Dados do paciente
			dto.setNomePaciente((String) receita[7]);
			dto.setIdadePaciente((Integer) receita[9]);
			dto.setDocumentoPaciente((String) receita[4]);

			if (receita[10] != null) {
				dto.setTipoIdadePaciente(String.valueOf(receita[10]));
			}

			if (receita[8] != null) {
				dto.setSexo(String.valueOf(receita[8]));
			}

			String urlBaseImagem = parametroService
				.buscarPorChave(ParametroEnum.APLICATIVOS_INTERNOS_URL_BASE_IMAGENS.getDescricao()).getValor();

			dto.setProdutos(
				pedidoRepositoryImpl.buscarProdutosPorPedido(
					numeroPedido, urlBaseImagem, captacao, (captacao ? dto.getNumeroReceita() : null)
				)
			);

			dto.setQuatroPontoZero(((Integer) receita[40]) == 1);
			dto.setReceitaDigital(false);

			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public void removerProdutoReceitaControlado(Integer numeroPedido) {

		String fromReceitaProdutoControlado = "from RECEITA_PRODUTO_CONTROLADO ";

		StringBuilder sql = new StringBuilder(" delete from dbo.RECEITA_LOTE_PRODUTO ")
									  .append("where RCPC_CD_RECEITA_PRODUTO_CONTROLADO in (select RCPC_CD_RECEITA_PRODUTO_CONTROLADO  ")
									  .append(fromReceitaProdutoControlado)
									  .append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido))");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .executeUpdate();

		sql = new StringBuilder(" delete from dbo.DOCUMENTO_ESPERADO ")
						.append(" where RCPC_CD_RECEITA_PRODUTO_CONTROLADO in (select RCPC_CD_RECEITA_PRODUTO_CONTROLADO  ")
						.append("from RECEITA_PRODUTO_CONTROLADO ")
						.append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido)) ");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .executeUpdate();

		sql = new StringBuilder(" delete from dbo.POSOLOGIA ")
						.append(" where RCPC_CD_RECEITA_PRODUTO_CONTROLADO in (select RCPC_CD_RECEITA_PRODUTO_CONTROLADO  ")
						.append(fromReceitaProdutoControlado)
						.append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido)) ");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .executeUpdate();

		sql = new StringBuilder(" delete from RECEITA_PRODUTO_CONTROLADO ")
					    .append(" where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido) ");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .executeUpdate();

	}
	
	public boolean isEntregaViaMotociclistaEContemControlado(Integer numeroPedido) {
		try {
			em.createNativeQuery(
					new StringBuilder(" select 1 from PEDIDO p (nolock) ")
					  		  .append(" where p.PEDI_NR_PEDIDO = :numeroPedido ")
					  		  .append(" and TPFR_CD_TIPO_FRETE = 1 ")
					  		  .append(" and exists (select 1 from ITEM_PEDIDO ip (nolock) where ip.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO and ip.ITPD_FL_PRODUTO_CONTROLADO = 'S') ").toString())
			   .setParameter(NUMERO_PEDIDO, numeroPedido)
			   .getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}
	
	public boolean existeDadosDaReceita(Long numeroPedido) {
		StringBuilder sql = new StringBuilder(" select 1 from RECEITA_PRODUTO_CONTROLADO ")
			    					  .append(" where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido) ");

		return !em.createNativeQuery(sql.toString())
				  .setParameter(NUMERO_PEDIDO, numeroPedido)
				  .getResultList()
				  .isEmpty();
	}

	@Override
	@Transactional("drogatelTransactionManager")
	public void atualizarAutorizacaoPeloNumeroPedidoEReceita(Long numeroPedido, String numeroReceita, String numeroAutorizacao) {
		StringBuilder sql = new StringBuilder(" update rpc set rcpc_cd_autorizacao = :numeroAutorizacao ")
									  .append(" from RECEITA_PRODUTO_CONTROLADO rpc  ")
									  .append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido) and rcpc_nr_numero_receita = :numeroReceita ");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .setParameter("numeroReceita", numeroReceita)
		  .setParameter("numeroAutorizacao", numeroAutorizacao)
		  .executeUpdate();
	}
	
	@Override
	@Transactional("drogatelTransactionManager")
	public void removerAutorizacaoPeloNumeroPedido(Long numeroPedido) {
		StringBuilder sql = new StringBuilder(" update rpc set rcpc_cd_autorizacao = null ")
									  .append(" from RECEITA_PRODUTO_CONTROLADO rpc  ")
									  .append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido) ");

		em.createNativeQuery(sql.toString())
		  .setParameter(NUMERO_PEDIDO, numeroPedido)
		  .executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DadosCaptacaoConferenciaDTO buscarDadosCaptacaoParaConferencia(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT ")
			.append("rpc.rcpc_cd_receita_produto_controlado AS codigo_receita, ")
			.append("ip.itpd_cd_item_pedido AS codigo_item, ")
			.append("c.clnt_ds_nome AS nome_cliente, ")
			.append("c.clnt_tn_cpf_cnpj AS documento_cliente, ")
			.append("rpc.rcpc_cd_autorizacao AS numero_autorizacao, ")
			.append("rpc.rcpc_dt_emissao_receita AS data_emissao, ")
			.append("tlp.tplp_sg_psico AS tipo_receita, ")
			.append("rpc.chave_medico AS chave_medico, ")
			.append("rpc.rcpc_nr_caixas AS quantidade_produto, ")
			.append("pm.prme_tx_descricao2 AS descricao_produto, ")
			.append("rpc.rcpc_nm_paciente AS nome_paciente, ")
			.append("rpc.rcpc_fl_sexo_paciente AS sexo_paciente, ")
			.append("rpc.rcpc_nr_idade_paciente AS idade_paciente, ")
			.append("IIF(ip.itpd_fl_produto_antibiotico = 'S', 'TRUE', 'FALSE') AS is_antibiotico, ")
			.append("u.usua_tx_matricula AS matricula_vendedor_captacao, ")
			.append("rpc.usua_cd_usuario AS usuario_conferente, ")
			.append(" p.TPFR_CD_TIPO_FRETE as tipo_frete ")
			.append("FROM receita_produto_controlado rpc (NOLOCK) ")
			.append("JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
			.append("JOIN pedido p (NOLOCK) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ")
			.append("JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
			.append("JOIN produto_mestre pm (NOLOCK) ON pm.prme_cd_produto = ip.prme_cd_produto ")
			.append("LEFT JOIN prod_mestre_ps_val pmpv (NOLOCK) ON pm.prme_cd_produto = pmpv.prme_cd_produto ")
			.append("  AND pmpv.pmpv_dt_fimval IS NULL ")
			.append("LEFT JOIN tp_lista_psicotrop tlp (NOLOCK) ON pmpv.tplp_sq_tplistasps = tlp.tplp_sq_tplistasps ")
			.append("LEFT JOIN usuario u (NOLOCK) ON u.usua_cd_usuario = rpc.xxxx_cd_usualt ")
			.append("WHERE ip.pedi_nr_pedido = :numeroPedido");

		List<Tuple> result = this.em.createNativeQuery(sql.toString(), Tuple.class)
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.getResultList();

		Tuple dadosPedido = result.iterator().next();
		boolean hasConferenciaRealizada = result.stream()
			.allMatch(tupla -> Objects.nonNull(tupla.get("usuario_conferente", Integer.class)));

		List<ReceitaConferenciaDTO> receitas = result.stream()
			.map(tupla -> {
				MedicoDTO prescritor = new MedicoDTO();

				String chaveMedico = tupla.get("chave_medico", String.class);
				Matcher matcher = Pattern.compile("^(?<tipo>\\w)\\s*(?<numero>\\d+)\\s*(?<uf>\\w+)$").matcher(chaveMedico);

				if (matcher.find()) {
					prescritor.setConselho(TipoRegistroPrescritorEnum.getValueByCodigo(matcher.group("tipo")).name());
					prescritor.setNumeroRegistro(Integer.parseInt(matcher.group("numero")));
					prescritor.setUfRegistro(matcher.group("uf"));
				}

				ItemPedidoDTO item = new ItemPedidoDTO();
				item.setCodigo(tupla.get("codigo_item", Integer.class));
				item.setQuantidadePedida(tupla.get("quantidade_produto", Integer.class));
				item.setDescricaoProduto(tupla.get("descricao_produto", String.class));
				item.setAntibiotico(Boolean.parseBoolean(tupla.get("is_antibiotico", String.class)));

				String sexoPaciente = String.valueOf(tupla.get("sexo_paciente", Character.class));
				SexoEnum sexo = SexoEnum.getValorPorSigla(sexoPaciente);
				PacienteDTO paciente = new PacienteDTO();
				paciente.setNome(tupla.get("nome_paciente", String.class));
				paciente.setSexo(Objects.nonNull(sexo) ? sexo.getDescricao() : null);
				paciente.setIdade(tupla.get("idade_paciente", Integer.class));

				return ReceitaConferenciaDTO.builder()
					.codigo(tupla.get("codigo_receita", Integer.class))
					.numeroAutorizacao(tupla.get("numero_autorizacao", String.class))
					.tipo(tupla.get("tipo_receita", String.class))
					.dataEmissao(tupla.get("data_emissao", Date.class))
					.prescritor(prescritor)
					.paciente(paciente)
					.item(item)
					.build();
			})
			.collect(Collectors.groupingBy(ReceitaConferenciaDTO::getNumeroAutorizacao))
			.values().stream()
			.map(receitasAgrupadas -> {
				List<ItemPedidoDTO> produtos = receitasAgrupadas.stream()
					.map(ReceitaConferenciaDTO::getItem)
					.collect(Collectors.toList());

				ReceitaConferenciaDTO receita = receitasAgrupadas.iterator().next();
				receita.setProdutos(produtos);
				return receita;
			}).collect(Collectors.toList());

		return DadosCaptacaoConferenciaDTO.builder()
			.numeroPedido(numeroPedido)
			.nomeCliente(dadosPedido.get("nome_cliente", String.class))
			.documentoCliente(dadosPedido.get("documento_cliente", String.class))
			.matriculaVendedorCaptacao(dadosPedido.get("matricula_vendedor_captacao", String.class))
			.conferenciaJaRealizada(hasConferenciaRealizada)
			.receitas(receitas)
			.quatroPontoZero(dadosPedido.get("tipo_frete", Integer.class) == 1)
			.receitaDigital(false)
			.build();
	}

	@Override
    public List<PacienteDTO> buscaPacientesComandaSeparacao(Long numeroPedido){
        StringBuilder sql = new StringBuilder()
        .append("SELECT DISTINCT ")
        .append("rpc.rcpc_nm_paciente AS nome_paciente, ")
        .append("Iif(rpc.rcpc_nr_idade_paciente IS NULL, ")
        .append("    rpc.rcpc_nr_idade_paciente, ")
        .append("    Dateadd(year, -1 * rpc.rcpc_nr_idade_paciente, CURRENT_TIMESTAMP) ")
        .append(") AS data_nascimento, ")
        .append("rpc.rcpc_fl_sexo_paciente AS sexo ")
        .append("FROM RECEITA_PRODUTO_CONTROLADO rpc ")
        .append("WHERE itpd_cd_item_pedido IN (SELECT ip.itpd_cd_item_pedido ")
        .append("                               FROM   ITEM_PEDIDO ip ")
        .append("                               INNER JOIN PEDIDO p ON p.PEDI_NR_PEDIDO = ip.pedi_nr_pedido ")
        .append("                               WHERE  ip.pedi_nr_pedido = :numeroPedido ")
        .append("                               AND p.TPFR_CD_TIPO_FRETE = 1 ")
        .append("                               AND ip.ITPD_FL_PRODUTO_CONTROLADO = 'S') ")
        .append("AND rpc.rcpc_nm_paciente IS NOT NULL ");

        List<Tuple> resultado = this.em.createNativeQuery(sql.toString(), Tuple.class)
            .setParameter(NUMERO_PEDIDO, numeroPedido)
            .getResultList();

        return resultado.stream()
            .map(tupla -> {
                PacienteDTO paciente = new PacienteDTO();
                paciente.setSexo(tupla.get("sexo", Character.class).toString());
                paciente.setDataNascimento(tupla.get("data_nascimento", Date.class));
                paciente.setNome(tupla.get("nome_paciente", String.class));
                return paciente;
            })
            .collect(Collectors.toList());
	}
}