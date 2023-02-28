package com.clique.retire.model.drogatel;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "FALTA")
public class Falta extends BaseEntity {

	private static final long serialVersionUID = -6932228446779212513L;

	public Falta(String codigoUsuario) {
		super(codigoUsuario);
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "FLTA_CD_FALTA")
    private Integer id;

    @Column(name = "FLTA_DH_INICIO_SEPARACAO")
    private Date dataInicioSeparacao;

    @Column(name = "FLTA_DH_TERMINO_SEPARACAO")
    private Date dataTerminoSeparacao;

    @ManyToOne
    @JoinColumn(name="polo_cd_polo")
    private Polo polo;

    @ManyToOne
    @JoinColumn(name="PRME_CD_PRODUTO")
    private Produto produto;

    @Column(name ="PRME_NR_QUANTIDADE_FALTA")
    private Integer quantidadeFalta;

    @Column(name="usua_cd_usuario")
    private Integer usuario;

    @ManyToOne
    @JoinColumn(name="pedi_nr_pedido")
    private Pedido pedido;

    @Column(name = "FLTA_VL_PRECODEPOSITO")
    private Double precoDeposito;

    @Column(name = "FLTA_QT_ESTOQDEPOSITO")
    private Integer quantidadeEstoqueDeposito;

    @Column(name = "FLTA_QT_ESTOQPOLO")
    private  Integer quantidadeEstoquePolo;


    @Column(name="FLTA_NR_QUANTIDADE_PEDIDA")
    private Integer quantidadePedida;

    @Column(name = "FLTA_FL_ENTRADA_NO_DIA")
    private boolean flagEntraNoDia;

    @Column(name = "FLTA_NR_ENTRADA_NO_DIA")
    private Integer entradaNoDia;

    @Column(name="IPRQTDRESERVADA")
    private Integer quantidadeReservada;

}
