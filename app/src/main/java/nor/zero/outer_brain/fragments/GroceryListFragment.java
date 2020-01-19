package nor.zero.outer_brain.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;

import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.MyDatabaseDAO.*;
import static nor.zero.outer_brain.Constants.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryListFragment extends Fragment {
    View contentView;
    MyDatabaseDAO dao;
    LinkedList<HashMap<String,String>> dataList;
    MyAdapter adapter;
    GroceryEditorFragment groceryEditorFragment;



    public GroceryListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.item_grocery_list,container,false); }
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
                addItem();
                break;
            case REQUEST_CODE_EDIT:
                if(resultCode == Activity.RESULT_FIRST_USER)
                    deleteItem(data);   //原始資料 dataList 刪除 position 位置資料
                else if(resultCode == Activity.RESULT_OK)
                    updateItem(data);   //原始資料 dataList 修改 position 位置資料
                break;
        }
    }

    private void initView(){
        dataList = new LinkedList<>();
        loadData();
        adapter = new MyAdapter();
        ListView listView = contentView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listViewClick);
        Button btnAdd = contentView.findViewById(R.id.btnAddItem);
        btnAdd.setOnClickListener(btnClick);
    }
    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnAddItem:
                    groceryEditorFragment = new GroceryEditorFragment(false,-1,dataList);
                    groceryEditorFragment.setTargetFragment(GroceryListFragment.this,REQUEST_CODE_EDIT);
                    groceryEditorFragment.show(getActivity().getSupportFragmentManager(),"groceryAdder");
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener listViewClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            groceryEditorFragment = new GroceryEditorFragment(true,position,dataList);
            groceryEditorFragment.setTargetFragment(GroceryListFragment.this,REQUEST_CODE_EDIT);
            groceryEditorFragment.show(getActivity().getSupportFragmentManager(),"groceryEditor");
        }
    };
    private void loadData(){
        Cursor cursor = dao.getAllCursor(TABLE_ITEM_GROCERY);
        if(cursor!= null && cursor.getCount()>0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                HashMap<String,String> item = new HashMap<>();
                String temp;
                temp = cursor.getString(cursor.getColumnIndex(KEY_ID));
                item.put(KEY_ID,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_GROCERY_NAME));
                item.put(COLUMN_ITEM_GROCERY_NAME,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_GROCERY_UNIT));
                item.put(COLUMN_ITEM_GROCERY_UNIT,temp);
                dataList.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
    private void addItem(){ //原始資料 dataList 增加一筆資料
        dataList.clear();
        loadData();
        adapter.notifyDataSetChanged();
        groceryEditorFragment.dismiss();
    }
    private void deleteItem(Intent data){ //原始資料 dataList 刪除 position 位置資料
        int position = data.getIntExtra(POSITION,-1);
        if(position != -1)
            dataList.remove(position);
        adapter.notifyDataSetChanged();
        groceryEditorFragment.dismiss();
    }
    private void updateItem(Intent data){
        ContentValues values = data.getExtras().getParcelable(ITEM);
        int position = data.getIntExtra(POSITION,-1);
        String _id = data.getStringExtra(KEY_ID);
        if(position==-1)
            return;
        HashMap<String,String> item = getItem(values);
        item.put(KEY_ID,_id);
        dataList.set(position,item);
        adapter.notifyDataSetChanged();
        groceryEditorFragment.dismiss();
    }
    private HashMap<String,String> getItem(ContentValues values){
        HashMap<String,String> item = new HashMap<>();
        item.put(COLUMN_ITEM_GROCERY_NAME,values.getAsString(COLUMN_ITEM_GROCERY_NAME));
        item.put(COLUMN_ITEM_GROCERY_UNIT,values.getAsString(COLUMN_ITEM_GROCERY_UNIT));
        return item;
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
            View view = getLayoutInflater().inflate(R.layout.item_grocery,null);
            TextView v = view.findViewById(R.id.count);
            v.setText(""+(position+1));
            v = view.findViewById(R.id.tvName);
            v.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_NAME));
            v = view.findViewById(R.id.tvUnit);
            v.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_UNIT));
            return view;
        }
    }
}
