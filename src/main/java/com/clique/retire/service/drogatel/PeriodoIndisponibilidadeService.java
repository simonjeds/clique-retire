package com.clique.retire.service.drogatel;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.infra.exception.ErroValidacaoException;
import com.clique.retire.infra.exception.ForbiddenException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.PeriodoIndisponibilidade;
import com.clique.retire.repository.drogatel.PeriodoIndisponibilidadeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PeriodoIndisponibilidadeService {

  private final PeriodoIndisponibilidadeRepository repository;
  private final ParametroService parametroService;

  public PeriodoIndisponibilidadeService(PeriodoIndisponibilidadeRepository repository, ParametroService parametroService) {
    this.repository = repository;
    this.parametroService = parametroService;
  }

  public void validarMatricula(Integer codigoUsuario) {
    DrogatelParametro parametro = parametroService.buscarPorChave(
      ParametroEnum.CLIQUE_RETIRE_PERIODO_INDISPONIBILIDADE_USUARIOS.getDescricao()
    );

    ForbiddenException forbiddenException = new ForbiddenException(
      "Usuário não possui permissão para acessar essa funcionalidade."
    );

    if (Objects.isNull(parametro) || Objects.isNull(parametro.getValor())) {
      throw forbiddenException;
    }

    List<String> codigos = Arrays.asList(parametro.getValor().split(";"));

    if (!codigos.contains(codigoUsuario.toString())) {
      throw forbiddenException;
    }
  }

  public List<PeriodoIndisponibilidade> listarTodos() {
    Sort sort = Sort.by(Sort.Direction.DESC, "dataHoraInicio");
    Long quantidadeMeses = this.getFiltroQuantidadeDeMeses();

    if (Objects.isNull(quantidadeMeses)) {
      return this.repository.findAll(sort);
    }

    LocalDateTime dataHoraInicioFiltro = LocalDateTime.now().minusMonths(quantidadeMeses);
    return this.repository.findByDataHoraInicioGreaterThan(dataHoraInicioFiltro, sort);
  }

  @Transactional("drogatelTransactionManager")
  public PeriodoIndisponibilidade salvar(PeriodoIndisponibilidade periodoIndisponibilidade) {
    log.info("Salvando PeriodoIndisponibilidade: {}", periodoIndisponibilidade);
    this.validarPeriodo(periodoIndisponibilidade);
    return this.repository.save(periodoIndisponibilidade);
  }

  @Transactional("drogatelTransactionManager")
  public void deletarPorId(Long id) {
    try {
      log.info("Removendo o PeriodoIndisponibilidade com id '{}'", id);
      this.repository.deleteById(id);
    } catch (EmptyResultDataAccessException exception) {
      throw new EntidadeNaoEncontradaException("Período não encontrado.");
    }
  }

  private void validarPeriodo(PeriodoIndisponibilidade periodo) {
    if (
      periodo.getDataHoraFim().isBefore(periodo.getDataHoraInicio()) ||
      periodo.getDataHoraFim().isEqual(periodo.getDataHoraInicio())
    ) {
      throw new ErroValidacaoException("Data/hora de fim do período deve ser maior que a Data/hora de início.");
    }
  }

  public Long getFiltroQuantidadeDeMeses() {
    DrogatelParametro parametro = parametroService.buscarPorChave(
      ParametroEnum.CLIQUE_RETIRE_PERIODO_INDISPONIBILIDADE_FILTRO.getDescricao()
    );

    if (Objects.isNull(parametro) || StringUtils.isBlank(parametro.getValor())) {
      return null;
    }

    return Long.parseLong(parametro.getValor());
  }

}
