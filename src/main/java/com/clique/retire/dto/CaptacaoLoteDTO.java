package com.clique.retire.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class CaptacaoLoteDTO  {
	
	private Long idCapitacao;
	
	@NotNull
	private Long idPedido;

	@NotNull
	private Long idProduto;
	
	@NotNull
	private Long idLoja;
	
	@NotNull
	private Long codigoItemCR;

	@NotBlank
	@Size(max = 60)
	private String lote;

}
