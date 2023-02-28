package com.clique.retire.enums;

import lombok.Getter;

public enum StatusPrePedidoSiacEnum {
	E("Erro"),
	F("Fechado"),
	A("Andamento"),
	N("Novo");
	
	@Getter
	private String descricao;
	 
	private StatusPrePedidoSiacEnum(String descricao) {		    
        this.descricao = descricao;	        
	}
}
