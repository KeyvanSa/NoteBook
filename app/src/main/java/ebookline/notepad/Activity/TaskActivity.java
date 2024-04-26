package ebookline.notepad.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import ebookline.notepad.Adapter.TaskAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Dialogs.ColorPickerDialog;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Model.Task;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityTaskBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TaskActivity extends AppCompatActivity implements TaskAdapter.ItemCheckListener , TaskAdapter.ItemClickListener
{
    ActivityTaskBinding binding;

    HelperClass helper;
    DBHelper db;

    private List<Task> taskList;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_task);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper=new HelperClass(this);
        db=new DBHelper(this);

        getTaskList();

        binding.menuItemAddTask.setOnClickListener(view -> {
            addTask(null);
            binding.menu.close(true);
        });
        
    }

    private void addTask(Task newTask)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View addTaskView = View.inflate(this,R.layout.layout_bottom_sheet_add_task,null);

        EditText editTextTaskTitle = addTaskView.findViewById(R.id.edittextTitle);
        ImageView imageViewChooseColor = addTaskView.findViewById(R.id.imageViewChooseColor);
        CheckBox checkBoxIsChecked = addTaskView.findViewById(R.id.checkBoxIsChecked);
        Button buttonAddTask = addTaskView.findViewById(R.id.buttonAddCategory);

        if(newTask!=null) {
            task = newTask;
            editTextTaskTitle.setText(newTask.getTitle());
            if(task.getCheck()==1)
                checkBoxIsChecked.setChecked(true);

            buttonAddTask.setText(getString(R.string.task_edit));
        } else {
            task = new Task();
            task.setColor(Constants.TaskColorsList.get(0));

            buttonAddTask.setText(getString(R.string.task_add));
        }

        Drawable progressDrawable = imageViewChooseColor.getDrawable();
        progressDrawable.setColorFilter(Color.parseColor(task.getColor()),android.graphics.PorterDuff.Mode.SRC_IN);
        imageViewChooseColor.setImageDrawable(progressDrawable);

        imageViewChooseColor.setOnClickListener(view ->
        {
            ColorPickerDialog dialog = new ColorPickerDialog(this);
            dialog.setColorsList(Constants.TaskColorsList);
            dialog.setOnClickButtonListener(new ColorPickerDialog.OnClickButtonListener() {
                @Override
                public void chooseColor(String colorCode) {
                    Drawable progressDrawable = imageViewChooseColor.getDrawable();
                    progressDrawable.setColorFilter(Color.parseColor(colorCode),android.graphics.PorterDuff.Mode.SRC_IN);
                    imageViewChooseColor.setImageDrawable(progressDrawable);

                    task.setColor(colorCode);
                }
                @Override
                public void chooseBack() {}
            });
            dialog.showDialog();
        });

        buttonAddTask.setOnClickListener(view -> {

            if(editTextTaskTitle.getText().toString().length()==0){
                helper.showToast(getResources().getString(R.string.enter_title),2);
                return;
            }

            task.setTitle(editTextTaskTitle.getText().toString());
            if(checkBoxIsChecked.isChecked())
                task.setCheck(1);
            else task.setCheck(0);

            if(newTask==null){
                if(db.addTask(task)>0){
                    helper.showToast(getResources().getString(R.string.task_add_successfully),3);
                }else helper.showToast(getResources().getString(R.string.task_add_denied),2);
            }else{
                if(db.updateTask(task)>0){
                    helper.showToast(getResources().getString(R.string.task_edit_successfully),3);
                }else helper.showToast(getResources().getString(R.string.task_edit_denied),2);
            }

            getTaskList();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(addTaskView);
        bottomSheetDialog.show();
    }

    private void getTaskList()
    {
        taskList=db.getTasks();
        TaskAdapter taskAdapter = new TaskAdapter(this, taskList);
        taskAdapter.setClickListener(this);
        taskAdapter.setCheckListener(this);
        binding.recyclerTasks.setAdapter(taskAdapter);

        if(taskAdapter.getItemCount()==0) {
            binding.relativeEmptyList.setVisibility(View.VISIBLE);
            binding.relativeShowProgressTasks.setVisibility(View.GONE);
            return;
        }

        binding.relativeEmptyList.setVisibility(View.GONE);
        binding.relativeShowProgressTasks.setVisibility(View.VISIBLE);

        int progress=0;
        for(Task t:taskList)
            if(t.getCheck()==1)
                progress++;

        binding.progressBarProgressTasks.setProgress((progress*100)/taskList.size());
        binding.textViewProgressTasks.setText(String.valueOf(binding.progressBarProgressTasks.getProgress()));

    }

    @Override
    public void onItemClick(View view, int position) {
        addTask(taskList.get(position));
    }

    @Override
    public boolean onItemLongClick(View view, int position)
    {
        CustomDialog dialog =new CustomDialog(TaskActivity.this);
        dialog.setTitle(String.format(getResources().getString(R.string.task_delete),""));
        dialog.setText(String.format(getResources().getString(R.string.task_delete),taskList.get(position).getTitle()));

        dialog.setButtonOkText(getResources().getString(R.string.ok));
        dialog.setButtonNoText(getResources().getString(R.string.no));
        dialog.setClickListener(new CustomDialog.ItemClickListener() {
            @Override
            public void onPositiveItemClick(View view1) {
                db.deleteTask(taskList.get(position));
                getTaskList();
            }
            @Override
            public void onNegativeItemClick(View view1) {}
        });
        dialog.showDialog();
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemCheck(View view, int position, boolean isChecked) {
        task = taskList.get(position);
        if(isChecked)
            task.setCheck(1);
        else task.setCheck(0);
        db.updateTask(task);
        getTaskList();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}