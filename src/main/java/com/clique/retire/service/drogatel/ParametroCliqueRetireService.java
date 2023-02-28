package com.clique.retire.service.drogatel;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.repository.drogatel.ParametroCliqueRetireCustom;

@Service
public class ParametroCliqueRetireService {

	@Autowired
	private ParametroCliqueRetireCustom parametroCliqueRetireRepository;
	
	@Autowired
	private DrogatelParametroRepository drogatelParametroRepository;
	
	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;

	@Transactional("drogatelTransactionManager")
	public String buscarPorNome(ParametroEnum parametro) {
		return parametroCliqueRetireRepository.findByNome(parametro.getDescricao());
	}
	
	@Transactional(value = "drogatelTransactionManager", isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public boolean isLojaHabilitadaParaNFe(String ipCliente) {
		Integer codigoFilial = controleIntranetRepository.findFilialByIp(ipCliente);
		if(codigoFilial == null) {
			return false;
		}

		return isFilialHabilitadaParaNFe(codigoFilial);
	}

	public boolean isFilialHabilitadaParaNFe(Integer codigoFilial) {
		DrogatelParametro retorno = drogatelParametroRepository.findByNome(ParametroEnum.LOJAS_HABILITADO_NOTA_FISCAL.getDescricao());
		if (retorno == null || retorno.getValor().isEmpty()) {
			return false;
		}
		
		return Arrays.asList(retorno.getValor().split(";")).stream().anyMatch(id -> codigoFilial.toString().equals(id));
	}
	
}
