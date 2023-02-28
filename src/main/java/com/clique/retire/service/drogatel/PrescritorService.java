package com.clique.retire.service.drogatel;

import com.clique.retire.dto.MedicoDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.util.FeignUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PrescritorService extends GeraToken {

  private static final String PRESCRITOR_NAO_ENCONTRADO = "Prescritor não encontrado";

  private final DrogatelParametroRepository drogatelParametroRepository;

  @Transactional("drogatelTransactionManager")
  public MedicoDTO consultaPrescritor(String conselho, String ufRegistro, Integer numeroRegistro) {
    try {
      DrogatelParametro parametro = drogatelParametroRepository.findByNome(
        ParametroEnum.APPARAUJO_COM_BR.getDescricao()
      );
      MedicoResponseDTO response = FeignUtil.getPrescritorClient(parametro.getValor())
        .getMedico(conselho, ufRegistro, numeroRegistro, getAuthToken()
      );

      if (response == null) {
        throw new BusinessException(PRESCRITOR_NAO_ENCONTRADO);
      }

      return response.getData();
    } catch (Exception e) {
      log.error("Erro ao consultar médico.", e);
      throw new BusinessException(PRESCRITOR_NAO_ENCONTRADO);
    }
  }

  @Getter
  @Setter
  public static class MedicoResponseDTO {
    private MedicoDTO data;
  }

}
