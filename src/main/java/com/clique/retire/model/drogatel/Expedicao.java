package com.clique.retire.model.drogatel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Framework
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EXPEDICAO")
public class Expedicao extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="EXPE_CD_EXPEDICAO")
	private Integer codigo;
	
	@Column(name="EXPE_DH_EXPEDICAO")
    private Date dataHoraExpedicao;
	
	@Column(name="EXPE_DH_RETORNO")
    private Date dataHoraRetorno;

	@Column(name="EXPE_QT_ENTREGA")
    private Integer quantidadeEntrega;
	
	@ManyToOne
	@JoinColumn(name="POLO_CD_POLO")
    private Polo polo;
	
	@ManyToOne
	@JoinColumn(name="USUA_CD_RESP_EXPEDICAO")
    private Usuario responsavelExpedicao;
	
	@ManyToOne
	@JoinColumn(name="USUA_CD_RESP_RETORNO")
    private Usuario responsavelRetorno;

	@ManyToOne
	@JoinColumn(name="MOAT_CD_MOTOCICLIS")
	private MotociclistaAtividade motociclistaAtividade;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy= "expedicao", cascade=CascadeType.ALL)
	private List<ExpedicaoPedido> expedicaoPedido = new ArrayList<>();

}