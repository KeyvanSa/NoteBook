package ebookline.notepad.Activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ebookline.notepad.ThemeManager;
import ebookline.notepad.databinding.ActivityAboutBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutActivity extends AppCompatActivity
{
    ActivityAboutBinding about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        about = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(about.getRoot());

        about.appbar.setExpanded(false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


}