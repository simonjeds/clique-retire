package com.clique.retire.model.drogatel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.enums.converter.FasePedidoEnumConverter;
import com.clique.retire.enums.converter.TipoPedidoEnumConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "PEDIDO")
public class Pedido extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "pedi_nr_pedido")
	@EqualsAndHashCode.Include
	private Long numeroPedido;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemPedido> itensPedido;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<HistoricoFasePedido> listHistoricoFasePedido = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<ProblemaSeparacaoPedido> problemasSeparacao = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<HistoricoNotaFiscal> listHistoricoNotaFiscal = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<RegistroPedido> listRegistroPedido = new ArrayList<>();

	@Column(name = "REPE_CD_REGISTRO_PEDIDO")
	private Integer codigoRegistroPedido;

	@Column(name = "EDRC_CD_ENDERECO")
	private Integer idEndereco;

	@OneToOne(mappedBy = "pedido")
	private NotaFiscal notaFiscal;

	@ManyToOne
	@JoinColumn(name = "polo_cd_polo")
	private Polo polo;

	@Column(name = "fili_cd_filial_gerencial")
	private Integer codigoFilialGerencial;

	@Column(name = "pedi_nr_pedido_ecommerce")
	private Long codigoVTEXSomenteNumeros;

	@Column(name = "pedi_nr_ecom_cliente")
	private String codigoVTEX;

	@Column(name = "pedi_vl_total_pedido")
	private Double valorTotal;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_EMITE_NOTA")
	private SimNaoEnum emiteNota;

	@Column(name = "PEDI_TN_TELEFONE_ENTREGA")
	private String telefoneEntrega;

	@Column(name = "PEDI_VL_DOACAO")
	private Double valorDoacao;

	@Column(name = "USUA_CD_USUARIO")
	private Long idUsuario;

	@Column(name = "PEDI_FL_TIPO_HORARIO_ENTREGA")
	private String tipoHorarioEntrega;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_RECEITA_CONTROL_APROVADA")
	private SimNaoEnum receitaControladoAprovada;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_REC_LIB")
	private SimNaoEnum recebimentoLiberado;

	@Column(name = "PEDI_FL_TIPO_HORARIO_BUSCAR_RECEITA")
	private String tipoHorarioBuscarReceita;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_REC_CONF")
	private SimNaoEnum recebimentoConfirmado;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_TAXA_ENTREGA_ISENTADA")
	private SimNaoEnum taxaEntregaIsentada;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_GERADO_TELEMARKETING")
	private SimNaoEnum geradoTelemarketing;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_MARKETPLACE")
	private SimNaoEnum marketplace;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_INDICADOR_TRANSF_POLO")
	private SimNaoEnum transferenciaPolo;

	@Column(name = "TPFR_CD_TIPO_FRETE")
	private Long idTipoFrete;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_GRANDE_VOLUME")
	private SimNaoEnum grandeVolume;

	@Enumerated(EnumType.STRING)
	@Column(name = "PEDI_FL_SUPERVENDEDOR")
	private SimNaoEnum superVendedor;

	@Column(name = "pedi_dh_inicio_negociacao")
	private Date inicioNegociacao;

	@Column(name = "FILI_CD_FILIAL_ARAUJO_TEM")
	private Integer codigoFilialAraujoTem;

	@Column(name = "PEDI_FL_ENT_DIF_END_CLI")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum enderecoDiferenteCliente;

	@Column(name = "PEDI_DH_BUSCAR_RECEITA_FINAL")
	private Date dataFinalBuscarReceita;

	@Column(name = "PEDI_VL_TOTAL_ITENS_PEDIDO")
	private Double valorTotalItens;

	@Column(name = "pedi_vl_taxa_entrega")
	private Double valorTaxaEntrega;

	@Column(name = "PEDI_DH_PREVISAO_ENTREGA_INICIAL")
	private Date dataPrevisaoInicial;

	@Column(name = "PEDI_DH_PREVISAO_ENTREGA_FINAL")
	private Date dataPrevisaoFinal;

	@Column(name = "PEDI_DH_BUSCAR_RECEITA_INICIAL")
	private Date dataInicialBuscarReceita;

	@Column(name = "PEDI_DH_INICIO_ATENDIMENTO")
	private Date dataInicioIntegracao;

	@Column(name = "NRTL_CD_NUMERO_TELEFONE")
	private Long numeroTelefone;
	
	@Convert(converter = FasePedidoEnumConverter.class)
	@Column(name = "pedi_fl_fase")
	private FasePedidoEnum fasePedido;

	@Convert(converter = TipoPedidoEnumConverter.class)
	@Column(name = "pedi_tp_pedido")
	private TipoPedidoEnum tipoPedido;

	@Column(name = "pedi_fl_operacao_loja")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum pedidoLoja;

	@ManyToOne
	@JoinColumn(name = "clnt_cd_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "polo_cd_polo", insertable = false, updatable = false)
	private Filial filial;

	@Column(name = "PEDI_DH_TERMINO_ATENDIMENTO")
	private Date dataTerminoIntegracao;

	@Column(name = "PESE_NR_SERVICO")
	private Integer numeroPedidoServico;

    @OneToOne(mappedBy = "pedido")
    private IntegracaoPedido integracaoPedido;

	@Transient
	private boolean editado;

	public Pedido(String codigoUsuario) {
		super(codigoUsuario);
	}

	public Pedido(Long numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

}
