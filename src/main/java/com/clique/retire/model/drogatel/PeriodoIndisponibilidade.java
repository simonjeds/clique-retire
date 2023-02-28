package com.clique.retire.model.drogatel;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.SistemaEnum;
import com.clique.retire.enums.TipoOcorrenciaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "DRGTBLPEIPERIODOINDISP")
@SQLDelete(sql = "UPDATE DRGTBLPEIPERIODOINDISP SET PEISTAATIVO = 'N' WHERE PEISEQ = ?")
@Where(clause = "PEISTAATIVO = 'S'")
public class PeriodoIndisponibilidade extends BaseEntityAraujo {
  private static final long serialVersionUID = 1L;

  public static final String FORMATO_DATA = "dd/MM/yyyy HH:mm";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "PEISEQ")
  private Long id;

  @Column(name = "PEIDTHINICIO")
  private LocalDateTime dataHoraInicio;

  @Column(name = "PEIDTHFIM")
  private LocalDateTime dataHoraFim;

  @Column(name = "PEIQTDDURACAOSEG")
  private Long duracaoSegundos;

  @Column(name = "PEIDESMOTIVO")
  private String motivo;

  @Builder.Default
  @Column(name = "PEITIPOCORRENCIA")
  private TipoOcorrenciaEnum tipoOcorrencia = TipoOcorrenciaEnum.MANUTENCAO;

  @Builder.Default
  @Column(name = "PEISIGSISTEMA")
  private SistemaEnum sistema = SistemaEnum.CLIQUE_RETIRE;

  @Builder.Default
  @Column(name = "PEISTAATIVO")
  @Enumerated(EnumType.STRING)
  private SimNaoEnum ativo = SimNaoEnum.S;

  public PeriodoIndisponibilidade(String codigoUsuario) {
    super(codigoUsuario);
  }

}
