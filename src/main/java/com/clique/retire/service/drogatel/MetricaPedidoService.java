package com.clique.retire.service.drogatel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.FilialFirebaseDTO;
import com.clique.retire.dto.HistoricoMetricaDTO;
import com.clique.retire.dto.PedidoDataMetricasDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.HistoricoMetrica;
import com.clique.retire.repository.drogatel.FeriadoRepository;
import com.clique.retire.repository.drogatel.HistoricoMetricaRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.util.FirebaseUtil;

@Service
public class MetricaPedidoService {

	private static final int PERMANECEU_POSICAO = 0;

	private static final int DESCEU_POSICAO = -1;

	private static final int SUBIU_POSICAO = 1;

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricaPedidoService.class);
	
	private static final Integer CODIGO_USUARIO = 1;

	private static final String NOME_ULTIMA_ESTACAO = "PAINEL_CLIQUE_RETIRE";

	@Autowired
	private HistoricoMetricaRepositoryCustom histMetricaRepositoryCustom;
	
	@Autowired
	private ParametroCliqueRetireService parametroCliqueRetireService;

	@Autowired
	private FirebaseUtil firebaseUtil;
	
	@Autowired
	private PedidoRepositoryCustom pedidoRepositoryCustom;
	
	@Autowired
	private HistoricoMetricaRepositoryCustom historicoMetricaRepositoryCustom;
	
	@Autowired
	private FeriadoRepository feriadoRepository;
	
	public void gerarMetricasFiliais() {
		try {
			
			LocalDate dataAtual = LocalDate.now();
	
			LocalDate trintaDiasAnteriores = dataAtual.minusDays(30);
			List<HistoricoMetricaDTO> listHistoricoMetricaDTO = histMetricaRepositoryCustom.consultarMetricas(trintaDiasAnteriores,
					dataAtual);
	
			// Agrupa o histórico de métrica por filial
			Map<Integer, List<HistoricoMetricaDTO>> mapHistoricoMetrica = listHistoricoMetricaDTO.stream()
					.collect(Collectors.groupingBy(HistoricoMetricaDTO::getNumeroFilial));
			
			List<FilialFirebaseDTO> listFilialFirebaseDTO = new ArrayList<>();
			
			int prazoProcessamentoPedido = Integer.parseInt(parametroCliqueRetireService.buscarPorNome(ParametroEnum.PRAZO_PROCESSAMENTO_PEDIDO));
			
			mapHistoricoMetrica.entrySet().forEach(e -> {

				List<HistoricoMetricaDTO> listHistoricoMetrica = e.getValue();

				int quantidadeMetricas = listHistoricoMetrica.size();
				int dentroDoPrazo = 0;
				int mediaTempoProcessamento = 0;
				int somaTemposSeparacaoERegistro = 0;
				String percentualPedidosProcessadosDsc = "";
				double percentualPedidosProcessados = 0.0;

				if(listHistoricoMetrica.size() > 1 || listHistoricoMetrica.get(0).getId() != null) {
					for(HistoricoMetricaDTO historicoMetricaDTO : listHistoricoMetrica) {
						int somaTempos = historicoMetricaDTO.getTempoInicioSeparacao() + historicoMetricaDTO.getTempoRegistro();
						if(somaTempos < prazoProcessamentoPedido) {
							dentroDoPrazo++;
						}
						somaTemposSeparacaoERegistro += somaTempos;
					}

					mediaTempoProcessamento = somaTemposSeparacaoERegistro / quantidadeMetricas;
					percentualPedidosProcessados = ((double)(dentroDoPrazo) / (double)(quantidadeMetricas) * 100);
				}

				// Formatando para duas casas decimais
				NumberFormat formatter = new DecimalFormat("#.##");
				percentualPedidosProcessadosDsc = formatter.format(percentualPedidosProcessados).concat("%");

				// Consolidando as metricas por filial
				FilialFirebaseDTO filialFirebaseDTO = new FilialFirebaseDTO();
				filialFirebaseDTO.setTotalPedidosLoja(listHistoricoMetrica.get(0).getQuantidadePedidosDestaLoja());
				filialFirebaseDTO.setNomeLoja(listHistoricoMetrica.get(0).getNomeFilial());
				filialFirebaseDTO.setNumeroFilial(listHistoricoMetrica.get(0).getNumeroFilial());
				filialFirebaseDTO.setPercentualPedidosProcessados(percentualPedidosProcessados);
				filialFirebaseDTO.setPedidosProcessadosPrazo(percentualPedidosProcessadosDsc);
				filialFirebaseDTO.setTempoProcessamento(mediaTempoProcessamento);
				filialFirebaseDTO.setNovosPedidos(listHistoricoMetrica.get(0).getNovosPedidos());

				listFilialFirebaseDTO.add(filialFirebaseDTO);
			});
			
			// Ordenando pelo percentual de pedidos processados no prazo, caso ocorra empate será ordenado pelo tempo de processamento
			ordernarFiliaisParaRanking(listFilialFirebaseDTO);
			
			List<FilialFirebaseDTO> listaFiliaisExistentesFirebase = firebaseUtil.buscarTodasAsFiliais();
	
			int ranking = 1;
			for(FilialFirebaseDTO filial : listFilialFirebaseDTO) {
				filial.setRankingLoja(ranking++);
				
				// Setando valores da loja destaque
				filial.setLojaDestaque(listFilialFirebaseDTO.get(0).getNomeLoja());
				filial.setPedidosProcessadosPrazoDestaque(listFilialFirebaseDTO.get(0).getPedidosProcessadosPrazo());
				filial.setTotalPedidosLojaDestaque(listFilialFirebaseDTO.get(0).getTotalPedidosLoja());
				
				Optional<FilialFirebaseDTO> filialFirebaseDTO = listaFiliaisExistentesFirebase.stream()
						.filter(fili -> fili.getNumeroFilial().equals(filial.getNumeroFilial()))
						.findFirst();
				Integer rankingAnterior = null;
				if (filialFirebaseDTO.isPresent()) {
					rankingAnterior = filialFirebaseDTO.get().getRankingLoja();
				}
				
				// Verificando se a loja subiu, desceu ou permaneceu na mesma posição no ranking.
				if(rankingAnterior != null && rankingAnterior > ranking) {
					// subiu de posição
					filial.setTrendUp(SUBIU_POSICAO);
				}else if(rankingAnterior != null && rankingAnterior < filial.getRankingLoja()) {
					 // desceu de posição
					filial.setTrendUp(DESCEU_POSICAO);
				}else{
					 // permaneceu na mesma posição
					filial.setTrendUp(PERMANECEU_POSICAO);
				}
			}
			
			firebaseUtil.saveOrUpdateBatch(listFilialFirebaseDTO);
		}catch(Exception e) {
			LOGGER.info("", e);
			e.printStackTrace();
		}
	}
	
	@Transactional("drogatelTransactionManager")
	public void calcularMetricasDoPedido() {
		List<PedidoDataMetricasDTO> pedidos = pedidoRepositoryCustom.buscarPedidosParaCalculoMetricas();
		LocalDate dataFim = LocalDate.now();
		LocalDate dataInicio = dataFim.minusMonths(1);
		List<Date> feriados = feriadoRepository.buscarFeriadosPorPeriodo(dataInicio, dataFim);
		List<HistoricoMetrica> metricas = new ArrayList<>();
		HistoricoMetrica metrica;
		
		LOGGER.info("[METRICAS] QUANTIDADE PEDIDOS PARA SALVAR METRICAS -->>> {}", pedidos.size());
		
		for (PedidoDataMetricasDTO pedido : pedidos) {
			try {
				metrica = new HistoricoMetrica(CODIGO_USUARIO, NOME_ULTIMA_ESTACAO);
				metrica.setNumeroFilial(pedido.getFilial());
				metrica.setNumeroPedido(pedido.getNumeroPedido());
				
				Integer tempoIntegracao = calcularDiferencaEntreDatas(pedido.getDataTerminoIntegracao(), pedido.getDataInicioIntegracao());
				Integer tempoSeparacao = calcularTempoConsiderandoHorarioFuncionamento(pedido, feriados, pedido.getDataTerminoIntegracao(), null, null, pedido.getDataInicioSeparacao());
				Integer tempoRegistro = calcularTempoConsiderandoHorarioFuncionamento(pedido, feriados, pedido.getDataInicioSeparacao(), pedido.getDataEntrouNegociacao(), pedido.getDataRecebeuUltimaTransferencia(), pedido.getDataTerminoRegistro());

				metrica.setTempoIntegracao(tempoIntegracao);
				metrica.setTempoSeparacao(tempoSeparacao);
				metrica.setTempoRegistro(tempoRegistro);
				
				metricas.add(metrica);
			} catch (Exception e) {
				LOGGER.info("Ocorreu erro ao calcular as métricas para o pedido - {}", pedido.getNumeroPedido());
				LOGGER.info("", e);
				e.printStackTrace();
			}
		}
		
		LOGGER.info("[METRICAS] VAI CHAMAR O SALVAR EM BATCH -->>> {}", metricas.size());
		
		if(!metricas.isEmpty()) {
			salvarMetricas(metricas);
		}
		
		LOGGER.info("[METRICAS] Finalizou o cálculo de métricas.");
	}
	
	private void salvarMetricas(List<HistoricoMetrica> metricas) {
		List<List<HistoricoMetrica>> splitMetricas = chopped(metricas,180);
		for(List<HistoricoMetrica> metrica: splitMetricas) {
			historicoMetricaRepositoryCustom.salvarEmBatch(metrica);
		}
	}
	
	public static  List<List<HistoricoMetrica>> chopped(List<HistoricoMetrica>list, final int L) {
		List<List<HistoricoMetrica>> parts = new ArrayList<>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}
	
	private Integer calcularTempoConsiderandoHorarioFuncionamento(PedidoDataMetricasDTO pedido, List<Date> feriados, Date dataInicio, Date periodoInicioDesconsiderado, Date periodoFimDesconsiderado, Date dataFim) {
		
		if(dataFim.before(dataInicio)) {
			return 0;
		}
		
		Calendar dataAux = Calendar.getInstance();
		dataAux.setTime(dataInicio);
	
		Date dataAbertura;
		Calendar horarioAbertura = Calendar.getInstance();
		
		Date dataFechamento;
		Calendar horarioFechamento = Calendar.getInstance();
		
		Calendar periodoDesconsideradoInicio = Calendar.getInstance();
		if (periodoInicioDesconsiderado != null) {
			periodoDesconsideradoInicio.setTime(periodoInicioDesconsiderado);
		}

		Calendar periodoDesconsideradoFim = Calendar.getInstance();
		if (periodoFimDesconsiderado != null) {
			periodoDesconsideradoFim.setTime(periodoFimDesconsiderado);
		}

		Integer minutosConsideradosDeDiferenca = 0;
		Integer diferenteEmMinutosEntreDatas = 0;
		
		do {
			dataAbertura = definirHorarioAberturaFilial(pedido, feriados, dataAux);
			if (dataAbertura == null) {
				dataAux.add(Calendar.DAY_OF_MONTH, 1);
				zerarHoraMinutoSegundo(dataAux);
				continue;
			}
			horarioAbertura.setTime(dataAbertura);
			igualarAnoMesDia(dataAux, horarioAbertura);

			dataFechamento = definirHorarioFechamentoFilial(pedido, feriados, dataAux);
			if (dataFechamento == null) {
				dataAux.add(Calendar.DAY_OF_MONTH, 1);
				zerarHoraMinutoSegundo(dataAux);
				continue;
			}
			horarioFechamento.setTime(dataFechamento);
			igualarAnoMesDia(dataAux, horarioFechamento);

			if (verificarDataDentroDoPrazo(dataAux, horarioAbertura, horarioFechamento)) {
				// Verifica se existe período a ser desconsiderado.
				if (periodoInicioDesconsiderado == null || periodoFimDesconsiderado == null) {
					if(verificarDatasEstaoNoMesmoDia(dataAux.getTime(), dataFim)) {
						diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(dataFim, dataAux.getTime());
						minutosConsideradosDeDiferenca += diferenteEmMinutosEntreDatas;
					}else {
						diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(horarioFechamento.getTime(), dataAux.getTime());
						minutosConsideradosDeDiferenca += diferenteEmMinutosEntreDatas;
					}
				}else {
					if(dataAux.before(periodoDesconsideradoInicio)){
						diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(periodoInicioDesconsiderado, dataAux.getTime());
						minutosConsideradosDeDiferenca += diferenteEmMinutosEntreDatas;
						diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(periodoFimDesconsiderado, dataAux.getTime());
					} else {
						if(verificarDatasEstaoNoMesmoDia(dataAux.getTime(), dataFim)) {
							diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(dataFim, dataAux.getTime());
							minutosConsideradosDeDiferenca += diferenteEmMinutosEntreDatas;
						}else {
							diferenteEmMinutosEntreDatas = calcularDiferencaEntreDatas(horarioFechamento.getTime(), dataAux.getTime());
							minutosConsideradosDeDiferenca += diferenteEmMinutosEntreDatas;
						}	
					}
				}
				
				
				dataAux.add(Calendar.MINUTE, diferenteEmMinutosEntreDatas>0?diferenteEmMinutosEntreDatas:1);
			}else {
				// Loja fechada, portanto, pega o próximo horário de abertura.
				dataAux = definirProximoHorarioAbertura(dataAux, pedido, feriados);
			}
		} while (dataAux.getTime().before(dataFim));
		
		return minutosConsideradosDeDiferenca;
	}

	private boolean verificarDatasEstaoNoMesmoDia(Date dataRef, Date dataAux) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		return fmt.format(dataRef).equals(fmt.format(dataAux));
	}
	
	private Calendar definirProximoHorarioAbertura(Calendar dataAtual, PedidoDataMetricasDTO pedido, List<Date> feriados) {
		
		Date dataProximaAbertura;
		Calendar proximaAbertura = Calendar.getInstance();
		while (true) {
			dataAtual.add(Calendar.DATE, 1);
			dataProximaAbertura = definirHorarioAberturaFilial(pedido, feriados, dataAtual);
			if (dataProximaAbertura != null) {
				proximaAbertura.setTime(dataProximaAbertura);
				igualarAnoMesDia(dataAtual, proximaAbertura);
				return proximaAbertura;
			}
		}
	}

	private Date definirHorarioAberturaFilial(PedidoDataMetricasDTO pedido, List<Date> feriados, Calendar data) {
		if(isFeriado(feriados, data.getTime())) {
			return pedido.getHorarioAberturaFeriado();
		}
		
		switch (data.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			return pedido.getHorarioAberturaSegunda();
			
		case Calendar.TUESDAY:
			return pedido.getHorarioAberturaTerca();
			
		case Calendar.WEDNESDAY:
			return pedido.getHorarioAberturaQuarta();
			
		case Calendar.THURSDAY:
			return pedido.getHorarioAberturaQuinta();
			
		case Calendar.FRIDAY:
			return pedido.getHorarioAberturaSexta();
			
		case Calendar.SATURDAY:
			return pedido.getHorarioAberturaSabado();
			
		case Calendar.SUNDAY:
			return pedido.getHorarioAberturaDomingo();
			
		default:
			return null;
		}
	}
	
	private Date definirHorarioFechamentoFilial(PedidoDataMetricasDTO pedido, List<Date> feriados, Calendar data) {
		if(isFeriado(feriados, data.getTime())) {
			return pedido.getHorarioFechamentoFeriado();
		}
		
		switch (data.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			return pedido.getHorarioFechamentoSegunda();
			
		case Calendar.TUESDAY:
			return pedido.getHorarioFechamentoTerca();
			
		case Calendar.WEDNESDAY:
			return pedido.getHorarioFechamentoQuarta();
			
		case Calendar.THURSDAY:
			return pedido.getHorarioFechamentoQuinta();
			
		case Calendar.FRIDAY:
			return pedido.getHorarioFechamentoSexta();
			
		case Calendar.SATURDAY:
			return pedido.getHorarioFechamentoSabado();
			
		case Calendar.SUNDAY:
			return pedido.getHorarioFechamentoDomingo();
			
		default:
			return null;
		}
	}

	private boolean isFeriado(List<Date> feriados, Date data) {
		Calendar dataAuxiliar = Calendar.getInstance();
		dataAuxiliar.setTime(data);
 		zerarHoraMinutoSegundo(dataAuxiliar);
		
		return feriados.contains(dataAuxiliar.getTime());
	}

	private Integer calcularDiferencaEntreDatas(Date dataFim, Date dataInicio) {
		if(dataFim.before(dataInicio)) {
			return 0;
		}
		
		long diffInMillies = Math.abs(dataFim.getTime() - dataInicio.getTime());
		long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

		// Arredonda a diferença para cima.
		if (diff * 60 * 1000 < diffInMillies) {
			diff++;
		}

		return Integer.valueOf(String.valueOf(diff));
	}
	
	private void zerarHoraMinutoSegundo(Calendar data) {
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);
	}
	
	private boolean verificarDataDentroDoPrazo(Calendar data, Calendar inicioPrazo, Calendar fimPrazo) {
		return (data.after(inicioPrazo) || data.equals(inicioPrazo)) && data.before(fimPrazo);
	}

	private void igualarAnoMesDia(Calendar origem, Calendar destinatario) {
		destinatario.set(Calendar.YEAR, origem.get(Calendar.YEAR));
		destinatario.set(Calendar.MONTH, origem.get(Calendar.MONTH));
		destinatario.set(Calendar.DAY_OF_MONTH, origem.get(Calendar.DAY_OF_MONTH));
	}
	
	private void ordernarFiliaisParaRanking(List<FilialFirebaseDTO> listFilialFirebaseDTO) {
		listFilialFirebaseDTO.sort(new Comparator<FilialFirebaseDTO>() {
			@Override
			public int compare(FilialFirebaseDTO f1, FilialFirebaseDTO f2) {
				int comparador;
				comparador = f2.getPercentualPedidosProcessados().compareTo(f1.getPercentualPedidosProcessados());
			    if (comparador == 0) {
			    	comparador = f1.getTempoProcessamento().compareTo(f2.getTempoProcessamento());
			    }
			    return comparador;
			}
		});
	}
}