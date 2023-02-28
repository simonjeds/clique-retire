package com.clique.retire.dto;

import java.util.List;
import java.util.Objects;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.TipoEdicaoPedido;
import com.clique.retire.enums.TipoPagamentoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"tiposPagamentoEnum", "tipoFrete"})
public class PedidoFaltaDTO {

	private Long codigo;
	private String nomeCliente;
	private FasePedidoEnum fase;
	private List<ProdutoFaltaDTO> produtos;
	private Integer codigoFilial;
	private String tipoPedido;
	private TipoEdicaoPedido tipoApontamento;
	private List<TipoPagamentoEnum> tiposPagamentoEnum;
	private boolean permiteApontamentoFalta;
	private Integer tipoFrete;
	private String documento;
	private boolean falhaSinalizacaoZeroBalcao;
	private boolean isContemPBM;

	public boolean isPagamentoAntecipado() {
		return TipoPedidoEnum.ARAUJOTEM.getChave().equals(tipoPedido) /*||
				TipoPagamentoEnum.isPagamentoAntecipado(tiposPagamentoEnum)*/;
	}

	public String[] getTiposPagamento() {
		return Objects.nonNull(tiposPagamentoEnum)
		   		? tiposPagamentoEnum.stream().map(TipoPagamentoEnum::getDescricao).toArray(String[]::new)
				: null;
	}

}
