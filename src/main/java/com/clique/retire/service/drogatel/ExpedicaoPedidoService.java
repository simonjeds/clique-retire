package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.PedidoServico;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.drogatel.ExpedicaoPedidoRepository;
import com.clique.retire.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExpedicaoPedidoService {

	@Autowired
	private ExpedicaoPedidoRepository repository;
	
	public ExpedicaoPedido finalizarExpedicaoPedidoMaisRecente(PedidoServico pedidoServico) {
		Optional<ExpedicaoPedido> optionalEP = obterExpedicaoPedidoMaisRecente(pedidoServico);
		if (optionalEP.isPresent()) {
			log.info("Finalizando a expedição mais recente do pedido de serviço {}", pedidoServico.getCodigo());
			Date dataAtual = new Date();
			
			ExpedicaoPedido expedicaoPedido = optionalEP.get();
			expedicaoPedido.setIndicadorRetorno(SimNaoEnum.S);
			expedicaoPedido.setDataHoraRealEntrega(dataAtual);
			expedicaoPedido.setUltimaAlteracao(dataAtual);
		    expedicaoPedido.getExpedicao().setDataHoraRetorno(dataAtual);
		    expedicaoPedido.getExpedicao().setResponsavelRetorno(new Usuario(Constantes.USUARIO_ADMINISTRADOR));
		    expedicaoPedido.getExpedicao().setUltimaAlteracao(dataAtual);

		    repository.save(expedicaoPedido);
		    
		    return expedicaoPedido;
		}
		return null;
	}
	
	public Optional<ExpedicaoPedido> obterExpedicaoPedidoMaisRecente(PedidoServico pedidoServico) {
		return repository.findByPedidoServicoAndIndicadorRetornoOrderByCodigoDesc(pedidoServico, SimNaoEnum.N)
						 .stream()
						 .findFirst();
	}
}
