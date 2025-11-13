package ebookline.notepad.Model;

import android.graphics.drawable.Drawable;

public class Apps
{
    int id;
    Drawable icon;
    String name;
    String packageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Apps(int id, Drawable icon, String name, String packageName) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
    }
    public Apps(){}


}
