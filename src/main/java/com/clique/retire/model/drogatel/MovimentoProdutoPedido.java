package com.clique.retire.model.drogatel;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "DRGTBLMPPMOVPRODUTOPEDID")
public class MovimentoProdutoPedido extends BaseEntityAraujo {

    @Id
    @Column(name = "MPPCOD")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer codigo;

    @Column(name = "PEDI_FL_FASE")
    private FasePedidoEnum fasePedido;

    @Column(name = "MPPQTD")
    private Integer quantidade;

    @Column(name = "MPPIDCATENDIMENTO")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum atendimento = SimNaoEnum.N;

    @Column(name = "MPPIDCPRIMEIRO")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum primeiro = SimNaoEnum.N;

    @Column(name = "PRME_CD_PRODUTO")
    private Integer codigoProduto;

    @ManyToOne
    @JoinColumn(name = "MTDR_CD_MOTIVO_DROGATEL")
    private MotivoDrogatel motivoDrogatel;

    @ManyToOne
    @JoinColumn(name = "POLO_CD_POLO")
    private Polo poloPedido;

    @ManyToOne
    @JoinColumn(name = "PEDI_NR_PEDIDO")
    private Pedido pedido;

    public MovimentoProdutoPedido(Integer codigoUsuario) {
        super(codigoUsuario);
    }

}
