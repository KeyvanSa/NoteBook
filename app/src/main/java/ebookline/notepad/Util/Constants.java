package ebookline.notepad.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants
{
    public static final String DB_NAME = "note.db";
    public static final int    DB_VERSION = 3;

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

    public static final String TBL_TASK_NAME = "task";
    public static final String IS_CHECKED    = "checked";

    public static final ArrayList<String> categoryColorsList =
            new ArrayList<>(Arrays.asList("#95afc0", "#ff7979", "#ffbe76", "#7ed6df", "#badc58"));

    public static final ArrayList<String> TextColorsList =
            new ArrayList<>(Arrays.asList("#000000", "#ff7979", "#ffbe76", "#7ed6df", "#badc58"));

    public static final ArrayList<String> TaskColorsList =
            new ArrayList<>(Arrays.asList("#95afc0", "#ff7979", "#ffbe76", "#7ed6df", "#badc58"));

    public static final ArrayList<String> SpecialCharactersColorsList =
            new ArrayList<>(Arrays.asList("#1E88E5", "#7CB342", "#00BCD4", "#E91E63", "#FF9800","#FD552A","#ffed37"));

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
    
    public static final String FIND_HASHTAG="fhashtag";
    public static final String FIND_MENTION="fmention";
    public static final String FIND_URL="furl";
    public static final String FIND_MAIL="fmail";
    public static final String FIND_PHONE="fphone";

    public static final String CLICK_HASHTAG="chashtag";
    public static final String CLICK_MENTION="cmention";
    public static final String CLICK_URL="curl";
    public static final String CLICK_MAIL="cmail";
    public static final String CLICK_PHONE="cphone";

    public static final String COLOR_HASHTAG="cohashtag";
    public static final String COLOR_MENTION="comention";
    public static final String COLOR_URL="courl";
    public static final String COLOR_MAIL="comail";
    public static final String COLOR_PHONE="cophone";

    public static final boolean bazzar_or_myket = false; // true is bazzar

}
