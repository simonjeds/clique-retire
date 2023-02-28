package com.clique.retire.service.drogatel;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.AvaliacaoDTO;
import com.clique.retire.infra.exception.ErroValidacaoException;
import com.clique.retire.model.drogatel.Avaliacao;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.AvaliacaoCustom;
import com.clique.retire.util.WebUtils;

@Service
public class AvaliacaoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AvaliacaoService.class);
	
	private static final String USUARIO_NAO_HABILITADO_PARA_REALIZAR_A_AVALIACAO = "Seu usuário ainda não está habilitado para realizar a avaliação.";

	@Autowired
	private AvaliacaoCustom avaliacaoRepository;

	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;
	
	@Transactional("drogatelTransactionManager")
	public void salvarAvaliacao(String enderecoIp, Integer codUsuario, String comentario, Integer nota) {
		
		Integer codigoLoja = controleIntranetRepository.findFilialByIp(enderecoIp);
		
		LOGGER.info("### ID_LOJA ### {}", codigoLoja);
		
		AvaliacaoDTO avaliacaoDTO = avaliacaoRepository.buscarDadosComplementares(codUsuario);
		
		Avaliacao avaliacao = new Avaliacao(codUsuario.toString());
		avaliacao.setEstacaoUltimaAtualizacao(WebUtils.getHostName());
		avaliacao.setCodigoFilial(codigoLoja);
		avaliacao.setCodigoUsuarioAlteracao(codUsuario.toString());
		avaliacao.setNota(nota);
		avaliacao.setComentario(comentario);
		
		Integer quantidadeSeparacoesRealizadas = avaliacaoDTO.getQuantidadeSeparacoesRealizadas() == null ? 0 : avaliacaoDTO.getQuantidadeSeparacoesRealizadas();
		Integer quantidadeEntregasRealizadas = avaliacaoDTO.getQuantidadeEntregasRealizadas() == null ? 0 : avaliacaoDTO.getQuantidadeEntregasRealizadas();
		Integer quantidadeApontamentosFaltasRealizadas = avaliacaoDTO.getQuantidadeApontamentosFaltasRealizadas() == null ? 0 : avaliacaoDTO.getQuantidadeApontamentosFaltasRealizadas();
		Integer quantidadeRecebimentosMercadoriasRealizadas = avaliacaoDTO.getQuantidadeRecebimentosMercadoriasRealizadas() == null ? 0 : avaliacaoDTO.getQuantidadeRecebimentosMercadoriasRealizadas();
		
		avaliacao.setQuantidadeSeparacoesRealizadas(quantidadeSeparacoesRealizadas);
		avaliacao.setQuantidadeEntregasRealizadas(quantidadeEntregasRealizadas);
		avaliacao.setQuantidadeApontamentosFaltasRealizadas(quantidadeApontamentosFaltasRealizadas);
		avaliacao.setQuantidadeRecebimentosMercadoriasRealizadas(quantidadeRecebimentosMercadoriasRealizadas);
		avaliacao.setQuantidadeControladosRealizados(0);
		avaliacao.setQuantidadeFluxosExperidadosRealizados(0);
		
		avaliacaoRepository.salvarAvaliacao(avaliacao);
	}
	
	@Transactional("drogatelTransactionManager")
	public void validarAvaliacao(Integer codUsuario) {
		AvaliacaoDTO avaliacaoDTO = avaliacaoRepository.buscarDadosComplementares(codUsuario);
		Avaliacao avaliacao = avaliacaoRepository.recuperarAvaliacaoPorDia(codUsuario, new Date());
		
		LOGGER.info("### AvaliacaoDTO ### {}", avaliacaoDTO);
		LOGGER.info("### Avaliacao Realizada ### {}", avaliacao);
		
		if((avaliacaoDTO.getQuantidadeSeparacoesRealizadas() == null && 
				avaliacaoDTO.getQuantidadeEntregasRealizadas() == null && 
				avaliacaoDTO.getQuantidadeApontamentosFaltasRealizadas() == null &&
				avaliacaoDTO.getQuantidadeRecebimentosMercadoriasRealizadas() == null &&
				avaliacaoDTO.getQuantidadeControladosRealizados() == null && 
				avaliacaoDTO.getQuantidadeFluxosExperidadosRealizados() == null) || avaliacao != null) {
			
			throw new ErroValidacaoException(USUARIO_NAO_HABILITADO_PARA_REALIZAR_A_AVALIACAO);
		}
	}
	
}
