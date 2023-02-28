package com.clique.retire.repository.cosmos;

import java.util.List;

public interface ControleIntranetRepositoryCustom {

	Integer findFilialByIp(String ip);

	List<String> findIpsPorFilial(Integer idFilial);

}
