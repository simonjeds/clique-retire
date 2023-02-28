package com.clique.retire.service.drogatel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.repository.drogatel.IMGRepositoryImpl;

@Service
public class IMGService {

	@Autowired
	private IMGRepositoryImpl repository;
	
	public List<byte[]> obterListaReceitaDigital(Long numeroPedido) {
		return repository.obterListaReceitaDigital(numeroPedido);
	}
	
	public boolean isContemReceitaDigital(Long numeroPedido) {
		return repository.isContemReceitaDigital(numeroPedido);
	}

}
