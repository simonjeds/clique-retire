package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PBMAutorizadorErrorDTO {

  @SerializedName("StatusCode")
  private String statusCode;

  @SerializedName("Message")
  private String message;

}
