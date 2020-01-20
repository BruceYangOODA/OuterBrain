package nor.zero.outer_brain.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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

public class GroceryEditorFragment extends DialogFragment {
    MyDatabaseDAO dao;
    LinkedList<HashMap<String,String>> dataList;
    EditText etName,etUnit;
    int position;
    boolean isEditor;
    View dialogView;
    public GroceryEditorFragment(boolean isEditor, int position, LinkedList dataList){
        this.position = position;
        this.dataList = dataList;
        this.isEditor = isEditor;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.item_grocery_editor,null);
        builder.setView(dialogView)
                .setTitle(getString(R.string.sys_edit_grocery))
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
                    GroceryEditorFragment.this.dismiss();
                    break;
                case R.id.btnDelete:
                    askForDelete();
                    break;
                case R.id.btnEdit:
                    if(isEditor){
                        updateDAO();
                          }
                    else {
                        insertDAO();
                          }
                    break;
            }
        }
    };
    private void initDialogView(){
        etName = dialogView.findViewById(R.id.etName);
        etUnit = dialogView.findViewById(R.id.etUnit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(btnClick);
        btnEdit.setOnClickListener(btnClick);
        btnCancel.setOnClickListener(btnClick);

        if(isEditor){
            etName.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_NAME));
            etUnit.setText(dataList.get(position).get(COLUMN_ITEM_GROCERY_UNIT));
            btnEdit.setText(getString(R.string.btn_edit));
        }
        else {
            btnDelete.setVisibility(View.GONE);
            btnEdit.setText(getString(R.string.btn_add));
        }

    }

    private void insertDAO(){
        if(!checkInputValidate())
            return;
        long id = dao.insert(TABLE_ITEM_GROCERY,getContentValues());
        if(id==0)   //新增失敗
            return;
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(REQUEST_CODE_ADD,
                Activity.RESULT_OK,intent);
    }
    private void deleteDAO(){
        //刪除資料庫資料
        String id = dataList.get(position).get(KEY_ID);
        boolean ok = dao.delete(TABLE_ITEM_GROCERY,id);
        if(!ok)    //刪除失敗
            return;
        Intent intent = new Intent();
        intent.putExtra(POSITION,position);
        getTargetFragment().onActivityResult(REQUEST_CODE_EDIT,
                Activity.RESULT_FIRST_USER,intent);
    }
    private void updateDAO(){
        if(!checkInputValidate())
            return;
        String id = dataList.get(position).get(KEY_ID);
        //資料庫更新
        boolean ok = dao.update(TABLE_ITEM_GROCERY,id,getContentValues());
        if(!ok)
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
        values.put(COLUMN_ITEM_GROCERY_NAME,etName.getText().toString());
        values.put(COLUMN_ITEM_GROCERY_UNIT,etUnit.getText().toString());
        return values;
    }

    private boolean checkInputValidate(){
        boolean result = true;
        if(etName.getText().toString().equals(""))
            result = false;
        if(result == false)
            Toast.makeText(getContext(),getString(R.string.sys_invalidate_input_grocery),Toast.LENGTH_SHORT).show();
        return result;
    }
    private void askForDelete(){
        String item = etName.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.sys_delete_grocery_item))
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
}
