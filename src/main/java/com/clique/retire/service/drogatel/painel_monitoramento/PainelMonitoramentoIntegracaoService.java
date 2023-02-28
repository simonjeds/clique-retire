package com.clique.retire.service.drogatel.painel_monitoramento;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.painel_monitoramento.IdentificacaoDTO;
import com.clique.retire.dto.painel_monitoramento.PainelMonitoramentoIntegracaoDTO;
import com.clique.retire.dto.painel_monitoramento.ReprocessamentoDTO;
import com.clique.retire.repository.drogatel.painel_monitoramento.PainelMonitoramentoIntegracaoRepository;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PainelMonitoramentoIntegracaoService {	
	
	@Autowired
	private PainelMonitoramentoIntegracaoRepository painelMonitoramentoIntegracao;
	
	public void integraPanelMonitoramentoAraujo(String urlRequest, String passo, String error, String message, String numeroPedido) {
		try {

			PainelMonitoramentoIntegracaoDTO painelMonitoramentoIntegracaoDTO = 
					this.populaPainelMonitoramentoIntegracao(urlRequest, passo, error, message, numeroPedido);

			Gson gson = new Gson();
			String painelMonitoramentoIntegracaoJson = gson.toJson( painelMonitoramentoIntegracaoDTO );
			
			PainelMonitoramentoIntegracaoDTO panelAraujo = this.painelMonitoramentoIntegracao.integrationPanelAraujo("VendasDelivery", painelMonitoramentoIntegracaoJson);
			log.info(panelAraujo);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}	
	}
	
	
	private PainelMonitoramentoIntegracaoDTO populaPainelMonitoramentoIntegracao(String urlRequest, String passo, String error, String message, String numeroPedido) {
		ReprocessamentoDTO reprocessamento;
	    LocalDateTime data = LocalDateTime.now();
	    
	    if(message == null) {
	    	reprocessamento = new ReprocessamentoDTO(Boolean.FALSE, "", "", "erro");
	    }else {
	    	reprocessamento= new ReprocessamentoDTO(Boolean.TRUE, "", urlRequest, message);
	    }  	
	    
	    return new PainelMonitoramentoIntegracaoDTO(
	    		new IdentificacaoDTO("Proceso Drogatel", numeroPedido),
	    		reprocessamento,
	    		"INICIADO", 
	    		passo, 
	    		"FALHA", 
	    		error, 
	    		"", 
	    		data.toString(), 
	    		data.toString(),  
	    		Integer.getInteger("1"), 
	    		Boolean.FALSE
	    ); 
	}
}
