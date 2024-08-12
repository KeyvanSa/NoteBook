package ebookline.notepad.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ebookline.notepad.Adapter.CategoryAdapter;
import ebookline.notepad.Adapter.CategoryAdapter2;
import ebookline.notepad.Adapter.TaskAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Dialogs.ColorPickerDialog;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Task;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityTaskBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TaskActivity extends AppCompatActivity implements
        TaskAdapter.ItemCheckListener , TaskAdapter.ItemClickListener ,
        CategoryAdapter2.ItemClickListener , CategoryAdapter2.ItemLongClickListener
{
    ActivityTaskBinding binding;

    HelperClass helper;
    DBHelper db;

    private List<Task> taskList;

    private  ArrayList<Category> categoryList;

    private Task task;

    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_task);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper=new HelperClass(this);
        db=new DBHelper(this);

        getCategoriesList();
        getTaskList(null,null);

        binding.menuItemAddTask.setOnClickListener(view -> {
            addTask(null);
            binding.menu.close(true);
        });

        binding.menuItemAddCategory.setOnClickListener(view -> {
            addCategory(null);
            binding.menu.close(true);
        });

        binding.recyclerTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(binding.menu.isShown())
                        binding.menu.hideMenu(true);
                }else
                if(!binding.menu.isShown())
                    binding.menu.showMenu(true);
            }
        });
        
    }

    private void addCategory(Category newCategory)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View addCategoryView = View.inflate(this,R.layout.layout_bottom_sheet_add_category,null);

        EditText editTextCategoryTitle = addCategoryView.findViewById(R.id.edittextTitle);
        Button buttonAddCategory = addCategoryView.findViewById(R.id.buttonAddCategory);
        ImageView imageViewChooseColor = addCategoryView.findViewById(R.id.imageViewChooseColor);

        category = new Category();
        if(newCategory!=null) {
            category=newCategory;

            editTextCategoryTitle.setText(newCategory.getTitle().substring(0, newCategory.getTitle().indexOf("(")));
            buttonAddCategory.setText(getString(R.string.category_edit));
        }else category.setColor(Constants.categoryColorsList.get(0));

        Drawable progressDrawable = imageViewChooseColor.getDrawable();
        progressDrawable.setColorFilter(Color.parseColor(category.getColor()),android.graphics.PorterDuff.Mode.SRC_IN);
        imageViewChooseColor.setImageDrawable(progressDrawable);

        imageViewChooseColor.setOnClickListener(view ->
        {
            ColorPickerDialog dialog = new ColorPickerDialog(this);
            dialog.setColorsList(Constants.categoryColorsList);
            dialog.setOnClickButtonListener(new ColorPickerDialog.OnClickButtonListener() {
                @Override
                public void chooseColor(String colorCode) {
                    Drawable progressDrawable = imageViewChooseColor.getDrawable();
                    progressDrawable.setColorFilter(Color.parseColor(colorCode),android.graphics.PorterDuff.Mode.SRC_IN);
                    imageViewChooseColor.setImageDrawable(progressDrawable);

                    category.setColor(colorCode);
                }
                @Override
                public void chooseBack() {}
            });
            dialog.showDialog();
        });

        buttonAddCategory.setOnClickListener(view1 -> {

            if(editTextCategoryTitle.getText().toString().length()==0){
                helper.showToast(getResources().getString(R.string.enter_title),2);
                return;
            }

            category.setTitle(editTextCategoryTitle.getText().toString());
            category.setParent(0);

            if(newCategory==null){
                if(db.addTaskCategory(category)>0){
                    helper.showToast(getResources().getString(R.string.category_add_successfully),3);
                }else helper.showToast(getResources().getString(R.string.category_add_denied),2);
            }else{
                if(db.updateTaskCategory(category)>0){
                    helper.showToast(getResources().getString(R.string.category_edit_successfully),3);
                }else helper.showToast(getResources().getString(R.string.category_edit_denied),2);
            }

            getCategoriesList();

            if(taskList.size() == db.count(Constants.TBL_TASK_NAME,null))
                getTaskList(null ,null);
            else getTaskList(Constants.CATEGORY+"="+category.getId(),null);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(addCategoryView);
        bottomSheetDialog.show();
    }

    private void addTask(Task newTask)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View addTaskView = View.inflate(this,R.layout.layout_bottom_sheet_add_task,null);

        EditText editTextTaskTitle = addTaskView.findViewById(R.id.edittextTitle);
        AutoCompleteTextView spinnerCategory = addTaskView.findViewById(R.id.spinnerCategory);
        ImageView imageViewChooseColor = addTaskView.findViewById(R.id.imageViewChooseColor);
        CheckBox checkBoxIsChecked = addTaskView.findViewById(R.id.checkBoxIsChecked);
        Button buttonAddTask = addTaskView.findViewById(R.id.buttonAddCategory);

        List<Category> taskCategoryList = db.getTaskCategories();
        CategoryAdapter categoryAdapter=
                new CategoryAdapter(this, R.layout.item_category , R.id.textViewTitle,taskCategoryList);
        spinnerCategory.setAdapter(categoryAdapter);

        task = new Task();
        if(newTask!=null) {
            task = newTask;
            editTextTaskTitle.setText(newTask.getTitle());
            if(task.getCheck()==1)
                checkBoxIsChecked.setChecked(true);

            buttonAddTask.setText(getString(R.string.task_edit));

            for(int i=0;i<taskCategoryList.size();i++){
                if(newTask.getCategory()==taskCategoryList.get(i).getId()){
                    spinnerCategory.setText(taskCategoryList.get(i).getTitle(),false);
                }
            }
        } else task.setColor(Constants.TaskColorsList.get(0));

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

            for(int i=0;i<taskCategoryList.size();i++){
             if(taskCategoryList.get(i).getTitle().equals(spinnerCategory.getText().toString().trim()))
                 task.setCategory(taskCategoryList.get(i).getId());
            }

            if(newTask==null){
                if(db.addTask(task)>0){
                    helper.showToast(getResources().getString(R.string.task_add_successfully),3);
                }else helper.showToast(getResources().getString(R.string.task_add_denied),2);
            }else{
                if(db.updateTask(task)>0){
                    helper.showToast(getResources().getString(R.string.task_edit_successfully),3);
                }else helper.showToast(getResources().getString(R.string.task_edit_denied),2);
            }

            if(taskList.size() == db.count(Constants.TBL_TASK_NAME,null))
                getTaskList(null ,null);
            else getTaskList(Constants.CATEGORY+"="+task.getCategory(),null);

            getCategoriesList();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(addTaskView);
        bottomSheetDialog.show();
    }

    private void getTaskList(String selection , String sort)
    {
        taskList=db.getTasks(selection,sort);
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

        if(progress==0) {
            binding.relativeShowProgressTasks.setVisibility(View.GONE);
        }else{
            binding.relativeShowProgressTasks.setVisibility(View.VISIBLE);
            binding.progressBarProgressTasks.setProgress((progress*100)/taskList.size());
            binding.textViewProgressTasks.setText(String.valueOf(binding.progressBarProgressTasks.getProgress()));
        }
    }

    private void getCategoriesList()
    {
        categoryList = new ArrayList<>();

        categoryList.add(new Category(-1 ,0,getResources().getString(R.string.all),Constants.categoryColorsList.get(0)));

        categoryList.addAll(db.getTaskCategories());

        for(Category category : categoryList){

            int count;
            if(category.getId()== -1)
                count = db.count(Constants.TBL_TASK_NAME,null);
            else count = db.count(Constants.TBL_TASK_NAME,Constants.CATEGORY+"="+category.getId());

            category.setTitle(String.format(Locale.ROOT,"%s(%d)", category.getTitle(),count));
        }

        CategoryAdapter2 categoryAdapter = new CategoryAdapter2(this,categoryList);
        categoryAdapter.setClickListener(this);
        categoryAdapter.setLongClickListener(this);
        binding.recyclerViewCategories.setAdapter(categoryAdapter);
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
                getTaskList(null,null);
            }
            @Override
            public void onNegativeItemClick(View view1) {}
        });
        dialog.showDialog();
        return false;
    }

    @Override
    public void onCategoryItemClick(View view, int position) {
        if(position==0)
            getTaskList(null,null);
        else getTaskList(Constants.CATEGORY+"="+categoryList.get(position).getId(),null);
    }

    @Override
    public void onCategoryItemLongClick(View view, int position) {

        if(position==0||position==1)
            return;

        category = categoryList.get(position);

        CustomDialog dialog = new CustomDialog(TaskActivity.this);
        dialog.setTitle(getResources().getString(R.string.options));
        dialog.setText(getResources().getString(R.string.category)+" "+ category.getTitle().substring(0,category.getTitle().indexOf("(")));
        dialog.setButtonOkText(getResources().getString(R.string.category_delete));
        dialog.setButtonNoText(getResources().getString(R.string.category_edit));

        dialog.setClickListener(new CustomDialog.ItemClickListener() {
            @Override
            public void onPositiveItemClick(View view) {
                int iDelete = db.deleteTaskCategory(category);
                if(iDelete>0) {
                    helper.showToast(getResources().getString(R.string.category_delete_success), 3);

                    getTaskList(null ,null);
                    getCategoriesList();
                }
                else helper.showToast(getResources().getString(R.string.category_delete_denied),2);
            }
            @Override
            public void onNegativeItemClick(View view) {
                addCategory(category);
            }
        });

        dialog.showDialog();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemCheck(View view, int position, boolean isChecked) {
        task = taskList.get(position);
        if(isChecked)
            task.setCheck(1);
        else task.setCheck(0);
        db.updateTask(task);

        int scrollPosition = binding.recyclerTasks.computeVerticalScrollOffset();

        if(taskList.size() == db.count(Constants.TBL_TASK_NAME,null))
            getTaskList(null ,null);
        else getTaskList(Constants.CATEGORY+"="+taskList.get(position).getCategory(),null);

        binding.recyclerTasks.smoothScrollToPosition(scrollPosition);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}