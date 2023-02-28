package com.clique.retire.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtiquetaItensDTO implements Serializable {

	private static final long serialVersionUID = -7084295650102070151L;
	private Long idProduto;
	private Integer quantidade;
	private String lote;
}
