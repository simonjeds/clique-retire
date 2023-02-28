package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemPendenteNegociarDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.ProcessoEnum;
import com.clique.retire.model.drogatel.HistoricoFasePedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.HistoricoFasePedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;

@Service
public class FasePedidoService {

	@Autowired
	private PedidoRepositoryCustom repositoryCustom;

	@Autowired
	private HistoricoFasePedidoRepository historicoFaseRepository;

	public FasePedidoEnum obtemNovaFasePedido(Pedido pedido, ProcessoEnum processo) {
		FasePedidoEnum fase = null;

		switch (processo) {
		case INICIAR_SEPARACAO:
			fase = FasePedidoEnum.EM_SEPARACAO;
			break;
		case FINALIZAR_SEPARACAO:

			if (FasePedidoEnum.AGUARDANDO_REGISTRO.equals(pedido.getFasePedido())
					&& pedido.getNumeroPedidoServico() != null) {
				fase = FasePedidoEnum.EM_REGISTRO;
			} else {
				List<ItemPendenteNegociarDTO> itensPendentes = repositoryCustom
						.obterItensPendentesANegociar(pedido.getNumeroPedido());

				if (itensPendentes != null && !itensPendentes.isEmpty()) {
					fase = FasePedidoEnum.AGUARDANDO_NEGOCIACAO;
				} else if (repositoryCustom.isPedidoComItensAgMercadoria(pedido.getNumeroPedido())) {
					fase = FasePedidoEnum.AGUARDANDO_MERCADORIA;
				} else if (repositoryCustom.isPedidoExigeReceita(pedido.getNumeroPedido())
						&& pedido.getNumeroPedidoServico() == null) {
					fase = FasePedidoEnum.AGUARDANDO_RECEITA;
				} else {
					fase = FasePedidoEnum.EM_REGISTRO;
				}
			}
			break;

		default:
			break;
		}

		return fase;
	}

	@Transactional("drogatelTransactionManager")
	public void registrarNovaFasePedido(Pedido pedido, ProcessoEnum processo, FasePedidoEnum faseAtual,
			Long codigoUsuario, Integer codigoPolo) {
		FasePedidoEnum novaFase = obtemNovaFasePedido(pedido, processo);

		salvarHistoricoFasePedido(pedido, novaFase, faseAtual, codigoUsuario, codigoPolo);
		pedido.setFasePedido(novaFase);
		repositoryCustom.alterarFasePedido(pedido.getNumeroPedido(), novaFase);
	}

	private void salvarHistoricoFasePedido(Pedido pedido, FasePedidoEnum novaFase, FasePedidoEnum faseAtual, Long codigoUsuario, Integer codigoPolo) {
		if (!novaFase.equals(faseAtual)) {
			HistoricoFasePedido historicoFase = new HistoricoFasePedido(codigoUsuario.toString());
			historicoFase.setFasePedidoAtual(novaFase);
			historicoFase.setCodigoPolo(codigoPolo);
			historicoFase.setPedido(pedido);
			historicoFase.setDataEntrouFaseAtual(new Date());
			historicoFaseRepository.save(historicoFase);
		}
	}

}
