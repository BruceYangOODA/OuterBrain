package nor.zero.outer_brain.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import nor.zero.outer_brain.AlarmReceiver;
import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.Constants.*;
import static nor.zero.outer_brain.MyDatabaseDAO.*;

public class ShoppingEditorFragment extends DialogFragment {
    MyDatabaseDAO dao;
    int position;
    LinkedList<HashMap<String,String>> dataList;//,shopItems;
    LinkedList<ContentValues> shopItems;
    View dialogView;
    boolean isEditor;   //true 修改清單 ; false 新增清單
    TextView tvDate, tvTime,tvRing,tvUri;
    EditText etItemTitle,etItemGrocery;
    Spinner spinnerShop;
    DatePickerFragment datePickerFragment;
    TimePickerFragment timePickerFragment;
    RingPickerFragment ringPickerFragment;
    SmartInputListFragment smartInputListFragment;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    String[] shopList;
    int selection =0;   //radioGroup 選取第幾項

    final static int REQUEST_CODE_DATE = 1;
    final static int REQUEST_CODE_TIME = 2;
    final static int REQUEST_CODE_RING = 3;
    final static int REQUEST_CODE_ALARMSET = 4;
    final static String PICK_DATE = "pickDate";
    final static String PICK_TIME = "pickTime";
    final static String PICK_RING_TITLE = "ringTitle";
    final static String PICK_RING_URI = "ringUri";

    public ShoppingEditorFragment(boolean isEditor, int position,
                                  LinkedList<HashMap<String,String>> dataList){
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
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.item_shopping_editor,null);
        builder.setView(dialogView)
                .setCancelable(true);
             //   .setTitle(getString(R.string.tv_item_topic))
        //        .setNegativeButton(getString(R.string.btn_cancel),null)
       //         .setPositiveButton(getString(R.string.btn_edit),null);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dao == null)
            dao = new MyDatabaseDAO(getContext());
        initView();
    }


    private void loadData(){
        shopItems = new LinkedList<>();
        Cursor cursor = dao.getAllCursor(TABLE_SHOP_LOCATION);
        if(cursor == null || cursor.getCount()<1){
            Toast.makeText(getContext(),getString(R.string.sys_input_shop_name_first),Toast.LENGTH_SHORT).show();
            ShoppingEditorFragment.this.dismiss();
        }
        cursor.moveToFirst();
        shopList = new String[cursor.getCount()];
        for(int i=0;i<cursor.getCount();i++){
            shopList[i] = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_NAME));
            ContentValues values = new ContentValues();
            values.put(COLUMN_SHOP_NAME,cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_NAME)));
            values.put(COLUMN_SHOP_LATITUDE,cursor.getFloat(cursor.getColumnIndex(COLUMN_SHOP_LATITUDE)));
            values.put(COLUMN_SHOP_LONGITUDE,cursor.getFloat(cursor.getColumnIndex(COLUMN_SHOP_LONGITUDE)));
            shopItems.add(values);
            cursor.moveToNext();
        }
        cursor.close();
    }
    View.OnClickListener viewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.tvDate:
                    pickDate();
                    break;
                case R.id.tvTime:
                    pickTime();
                    break;
                case R.id.tvRing:
                    pickRing();
                    break;
                case R.id.btnSmartInput:
                    smartInput();
                    break;
                case R.id.btnCancel:
                    ShoppingEditorFragment.this.dismiss();
                    break;
                case R.id.btnDelete:
                    askForDelete();
                    break;
                case R.id.btnEdit:
                    if(isEditor)
                        updateDAO();
                    else
                        insertDAO();
                    break;

            }

        }
    };
    private void initView(){
        loadData();
        etItemTitle = dialogView.findViewById(R.id.etSummary);
        etItemGrocery = dialogView.findViewById(R.id.etGrocery);
        tvDate = dialogView.findViewById(R.id.tvDate);
        tvTime = dialogView.findViewById(R.id.tvTime);
        tvRing = dialogView.findViewById(R.id.tvRing);
        tvUri = dialogView.findViewById(R.id.tvUri);

        radioGroup = dialogView.findViewById(R.id.radioGroup);
        spinnerShop = dialogView.findViewById(R.id.spinner);
        spinnerShop.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.spinner_shop,shopList));

        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnSmartInput = dialogView.findViewById(R.id.btnSmartInput);

        tvDate.setOnClickListener(viewClick);
        tvTime.setOnClickListener(viewClick);
        tvRing.setOnClickListener(viewClick);
        btnEdit.setOnClickListener(viewClick);
        btnCancel.setOnClickListener(viewClick);
        btnDelete.setOnClickListener(viewClick);
        btnSmartInput.setOnClickListener(viewClick);
        radioGroup.setOnCheckedChangeListener(radioGroupClick);
        TextView tvTopic = dialogView.findViewById(R.id.tvTopic);
        tvTopic.setText(isEditor ? getString(R.string.tv_item_topic_editor):
                getString(R.string.tv_item_topic_adder));
        btnEdit.setText(isEditor? getString(R.string.btn_edit): getString(R.string.btn_add));
//todo selection
        if(isEditor){
            etItemTitle.setText(dataList.get(position).get(COLUMN_BUY_SUMMARY));
            etItemGrocery.setText(dataList.get(position).get(COLUMN_BUY_GROCERY));
            String shopName = dataList.get(position).get(COLUMN_BUY_SHOP_NAME);
            int selector = getSpinnerSelection(shopName);
            if(selector != -1)
                spinnerShop.setSelection(selector);
            tvDate.setText(dataList.get(position).get(COLUMN_BUY_DATE));
            tvTime.setText(dataList.get(position).get(COLUMN_BUY_TIME));
            selector = -1;
            selector = Integer.parseInt(dataList.get(position).get(COLUMN_BUY_REMINDER));
            setRadioButtonSelected(selector);
            tvRing.setText(dataList.get(position).get(COLUMN_BUY_RING_TITLE));
            tvUri.setText(dataList.get(position).get(COLUMN_BUY_RING_URI));
        }
        else{
            radioGroup.check(R.id.radio0);  // radioGroup 預設選項 不用通知
            if(shopList.length>1)       // spinner 預設選項 第一個
                spinnerShop.setSelection(0);
            btnDelete.setVisibility(View.GONE);
        }

    }

    private void setRadioButtonSelected(int selector){
        if (selector == -1)
            return;
        switch (selector){
            case 0:
                radioGroup.check(R.id.radio0);
                break;
            case 1:
                radioGroup.check(R.id.radio1);
                break;
            case 2:
                radioGroup.check(R.id.radio2);
                break;
        }

    }

    private void pickDate(){
        datePickerFragment = new DatePickerFragment(tvDate.getText().toString());
        datePickerFragment.setTargetFragment(ShoppingEditorFragment.this,REQUEST_CODE_DATE);
        datePickerFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }
    private void pickTime(){
        timePickerFragment = new TimePickerFragment(tvTime.getText().toString());
        timePickerFragment.setTargetFragment(ShoppingEditorFragment.this,REQUEST_CODE_TIME);
        timePickerFragment.show(getActivity().getSupportFragmentManager(),"timePicker");
    }
    private void pickRing(){
        ringPickerFragment = new RingPickerFragment();
        ringPickerFragment.setTargetFragment(ShoppingEditorFragment.this,REQUEST_CODE_RING);
        ringPickerFragment.show(getActivity().getSupportFragmentManager(),"ringPicker");
    }
    private void smartInput(){
        smartInputListFragment = new SmartInputListFragment();
        smartInputListFragment.setTargetFragment(ShoppingEditorFragment.this,REQUEST_CODE_INPUT);
        smartInputListFragment.show(getActivity().getSupportFragmentManager(),"smartInput");
    }
    private void insertDAO(){
        if(!checkInputValidate())
            return;
        long id = dao.insert(TABLE_BUY_SHOPPING,getContentValues());
        if(id<1)
            return;
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(REQUEST_CODE_ADD, Activity.RESULT_OK,intent);
    }
    private void askForDelete(){
        String item = etItemGrocery.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.sys_delete_shopping_item))
                .setMessage(item)
                .setCancelable(true)
                .setNegativeButton(getString(R.string.btn_cancel),null)
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDAO();
                    }
                })
                .show();
    }
    private void deleteDAO(){
        String id = dataList.get(position).get(KEY_ID);
        boolean ok = dao.delete(TABLE_BUY_SHOPPING,id);
        if(!ok)
            return;
        Intent intent = new Intent();
        intent.putExtra(POSITION,position);
        getTargetFragment().onActivityResult(REQUEST_CODE_EDIT,Activity.RESULT_FIRST_USER,intent);
    }
    private void updateDAO(){
        if(!checkInputValidate())
            return;
        String id = dataList.get(position).get(KEY_ID);
        boolean ok = dao.update(TABLE_BUY_SHOPPING,id,getContentValues());  //更新資料庫
        if(ok){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(POSITION,position);
            ContentValues item = getContentValues();
            item.put(KEY_ID,dataList.get(position).get(KEY_ID));
            bundle.putParcelable(ITEM,item);
            intent.putExtras(bundle);
            //更新原始資料 與 View
            getTargetFragment().onActivityResult(REQUEST_CODE_EDIT,Activity.RESULT_OK,intent);
        }
    }
    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUY_SUMMARY,etItemTitle.getText().toString());
        values.put(COLUMN_BUY_GROCERY,etItemGrocery.getText().toString());
        values.put(COLUMN_BUY_SHOP_NAME,spinnerShop.getSelectedItem().toString());
        String shopName = spinnerShop.getSelectedItem().toString();
        int selector = getSpinnerSelection(shopName);
        if(selector != -1){
            values.put(COLUMN_BUY_LATITUDE,shopItems.get(selector).getAsFloat(COLUMN_SHOP_LATITUDE));
            values.put(COLUMN_BUY_LONGITUDE,shopItems.get(selector).getAsFloat(COLUMN_SHOP_LONGITUDE));
        }
        values.put(COLUMN_BUY_DATE,tvDate.getText().toString());
        values.put(COLUMN_BUY_TIME,tvTime.getText().toString());
        values.put(COLUMN_BUY_REMINDER,selection);
        values.put(COLUMN_BUY_RING_TITLE,tvRing.getText().toString());
        values.put(COLUMN_BUY_RING_URI,tvUri.getText().toString());
        if(isEditor)
            values.put(COLUMN_BUY_SHOW_ON,Integer.parseInt(dataList.get(position).get(COLUMN_BUY_SHOW_ON)));
        else
            values.put(COLUMN_BUY_SHOW_ON,0);
        return values;
    }

    private int getSpinnerSelection(String shopName){
        String temp;
        for(int i=0;i<shopItems.size();i++){
            temp = shopItems.get(i).getAsString(COLUMN_SHOP_NAME);
            if(temp.equals(shopName))
                return i;
        }
        return -1;
    }

    private boolean checkInputValidate(){
        boolean result = true;
        if(etItemGrocery.getText().toString().equals(""))
            result= false;
        if(result == false)
            Toast.makeText(getContext(),getString(R.string.sys_invalidate_input_shop),Toast.LENGTH_SHORT).show();
        return result;
    }
    RadioGroup.OnCheckedChangeListener radioGroupClick = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){     //監聽 RadioGroup 選擇改變
                case R.id.radio0:
                    selection = 0;
                    break;
                case R.id.radio1:
                    selection = 1;
                    break;
                case R.id.radio2:
                    selection = 2;
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
        switch (requestCode){
            case REQUEST_CODE_DATE:  // 從 getTargetFragment 傳回來的訊息
                String pickDay = data.getStringExtra(PICK_DATE);
                tvDate.setText(pickDay);
                datePickerFragment.dismiss();
                break;
            case REQUEST_CODE_TIME:
                String pickTime = data.getStringExtra(PICK_TIME);
                tvTime.setText(pickTime);
                timePickerFragment.dismiss();
                break;
            case REQUEST_CODE_RING:
                String ringTitle = data.getStringExtra(PICK_RING_TITLE);
                String ringUri = data.getStringExtra(PICK_RING_URI);
                tvRing.setText(ringTitle);
                tvUri.setText(ringUri);
                ringPickerFragment.dismiss();
                break;
            case REQUEST_CODE_INPUT:
                String result = data.getStringExtra(ITEM);
                result = result.substring(0,result.length()-1);
                result = etItemGrocery.getText().toString() + result;
                etItemGrocery.setText(result);
                smartInputListFragment.dismiss();
                break;
        }
    }


}
