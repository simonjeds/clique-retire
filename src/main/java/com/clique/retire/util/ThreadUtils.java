package com.clique.retire.util;

public class ThreadUtils {

  private ThreadUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static void execute(Runnable runnable) {
    new Thread(runnable).start();
  }

  public static void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

}
