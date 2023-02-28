package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.PBMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "rest/pbm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PBMController extends BaseController {

  private final PBMService service;

  @PutMapping("refazer-autorizacao")
  public ResponseEntity<BaseResponseDTO> regerarAutorizacaoPbm(@RequestParam Long numeroPedido) {
    return ok(this.service.regerarAutorizacaoPedido(numeroPedido));
  }

}
