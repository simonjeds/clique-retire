package com.clique.retire.infra.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.clique.retire.dto.BaseResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  private static final String ERROR = "error";
  private static final String UNKNOWN_ERROR = "Ocorreu um erro desconhecido. Contate o administrador do sistema.";

  @ExceptionHandler(EntidadeNaoEncontradaException.class)
  public ResponseEntity<BaseResponseDTO> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException exception) {
    return this.buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(ErroValidacaoException.class)
  public ResponseEntity<BaseResponseDTO> handleValidacaoException(ErroValidacaoException exception) {
    return this.buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }
  
  @ExceptionHandler(ConflitoException.class)
  public ResponseEntity<BaseResponseDTO> handleConflitoException(ConflitoException exception) {
    return this.buildResponse(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<BaseResponseDTO> handleForbiddenException(ForbiddenException exception) {
    return this.buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    List<ProblemDetails.Error> problemErrors = exception.getBindingResult().getAllErrors().stream()
      .map(objectError -> {
        String name = objectError.getObjectName();
        if (objectError instanceof FieldError) {
          name = ((FieldError) objectError).getField();
        }

        return ProblemDetails.Error.builder()
          .name(name)
          .message(objectError.getDefaultMessage())
          .build();
      })
      .collect(Collectors.toList());

    return this.buildResponse(HttpStatus.BAD_REQUEST, problemErrors);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<BaseResponseDTO> handleBusinessException(BusinessException exception) {
    return this.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponseDTO> handleException(Exception exception) {
    log.error(exception.getMessage(), exception);
    return this.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
  }

  private ResponseEntity<BaseResponseDTO> buildResponse(HttpStatus status, Object data) {
    int statusCode = status.value();
    BaseResponseDTO response = new BaseResponseDTO(statusCode, status.toString(), ERROR, data);
    return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
  }

}
