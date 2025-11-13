package ebookline.notepad.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Apps;
import ebookline.notepad.Model.Menu;
import ebookline.notepad.Model.Receiver;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.LayoutBottomSheetAddReceiverBinding;

public class BottomSheetReceiver extends BottomSheetDialogFragment
{
    LayoutBottomSheetAddReceiverBinding binding;

    private ItemClickListener mClickListener;

    private Context context;
    private DBHelper db;
    private HelperClass helper;

    private Receiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutBottomSheetAddReceiverBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init()
    {
        if(receiver==null) {
            receiver = new Receiver();
            receiver.setType(Constants.SMS);
        } else {
            binding.buttonDeleteReceiver.setVisibility(View.VISIBLE);
            binding.buttonAddReceiver.setText(context.getResources().getString(R.string.receiver_edit));
        }

        binding.edittextTitle.setText(receiver.getTitle()==null?"":receiver.getTitle());
        binding.edittextText.setText(receiver.getText()==null?"":receiver.getText());
        binding.edittextSender.setText(receiver.getSender()==null?"":receiver.getSender());
        binding.edittextContain.setText(receiver.getContain()==null?"":receiver.getContain());
        binding.edittextInformation.setText(receiver.getInformation()==null?"":receiver.getInformation());
        binding.checkBoxIsEnable.setChecked(receiver.isEnable());

        binding.spinnerType.setText(
                receiver.getType()!=null&&receiver.getType().equals(Constants.APP)?
                        context.getResources().getString(R.string.receiver_type_app):
                        context.getResources().getString(R.string.receiver_type_sms));

        binding.spinnerType.setOnTouchListener((view1, motionEvent) ->
        {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                List<Menu> menuList=new ArrayList<>();
                menuList.add(new Menu(1,getResources().getString(R.string.receiver_type_sms),0));
                menuList.add(new Menu(2,getResources().getString(R.string.receiver_type_app),0));

                MenuDialog dialog = new MenuDialog(context);
                dialog.setList(menuList);
                dialog.setOnClickButtonListener(menu ->
                {
                    binding.edittextSender.setEnabled(true);

                    binding.spinnerType.setText(menu.getTitle(),false);

                    if(menu.getId()==1) {
                        binding.edittextSender.setEnabled(true);
                        receiver.setType(Constants.SMS);
                    }else{
                        BottomSheetApp bottomSheet=new BottomSheetApp(context);
                        bottomSheet.setClickListener(app ->
                        {
                            binding.edittextSender.setText(app.getPackageName());
                            binding.edittextSender.setEnabled(false);
                            receiver.setType(Constants.APP);
                        });
                        bottomSheet.show(((FragmentActivity)context).getSupportFragmentManager(),bottomSheet.getTag());
                    }
                  });

                dialog.show(((FragmentActivity)context).getSupportFragmentManager(),dialog.getTag());
            }
            return true;
        });

        binding.buttonAddReceiver.setOnClickListener(view ->
        {
            if(TextUtils.isEmpty(Objects.requireNonNull(binding.edittextTitle.getText()).toString())){
                helper.showToast(context.getResources().getString(R.string.enter_title),2);
                return;
            }else receiver.setTitle(binding.edittextTitle.getText().toString());

            if(TextUtils.isEmpty(Objects.requireNonNull(binding.edittextText.getText()).toString())){
                helper.showToast(context.getResources().getString(R.string.enter_text),2);
                return;
            }else receiver.setText(binding.edittextText.getText().toString());

            if(TextUtils.isEmpty(Objects.requireNonNull(binding.edittextSender.getText()).toString())){
                helper.showToast(context.getResources().getString(R.string.enter_sender),2);
                return;
            }else receiver.setSender(binding.edittextSender.getText().toString());

            if(TextUtils.isEmpty(Objects.requireNonNull(binding.spinnerType.getText()).toString())){
                helper.showToast(context.getResources().getString(R.string.enter_type),2);
                return;
            }

            if(!TextUtils.isEmpty(Objects.requireNonNull(binding.edittextContain.getText()).toString()))
                receiver.setContain(binding.edittextContain.getText().toString());
            else receiver.setContain(null);

            if(!TextUtils.isEmpty(Objects.requireNonNull(binding.edittextInformation.getText()).toString()))
                receiver.setInformation(binding.edittextInformation.getText().toString());
            else receiver.setInformation(null);

            receiver.setEnable(binding.checkBoxIsEnable.isChecked());

            long state =0;
            if(receiver.getId()==0) {
                receiver.setTime(String.valueOf(System.currentTimeMillis()));
                state = db.addReceiver(receiver);
                if(state>0) {
                    helper.showToast(getResources().getString(R.string.receiver_added_successful), 3);
                } else helper.showToast(getResources().getString(R.string.receiver_added_denied),2);
            } else {
                state = db.updateReceiver(receiver);
                if(state>0) {
                    helper.showToast(getResources().getString(R.string.receiver_edited_successful), 3);
                } else helper.showToast(getResources().getString(R.string.receiver_edited_denied),2);
            }
            if(mClickListener!=null)
                mClickListener.onSave(receiver,state);
            dismiss();
        });

        binding.buttonDeleteReceiver.setOnClickListener(view ->
        {
            if(receiver==null)
                return;

            CustomDialog customDialog = new CustomDialog(context);
            customDialog.setTitle(context.getResources().getString(R.string.receiver_delete));
            customDialog.setText(receiver.getTitle());
            customDialog.setButtonOkText(context.getResources().getString(R.string.ok));
            customDialog.setButtonNoText(context.getResources().getString(R.string.no));
            customDialog.setClickListener(new CustomDialog.ItemClickListener() {
                @Override
                public void onPositiveItemClick(View view) {

                    int delete= db.deleteReceiver(receiver);
                    if(delete>0)
                        helper.showToast(getResources().getString(R.string.receiver_deleted_successful), 3);
                    else helper.showToast(getResources().getString(R.string.receiver_deleted_denied),2);

                    if(mClickListener!=null)
                        mClickListener.onDelete(receiver, receiver.getId());

                    dismiss();
                }
                @Override
                public void onNegativeItemClick(View view) {
                    customDialog.dismiss();
                }
            });
            customDialog.showDialog();
        });

        if(receiver.getInformation()!=null&&!TextUtils.isEmpty(binding.edittextInformation.getText().toString()))
            binding.textViewResetInformation.setVisibility(View.VISIBLE);
        binding.textViewResetInformation.setOnClickListener(view -> {
            binding.edittextInformation.setText("");
            binding.textViewResetInformation.setVisibility(View.GONE);
        });
    }

    public BottomSheetReceiver(Context context){
        this.context=context;
        db= new DBHelper(context);
        helper = new HelperClass(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onSave(Receiver receiver,long state);
        void onDelete(Receiver receiver,long id);
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.setTheme(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
