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
@Table(name="DRGTBLACRAVALIACAOCLIRET_HST")
public class Avaliacao extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ACRSEQHST")
    private Integer id;

	@Column(name="FILI_CD_FILIAL")
    private Integer codigoFilial;
	
	@Column(name="ACRVALNOTA")
    private Integer nota;
	
	@Column(name = "ACRTXTCOMENTARIO")
	private String comentario;

	@Column(name = "ACRNUMVERSAOPAINEL")
	private Integer numeroVersaoPainel = NUMERO_VERSAO_PAINEL_CLIQUE_RETIRE;
	
	@Column(name = "ACRNUMSEPARACOES")
	private Integer quantidadeSeparacoesRealizadas;

	@Column(name = "ACRNUMCONTROLADOS")
	private Integer quantidadeControladosRealizados;

	@Column(name = "ACRNUMENTREGAS")
	private Integer quantidadeEntregasRealizadas;

	@Column(name = "ACRNUMFALTA")
	private Integer quantidadeApontamentosFaltasRealizadas;

	@Column(name = "ACRNUMRECEBIMENTOS")
	private Integer quantidadeRecebimentosMercadoriasRealizadas;

	@Column(name = "ACRNUMEXPIRADOS")
	private Integer quantidadeFluxosExperidadosRealizados;
	
	public Avaliacao(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public Avaliacao() {
	}
}
