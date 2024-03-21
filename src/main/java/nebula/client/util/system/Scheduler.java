package nebula.client.util.system;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class Scheduler {

  public static void schedule(Runnable task, long in) {
    new Thread(() -> {
      try {
        Thread.sleep(in);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      task.run();
    }, "Task").start();
  }
}
