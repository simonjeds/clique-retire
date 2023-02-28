package com.clique.retire.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoUzDTO {

	private List<PedidoProdutoUzDTO> data;
	private Integer count;

}
