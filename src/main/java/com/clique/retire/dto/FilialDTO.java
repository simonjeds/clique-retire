package com.clique.retire.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilialDTO  implements Serializable{
	
	private static final long serialVersionUID = -7084295650102070151L;
	
	private Integer id;
	private String nome;
	private String ddd;
	private String telefone;
	private String endereco;
	private String bairro;
	private String cidade;
	private String siglaEstado;
	private String cep;
	private String cnpj;
	
	public static FilialDTO filialSemDados(Integer idFilial) {
		return FilialDTO.builder()
					    .id(idFilial).nome("").ddd("").telefone("")
					    .endereco("").bairro("").cidade("").cep("").siglaEstado("")
					    .cnpj("")
					    .build();
		
	}
	
}
