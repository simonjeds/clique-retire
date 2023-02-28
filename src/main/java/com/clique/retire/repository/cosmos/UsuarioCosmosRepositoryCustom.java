package com.clique.retire.repository.cosmos;

public interface UsuarioCosmosRepositoryCustom{
	
	
	/**
	 * @param matricula
	 * @return o codigo do usuario a partir de uma matricula
	 */
	public Integer buscarCodigoUsuarioPorMatricula(String matricula);
	
	/**
	 * Busca o código do usuário referente a matrícula informada, desconsiderando usuários com o cargo
	 * OPERADOR DE CAIXA
	 * @param matriculaUsuario matricula do usuário
	 * @return código do usuário
	 */
	Integer buscarUsuarioParaConferenciaCaptacao(String matriculaUsuario);

}
