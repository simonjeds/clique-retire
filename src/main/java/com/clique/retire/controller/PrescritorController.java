package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.PrescritorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/rest/prescritor", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "medico", produces = "application/json")
@RequiredArgsConstructor
public class PrescritorController extends BaseController {

  private final PrescritorService service;

  @GetMapping
  @ApiOperation(value = "Busca a situacao do medico", notes = "Recupera a situacao do medico")
  public ResponseEntity<BaseResponseDTO> getPrescritor(
    @RequestParam String conselho, @RequestParam String ufRegistro, @RequestParam Integer numeroRegistro
  ) {
    return ok(service.consultaPrescritor(conselho, ufRegistro, numeroRegistro));
  }

}
