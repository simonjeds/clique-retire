package com.clique.retire.dto;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.clique.retire.enums.FasePedidoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEntregaDTO {

	private Integer id;
	private String codigoVTEX;
	private String nomeCliente;
	private String cpf;
	private String nomeFilial;
	private boolean mesmaFilial;
	private FasePedidoEnum fase;
	private Integer filial;
	private String tipoRetirada;
	private String tipoPedido;
	private String telefone;
	private boolean controlado;
	private boolean antibiotico;
	private boolean superVendedor;
	private boolean possuiCaptacao;
	private String mensagemErro;
	private boolean nfEmitida;
	private boolean autorizacaoGerada;
	private boolean quatroPontoZero;
	private boolean receitaDigital;
	private boolean pagamentoEmDinheiro;
	private String prePedido;
	private boolean isGeladeira;

	public String getDescricaoFase() {
		if (Objects.isNull(fase)) return null;

		return !StringUtils.isEmpty(fase.getDescricaoCombo()) ? fase.getDescricaoCombo() : fase.getValor();
	}

	public boolean isPossuiPin() {
		return StringUtils.isNotBlank(codigoVTEX) && codigoVTEX.toUpperCase().contains("RPP");
	}

}
