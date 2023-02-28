package com.clique.retire.service.drogatel;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.clique.retire.client.rest.DrogatelClient;
import com.clique.retire.dto.CancelamentoPedidoDrogatelDTO;
import com.clique.retire.dto.MovimentoPedidoDrogatelDTO;
import com.clique.retire.dto.RespostaPedidoDrogatelDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.util.FeignUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrogatelService {

    private final ParametroService parametroService;

    public RespostaPedidoDrogatelDTO cancelarPedido(CancelamentoPedidoDrogatelDTO pedido) {
        DrogatelClient client = obterClient(ParametroEnum.ENDPOINT_CANCELAMENTO_PEDIDO);
        return resposta(client.cancelarPedido(pedido));
    }

	public RespostaPedidoDrogatelDTO movimentacaoPedidoEcommerce(MovimentoPedidoDrogatelDTO pedido) {
        DrogatelClient client = obterClient(ParametroEnum.ENDPOINT_MOVIMENTACAO_PEDIDO_ECOMMERCE);
        return resposta(client.movimentacaoPedidoEcommerce(pedido));
    }
	
	public boolean criarPedidoDeServico(Long numeroPedido) {
		String url = parametroService.buscarPorChave(ParametroEnum.ENDPOINT_CRIAR_PEDIDO_SERVICO)
									 .replace("{numeroPedido}", numeroPedido.toString());
        return Objects.nonNull(FeignUtil.getDrogatelClient(url).criarPedidoDeServico().body());
    }
	
	public boolean sinalizarApontamentoFaltaZeroBalcao(Integer idFilial, Integer codigoProdudo, Integer idUsuario) {
		String url = parametroService.buscarPorChave(ParametroEnum.ENDPOINT_ZERO_BALCAO)
				 					 .replace("{produtoMestre}", codigoProdudo.toString())
				 					 .replace("{filialProduto}", idFilial.toString())
				 					 .replace("{usuarioResponsavel}", idUsuario.toString());
		
		RespostaPedidoDrogatelDTO dto = null;
		try {
			dto = FeignUtil.getDrogatelClient(url).sinalizarApontamentoFaltaZeroBalcao(); 
			log.info(dto.getMensagem() + " Produto {} / Filial {}. ", codigoProdudo, idFilial);
		} catch (Exception e) {
			log.info("Falha na comunicação ZERO BALCÃO referente ao apontamento de falta. Produto {} / Filial {}. ", codigoProdudo, idFilial);
		}
		return Objects.nonNull(dto) && dto.getSucesso();
    }
    
    private DrogatelClient obterClient(ParametroEnum parametro) {
    	String url = parametroService.buscarPorChave(parametro);
		return FeignUtil.getDrogatelClient(url);
	}
    
    private RespostaPedidoDrogatelDTO resposta(RespostaPedidoDrogatelDTO respostaDrogatel) {
    	String mensagem = respostaDrogatel.getMensagem();
    	if (mensagem == null) 
    		return respostaDrogatel;
    	
    	String exception = "Exception:";
        int indiceFimExcecao = mensagem.lastIndexOf(exception);
    	
        respostaDrogatel.setMensagem(mensagem.substring(indiceFimExcecao + exception.length()));
        return respostaDrogatel;
	}
}
