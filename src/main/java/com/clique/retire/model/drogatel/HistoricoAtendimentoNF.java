package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="DRGTBLHNFHISTORICONF_HST")
public class HistoricoAtendimentoNF extends BaseEntityAraujo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="HNFSEQ")
	private Integer id;
	
	@Column(name="PEDI_NR_PEDIDO")
	private Integer numeroPedido;
	
	@Column(name="PRME_CD_PRODUTO")
	private Integer codigoProduto;
	
	@Column(name="HNFQTDRECEBIDA")
	private Integer quantidadeRecebida;
	
	@Column(name="HNFNUMCHAVENF")
	private String chaveNF;
	
	public HistoricoAtendimentoNF(Integer codigoUsuario) {
		super(codigoUsuario.toString());
	}
}