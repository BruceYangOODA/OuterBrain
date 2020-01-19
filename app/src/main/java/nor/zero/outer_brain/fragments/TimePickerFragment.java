package nor.zero.outer_brain.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import nor.zero.outer_brain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment {

    String targetTime;
    TimePicker timePicker;
    public TimePickerFragment(String targetTime) {
        this.targetTime = targetTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.item_time_picker, container, false);
        timePicker = dialogView.findViewById(R.id.timePicker);
        setTimeView();  //如果時間有資料,就選擇時間點
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(btnOkClick);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(btnCancelClick);

        getDialog().setTitle(getString(R.string.sys_select_time));

        return dialogView;
    }
    private void setTimeView(){
        if(!targetTime.equals("")){
            String[] temp = targetTime.split(":");
            int hour = Integer.parseInt(temp[0]);
            int minutes = Integer.parseInt(temp[1]);
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minutes);
        }
    }

    View.OnClickListener btnOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int hour = timePicker.getCurrentHour();
            int minutes = timePicker.getCurrentMinute();
            String result = (hour<10?"0":"")+ hour +":"+ (minutes<10?"0":"")+minutes;
            Intent intent = new Intent();
            intent.putExtra(ShoppingEditorFragment.PICK_TIME,result);
            getTargetFragment().onActivityResult(ShoppingEditorFragment.REQUEST_CODE_TIME,
                    Activity.RESULT_OK,intent);
        }
    };
    View.OnClickListener btnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerFragment.this.dismiss();
        }
    };

}
