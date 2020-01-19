package nor.zero.outer_brain.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.LinkedList;

import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.MyDatabaseDAO.*;
import static nor.zero.outer_brain.Constants.*;

public class ShopListFragment extends Fragment {
    MyDatabaseDAO dao;
    View contentView;
    ShopEditorFragment shopEditorFragment;
    LinkedList<HashMap<String,String>> dataList;
    MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.item_shop_list,container,false); }
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
                    addItem();  //原始資料 dataList 增加一筆資料
                break;
            case REQUEST_CODE_EDIT:
                if(resultCode == Activity.RESULT_FIRST_USER)
                    deleteItem(data);   //原始資料 dataList 刪除 position 位置資料
                else if(resultCode == Activity.RESULT_OK)
                    updateItem(data);   //原始資料 dataList 修改 position 位置資料
            break;
        }
    }
    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnAddItem:
                    shopEditorFragment = new ShopEditorFragment(false,-1,dataList);
                    shopEditorFragment.setTargetFragment(ShopListFragment.this,REQUEST_CODE_ADD);
                    shopEditorFragment.show(getActivity().getSupportFragmentManager(),"shopAdder");
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener listViewClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            shopEditorFragment = new ShopEditorFragment(true,position,dataList);
            shopEditorFragment.setTargetFragment(ShopListFragment.this,REQUEST_CODE_EDIT);
            shopEditorFragment.show(getActivity().getSupportFragmentManager(),"shopEditor");
        }
    };

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
    private void addItem(){ //原始資料 dataList 增加一筆資料
        dataList.clear();
        loadData();
        adapter.notifyDataSetChanged();
        shopEditorFragment.dismiss();
    }
    private void deleteItem(Intent data){ //原始資料 dataList 刪除 position 位置資料
        int position = data.getIntExtra(POSITION,-1);
        if(position != -1)
            dataList.remove(position);
        adapter.notifyDataSetChanged();
        shopEditorFragment.dismiss();
    }
    private void updateItem(Intent data){  //原始資料 dataList 修改 position 位置資料
        ContentValues values = data.getExtras().getParcelable(ITEM);
        int position = data.getIntExtra(POSITION,-1);
        String _id = data.getStringExtra(KEY_ID);
        if(position==-1)
            return;
        HashMap<String,String> item = getItem(values);
        item.put(KEY_ID,_id);
        dataList.set(position,item);
        adapter.notifyDataSetChanged();
        shopEditorFragment.dismiss();
    }
    private HashMap<String,String> getItem(ContentValues values){
        HashMap<String,String> item = new HashMap<>();
        item.put(COLUMN_SHOP_NAME,values.getAsString(COLUMN_SHOP_NAME));
        item.put(COLUMN_SHOP_LATITUDE,values.getAsString(COLUMN_SHOP_LATITUDE));
        item.put(COLUMN_SHOP_LONGITUDE,values.getAsString(COLUMN_SHOP_LONGITUDE));
        item.put(COLUMN_SHOP_CLASSIFY,values.getAsString(COLUMN_SHOP_CLASSIFY));
        item.put(COLUMN_SHOP_ADDRESS,values.getAsString(COLUMN_SHOP_ADDRESS));
        item.put(COLUMN_SHOP_NOTE,values.getAsString(COLUMN_SHOP_NOTE));
        return item;
    }
    private void loadData(){
        Cursor cursor = dao.getAllCursor(TABLE_SHOP_LOCATION);
        if(cursor!= null && cursor.getCount()>0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                HashMap<String,String> item = new HashMap<>();
                String temp;
                temp = cursor.getString(cursor.getColumnIndex(KEY_ID));
                item.put(KEY_ID,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_NAME));
                item.put(COLUMN_SHOP_NAME,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_LATITUDE));
                item.put(COLUMN_SHOP_LATITUDE,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_LONGITUDE));
                item.put(COLUMN_SHOP_LONGITUDE,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_CLASSIFY));
                item.put(COLUMN_SHOP_CLASSIFY,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_ADDRESS));
                item.put(COLUMN_SHOP_ADDRESS,temp);
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_SHOP_NOTE));
                item.put(COLUMN_SHOP_NOTE,temp);
                dataList.add(item);
                cursor.moveToNext();
            }
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
            View view = getLayoutInflater().inflate(R.layout.item_shop,null);
            TextView v = view.findViewById(R.id.count);
            v.setText(""+(position+1));
            v = view.findViewById(R.id.tvShopName);
            v.setText(dataList.get(position).get(COLUMN_SHOP_NAME));
            v = view.findViewById(R.id.tvClassify);
            v.setText(dataList.get(position).get(COLUMN_SHOP_CLASSIFY));
            v = view.findViewById(R.id.tvAddress);
            v.setText(dataList.get(position).get(COLUMN_SHOP_ADDRESS));
            v = view.findViewById(R.id.tvNote);
            v.setText(dataList.get(position).get(COLUMN_SHOP_NOTE));
            return view;
        }
    }
}
