package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "DEPOSITO")
public class Deposito extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DEPO_CD_DEPOSITO")
	private Integer codigo;

	@Column(name = "DEPO_NM_FANTASIA")
	private String nomeFantasia;

	@Column(name = "DEPO_NM_LOGRAD")
	private String nomeLogradouro;

	@Column(name = "DEPO_TP_LOGRAD")
	private String tipoLogradouro;

	@Column(name = "DEPO_TX_NR_LOGRAD")
	private String numeroLogradouro;

	@Column(name = "DEPO_NM_COMPLEMEN")
	private String nomeComplemento;

	@Column(name = "DEPO_NM_BAIRRO")
	private String nomeBairro;

	@Column(name = "DEPO_TN_CEP")
	private String cep;

	@Column(name = "DEPO_TN_DDD1")
	private String ddd;

	@Column(name = "DEPO_TN_TEL1")
	private String numeroTelefone;

	@Column(name = "DEPO_NR_IP")
	private String enderecoIP;

	@Column(name = "DEPO_FL_ATENDE_DROGATEL")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum atendeDrogatel;

	@Column(name = "DEPO_NR_PRIORIDADE_ATENDIMENTO")
	private Integer prioridadeAtendimento;

}
