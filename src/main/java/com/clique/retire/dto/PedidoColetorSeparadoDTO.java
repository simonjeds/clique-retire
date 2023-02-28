package com.clique.retire.dto;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoColetorSeparadoDTO {

	private Integer numeroPedido;
	private List<ItemPedidoColetorSeparadoDTO> itemPedidoColetorSeparadoDTO;
}
