package com.clique.retire.service.drogatel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.PanelDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.repository.drogatel.IntegracaoConsultaPanelRepository;

@Service
public class PanelService {

	@Autowired
	private IntegracaoConsultaPanelRepository integracaoConsultaPanel;

	
	public List<PanelDTO>getPanelList(String polo) {
		List<PanelDTO> panelList = integracaoConsultaPanel.searchPanel(polo,"R");
		
		
		for (PanelDTO panel : panelList) {
			panel.setSituacaoPedido(
					FasePedidoEnum.buscarPorChave(panel.getSituacaoPedido()) == null ? panel.getSituacaoPedido() :
						FasePedidoEnum.buscarPorChave(panel.getSituacaoPedido()).getValor());
			if(panel.getDetalheErro() != null) {
				String treatData = treatDataDetalheErro(panel.getDetalheErro());
				panel.setDetalheErro(treatData);				
			}
		}
		return panelList;
	}
	
	private String treatDataDetalheErro(String data) {
		return data. replace("[{\\\"messagem\\\":\\\"", "").replace("\\\"}]", "").replace("\"", "");
	}
}
