package com.clique.retire.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItensProdutoDTO {

	private List<ItemPedidoDTO> itens;

}