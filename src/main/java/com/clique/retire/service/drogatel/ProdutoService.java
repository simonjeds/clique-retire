package com.clique.retire.service.drogatel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;

@Service
public class ProdutoService {

	private static final String BIPAGEM_DE_LOTE_OBRIGATORIA = "O item atual exige bipagem de lote.";
	private static final String QUANTIDADE_BIPADA_INVALIDA = "Verifique a quantidade bipada para o lote.";

	@Autowired
	private PedidoRepositoryCustom pedidoRepositoryCustom;

	public boolean validarBipagemExigeReceita(ItemPedidoDTO item) {
		boolean exigeReceita = pedidoRepositoryCustom.isProdutoExigeReceita(item.getCodigoProduto());

		if (exigeReceita) {
			if (StringUtils.isBlank(item.getLote())) {
				throw new BusinessException(BIPAGEM_DE_LOTE_OBRIGATORIA);
			}
			
			if (item.getQuantidadeSeparada() <= 0 ) {
				throw new BusinessException(QUANTIDADE_BIPADA_INVALIDA);
			}

		}

		return exigeReceita;
	}

	public boolean isProdutoGeladeira(Long numeroPedido) {
		return pedidoRepositoryCustom.isProdutoGeladeira(numeroPedido);
	}
}
