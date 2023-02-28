package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.clique.retire.dto.CancelamentoPedidoControladoDTO;
import com.clique.retire.dto.CancelamentoPedidoDTO;
import com.clique.retire.dto.CancelamentoPedidoDrogatelDTO;
import com.clique.retire.dto.RespostaPedidoDrogatelDTO;
import com.clique.retire.dto.SolicitacaoCancelamentoPedidoDTO;
import com.clique.retire.enums.FirebaseFieldEnum;
import com.clique.retire.model.drogatel.CancelamentoPedido;
import com.clique.retire.model.drogatel.MotivoDrogatel;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.model.enums.TipoTaxaEntregaEnum;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.CancelamentoPedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.UsuarioRepository;
import com.clique.retire.service.cosmos.UsuarioCosmosService;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.util.StringUtil;
import com.clique.retire.util.WebUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CancelamentoPedidoService {

    private final FilialService filialService;
    private final DrogatelService drogatelService;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CancelamentoPedidoRepository repository;
    private final UsuarioCosmosService usuarioCosmosService;
    private final MotivoDrogatelService motivoDrogatelService;
    private final PedidoRepositoryCustom pedidoRepositoryCustom;
    private final ControleIntranetRepositoryCustom controleIntranetRepository;
    private final AutorizacaoSuperVendedorService autorizacaoSuperVendedorService;
    private final ReceitaProdutoControladoService receitaProdutoControladoService;
    private final IMGService imgService;
    private final PendenciaPedidoService pendenciaPedidoService;

    public CancelamentoPedidoDTO buscarPedidoCancelamentoPorUsuarioOuFilial() {
        Integer codigoUsuario = SecurityUtils.getCodigoUsuarioLogado();
        Integer filial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
        if (Objects.isNull(filial)) return null;

        Optional<CancelamentoPedido> cancelamentoPedidoOptional = repository.findByUsuario(codigoUsuario, filial);

        if (cancelamentoPedidoOptional.isPresent()) {
            CancelamentoPedido cancelamentoPedido = cancelamentoPedidoOptional.get();
            return CancelamentoPedidoDTO.builder()
                    .numeroPedido(cancelamentoPedido.getPedido().getNumeroPedido().intValue())
                    .nomeCliente(cancelamentoPedido.getPedido().getCliente().getNome())
                    .build();
        }

        Optional<CancelamentoPedidoDTO> cancelamentoPedidoDTOOptional = repository.buscarPedidoCancelamentoPorFilial(filial);

        cancelamentoPedidoDTOOptional.ifPresent(cancelamentoPedidoDTO -> {
            Pedido pedido = pedidoRepository.findByNumeroPedido(cancelamentoPedidoDTO.getNumeroPedido().longValue());
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(codigoUsuario);

            if (Objects.nonNull(pedido) && usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                CancelamentoPedido cancelamentoPedido = new CancelamentoPedido(usuario, pedido);
                repository.save(cancelamentoPedido);
                log.info("O processo de retorno dos produtos do pedido '{}' cancelado, foi iniciado pelo usuário '{}'.",
                        pedido.getNumeroPedido(), usuario.getCodigoUsuario());

                filialService.atualizarDadosFirebase(filial, FirebaseFieldEnum.PEDIDOS_CANCELADOS);
            }
        });

        return cancelamentoPedidoDTOOptional.orElse(null);
    }

    public CancelamentoPedidoDTO finalizarRetornoMercadoria() {
        Integer codigoUsuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
        Integer filial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
        if (Objects.isNull(filial)) return null;

        Optional<CancelamentoPedido> cancelamentoPedidoOptional = repository.findByUsuario(codigoUsuarioLogado, filial);

        cancelamentoPedidoOptional.ifPresent(cancelamentoPedido -> {
            cancelamentoPedido.setFim(new Date());
            repository.save(cancelamentoPedido);
            log.info("O processo de retorno dos produtos do pedido '{}' cancelado, foi finalizado pelo usuário '{}'.",
                    cancelamentoPedido.getPedido().getNumeroPedido(), codigoUsuarioLogado);
        });

        filialService.atualizarDadosFirebase(filial, FirebaseFieldEnum.PEDIDOS_PENDENTES);

        return cancelamentoPedidoOptional
                .filter(cancelamentoPedido -> Objects.nonNull(cancelamentoPedido.getPedido().getNotaFiscal()))
                .map(cancelamentoPedido -> CancelamentoPedidoDTO.builder()
                        .numeroNotaFiscal(cancelamentoPedido.getPedido().getNotaFiscal().getNumeroNota())
                        .build()
                ).orElse(null);
    }

    public CancelamentoPedidoControladoDTO cancelarPedidoControlado(SolicitacaoCancelamentoPedidoDTO request) {
        boolean usuarioValido = autorizacaoSuperVendedorService.validarUsuario(request.getMatricula(), request.getSenha());
        String tipoTaxaEntrega = pedidoRepositoryCustom.obterPedidoControladoParaCancelamento(request.getNumeroPedido());
        TipoTaxaEntregaEnum tipoPedido = TipoTaxaEntregaEnum.buscarPorChave(tipoTaxaEntrega);

        RespostaPedidoDrogatelDTO respostaDrogatel = TipoTaxaEntregaEnum.CARTAO_CREDITO.equals(tipoPedido) || 
        											 TipoTaxaEntregaEnum.DINHEIRO.equals(tipoPedido) 
                ? cancelarPedidoNoDrogatel(request)
                : new RespostaPedidoDrogatelDTO();

        if (Boolean.TRUE.equals(respostaDrogatel.getSucesso())) {
        	log.info("Pedido {} cancelado com sucesso!!", request.getNumeroPedido());
            Usuario usuario = usuarioCosmosService.buscarPelaMatricula(request.getMatricula());
            Pedido pedido = new Pedido(request.getNumeroPedido().longValue());
            CancelamentoPedido cancelamentoPedido = new CancelamentoPedido(usuario, pedido);
            cancelamentoPedido.setInicio(new Date());
            cancelamentoPedido.setFim(new Date());
            repository.save(cancelamentoPedido);
        } else {
        	log.info("Não houve sucesso no cancelamento do pedido {}.", request.getNumeroPedido());
        }
        
        if (!request.getMotivo().equals("Cliente não entregou as receitas para o motociclista") 
        	&& receitaProdutoControladoService.isEntregaViaMotociclistaEContemControlado(request.getNumeroPedido()) 
        	&& !imgService.isContemReceitaDigital(request.getNumeroPedido().longValue())) {
        	pendenciaPedidoService.gerarPendencia(request.getNumeroPedido(), Constantes.FILA_RECEITA_DIVERGENTE, request.getMotivo());
        }

        return CancelamentoPedidoControladoDTO.builder()
                .matriculaValida(usuarioValido)
                .pagamentoAntecipado(TipoTaxaEntregaEnum.PAGAMENTO_ANTECIPADO.equals(tipoPedido))
                .pedidoCanceladoDTO(respostaDrogatel)
                .build();
    }

    private RespostaPedidoDrogatelDTO cancelarPedidoNoDrogatel(SolicitacaoCancelamentoPedidoDTO request) {
        MotivoDrogatel motivoCancelamento = motivoDrogatelService.buscarMotivoParaCancelamentoDePedidoNoDrogatel();
        CancelamentoPedidoDrogatelDTO pedido = CancelamentoPedidoDrogatelDTO.builder()
                .codigoMotivoCancelamento(motivoCancelamento.getId())
                .descricaoMotivoCancelamento(StringUtil.removerAcentos(request.getMotivo()))
                .numeroPedido(request.getNumeroPedido())
                .matriculaResponsavel(Integer.valueOf(request.getMatricula()))
                .build();
        return drogatelService.cancelarPedido(pedido);
    }

}