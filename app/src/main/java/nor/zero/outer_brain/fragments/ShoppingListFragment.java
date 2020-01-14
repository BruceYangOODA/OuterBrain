package nor.zero.outer_brain.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.Constants.*;

public class ShoppingListFragment extends Fragment {

    //private Button btnAddItem;
    private ListView listContainer;
    private View contentView;
    private MyAdapter myAdapter;
    private LinkedList<ArrayList<String>> dataList;

    public ShoppingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_shopping_list, container, false);;
            initView();
        }
        return contentView;
    }

    private void initView(){
        Button btnAddItem = contentView.findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(btnAddItemClick);
        listContainer = contentView.findViewById(R.id.listContainer);
        dataList = new LinkedList<>();
        //test Data
        ArrayList<String> item = new ArrayList();
        item.add("1");
        item.add("代辦");
        item.add("剪刀,石頭");
        item.add("aaa");
        dataList.add(item);
        ArrayList<String> item2 = new ArrayList();
        item2.add("2");
        item2.add("END");
        item2.add("布");
        item2.add("bbb");
        dataList.add(item2);


        myAdapter = new MyAdapter();
        listContainer.setAdapter(myAdapter);
        listContainer.setOnItemClickListener(itemClickListener);

    }

    View.OnClickListener btnAddItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addItemView();
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.v("aaa","point"+position);
            editItem(position);
            //dataList.get(position).clear();
            //ArrayList<String> aaa = new ArrayList<>();
            //dataList.get(position).addAll(aaa);
            //addItemView();
        }
    };

    private void editItem(final int position){
        EditorDialogFragment editor = new EditorDialogFragment(true,position,dataList);
        editor.show(getChildFragmentManager(),"Editor");

    }

    private void addItemView(){
        EditorDialogFragment editor = new EditorDialogFragment(false,-1,dataList);
        //editor.show(getActivity().getSupportFragmentManager(),"Dialog");
        editor.show(getChildFragmentManager(),"Adder");
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
            View view = getLayoutInflater().inflate(R.layout.item_shopping_list,null);
            TextView v = view.findViewById(R.id.tvItemTitle);
            v.setText(dataList.get(position).get(ITEM_TITLE));
            v = view.findViewById(R.id.tvItemGrocery);
            v.setText(dataList.get(position).get(ITEM_GROCERY));
            v = view.findViewById(R.id.tvItemLocation);
            v.setText(dataList.get(position).get(ITEM_LOCATION));

            return view;
        }
    }


}
