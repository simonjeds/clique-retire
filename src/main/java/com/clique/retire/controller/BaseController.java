package com.clique.retire.controller;

import static com.clique.retire.util.PbmUtils.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ErroPBMResponseDTO;

public class BaseController {
    
	protected static final String ERROR = "error";
	protected static final String SUCCESS = "success";
	protected static final String OCORREU_UM_ERRO = "Ocorreu um erro desconhecido. Contate o administrador do sistema.";
	protected static final String ERRO_PBM = "Erro relacionado a PBM. Será necessário refazer a autorização. Favor entra em contato com o SAC no telefone 3270-5000, opção 2.";
	
	protected Integer getCodigoUsuarioLogado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return Integer.valueOf((String)authentication.getPrincipal()); 
	}
	
	protected Integer getCodigoUsuarioCosmosTokenUsuarioSenf() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String codigos = (String)authentication.getPrincipal();
		return Integer.valueOf(codigos.split(";")[1]); 
	}
	
	protected Integer getCodigoUsuarioSenf() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String codigos = (String)authentication.getPrincipal();
		String idUsuario = codigos.split(";")[0];
		return Integer.valueOf(idUsuario);
	}
	
	protected ResponseEntity<BaseResponseDTO> ok(Object data) {
		return sucesso(HttpStatus.OK.value(), data);
	}
	
	protected ResponseEntity<BaseResponseDTO> accepted(Object data) {
		return sucesso(HttpStatus.ACCEPTED.value(), data);
	}

	protected ResponseEntity<BaseResponseDTO> erro(Object data) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), data);
	}

	protected ResponseEntity<BaseResponseDTO> sucesso(Integer codeStatus, Object data) {
		BaseResponseDTO response = new BaseResponseDTO();
		response.setCode(codeStatus);
		response.setData(data);
		response.setMessage(SUCCESS);
		return ResponseEntity.ok(response);
	}

	private ResponseEntity<BaseResponseDTO> error(Integer codeStatus, Object data) {
		BaseResponseDTO response = new BaseResponseDTO();
		response.setCode(codeStatus);
		response.setData(data);
		response.setMessage(ERROR);
		return ResponseEntity.status(codeStatus).body(response);
	}
	
	protected ErroPBMResponseDTO montarRetornoErroPBM(String mensagem) {
		return ErroPBMResponseDTO.builder()
			.erroPbm(isErroPBMExcetoCartaoRoubacoExtraviado(mensagem))
			.mensagem(isErroPBMCartaoRoubacoExtraviado(mensagem) ? ERRO_PBM : mensagem)
			.build();
	}
}
