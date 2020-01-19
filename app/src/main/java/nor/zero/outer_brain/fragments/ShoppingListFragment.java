package nor.zero.outer_brain.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.LinkedList;

import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.Constants.*;
import static nor.zero.outer_brain.MyDatabaseDAO.*;

public class ShoppingListFragment extends Fragment {

    MyDatabaseDAO dao;
   // ListView listView;
    View contentView;
    MyAdapter adapter;
    LinkedList<HashMap<String,String>> dataList;
    ToggleButton toggleButton;

    ShoppingEditorFragment shoppingEditorFragment;

    final static int REQUEST_CODE_ADD = 1;
    final static int REQUEST_CODE_EDIT = 2;

    public ShoppingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_shopping_list, container, false);;
        }
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dao == null)
            dao = new MyDatabaseDAO(getContext());
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_ADD:
                refreshView();
                break;
            case REQUEST_CODE_EDIT:
                break;
        }
    }

    private void refreshView(){
        dataList.clear();
        loadData();
        adapter.notifyDataSetChanged();
        shoppingEditorFragment.dismiss();
    }

    private void initView(){
        dataList = new LinkedList<>();
        loadData();
        adapter = new MyAdapter();
        ListView listView = contentView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        Button btnAddItem = contentView.findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(viewClick);
    }
    private void loadData(){
        Cursor cursor = dao.getAllCursor(TABLE_BUY_SHOPPING);
        if(cursor == null || cursor.getCount()<1)
            return;
        cursor.moveToFirst();
        String temp;
        while (!cursor.isAfterLast()){
            HashMap<String,String> item = new HashMap<>();
            temp = cursor.getString(cursor.getColumnIndex(KEY_ID));
            item.put(KEY_ID,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_SUMMARY));
            item.put(COLUMN_BUY_SUMMARY,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_GROCERY));
            item.put(COLUMN_BUY_GROCERY,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_SHOP_NAME));
            item.put(COLUMN_BUY_SHOP_NAME,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_LATITUDE));
            item.put(COLUMN_BUY_LATITUDE,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_LONGITUDE));
            item.put(COLUMN_BUY_LONGITUDE,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_DATE));
            item.put(COLUMN_BUY_DATE,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_TIME));
            item.put(COLUMN_BUY_TIME,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_REMINDER));
            item.put(COLUMN_BUY_REMINDER,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_RING_TITLE));
            item.put(COLUMN_BUY_RING_TITLE,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_RING_URI));
            item.put(COLUMN_BUY_RING_URI,temp);
            temp = cursor.getString(cursor.getColumnIndex(COLUMN_BUY_SHOW_ON));
            item.put(COLUMN_BUY_SHOW_ON,temp);
            dataList.add(item);
           // Log.v("aaa","item"+item.toString());
            cursor.moveToNext();
        }
        cursor.close();
    }

    View.OnClickListener viewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnAddItem:
                    insertItem();
                    break;
            }

        }
    };
    ToggleButton.OnCheckedChangeListener toggleClick = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View view = (View)buttonView.getParent();
            TextView v = view.findViewById(R.id.tvCount);
            int selection = Integer.parseInt(v.getText().toString());
            int check = isChecked?1:0;
            int id = Integer.parseInt(dataList.get(selection-1).get(KEY_ID));
            ContentValues values = new ContentValues();
            values.put(COLUMN_BUY_SHOW_ON,check);
            boolean ok = dao.update(TABLE_BUY_SHOPPING,id,values);
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateItem(position);
        }
    };

    private void updateItem(int position){
        shoppingEditorFragment = new ShoppingEditorFragment(true,position,dataList);
        shoppingEditorFragment.setTargetFragment(ShoppingListFragment.this,REQUEST_CODE_EDIT);
        shoppingEditorFragment.show(getActivity().getSupportFragmentManager(),"Editor");

    }
    private void insertItem(){
        shoppingEditorFragment = new ShoppingEditorFragment(false,-1,dataList);
        shoppingEditorFragment.setTargetFragment(ShoppingListFragment.this,REQUEST_CODE_ADD);
        shoppingEditorFragment.show(getActivity().getSupportFragmentManager(),"Adder");
    }


    private class MyAdapter extends BaseAdapter{


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
            View view = getLayoutInflater().inflate(R.layout.item_shopping,null);
            toggleButton = view.findViewById(R.id.tglShow);
            String temp = dataList.get(position).get(COLUMN_BUY_SHOW_ON);
            if(temp.equals("1"))
                toggleButton.setChecked(true);
            toggleButton.setOnCheckedChangeListener(toggleClick);
            TextView v = view.findViewById(R.id.tvSummary);
            v.setText(dataList.get(position).get(COLUMN_BUY_SUMMARY));
            v = view.findViewById(R.id.tvCount);
            v.setText(""+(position+1));
            v = view.findViewById(R.id.tvShopName);
            v.setText(dataList.get(position).get(COLUMN_BUY_SHOP_NAME));
            v = view.findViewById(R.id.tvGrocery);
            v.setText(dataList.get(position).get(COLUMN_BUY_GROCERY));
            temp = dataList.get(position).get(COLUMN_BUY_REMINDER);
            ImageView icon = view.findViewById(R.id.imgIcon);
            if(!temp.equals("0"))
                icon.setImageResource(R.drawable.alarm);
            return view;
        }
    }


}
