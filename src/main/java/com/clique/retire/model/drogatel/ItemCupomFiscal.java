package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="ITEM_CUPOM_FISCAL")
public class ItemCupomFiscal extends BaseEntity{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITCU_CD_ITEM")
	private Integer codigo;

	@Column(name = "ITCU_QT_DEVOLVIDA")
	private Integer quantidadeDevolvida;

	@Column(name = "ITCU_DH_DEVOLUCAO")
	private Date dataHoraDevolucao;

	@ManyToOne
	@JoinColumn(name = "USUA_CD_USUARIO_GERENTE")
	private Usuario gerente;

	@ManyToOne
	@JoinColumn(name = "USUA_CD_USUARIO_DEVOLUCAO")
	private Usuario responsavelDevolucao;

	@ManyToOne
	@JoinColumn(name = "CUFI_CD_CUPOM", updatable = false)
	private CupomFiscal cupom;

	@OneToOne
	@JoinColumn(name = "ITPD_CD_ITEM_PEDIDO", updatable = false)
	private ItemPedido itemPedido;

	@Column(name = "PRME_CD_PRODUTO")
	private Integer codigoProdutoMestre;

	@Column(name = "ITCU_VALOR_UNITARIO")
	private Double valorUnitario;

	@Column(name = "ITCU_FL_INDICADOR_ITEM_FRETE")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum indicadorItemFrete;

	public ItemCupomFiscal(String codigoUsuario) {
		super(codigoUsuario);
	}
}