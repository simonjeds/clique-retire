package com.clique.retire.dto;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.SistemaEnum;
import com.clique.retire.enums.TipoOcorrenciaEnum;
import com.clique.retire.model.drogatel.PeriodoIndisponibilidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodoIndisponibilidadeDTO {

  private Long id;

  @NotNull
  @JsonFormat(pattern = PeriodoIndisponibilidade.FORMATO_DATA)
  private LocalDateTime dataHoraInicio;

  @NotNull
  @JsonFormat(pattern = PeriodoIndisponibilidade.FORMATO_DATA)
  private LocalDateTime dataHoraFim;

  @NotBlank
  private String motivo;

  public PeriodoIndisponibilidadeDTO(PeriodoIndisponibilidade periodoIndisponibilidade) {
    this.id = periodoIndisponibilidade.getId();
    this.dataHoraInicio = periodoIndisponibilidade.getDataHoraInicio();
    this.dataHoraFim = periodoIndisponibilidade.getDataHoraFim();
    this.motivo = periodoIndisponibilidade.getMotivo();
  }

  public PeriodoIndisponibilidade toEntity(Integer codigoUsuario) {
    PeriodoIndisponibilidade periodo = new PeriodoIndisponibilidade(codigoUsuario.toString());
    periodo.setId(id);
    periodo.setDataHoraInicio(dataHoraInicio);
    periodo.setDataHoraFim(dataHoraFim);
    periodo.setMotivo(motivo);
    periodo.setDuracaoSegundos(ChronoUnit.SECONDS.between(dataHoraInicio, dataHoraFim));
    periodo.setTipoOcorrencia(TipoOcorrenciaEnum.MANUTENCAO);
    periodo.setSistema(SistemaEnum.CLIQUE_RETIRE);
    periodo.setAtivo(SimNaoEnum.S);
    return periodo;

  }

}
