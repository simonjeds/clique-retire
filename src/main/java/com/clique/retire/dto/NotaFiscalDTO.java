package com.clique.retire.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotaFiscalDTO {

	private String codigoFilial;
	private List<ItemNotaFiscalDTO> itens;
	private boolean notaFiscalJaRecebida;
	private Integer codigoPedidoTransferencia;
	private String recebedor;
	private String chave;
	private Date dataRecebimento;
	private List<HistoricoAtendimentoNFDTO> historicos = new ArrayList<>();

	public NotaFiscalDTO(String recebedor, String chave, Date dataRecebimento) {
		super();
		this.recebedor = recebedor;
		this.chave = chave;
		this.dataRecebimento = dataRecebimento;
		this.notaFiscalJaRecebida = StringUtils.isNotBlank(recebedor);
	}
}
