package com.clique.retire.model.drogatel;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DRGTBLCFPCONTRFASINTPED")
public class IntegracaoPedido extends BaseEntityAraujo {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CFPSEQ")
    @EqualsAndHashCode.Include
    private Long cfpSec;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PEDI_NR_PEDIDO", referencedColumnName = "PEDI_NR_PEDIDO")
    private Pedido pedido;

    @Column(name = "PEDI_TX_FASE_INTEGRACAO_DRIN")
    private String faseIntegracao;

    @Column(name = "PEDI_TX_ERRO_INTEGRACAO")
    private String erroIntegracao;

}
