package ebookline.notepad.Model;

public class Receiver
{
    int id;

    boolean enable;

    String title;
    String text;
    String sender;
    String time;
    String information;
    String contain;
    String type;

    public Receiver(int id, boolean enable, String title,
                    String text, String sender, String time,
                    String information, String contain, String type)
    {
        this.id = id;
        this.enable = enable;
        this.title = title;
        this.text = text;
        this.sender = sender;
        this.time = time;
        this.information = information;
        this.contain = contain;
        this.type = type;
    }

    public Receiver(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getContain() {
        return contain;
    }

    public void setContain(String contain) {
        this.contain = contain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
