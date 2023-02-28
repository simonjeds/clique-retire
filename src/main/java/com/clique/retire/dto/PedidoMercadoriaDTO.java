package com.clique.retire.dto;

import java.util.Date;
import java.util.List;

import com.clique.retire.enums.FasePedidoEnum;

import lombok.Data;

@Data
public class PedidoMercadoriaDTO {

	private Integer codigo;
	private Date dataAlteracao;
	private FasePedidoEnum fase;
	private List<ItemPedidoMercadoriaDTO> itens;
}
