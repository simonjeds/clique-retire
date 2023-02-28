package com.clique.retire.service.drogatel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.util.FeignUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import feign.Response;

@Service
public class LojasPreProducaoService {

	@Value("${url.lojas.pre}")
	private String url;

	public Object getLojasPreProducao() {
		BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
		Response reponse = FeignUtil.getLojasPreProducaoClient(url).lojaPreProducao();
		baseResponseDTO.setCode(200);

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		return gson.fromJson(reponse.body().toString(), Object.class);

	}

}
