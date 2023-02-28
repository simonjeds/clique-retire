package com.clique.retire.util;

import com.clique.retire.client.rest.BackofficeClient;
import com.clique.retire.client.rest.CaptacaoClient;
import com.clique.retire.client.rest.CaptacaoDrogatelClient;
import com.clique.retire.client.rest.ConexaoDeliveryClient;
import com.clique.retire.client.rest.DanfePrinterClient;
import com.clique.retire.client.rest.DrogatelClient;
import com.clique.retire.client.rest.EmitirEtiquetaClient;
import com.clique.retire.client.rest.IntegradorExpedicaoClient;
import com.clique.retire.client.rest.IntegradorSiacClient;
import com.clique.retire.client.rest.LojasPreProducaoClient;
import com.clique.retire.client.rest.PBMClient;
import com.clique.retire.client.rest.PedidoClient;
import com.clique.retire.client.rest.PedidoDiretoClient;
import com.clique.retire.client.rest.PrescritorClient;
import com.clique.retire.client.rest.RappiClient;
import com.clique.retire.client.rest.ScheduleClient;

import feign.Feign;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeignUtil {

	public static BackofficeClient getBackofficeClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(BackofficeClient.class, url);
	}

	public static ScheduleClient getScheduleClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(ScheduleClient.class, url);
	}

	public static PedidoClient getPedidoClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(PedidoClient.class, url);
	}

	public static PedidoDiretoClient getPedidoDiretoClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(PedidoDiretoClient.class, url);
	}

	public static EmitirEtiquetaClient getGerarEtiquetaClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(EmitirEtiquetaClient.class, url);
	}

	public static PrescritorClient getPrescritorClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(PrescritorClient.class, url);
	}

	public static CaptacaoClient getCaptacaoClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(CaptacaoClient.class, url);
	}

	public static CaptacaoDrogatelClient getCaptacaoDrogatelClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.retryer(Retryer.NEVER_RETRY)
				.target(CaptacaoDrogatelClient.class, url);
	}

	public static LojasPreProducaoClient getLojasPreProducaoClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(LojasPreProducaoClient.class, url);
	}

	public static RappiClient getRappiClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(RappiClient.class, url);
	}

	public static PBMClient getPBMClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(PBMClient.class, url);
	}

	public static ConexaoDeliveryClient getConexaoDeliveryClient(String url) {
		return Feign.builder()
				.client(new OkHttpClient())
				.retryer(new Retryer.Default())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(ConexaoDeliveryClient.class, url);
	}

	public static IntegradorExpedicaoClient getIntegradorExpedicaoClient(String url) {
		return Feign.builder()
			.client(new OkHttpClient())
			.retryer(new Retryer.Default())
			.encoder(new GsonEncoder())
			.decoder(new GsonDecoder())
			.target(IntegradorExpedicaoClient.class, url);
	}

	public static DanfePrinterClient getDanfePrinterClient(String url) {
		return Feign.builder()
			.client(new OkHttpClient())
			.encoder(new GsonEncoder())
			.decoder(new GsonDecoder())
			.target(DanfePrinterClient.class, url);
	}

    public static DrogatelClient getDrogatelClient(String url) {
    	/*Duration tresMinutos = Duration.ofMinutes(3)
    	int tresMinutosEmMilisegundos = (int) tresMinutos.toMillis()*/
        return Feign.builder()
            .client(new OkHttpClient())
            .encoder(new GsonEncoder())
            .decoder(new GsonDecoder())
            //.options(new Options(tresMinutosEmMilisegundos,tresMinutosEmMilisegundos))
            .retryer(Retryer.NEVER_RETRY)
            .target(DrogatelClient.class, url);
    }
    
    public static IntegradorSiacClient getIntegradorSiacClient(String url) {
        return Feign.builder()
            .client(new OkHttpClient())
            .encoder(new GsonEncoder())
            .decoder(new GsonDecoder())
            .retryer(Retryer.NEVER_RETRY)
            .target(IntegradorSiacClient.class, url);
    }
}
