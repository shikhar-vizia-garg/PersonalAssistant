package shikhar.personalassistant;

/**
 * Created by Shikhar Garg on 03-04-2016.
 */
public class ReminderModel {
    String title;
    String content;
    String deadline;

    public ReminderModel() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
