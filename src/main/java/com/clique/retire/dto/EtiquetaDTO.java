package com.clique.retire.dto;

import java.io.Serializable;

import com.clique.retire.model.drogatel.Usuario;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EtiquetaDTO implements Serializable {
	
	private static final long serialVersionUID = -1689578224494435647L;
	
	@ApiModelProperty(value = "Numero de autorizacao ")
	private Long numeroAutorizacao;
	@ApiModelProperty(value = "Matricula do vendedor  ")
	private String matriculaVendedor;
	@ApiModelProperty(value = "Id do pedido ")
	private Long idPedido;

	private Usuario vendedor;
	private Usuario vendedorConferente;
	
}
