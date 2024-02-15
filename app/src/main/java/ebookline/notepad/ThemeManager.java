package ebookline.notepad;

import android.content.Context;
import android.content.res.Resources;

import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;

abstract public class ThemeManager
{
    public static void setTheme(Context context) {

        SharedHelper shared=new SharedHelper(context);

        int theme = shared.getInt(Constants.THEME,1);

        if (theme==1) {
            context.setTheme(R.style.Theme_NotePad);
        }

        if (theme==2) {
            context.setTheme(R.style.DarkTheme_NotePad);
        }
    }
}
