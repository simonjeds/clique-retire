package com.clique.retire.dto;

import com.clique.retire.enums.SimNaoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaProdutoControladoDTO {
	
	private Long id;
	private String numeroReceita;
	private String dataEmissaoReceita;
	private SimNaoEnum receitaAssinada;
	private SimNaoEnum receitaOriginal;
	private SimNaoEnum receitaSemRasura;
	private String descIdentidadeCliente;
	private String chaveMedico;
	private Long idProduto;
	private Long idItemPedido;
	private Integer numeroCaixa;
	private String idTipoReceita;
	private String descOrgaoEmissorDocumentoComprador;
	private String tipoDocumentoComprador;
	private String ufEmissaoDocumentoComprador;
	private Long NULL;
	private String justificativa;
	private Long idCodigoUsuario;
	private Long idReceitaImagem;
	private String nomePaciente;
	private SimNaoEnum sexoPaciente;
	private Integer idadePaciente;
	private String tipoIdadePaciente;
	private String taxaCidade;
	private SimNaoEnum usoProlongado;
	private boolean antibiotico;
	private SimNaoEnum envioDigital;	

}
