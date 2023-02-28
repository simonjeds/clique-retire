package com.clique.retire.service.drogatel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ClienteDTO;
import com.clique.retire.model.drogatel.Cliente;
import com.clique.retire.repository.drogatel.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Transactional("drogatelTransactionManager")
	public ClienteDTO consultarDocumento(String documento) {
		
		Cliente cliente = clienteRepository.findByDocumento(documento);

		if (cliente != null) {
		return ClienteDTO.builder()
				.id(cliente.getId())
				.nome(cliente.getNome())
				.email(cliente.getEmail())
				.documento(cliente.getDocumento())
				.sexo(cliente.getSexo())
				.celular(cliente.getCelular())
				.build();
		}
		return ClienteDTO.builder().build();
		
	}
	
	@Transactional("drogatelTransactionManager")
	public boolean validacaoCliente(ClienteDTO clienteDTO) {
		return true;
	}	

}