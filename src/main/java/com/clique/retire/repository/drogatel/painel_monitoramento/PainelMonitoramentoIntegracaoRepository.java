package com.clique.retire.repository.drogatel.painel_monitoramento;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clique.retire.dto.painel_monitoramento.PainelMonitoramentoIntegracaoDTO;

@FeignClient(name = "integracaoConsultaPanel", url = "${cipa.integracao.api}")
public interface PainelMonitoramentoIntegracaoRepository {
		
	@PostMapping(value="processos/nome/{nome}/interacao", consumes = MediaType.APPLICATION_JSON_VALUE)
	PainelMonitoramentoIntegracaoDTO integrationPanelAraujo(@PathVariable("nome") String nome, @RequestBody String painelMonitoramentoIntegracao);

}
