package com.clique.retire.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReceitaSkuDTO {

	private Long numeroPedido;
	private String dataReceita;
	private Date dataReceitaCaptacao;
	private String numeroReceita;
	private String numeroNotificacao;
	private String conselho;
	private String uf;
	private String numeroRegistro;
	private String nomeProfissional;
	private String nomeCliente;
	private String celular;
	private String cpfCnpjCliente;
	private String documentoCliente;
	private String orgaoExpedidor;
	private String ufCliente;
	private String cep;
	private String logradouro;
	private String bairro;
	private Integer numero;
	private String complemento;
	private String cidade;
	private boolean antibiotico;
	private boolean clientePaciente;
	private String nomePaciente;
	private String dataNascimentoPaciente;
	private Date dataNascimentoPacienteCaptacao;
	private String sexo;
	private Integer tipoReceita;
	private String tipoDocumentoComplemento;
	private List<ProdutoDTO> produtos;

	private Integer codigoPolo;
	private String dataNascimentoCliente;
	private Date dataNascimentoClienteCaptacao;
	private String sexoCliente;
	private String emailCliente;
	private String documentoPaciente;
	private Integer idadePaciente;
	private String tipoIdadePaciente;
	private Integer idCliente;
	private boolean proximaReceita;

	private String numeroAutorizacao;
	private Integer codigoUsuarioCaptacao;
	private Integer codigoUsuarioConferente;
	private Date dataCaptacao;
	private boolean quatroPontoZero;
	private boolean receitaDigital;

	@JsonIgnore
	public boolean hasTodasReceitasCaptadas() {
		return !proximaReceita;
	}

}
