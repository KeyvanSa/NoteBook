package ebookline.notepad.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import ebookline.notepad.Adapter.FilePickerAdapter;
import ebookline.notepad.Model.FileModel;
import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class FilePickerDialog extends Dialog implements FilePickerAdapter.ItemClickListener ,
                                                        View.OnClickListener
{
    Context context;
    HelperClass helper;

    RecyclerView recyclerView;
    ImageView imageViewInfo;
    Button buttonPositive , buttonNegative;
    TextView textViewTitle;

    LinearLayout linearLayoutEmptyList;

    FilePickerAdapter adapter;
    File file ;

    OnClickButtonListener onClickButtonListener;

    private boolean fileSelection=true;
    private boolean showHiddenFiles=true;
    private String strMainPath;
    private String strTitle;
    private String backButtonText;
    private String[] typeShowFiles;

    private void init()
    {
        helper = new HelperClass(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_filepicker_dialog);

        setStrMainPath((getStrMainPath()==null) ? Environment.getExternalStorageDirectory().toString() : strMainPath);

        file = new File(getStrMainPath());

        linearLayoutEmptyList = this.findViewById(R.id.linearLayoutEmptyList);
        imageViewInfo = this.findViewById(R.id.imageViewInfo);
        buttonPositive = this.findViewById(R.id.buttonPositive);
        buttonNegative = this.findViewById(R.id.buttonNegative);
        textViewTitle = this.findViewById(R.id.textViewTitle);
        buttonPositive.setOnClickListener(this);
        buttonNegative.setOnClickListener(this);
        recyclerView = this.findViewById(R.id.recycler);

        setBackButtonText( (getBackButtonText()==null) ? context.getResources().getString(R.string.back) : getBackButtonText());

        buttonPositive.setVisibility(isFileSelection() ? View.GONE : View.VISIBLE);
        if(buttonPositive.getVisibility()==View.GONE){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2.0f
            );
            buttonNegative.setLayoutParams(param);
        }

        textViewTitle.setText((getStrTitle()==null) ? context.getResources().getString(R.string.choose_folder) : getStrTitle());
        buttonPositive.setText((getBackButtonText()==null) ? context.getResources().getString(R.string.backup) : getBackButtonText());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!helper.hasWriteManagePermission())
                imageViewInfo.setVisibility(View.VISIBLE);
            else imageViewInfo.setVisibility(View.GONE);
        }

        imageViewInfo.setOnClickListener(view ->
                helper.showAlert(context.getResources().getString(R.string.access_management_storage),1));

        setRecyclerAdapter();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        recyclerView.getLayoutParams().height= height - ((35*height)/100) ;
    }

    @Override
    public void onItemClick(View view, int position)
    {
        FileModel fileModel = adapter.getItem(position);

        if(position==0 && getStrMainPath().equals(file.getPath())) {
            dismiss();
            return;
        }

        if(position==0)
            file = new File(Objects.requireNonNull(file.getParent()));
        else file = new File(fileModel.getPath());

        if(file.isFile()) {
            onClick(buttonPositive);
            dismiss();
            return;
        }

       setRecyclerAdapter();
    }

    @Override
    public void onClick(View view)
    {
        Button button = (Button) view;

        if(button.getId() == buttonPositive.getId()){

            if(onClickButtonListener==null)
                return;

            this.onClickButtonListener.chooseFolder(file);

            dismiss();

        }else if(button.getId() == buttonNegative.getId()){

            if(!onClickButtonListener.chooseBack()){

                if(getStrMainPath().equals(file.getPath())) {
                    dismiss();
                    return;
                }

                file = new File(Objects.requireNonNull(file.getParent()));

                setRecyclerAdapter();

            }else this.onClickButtonListener.chooseBack();

        }
    }

    private void setRecyclerAdapter() {
        adapter = new FilePickerAdapter(context,getList());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if(adapter.getItemCount()<=1)
            linearLayoutEmptyList.setVisibility(View.VISIBLE);
        else linearLayoutEmptyList.setVisibility(View.GONE);

    }

    private ArrayList<FileModel> getList()
    {
        ArrayList<FileModel> files = new ArrayList<>();

        files.add(new FileModel("",context.getResources().getString(R.string.back),""));

        if(file.isFile())
            return files;

        File[] filesList = file.listFiles();

        if(filesList==null || filesList.length==0)
            return files;

        if(isShowHiddenFiles() && isFileSelection())
            for(File f : filesList)
                if(f.getName().startsWith("."))
                    files.add(new FileModel(f.getPath(),
                            f.getName(),f.getName().substring(
                            f.getName().lastIndexOf(".")+1 )));

        for(File f : filesList)
            if(f.isDirectory() && !f.getName().startsWith("."))
                files.add(new FileModel(f.getPath(),
                        f.getName(),""));

        if(isFileSelection())
            for(File f : filesList)
                if(f.isFile() && !f.getName().startsWith("."))
                    if(addFileWithExtension(f))
                        files.add(new FileModel(f.getPath(),
                                f.getName(),f.getName().substring(
                                f.getName().lastIndexOf(".")+1 )));

        return files;
    }

    private boolean addFileWithExtension(File f)
    {
        if(f==null || f.isDirectory())
            return false;

        if(getTypeShowFiles()==null || getTypeShowFiles().length==0)
            return true;

        for(String str : getTypeShowFiles()){
            if(f.getName().substring(f.getName().lastIndexOf(".")+1)
                    .equals(str))
                return true;
        }

        if(f.getName().contains("notebook"))
            return true;

        return false;
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener){
        this.onClickButtonListener = onClickButtonListener;
    }

    public void showDialog(){
        init();
        show();
    }

    public FilePickerDialog(Context context){
        super(context);
        this.context=context;
    }

    public interface OnClickButtonListener {
        void chooseFolder(File folder);
        boolean chooseBack();
    }

    public void setTypeShowFiles(String[] typeShowFiles) {
        this.typeShowFiles = typeShowFiles;
    }

    public String[] getTypeShowFiles() {
        return typeShowFiles;
    }

    public void setBackButtonText(String backButtonText) {
        this.backButtonText = backButtonText;
    }

    public String getBackButtonText() {
        return backButtonText;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrMainPath(String strMainPath) {
        this.strMainPath = strMainPath;
    }

    public String getStrMainPath() {
        return strMainPath;
    }

    public void setShowHiddenFiles(boolean showHiddenFiles) {
        this.showHiddenFiles = showHiddenFiles;
    }

    public boolean isShowHiddenFiles() {
        return showHiddenFiles;
    }

    public void setFileSelection(boolean fileSelection) {
        this.fileSelection = fileSelection;
    }

    public boolean isFileSelection() {
        return fileSelection;
    }
}
