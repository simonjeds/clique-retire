package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.StatusPrePedidoSiacEnum;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.model.drogatel.RegistroPedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.cosmos.PrePedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.RegistroPedidoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistroPedidoService {

	@Autowired
	private RegistroPedidoRepository repository;

	@Autowired
	private SiacService siacService;

	@Autowired
	private PedidoRepositoryCustom pedidoCustomRepository;

	@Autowired
	private PrePedidoRepositoryCustom prePedidoCustomRepository;

	@Autowired
	private CupomFiscalService cupomFiscalService;

	@Transactional(value = "drogatelTransactionManager")
	public void cadastrarRegistroECupomPedidoSIAC(Pedido pedido, Integer codigoUsuario) {
		Usuario usuario = new Usuario();
		usuario.setCodigoUsuario(codigoUsuario);

		RegistroPedido registro = null;
		Integer idUltimoRegistro = null;

		for (Entry<String, Set<ItemPedido>> itens : siacService.obterChavePrePedido(pedido).entrySet()) {
			
			registro = new RegistroPedido(codigoUsuario.toString());
			registro.setAguardandoConfirmacaoDoSenf(SimNaoEnum.S);
			registro.setResponsavelECF(usuario);
			registro.setCodigoFilial(pedido.getPolo().getCodigo());
			registro.setStatusPrePedidoSIAC(StatusPrePedidoSiacEnum.N);
			registro.setPedido(pedido);
			registro.setResponsavelRegistro(usuario);
			registro.setDataInicioRegistro(new Date());
			registro.setRegistroLoja(SimNaoEnum.S);
			registro.setResponsavelRegistroCodigo(codigoUsuario.longValue());
			registro = repository.save(registro);
	
			idUltimoRegistro = registro.getCodigo();
			
			log.info("Foi gerado o registro do pedido identificado pelo número {} referente ao pedido {}.", 
					 idUltimoRegistro, pedido.getNumeroPedido());

			cupomFiscalService.cadastrarAtualizarCupomFiscal(itens.getValue(), idUltimoRegistro, codigoUsuario);
		}

		pedidoCustomRepository.atualizarUltimoCodigoRegistro(idUltimoRegistro, pedido.getNumeroPedido().intValue());
	}
	
	public void atualizarFasePrePedido(Long numeroPedido) {
		buscarPrePedidoSiac(numeroPedido).forEach(prePedidoSiac -> {
			String codigoOrigemPrePedido = prePedidoCustomRepository.buscarCodigoPrePedidoOrigem(prePedidoSiac.getNumeroPrePedido(),
																								 prePedidoSiac.getCodigoFilial());
			if (Objects.nonNull(codigoOrigemPrePedido)) {
				log.info("O pré-pedido identificado pelo código origem {}, referente ao pedido {}, será atualizado para a fase EM SEPARAÇÃO.", codigoOrigemPrePedido.trim(), numeroPedido);
				pedidoCustomRepository.atualizarFasePrePedido(codigoOrigemPrePedido.trim());
			}
		});
	}
	
	public List<PrePedidoSiac> buscarPrePedidoSiac(Long numeroPedido) {
		return pedidoCustomRepository.buscarPrePedidoSiac(Math.toIntExact(numeroPedido));
	}

	public RegistroPedidoService(RegistroPedidoRepository repository) {
		this.repository = repository;
	}

	@Transactional("drogatelTransactionManager")
	public void finalizarRegistroPedido(Integer numeroPedido) {
		List<RegistroPedido> registrosPedido = this.repository.findByNumeroPedido(numeroPedido.longValue());

		registrosPedido.forEach(registroPedido -> {
			registroPedido.setDataFimRegistro(new Date());
			registroPedido.setAguardandoConfirmacaoDoSenf(SimNaoEnum.N);
		});

		this.repository.saveAll(registrosPedido);
	}

}
