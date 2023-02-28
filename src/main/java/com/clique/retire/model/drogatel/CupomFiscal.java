package com.clique.retire.model.drogatel;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.clique.retire.enums.SituacaoCupomEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "CUPOM_FISCAL")
public class CupomFiscal extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUFI_CD_CUPOM")
	private Integer codigo;

	@Column(name = "CUFI_NR_CUPOM")
	private Integer numero;

	@Column(name = "CUFI_TP_SITUACAO")
	@Enumerated(EnumType.STRING)
	private SituacaoCupomEnum situacaoCupom;

	@Column(name = "CUFI_DH_CANCELAMENTO")
	private Date horarioCancelamento;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cupom", cascade = CascadeType.ALL)
	private List<ItemCupomFiscal> itensCuponsFiscais;

	@Column(name = "REPE_CD_REGISTRO_PEDIDO")
	private Integer codigoRegistro;

	@Column(name = "USUA_CD_CANCELAMENTO")
	private Integer codigoUsuarioCancelamento;

	@Column(name = "CUFI_NR_NF_OFFLINE")
	private Integer numeroNF;

	@Column(name = "CUFI_NR_ECF_OFFLINE")
	private Integer numeroECF;

	public CupomFiscal(String codigoUsuario) {
		super(codigoUsuario);
	}
}
