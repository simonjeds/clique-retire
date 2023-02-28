package com.clique.retire.service.drogatel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.util.FirebaseUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CronometroService {

  private final ParametroService parametroService;
  private final FirebaseUtil firebaseUtil;

  public CronometroService(ParametroService parametroService, FirebaseUtil firebaseUtil) {
    this.parametroService = parametroService;
    this.firebaseUtil = firebaseUtil;
  }

  public void atualizarDadosCronometro(Integer codigoLoja) {
    DrogatelParametro parametro = parametroService.buscarPorChave(ParametroEnum.CLIQUE_RETIRE_CRONOMETRO.getDescricao());
    if (parametro != null && StringUtils.isNotEmpty(parametro.getValor())) {
      try {
        CronometroParametros parametros = new ObjectMapper().readValue(parametro.getValor(), CronometroParametros.class);
        List<Integer> lojas = parametros.getLojas();

        if (lojas.contains(codigoLoja) || lojas.isEmpty()) {
          List<String> funcionalidades = this.montarCorpoFuncionalidesParaFirebase();
          this.firebaseUtil.atualizarCronometro(codigoLoja, parametros, funcionalidades);
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private List<String> montarCorpoFuncionalidesParaFirebase() {
    DrogatelParametro parametro = parametroService
      .buscarPorChave(ParametroEnum.CLIQUE_RETIRE_CRONOMETRO_FUNCIONALIDADES_BLOQUEADAS.getDescricao());

    if (Objects.nonNull(parametro) && StringUtils.isNotEmpty(parametro.getValor())) {
      Stream<String> todasFuncionalidades = Arrays.stream(parametro.getObservacao().split(";"));
      List<String> funcionalidadesBloqueadas = Arrays.asList(parametro.getValor().split(";"));

      return todasFuncionalidades.map(
        funcionalidade ->  new Gson().toJson(CliqueRetireFuncionalidade.builder()
          .id(funcionalidade)
          .ativo(!funcionalidadesBloqueadas.contains(funcionalidade))
          .build())
      ).collect(Collectors.toList());
    }

    return null;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CronometroParametros {
    private String dataInicio;
    private String dataFim;
    private String mensagem;
    private String dataFimManutencao;
    private String mensagemManutencao;
    private List<Integer> lojas;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CliqueRetireFuncionalidade {
    private String id;
    private boolean ativo;
  }

}
