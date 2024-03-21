package nebula.client.listener.event.render.overlay;

/**
 * @author Gavin
 * @since 03/20/24
 */
public final class EventRenderTabPlayerName {
  private String content;

  public EventRenderTabPlayerName(String content) {
    this.content = content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
