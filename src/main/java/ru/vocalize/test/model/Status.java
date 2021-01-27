package ru.vocalize.test.model;

import lombok.Data;

@Data
public class Status {
  private Boolean success;

  private String message;

  private Status() {
  }

  public static Status ok() {
    Status response = new Status();
    response.success = true;
    response.message = null;
    return response;
  }

  public static Status error(String error) {
    Status response = new Status();
    response.success = false;
    response.message = error;
    return response;
  }

  public static Status message(String message) {
    Status response = new Status();
    response.success = true;
    response.message = message;
    return response;
  }
}
