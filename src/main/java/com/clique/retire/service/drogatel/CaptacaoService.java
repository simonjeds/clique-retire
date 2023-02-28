package com.clique.retire.service.drogatel;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.client.rest.CaptacaoClient;
import com.clique.retire.dto.CaptacaoIntegracaoDTO;
import com.clique.retire.dto.CaptacaoLoteDTO;
import com.clique.retire.dto.ItensCaptacaoIntegracaoDTO;
import com.clique.retire.dto.ResumoCaptacaoPedidoDTO;
import com.clique.retire.dto.RetornoCaptacaoPedidoDTO;
import com.clique.retire.enums.SexoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ReceitaProdutoControlado;
import com.clique.retire.repository.drogatel.PedidoRepositoryImpl;
import com.clique.retire.repository.drogatel.ReceitaProdutoControladoRepository;
import com.clique.retire.util.FeignUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CaptacaoService extends GeraToken {

	private static final String RETORNO_CAPTACAO = "Não foi possível retornar a captação";
	private static final String RETORNO_LOTES_CONTROLADOS = "Não foi possível registrar os lotes controlados";
	private static final String MSG_SUCESSO_CAPTACAO = "sucesso";

	@Autowired
	private PedidoRepositoryImpl pedidoRepositoryImpl;
	
	@Autowired
	private ReceitaProdutoControladoRepository receitaProdutoControladoRepository;

	@Transactional("drogatelTransactionManager")
	public ResumoCaptacaoPedidoDTO getRetornarCaptacao(Long idPedido) {

		ResumoCaptacaoPedidoDTO dto = new ResumoCaptacaoPedidoDTO();
		try {			
			CaptacaoClient client = FeignUtil.getCaptacaoClient(getHostNameApp());
			JsonObject jsonObject = client.retornarCaptacao(idPedido, getAuthToken());
			
			Gson gson=new GsonBuilder().create();
			dto = gson.fromJson(jsonObject.get("data"), ResumoCaptacaoPedidoDTO.class);
			
			if (dto == null) {
				throw new BusinessException(RETORNO_CAPTACAO);
			}
		} catch (Exception e) {
			throw new BusinessException(RETORNO_CAPTACAO);
		}
		
		return dto;
	}

	private void getRetornarCaptacao(List<CaptacaoLoteDTO> lotes, Integer codigoUsuario) {
		try {
			Gson gson = new GsonBuilder().create();
			JsonObject jsonObject = FeignUtil.getCaptacaoClient(getHostNameApp()).captacaoLote(lotes, getAuthToken());
			
			Type type = new TypeToken<List<RetornoCaptacaoPedidoDTO>>() {}.getType();
			List<RetornoCaptacaoPedidoDTO> listaRetorno = gson.fromJson(jsonObject.get("data"), type);
			
			listaRetorno.forEach(l -> { 
				if (!l.getMensagem().equals(MSG_SUCESSO_CAPTACAO)) 
					throw new BusinessException(l.getMensagem()); 
				});
			
			int numeroReceita = 1;
			for (RetornoCaptacaoPedidoDTO retornoCaptacaoPedidoDTO : listaRetorno) {
				CaptacaoIntegracaoDTO dto = retornoCaptacaoPedidoDTO.getCaptacao();
				for (ItensCaptacaoIntegracaoDTO item : dto.getItens()) {
					salvarReceita(codigoUsuario, numeroReceita, dto, item);
				}	
				numeroReceita++;
			}

		} catch (BusinessException ex) {
			log.error("Erro ao tentar realizar a captação", ex);
			throw ex;
		} catch (Exception e) {
			log.error("Erro obter numero autorizacao", e);
			throw new BusinessException(RETORNO_LOTES_CONTROLADOS);
		}
	}

	private void salvarReceita(Integer codigoUsuario, int numeroReceita, CaptacaoIntegracaoDTO dto, ItensCaptacaoIntegracaoDTO item) throws ParseException {
		ReceitaProdutoControlado entity = new ReceitaProdutoControlado(String.valueOf(codigoUsuario));
		
		entity.setRcpc_fl_receita_assinada(SimNaoEnum.S);
		entity.setRcpc_fl_receita_sem_rasura(SimNaoEnum.S);
		entity.setRcpc_fl_receita_original(SimNaoEnum.S);
		entity.setRcpc_fl_envio_digital(SimNaoEnum.N);
		
		entity.setChave_medico(dto.getRegistro());
		
		entity.setRcpc_nr_numero_receita(numeroReceita + "");
		Date dataEmissaoReceita = Objects.nonNull(dto.getDataEmissao())
			? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dto.getDataEmissao()) : null;
		entity.setRcpc_dt_emissao_receita(dataEmissaoReceita);
		entity.setTire_sq_receita(Integer.parseInt(dto.getTipoReceita()));						
		entity.setRcpc_fl_uso_prolongado(item.getPronlogado().equals("S") ? SimNaoEnum.S : SimNaoEnum.N);
		entity.setRcpc_nr_caixa(item.getQuantidade());
		entity.setRcpc_cd_autorizacao(dto.getNumeroAutorizacao());
		
		entity.setRcpc_ds_identidade_cliente(dto.getDocumento());
		entity.setRcpc_ds_orgao_emissor_doc_comprador(dto.getOrgao());
		entity.setRcpc_ds_uf_emissao_doc_comprador(dto.getUf());
		entity.setRcpc_tx_cid("");
		
		entity.setRcpc_nm_paciente(dto.getNome());
		entity.setRcpc_nr_idade_paciente(dto.getIdade());
		entity.setRcpc_tp_idade_paciente(dto.getTipoIdade());
		entity.setRcpc_fl_sexo_paciente(SexoEnum.getValorPorSigla(dto.getSexo()));
		
		entity.setUltimaAlteracao(new Date());
		entity.setItemPedido(item.getCodigoItemCR());	
		receitaProdutoControladoRepository.saveAndFlush(entity);
	}
	
	public void fluxoRegistroCaptacao(Long numeroPedido, Integer codigoUsuario) {
		List<CaptacaoLoteDTO> lotes = pedidoRepositoryImpl.buscaLoteBipadoPorNumeroPedido(numeroPedido);
		getRetornarCaptacao(lotes, codigoUsuario);
	}
	
	public boolean isSuperVendedor(Long numeroPedido) {
		return pedidoRepositoryImpl.isSuperVendedor(numeroPedido);
	}
}
