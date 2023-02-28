package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.ResponseSAPConsultaApiDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "integracaoPlataformaVendasDelivery", url = "${cv.integracao.api}")
public interface IntegracaoCanaisVendasRepository {
	
		@GetMapping(value="consulta-status-grc?NumeroPedido={NumeroPedido}")
		ResponseSAPConsultaApiDTO statusContingencia(@PathVariable("NumeroPedido") Integer numeroPedido);
		
		@PostMapping(value = "OrdemVendaDelivery/confirmar-pagamento", consumes = MediaType.APPLICATION_JSON_VALUE)
		ResponseSAPConsultaApiDTO confirmarPagamento(@PathVariable("NumeroPedido") String numeroPedido);
		
		@PostMapping(value = "OrdemVendaDelivery", consumes = MediaType.APPLICATION_JSON_VALUE)
		ResponseSAPConsultaApiDTO emitirOrdemVenda(@PathVariable("NumeroPedido") String numeroPedido);
}
