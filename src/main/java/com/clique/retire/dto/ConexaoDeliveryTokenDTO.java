package com.clique.retire.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class ConexaoDeliveryTokenDTO {

  private DataDTO data;

  public String getToken() {
    return Objects.nonNull(data) ? data.token : null;
  }

  @Data
  static class DataDTO {
    private String userId;
    private String login;
    private String userName;
    private String token;
  }

}
