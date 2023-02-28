package com.clique.retire.enums;

import lombok.Getter;

public enum StatusPedidoEnum {

	FASE_BOL_SOLICITADO("Boleto solicitado e gerado."),
	FASE_ERRO_BOL_SOLICITADO("Erro: solicitação à Braspag do boleto."),
	FASE_SUCESSO_BOL_SOLICITADO("Boleto enviado por e-mail (SUCESSO)."),
	FASE_PAG_CONFI_BOL_VISTA("Pagamento do boleto à vista confirmado."),
	
	FASE_ADIANTAMENTO_ENVIDO_SAP("Adiantamento (etapa cliente) enviado ao SAP."),
	FASE_ERRO_ADIANTAMENTO_ENVIDO_SAP("Erro: solicitação ao SAP do adiantamento (etapa cliente)."),
	FASE_LANCAMENTO_ENVIDO_SAP("Adiantamento (etapa lançamento) enviado ao SAP."),
	FASE_ERRO_LANCAMENTO_ENVIDO_SAP("Erro: solicitação ao SAP do adiantamento (etapa lançamento)."),
	FASE_SUCESSO_LANCAMENTO_ENVIDO_SAP("Adiantamento realizado pelo SAP (SUCESSO)"),
	
	FASE_CONFIRMACAO_PAGAMENTO("Confirmação de pagamento solicitada."),
	FASE_ERRO_CONFIRMACAO_PAGAMENTO("Erro: confirmação de pagamento. "),
	
	FASE_VERIFICAR_STATUS_CONTIGENCIA("Situação de impressão em contingência solicitada ao SAP."),
	FASE_ERRO_VERIFICAR_STATUS_CONTIGENCIA("Erro: solicitação ao SAP da situação de impressão em contingência."),
	
	FASE_SOLICITA_EMISSAO_ORDEM_VENDA("Solicitação emissão da ordem de venda enviada ao SAP."),
	FASE_ERRO_SOLICITA_EMISSAO_ORDEM_VENDA("Erro: solicitação ao SAP da emissão da ordem de venda."),
	FASE_RETORNO_ORDEM_VENDA("Recebimento da emissão da ordem de venda do SAP."),
	FASE_ERRO_RETORNO_ORDEM_VENDA("Erro: tratamento do retorno da ordem de venda."),
	FASE_SUCESSO_ORDEM_VENDA("Finalização do registro e mudança da fase do pedido (SUCESSO).");
	
	
	StatusPedidoEnum(String descricaoPainel) {
		this.descricaoPainel = descricaoPainel;
	}
	
	@Getter
	private String descricaoPainel;
}
