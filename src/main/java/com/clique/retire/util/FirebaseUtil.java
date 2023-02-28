package com.clique.retire.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.clique.retire.service.drogatel.CronometroService.CronometroParametros;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import com.clique.retire.dto.FilialFirebaseDTO;
import com.clique.retire.enums.FirebaseFieldEnum;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseUtil {

  private static final String FILIAL_NAO_EXISTE_NO_FIREBASE = "Filial '{}' não existe no Firebase.";
  private static final String COLLECTION_NAME = "clique-retire-painel";

  private final Environment env;
  private final ResourceLoader resourceLoader;

  private Firestore db;

  @Bean
  public void startFirebase() {
    try {
      String databaseURL = env.getProperty("firebase.database.url");
      String serviceAccountKey = "classpath:firebase/" + env.getProperty("firebase.account.key");

      log.info("### URL FIREBASE ### {}", databaseURL);

      InputStream serviceAccount = resourceLoader.getResource(serviceAccountKey).getInputStream();

      FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(
        GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(databaseURL).build();

      FirebaseApp.initializeApp(options);

      this.db = FirestoreClient.getFirestore();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void atualizarCronometro(Integer codigoLoja, CronometroParametros parametros, List<String> funcionalidades) {
    try {
      DocumentReference docRef = this.db.collection(COLLECTION_NAME).document(codigoLoja.toString());

      Map<String, Object> dados = new HashMap<>();
      dados.put(FirebaseFieldEnum.CRONOMETRO_DATA_HORA_INICIO.getField(), parametros.getDataInicio());
      dados.put(FirebaseFieldEnum.CRONOMETRO_DATA_HORA_FIM.getField(), parametros.getDataFim());
      dados.put(FirebaseFieldEnum.CRONOMETRO_MENSAGEM.getField(), parametros.getMensagem());
      dados.put(FirebaseFieldEnum.CRONOMETRO_DATA_HORA_FIM_MANUTENCAO.getField(), parametros.getDataFimManutencao());
      dados.put(FirebaseFieldEnum.CRONOMETRO_MENSAGEM_MANUTENCAO.getField(), parametros.getMensagemManutencao());
      dados.put(FirebaseFieldEnum.CRONOMETRO_FUNCIONALIDADES.getField(), funcionalidades);

      if (this.verificarSeExisteFilial(docRef)) {
        docRef.update(dados);
      } else {
        docRef.set(dados);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  public void updateCustomValueByLoja(Integer numeroFilial, FirebaseFieldEnum key, Object value) {
    try {
      DocumentReference docRef = db.collection(COLLECTION_NAME).document(numeroFilial.toString());

      if (!this.verificarSeExisteFilial(docRef)) {
        log.error(FILIAL_NAO_EXISTE_NO_FIREBASE, numeroFilial);
        return;
      }

      Map<String, Object> data = new HashMap<>();
      data.put(key.getField(), value);
      docRef.update(data);
    } catch (InterruptedException | ExecutionException | NullPointerException e) {
      log.error(e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  public void updateCustomValuesByLoja(Integer numeroFilial, Map<FirebaseFieldEnum, Object> values) {
    try {
      DocumentReference docRef = db.collection(COLLECTION_NAME).document(numeroFilial.toString());

      if (!this.verificarSeExisteFilial(docRef)) {
        log.error(FILIAL_NAO_EXISTE_NO_FIREBASE, numeroFilial);
        return;
      }

      Map<String, Object> data = values.entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().getField(), Map.Entry::getValue));
      docRef.update(data);
    } catch (InterruptedException | ExecutionException | NullPointerException e) {
      log.error(e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  public boolean verificarSeExisteFilial(DocumentReference docRef) throws InterruptedException, ExecutionException {
    ApiFuture<DocumentSnapshot> documentSnapshot = docRef.get();
    DocumentSnapshot doc = documentSnapshot.get();
    return doc.exists();
  }

  public void saveOrUpdateBatch(List<FilialFirebaseDTO> filiais) {
    try {
      WriteBatch batch = db.batch();

      for (FilialFirebaseDTO filial : filiais) {
        Map<String, Object> dados = new HashMap<>();
        dados.put(FirebaseFieldEnum.NOME_LOJA.getField(), filial.getNomeLoja());
        dados.put(FirebaseFieldEnum.NOVOS_PEDIDOS.getField(), filial.getNovosPedidos());
        dados.put(FirebaseFieldEnum.RANKING_LOJA.getField(), filial.getRankingLoja());
        dados.put(FirebaseFieldEnum.PEDIDOS_PROCESSADOS_PRAZO.getField(), filial.getPedidosProcessadosPrazo());
        dados.put(FirebaseFieldEnum.TOTAL_PEDIDOS_LOJA.getField(), filial.getTotalPedidosLoja());
        dados.put(FirebaseFieldEnum.TREND_UP.getField(), filial.getTrendUp());
        dados.put(FirebaseFieldEnum.TOTAL_PEDIDOS_LOJA_DESTAQUE.getField(), filial.getTotalPedidosLoja());
        dados.put(FirebaseFieldEnum.LOJA_DESTAQUE.getField(), filial.getLojaDestaque());
        dados.put(FirebaseFieldEnum.PEDIDOS_PROCESSADOS_PRAZO_DESTAQUE.getField(),
          filial.getPedidosProcessadosPrazoDestaque());
        dados.put(FirebaseFieldEnum.TOTAL_PEDIDOS_LOJA_DESTAQUE.getField(), filial.getTotalPedidosLojaDestaque());

        DocumentReference filialRef = db.collection(COLLECTION_NAME).document(String.valueOf(filial.getNumeroFilial()));
        batch.set(filialRef, dados);
      }

      batch.commit();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public List<FilialFirebaseDTO> buscarTodasAsFiliais() {
    try {
      List<QueryDocumentSnapshot> documents = db.collection(COLLECTION_NAME).get().get().getDocuments();
      return documents.stream().map(doc -> {
        FilialFirebaseDTO dto = doc.toObject(FilialFirebaseDTO.class);
        dto.setNumeroFilial(Integer.valueOf(doc.getId()));
        return dto;
      }).collect(Collectors.toList());
    } catch (InterruptedException | ExecutionException e) {
      log.error(e.getMessage());
      Thread.currentThread().interrupt();
      return new ArrayList<>();
    }
  }
}