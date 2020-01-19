package nor.zero.outer_brain.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.LinkedList;

import nor.zero.outer_brain.MyDatabaseDAO;
import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.MyDatabaseDAO.*;
import static nor.zero.outer_brain.Constants.*;

public class ShopEditorFragment extends DialogFragment {
    MyDatabaseDAO dao;
    LinkedList<HashMap<String,String>> dataList;
    int position;
    boolean isEditor;
    View dialogView;
    EditText etShopName,etShopLatitude,etShopLongitude,etShopClassify,etAddress,etShopNote;
    public ShopEditorFragment(boolean isEditor, int position, LinkedList dataList){
        this.position = position;
        this.dataList = dataList;
        this.isEditor = isEditor;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Dialog dialog = super.onCreateDialog(savedInstanceState);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.item_shop_editor,null);
        builder.setView(dialogView)
                .setTitle(getString(R.string.sys_edit_shop))
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
    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnCancel:
                    ShopEditorFragment.this.dismiss();
                    break;
                case R.id.btnDelete:
                    deleteDAO();
                    break;
                case R.id.btnEdit:
                    if(isEditor){
                        updateDAO();  }
                    else {
                        insertDAO();  }
                    break;
            }
        }
    };

    private void initDialogView(){
        etShopName = dialogView.findViewById(R.id.etShopName);
        etShopLatitude = dialogView.findViewById(R.id.etLatitude);
        etShopLongitude = dialogView.findViewById(R.id.etLongitude);
        etShopClassify = dialogView.findViewById(R.id.etClassify);
        etAddress = dialogView.findViewById(R.id.etAddress);
        etShopNote = dialogView.findViewById(R.id.etNote);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(btnClick);
        btnEdit.setOnClickListener(btnClick);
        btnCancel.setOnClickListener(btnClick);

        if(isEditor){
            btnEdit.setText(getString(R.string.btn_edit));
            etShopName.setText(dataList.get(position).get(COLUMN_SHOP_NAME));
            etShopLatitude.setText(dataList.get(position).get(COLUMN_SHOP_LATITUDE));
            etShopLongitude.setText(dataList.get(position).get(COLUMN_SHOP_LONGITUDE));
            etShopClassify.setText(dataList.get(position).get(COLUMN_SHOP_CLASSIFY));
            etAddress.setText(dataList.get(position).get(COLUMN_SHOP_ADDRESS));
            etShopNote.setText(dataList.get(position).get(COLUMN_SHOP_NOTE));
        }
        else {
            btnDelete.setVisibility(View.GONE);
            btnEdit.setText(getString(R.string.btn_add));
        }
    }

    private void insertDAO(){
        if(!checkInputValidate())
            return;
        //資料庫資料新增
        long id = dao.insert(TABLE_SHOP_LOCATION,getContentValues());
        if(id==0)   //新增失敗
            return;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ITEM,getContentValues());
        intent.putExtras(bundle);
        //原始資料 dataList 增加一筆資料
        getTargetFragment().onActivityResult(REQUEST_CODE_ADD,
                Activity.RESULT_OK,intent);
    }
    private void deleteDAO(){
        //刪除資料庫資料
        String _id = dataList.get(position).get(KEY_ID);
        boolean _ok =dao.delete(TABLE_SHOP_LOCATION,_id);
        if(!_ok)    //刪除失敗
            return;
        Intent intent = new Intent();
        intent.putExtra(POSITION,position);
        getTargetFragment().onActivityResult(REQUEST_CODE_EDIT,
                Activity.RESULT_FIRST_USER,intent);
    }
    private void updateDAO(){
        if(!checkInputValidate())
            return;
        int _id = Integer.parseInt(dataList.get(position).get(KEY_ID));
        //資料庫更新
        boolean _ok = dao.update(TABLE_SHOP_LOCATION,_id,getContentValues());
        if(!_ok)    //更新失敗
            return;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION,position);
        bundle.putString(KEY_ID,dataList.get(position).get(KEY_ID));
        bundle.putParcelable(ITEM,getContentValues());
        intent.putExtras(bundle);
        //原始資料 dataList 修改 position 位置的資料
        getTargetFragment().onActivityResult(REQUEST_CODE_EDIT,
                Activity.RESULT_OK,intent);
    }
    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHOP_NAME,etShopName.getText().toString());
        float latitude = Float.parseFloat(etShopLatitude.getText().toString());
        values.put(COLUMN_SHOP_LATITUDE,latitude);
        float longitude = Float.parseFloat(etShopLongitude.getText().toString());
        values.put(COLUMN_SHOP_LONGITUDE,longitude);
        values.put(COLUMN_SHOP_CLASSIFY,etShopClassify.getText().toString());
        values.put(COLUMN_SHOP_ADDRESS,etAddress.getText().toString());
        values.put(COLUMN_SHOP_NOTE,etShopNote.getText().toString());
        return values;
    }
    private boolean checkInputValidate(){
        boolean result = true;
        if(etShopName.getText().toString().equals(""))
            result = false;
        if(etShopLatitude.getText().toString().equals(""))
            result = false;
        if(etShopLongitude.getText().toString().equals(""))
            result = false;
        if(result == false)
            Toast.makeText(getContext(),getString(R.string.sys_invalidate_input_shop),Toast.LENGTH_SHORT).show();
        return result;
    }
}
