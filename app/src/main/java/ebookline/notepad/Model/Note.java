package ebookline.notepad.Model;

public class Note
{
    int id;
    int pin;
    int category;
    int deleted;

    String title;
    String text;
    String color;
    String aTime;
    String cTime;

    public Note(int id,
                int pin,
                int category,
                String title,
                String text,
                String color,
                String aTime,
                String cTime,int deleted)
    {
        this.id = id;
        this.pin = pin;
        this.title = title;
        this.text = text;
        this.category = category;
        this.color = color;
        this.aTime = aTime;
        this.cTime = cTime;
        this.deleted = deleted;
    }

    public Note() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getaTime() {
        return aTime;
    }

    public void setaTime(String aTime) {
        this.aTime = aTime;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }
}
