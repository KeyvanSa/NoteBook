package ebookline.notepad.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Adapter.AppAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Apps;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.LayoutBottomSheetAppsListBinding;

public class BottomSheetApp extends BottomSheetDialogFragment
{
    LayoutBottomSheetAppsListBinding binding;

    private ItemClickListener mClickListener;

    private Context context;
    private DBHelper db;
    private HelperClass helper;

    private ArrayList<Apps> mainList;
    private ArrayList<Apps> searchList;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutBottomSheetAppsListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init()
    {
        new Thread(() -> {

            getInstalledApps();

            handler.post(() ->
            {
                setAdapter(mainList);
                binding.progressBarLoading.setVisibility(View.GONE);
            });

        }).start();

        binding.edittextAppName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0)
                    getSearchList(null);
                else getSearchList(charSequence.toString());
                setAdapter(searchList);
            }
        });

    }

    private void setAdapter(ArrayList<Apps> appsList)
    {
        if(appsList==null || appsList.size()==0){
            binding.recyclerApps.setAdapter(null);
            return;
        }

        AppAdapter adapter=new AppAdapter(context,appsList);
        adapter.setClickListener((app, position) ->
        {
            this.dismiss();
            if(mClickListener==null)
                return;
            mClickListener.onSelect(app);
        });
        binding.recyclerApps.setAdapter(adapter);
    }

    private void getInstalledApps()
    {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applicationList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        mainList=new ArrayList<>();

        for (ApplicationInfo appInfo:applicationList)
        {
            Apps app=new Apps();
            app.setName(appInfo.loadLabel(pm).toString());
            app.setPackageName(appInfo.packageName);
            app.setIcon(appInfo.loadIcon(context.getPackageManager()));
            mainList.add(app);
        }
    }

    private void getSearchList(String text)
    {
        if(searchList==null)
            searchList=new ArrayList<>();
        else searchList.clear();

        for (Apps app:mainList) {
           if(text==null || TextUtils.isEmpty(text))
               searchList.add(app);
           else
               if(app.getName().contains(text) || app.getPackageName().contains(text))
                   searchList.add(app);
        }
        setAdapter(searchList);
    }

    public BottomSheetApp(Context context){
        this.context=context;
        db= new DBHelper(context);
        helper = new HelperClass(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onSelect(Apps app);
    }
}
