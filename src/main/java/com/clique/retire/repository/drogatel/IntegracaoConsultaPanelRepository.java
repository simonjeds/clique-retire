package com.clique.retire.repository.drogatel;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clique.retire.dto.PanelDTO;

@FeignClient(name = "integracaoConsultaPanel", url = "${cp.consulta.api}")
public interface IntegracaoConsultaPanelRepository {

	@GetMapping("consulta-painel-operacional?Polo={Polo}&tipoEntrega={tipoEntrega}")
	List<PanelDTO> searchPanel(@PathVariable("Polo") String polo, @PathVariable("tipoEntrega") String tipoEntrega);

}
