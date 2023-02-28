package com.clique.retire.repository.drogatel;

import java.util.Date;

public interface ExpedicaoRepositoryCustom {

	public void gravarRetornoExpedicaoPedido(Date dataEntrega, Integer numeroPedido);
}
