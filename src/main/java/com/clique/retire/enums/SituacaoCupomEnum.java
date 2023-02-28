package com.clique.retire.enums;

import lombok.Getter;

public enum SituacaoCupomEnum {

	E("Emitido"),
	C("Cancelado");
	
	@Getter
	private String descricao;
	 
	private SituacaoCupomEnum(String descricao) {		    
        this.descricao = descricao;	        
	}
}
