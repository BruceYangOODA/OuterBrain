package nor.zero.outer_brain.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import nor.zero.outer_brain.AlarmReceiver;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.Constants.*;

public class EditorDialogFragment extends DialogFragment {
    int position;
    LinkedList<ArrayList<String>> dataList;
    View dialogView;
    boolean isEditor;   //true 修改清單 ; false 新增清單
    String[] sp = {"aaa","bbb","ccc"};
    TextView tvItemDate, tvItemTime,tvItemRing ;
    DatePickerFragment datePickerFragment;
    TimePickerFragment timePickerFragment;
    RingPickerFragment ringPickerFragment;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;

    final static int REQUEST_CODE_DATE = 1;
    final static int REQUEST_CODE_TIME = 2;
    final static int REQUEST_CODE_RING = 3;
    final static int REQUEST_CODE_ALARMSET = 4;
    final static String PICK_DATE = "pickDate";
    final static String PICK_TIME = "pickTime";

    public EditorDialogFragment(boolean isEditor,int position, LinkedList<ArrayList<String>> dataList){
        this.position = position;
        this.dataList = dataList;
        this.isEditor = isEditor;
    }

/*
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.item_shopping_editor,null);
        getDialog().setTitle(getString(R.string.tv_item_topic));

        EditText e = view.findViewById(R.id.tvItemTitle);
        e.setText(dataList.get(position).get(0));
        e = view.findViewById(R.id.tvItemGrocery);
        e.setText(dataList.get(position).get(1));
        return view;
    }
*/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       // Dialog dialog = super.onCreateDialog(savedInstanceState);
       // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_shopping_editor,null);
        builder.setView(dialogView)
                .setCancelable(true);
             //   .setTitle(getString(R.string.tv_item_topic))

        //        .setNegativeButton(getString(R.string.btn_cancel),null)
       //         .setPositiveButton(getString(R.string.btn_edit),null);
        initDialogView();

        return builder.create();
    }

    private int getSelectSpinnerId(){
        int result = 0;
        String location = dataList.get(position).get(ITEM_LOCATION);
        for(int i=0;i<sp.length;i++){
            if (sp[i].equals(location))
                return i;
        }
        return result;
    }

    private void initDialogView(){
        EditText etItemTitle = dialogView.findViewById(R.id.etItemTitle);
        EditText etItemGrocery = dialogView.findViewById(R.id.etItemGrocery);
        tvItemDate = dialogView.findViewById(R.id.tv_item_date);
        tvItemTime = dialogView.findViewById(R.id.tv_item_time);
        tvItemRing = dialogView.findViewById(R.id.tv_item_ring);

        radioGroup = dialogView.findViewById(R.id.radioGroup);

        if(isEditor){
            etItemTitle.setText(dataList.get(position).get(ITEM_TITLE));
            etItemGrocery.setText(dataList.get(position).get(ITEM_GROCERY));
            tvItemDate.setText(dataList.get(position).get(ITEM_DATE));

            Spinner spLocation = dialogView.findViewById(R.id.spinner);
            spLocation.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.spiner_location,sp));
            spLocation.setSelection(getSelectSpinnerId());
        }
        else{}

        tvItemDate.setOnClickListener(tvItemDateClick);
        tvItemTime.setOnClickListener(tvItemTimeClick);
        tvItemRing.setOnClickListener(tvItemRingClick);
        radioGroup.setOnCheckedChangeListener(radioGroupClick);
        TextView tvTopic = dialogView.findViewById(R.id.tv_item_topic);
        tvTopic.setText(isEditor ? getString(R.string.tv_item_topic_editor):
                getString(R.string.tv_item_topic_adder));
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        btnEdit.setText(isEditor? getString(R.string.btn_edit): getString(R.string.btn_add));
        btnEdit.setOnClickListener(btnEditClick);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(btnCancelClick);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(btnDeleteClick);
    }

    View.OnClickListener btnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditorDialogFragment.this.dismiss();
        }
    };
    View.OnClickListener btnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    View.OnClickListener btnEditClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    View.OnClickListener tvItemDateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            datePickerFragment = new DatePickerFragment(tvItemDate.getText().toString());
            datePickerFragment.setTargetFragment(EditorDialogFragment.this,REQUEST_CODE_DATE);
            datePickerFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
        }
    };
    View.OnClickListener tvItemTimeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timePickerFragment = new TimePickerFragment(tvItemTime.getText().toString());
            timePickerFragment.setTargetFragment(EditorDialogFragment.this,REQUEST_CODE_TIME);
            timePickerFragment.show(getActivity().getSupportFragmentManager(),"timePicker");
        }
    };
    View.OnClickListener tvItemRingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ringPickerFragment = new RingPickerFragment();
            ringPickerFragment.setTargetFragment(EditorDialogFragment.this,REQUEST_CODE_RING);
            ringPickerFragment.show(getActivity().getSupportFragmentManager(),"ringPicker");

        }
    };
    RadioGroup.OnCheckedChangeListener radioGroupClick = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case 1:

                    break;
                case 2:
                    String temp = tvItemTime.getText().toString();
                    if(!temp.equals("")){
                        setAlarmTime(temp); }
                    break;
                case 3:

                    break;
                    default:
                        break;
            }
        }
    };

    private void setAlarmTime(String temp){
        int hour = Integer.parseInt(temp.split(":")[0]);
        int minutes = Integer.parseInt(temp.split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minutes);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long timer = calendar.getTimeInMillis();
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                REQUEST_CODE_ALARMSET,intent,0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,timer,pendingIntent);
      //  alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(10*1000),
        //        0,pendingIntent);
        //getActivity().sendBroadcast();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_DATE){    // 從 getTargetFragment 傳回來的訊息
            String pickDay = data.getStringExtra(PICK_DATE);
            tvItemDate.setText(pickDay);
            datePickerFragment.dismiss();
        }
        else if(requestCode == REQUEST_CODE_TIME){
            String pickTime = data.getStringExtra(PICK_TIME);
            tvItemTime.setText(pickTime);
            timePickerFragment.dismiss();
        }
        else if(requestCode == REQUEST_CODE_RING){

            tvItemRing.setText("");
            ringPickerFragment.dismiss();
        }
    }


}
