package com.clique.retire.client.rest;

import feign.RequestLine;
import feign.Response;

public interface LojasPreProducaoClient {

	
	@RequestLine("GET ")
	public Response lojaPreProducao(); 
}
