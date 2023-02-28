package com.clique.retire.service.drogatel;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.util.FeignUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class SinalizadorService {

	public static final String COR_PRETA = "#000000";
	public static final String COR_VERDE = "#00FF00";

	private final DrogatelParametroRepository parametroRepository;

	public void sinalizarLojaLuxaFor(Integer filial, Integer novosPedidos) {
		DrogatelParametro parametro = parametroRepository.findByNome(ParametroEnum.URL_BASE_CLIQUE_RETIRE_SCHEDULE.getDescricao());

		if (novosPedidos == 0) {
			FeignUtil.getScheduleClient(parametro.getValor()).atualizarLedFilial(filial);
			return;
		}

		log.info("Sinalizando loja '{}' com '{}' novos pedidos.", filial, novosPedidos);
		FeignUtil.getScheduleClient(parametro.getValor()).atualizarCorSinalizador(COR_VERDE, filial);
	}

}
