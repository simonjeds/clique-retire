package com.clique.retire.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@Builder
public class ProdutoEntradaDTO {

	@SerializedName(value = "Codigo")
	private Integer codigo;

	@SerializedName(value = "PrecoPBM")
	private Integer precoPBM;

	@SerializedName(value = "CodigoAutorizadora")
	private Integer codigoAutorizadora;

	@SerializedName(value = "CodigosBarra")
	private List<String> codigosBarra;

	@SerializedName(value = "CodigoBarraBipado")
	private String codigoBarraBipado;

	@SerializedName(value = "DescricaoResumida")
	private String descricaoResumida;

	@SerializedName(value = "Digito")
	private Integer digito;

	@SerializedName(value = "IndicAntibiotico")
	private Boolean indicAntibiotico;

	@SerializedName(value = "IndicPsicotropico")
	private Boolean indicPsicotropico;

	@SerializedName(value = "IndicREC")
	private Boolean indicREC;

	@SerializedName(value = "IndicUsoContinuo")
	private Boolean indicUsoContinuo;

	@SerializedName(value = "IndicPBM")
	private Boolean indicPBM;

	@SerializedName(value = "IndicFP")
	private Boolean indicFP;

	@SerializedName(value = "TipoReceita")
	private String tipoReceita;

	@SerializedName(value = "NumeroLote")
	private String numeroLote;

	@SerializedName(value = "Quantidade")
	private Integer quantidade;

	@SerializedName(value = "PrecoDe")
	private Double precoDe;

	@SerializedName(value = "PrecoPor")
	private Double precoPor;

	@SerializedName(value = "PrecoPraticado")
	private Double precoPraticado;

	@SerializedName(value = "IndicadorTeclaDesconto")
	private Boolean indicadorTeclaDesconto;

	@SerializedName(value = "UsuarioTeclaDesconto")
	private Integer usuarioTeclaDesconto;

	@SerializedName(value = "CupomEpharma")
	private String cupomEpharma;

	@SerializedName(value = "CodigoConvenioSaude")
	private String codigoConvenioSaude;

}
