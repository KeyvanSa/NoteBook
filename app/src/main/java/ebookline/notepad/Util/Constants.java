package ebookline.notepad.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants
{
    public static final String DB_NAME = "note.db";
    public static final int    DB_VERSION = 2;

    public static final String ID = "id";
    public static final String COLOR = "color";
    public static final String TITLE = "title";

    public static final String TBL_NOTE_NAME = "notes";
    public static final String TEXT = "text";
    public static final String CATEGORY = "category";
    public static final String PIN = "pin";
    public static final String ATIME = "atime";
    public static final String ETIME = "etime";
    public static final String DELETED = "deleted";

    public static final String TBL_CATEGORY_NAME = "category";
    public static final String PARENT = "parent";

    public static final long DAYS_PAST_TO_DELETE_TRASH_NOTES = 30;

    public static final ArrayList<String> categoryColorsList =
            new ArrayList<>(Arrays.asList("#95afc0", "#ff7979", "#ffbe76", "#7ed6df", "#badc58"));

    public static final ArrayList<String> TextColorsList =
            new ArrayList<>(Arrays.asList("#000000", "#ff7979", "#ffbe76", "#7ed6df", "#badc58"));

    public static final String THEME="theme";

    public static final String TEXT_COLOR="text_color";

    public static final String PASSWORD="password";
    public static final String SECURITY_TEXT="sec";
    public static final String USE_PASSWORD="use_password";
    public static final String USE_FINGERPRINT="use_fingerprint";

    public static final String USE_EXPIRED_NOTE="use_expired_note";

    public static final String LANGUAGE="language";
    public static final String ENGLISH="en";
    public static final String PERSIAN="fa";

    public static final boolean bazzar_or_myket = false; // true is bazzar

}
