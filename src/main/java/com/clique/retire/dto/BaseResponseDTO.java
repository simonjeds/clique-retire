package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDTO {

	private Integer code;
	private String status;
	private String message;
	private Object data;
	
}
