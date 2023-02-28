package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelatorioPedidoSeparacaoDTO {

	private String numPedido;
	private String nomeCliente;
	private String nomeVendedor;
	private String tipoRetirada;
	private String filial;
	private String filialOrigemAraujoTem;
	private Integer idFilialOrigemAraujoTem;
	private boolean isPedidoAraujoTem = false;
	private boolean isPedidoPossuiVencCurto;
	private boolean isSuperVendedor;
	private String canalVenda;
	private Integer codFilial;
	private String numPrateleira;
	private String emitidoEm;
	private String tokenBox;
	private String idPedidoVtex;
	private String codTipoRetirada;
	private String codBox;
	private String mensagemControlado;
	private String etapaMensagem;
	private boolean medicamentoControlado;
	private boolean vacina;
	private String complemento;
	private String passo;
	private String mensagemTipoPassoDois;
	private String mensagemDescricaoPassoDois;
	private List<RelatorioProdutoSeparacaoDTO> listProduto;
	private List<RelatorioProdutoSeparacaoDTO> listVacinas;
	private List<RelatorioProdutoSeparacaoDTO> listaControlados;
	private List<RelatorioProdutoSeparacaoDTO> listAntibioticos;
	private List<RelatorioProdutoSeparacaoDTO> listRefrigerados;
	private String codigoPedidoParceiro;
    private boolean pedidoQuatroPontoZero = false;
    private boolean receitaDigital = false;
	private String pacientes;
	private boolean pagamentoEmDinheiro = false;
	private String troco;
	private String valorTotalPedido;
}