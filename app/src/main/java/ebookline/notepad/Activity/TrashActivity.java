package ebookline.notepad.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ebookline.notepad.Adapter.NoteAdapter;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityTrashBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TrashActivity extends AppCompatActivity implements NoteAdapter.ItemClickListener
{
    ActivityTrashBinding trashBinding;

    NoteAdapter adapter;

    Note note;

    DBHelper db;
    HelperClass helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        trashBinding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(trashBinding.getRoot());

        db = new DBHelper(this);
        helper = new HelperClass(this);

        try{
            getDeletedNotesList();
        }catch (Exception e){
            helper.exceptionHandler(e);
        }
    }

    private void getDeletedNotesList() {

        List<Note> list = db.getTrashNotes();

        adapter = new NoteAdapter(this,list);
        adapter.setClickListener(this);
        trashBinding.relativeNotes.setAdapter(adapter);

        if(adapter.getItemCount()==0)
            trashBinding.relativeEmptyList.setVisibility(View.VISIBLE);
        else trashBinding.relativeEmptyList.setVisibility(View.GONE);

    }

    @Override
    public void onItemClick(View view, int position) {
        note = adapter.getItem(position);

        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(getResources().getString(R.string.note_delete));
        dialog.setText(String.format(getResources().getString(R.string.note_delete_for_ever),note.getTitle()));
        dialog.setButtonOkText(getResources().getString(R.string.ok));
        dialog.setButtonNoText(getResources().getString(R.string.restore));

        dialog.setClickListener(new CustomDialog.ItemClickListener() {
            @Override
            public void onPositiveItemClick(View view) {
                if(db.deleteTrashNote(note)==1){
                    helper.showToast(getResources().getString(R.string.note_delete_success),3);
                    getDeletedNotesList();
                }else helper.showToast(getResources().getString(R.string.note_delete_denied),2);
            }

            @Override
            public void onNegativeItemClick(View view) {
                if(db.restoreTrashNote(note)>0){
                    helper.showToast(getResources().getString(R.string.note_add_successfully), 3);
                    getDeletedNotesList();
                }else helper.showToast(getResources().getString(R.string.note_edit_denied),2);

            }
        });
        dialog.showDialog();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}