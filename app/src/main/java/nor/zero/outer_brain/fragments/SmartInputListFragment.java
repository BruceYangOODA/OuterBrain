package nor.zero.outer_brain.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.LinkedList;

import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;

import static nor.zero.outer_brain.MyDatabaseDAO.*;
import static nor.zero.outer_brain.Constants.*;


public class SmartInputListFragment extends DialogFragment {
    MyDatabaseDAO dao;
    LinkedList<HashMap<String,String>> dataList;
    View dialogView;
    MyAdapter adapter;
    ListView listView;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.item_smart_input_list,null);
        builder.setView(dialogView)
                .setTitle(getString(R.string.sys_smart_input))
                .setCancelable(false);
        return builder.create();

    }
    @Override
    public void onResume() {
        super.onResume();
        if(dao == null)
            dao = new MyDatabaseDAO(getContext());
        initDialogView();
    }
    View.OnClickListener viewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnCancel:
                    SmartInputListFragment.this.dismiss();
                    break;
                case R.id.btnOk:
                    setShoppingItems();
                    break;
                case R.id.btnPlus:
                    plusCount((View)v.getParent());
                    break;
                case R.id.btnMinus:
                    minusCount((View)v.getParent());
                    break;
            }

        }
    };
    private void initDialogView(){
        dataList = new LinkedList<>();
        loadData();
        adapter = new MyAdapter();
        listView = dialogView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnCancel.setOnClickListener(viewClick);
        btnOk.setOnClickListener(viewClick);
    }

    private void setShoppingItems(){
        Intent intent = new Intent();
        intent.putExtra(ITEM,wrapShoppingItems());
        getTargetFragment().onActivityResult(REQUEST_CODE_INPUT, Activity.RESULT_OK,intent);
    }
    private String wrapShoppingItems(){
        String result = "";
        if(dataList.size()==0)
            return result;
        for(int i=0;i<listView.getCount();i++){
            TextView tvCount = listView.getChildAt(i).findViewById(R.id.tvCount);
            int count = Integer.parseInt(tvCount.getText().toString());
            if(count==0)
                continue;
            TextView tvName =  listView.getChildAt(i).findViewById(R.id.tvName);
            TextView tvUnit =  listView.getChildAt(i).findViewById(R.id.tvUnit);
            result += tvName.getText().toString()+" "+count+" "
                    +tvUnit.getText().toString()+SEP_COMMA;
        }
        return result;
    }
    private void plusCount(View view){
        TextView tvCount = view.findViewById(R.id.tvCount);
        int count = Integer.parseInt(tvCount.getText().toString());
        count++;
        tvCount.setText(""+count);
    }
    private void minusCount(View view){
        TextView tvCount = view.findViewById(R.id.tvCount);
        int count = Integer.parseInt(tvCount.getText().toString());
        if(count==0)
            return;
        count--;
        tvCount.setText(""+count);
    }
    private void loadData(){
        Cursor cursor = dao.getAllCursor(TABLE_ITEM_GROCERY);
        if(cursor == null || cursor.getCount()<1)
            return;
        cursor.moveToFirst();
        String temp;
        while (!cursor.isAfterLast()){
            HashMap<String,String> item = new HashMap<>();
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_GROCERY_NAME));
            item.put(COLUMN_ITEM_GROCERY_NAME,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_GROCERY_UNIT));
            item.put(COLUMN_ITEM_GROCERY_UNIT,temp);
            dataList.add(item);
            cursor.moveToNext();
        }
        cursor.close();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return dataList.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_smart_input,null);
            TextView v = view.findViewById(R.id.tvName);
            v.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_NAME));
            v = view.findViewById(R.id.tvUnit);
            v.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_UNIT));
            v = view.findViewById(R.id.tvCount);
            v.setText("0");
            Button btnPlus = view.findViewById(R.id.btnPlus);
            Button btnMinus = view.findViewById(R.id.btnMinus);
            btnPlus.setOnClickListener(viewClick);
            btnMinus.setOnClickListener(viewClick);
            return view;
        }
    }
}
