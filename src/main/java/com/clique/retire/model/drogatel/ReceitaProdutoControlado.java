package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clique.retire.enums.SexoEnum;
import com.clique.retire.enums.SimNaoEnum;

import com.clique.retire.enums.TipoDocumentoClienteEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "RECEITA_PRODUTO_CONTROLADO")
public class ReceitaProdutoControlado extends BaseEntity {

	private static final long serialVersionUID = 6088324080663974439L;

	public ReceitaProdutoControlado(String codigoUsuario) {
		super(codigoUsuario);
		this.rcpc_ds_tipo_doc_comprador = TipoDocumentoClienteEnum.CARTEIRA_DE_IDENTIDADE.getCodigo();
		this.etiquetaEmitida = SimNaoEnum.S;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RCPC_CD_RECEITA_PRODUTO_CONTROLADO")
	private Long id;

	@Column(name = "rcpc_nr_numero_receita")
	private String rcpc_nr_numero_receita;

	@Column(name = "rcpc_dt_emissao_receita")
	private Date rcpc_dt_emissao_receita;

	@Column(name = "rcpc_fl_receita_assinada")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum rcpc_fl_receita_assinada;

	@Column(name = "rcpc_fl_receita_original")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum rcpc_fl_receita_original;

	@Column(name = "rcpc_fl_receita_sem_rasura")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum rcpc_fl_receita_sem_rasura;

	@Column(name = "rcpc_ds_identidade_cliente")
	private String rcpc_ds_identidade_cliente;

	@Column(name = "chave_medico")
	private String chave_medico;

	@Column(name = "prme_cd_produto")
	private Long produto;
	

	@Column(name = "itpd_cd_item_pedido")
	private Long itemPedido;

	@Column(name = "rcpc_nr_caixas")
	private Integer rcpc_nr_caixa;

	@Column(name = "tire_sq_receita")
	private Integer tire_sq_receita;

	@Column(name = "rcpc_ds_orgao_emissor_doc_comprador")
	private String rcpc_ds_orgao_emissor_doc_comprador;

	@Builder.Default
	@Column(name = "rcpc_ds_tipo_doc_comprador")
	private String rcpc_ds_tipo_doc_comprador = TipoDocumentoClienteEnum.CARTEIRA_DE_IDENTIDADE.getCodigo();

	@Column(name = "rcpc_ds_uf_emissao_doc_comprador")
	private String rcpc_ds_uf_emissao_doc_comprador;

	@Column(name = "atsz_cd_atendimentosaizen")
	private Long NULL;

	@Column(name = "rcpc_fl_justificativa_anexada")
	private String rcpc_fl_justificativa_anexada;

	@Column(name = "usua_cd_usuario")
	private Long usua_cd_usuario;

	@Column(name = "reic_cd_receita_imagem")
	private Long reic_cd_receita_imagem;

	@Column(name = "rcpc_nm_paciente")
	private String rcpc_nm_paciente;

	@Column(name = "rcpc_fl_sexo_paciente")
	@Enumerated(EnumType.STRING)
	private SexoEnum rcpc_fl_sexo_paciente;

	@Column(name = "rcpc_nr_idade_paciente")
	private Integer rcpc_nr_idade_paciente;

	@Column(name = "rcpc_tp_idade_paciente")
	private String rcpc_tp_idade_paciente;

	@Column(name = "rcpc_tx_cid")
	private String rcpc_tx_cid;

	@Column(name = "rcpc_fl_uso_prolongado")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum rcpc_fl_uso_prolongado;

	@Column(name = "rcpc_fl_envio_digital")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum rcpc_fl_envio_digital;
	
	@Column(name = "rcpc_cd_autorizacao")
	private String rcpc_cd_autorizacao;

	@Builder.Default
	@Column(name = "rcpc_fl_etiquetaemitida")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum etiquetaEmitida = SimNaoEnum.S;

}
