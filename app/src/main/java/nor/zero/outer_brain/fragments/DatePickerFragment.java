package nor.zero.outer_brain.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.LinkedList;

import nor.zero.outer_brain.R;
import static nor.zero.outer_brain.Constants.*;
/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment {

    String targetDay;
    DatePicker datePicker;

    public DatePickerFragment(String targetDay) {
        this.targetDay = targetDay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.fragment_date_picker, container, false);
        datePicker = dialogView.findViewById(R.id.datePicker);
        setDateView();  //如果日期有資料,就選擇該日
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(btnOkClick);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(btnCancelClick);

        return dialogView;
    }

    private void setDateView(){
        if(!targetDay.equals("")){
            String[] temp = targetDay.split("-");
            int year = Integer.parseInt(temp[0]);
            int month = Integer.parseInt(temp[1]);
            int dayOfMonth = Integer.parseInt(temp[2]);
            datePicker.updateDate(year,month-1,dayOfMonth); //month 從0開始
        }
    }
    View.OnClickListener btnOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int year = datePicker.getYear();
            int month = datePicker.getMonth()+1;//month 從0開始
            int day = datePicker.getDayOfMonth();
            String result = ""+year+"-"+ (month<10?"0":"")+month +"-"+(day<10?"0":"")+day;
            Intent intent = new Intent();
            intent.putExtra(EditorDialogFragment.PICK_DATE,result);
            getTargetFragment().onActivityResult(EditorDialogFragment.REQUEST_CODE_DATE,
                    Activity.RESULT_OK,intent);     //用 setTargetFragment 註冊的訊息傳回資料
        }
    };
    View.OnClickListener btnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment.this.dismiss();
        }
    };

}
