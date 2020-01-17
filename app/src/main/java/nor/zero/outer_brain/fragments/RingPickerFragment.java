package nor.zero.outer_brain.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nor.zero.outer_brain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RingPickerFragment extends DialogFragment {

    View dialogView;
    ViewAnimator viewAnimator;
    ArrayList<HashMap<String,String>> dataSystemRing,dataCustomRing;
    MyAdapter adapterSystemRing,adapterCustomRing;
    ListView lvSystemRing,lvCustomRing;
    TextView tvSystemRing,tvCustomRing;
    ProgressDialog progressDialog;
    RingtoneManager ringtoneManager;
    MediaPlayer mediaPlayer;
    static int selectID = 0;
    static String CURRENT_PATH = "";
    final static String RING_TITLE = "ringTitle";
    final static String RING_URI = "ringUri";
    final static String FILE_TYPE = "fileType";
    final static String TYPE_FILE = "file";
    final static String TYPE_DIRECTORY = "directory";
    static String[] AUDIO_TYPE = {"mp3"} ;

    TextView testView;

    public RingPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.fragment_ring_picker, container, false);
        tvSystemRing = dialogView.findViewById(R.id.tvSystemRing);
        tvCustomRing = dialogView.findViewById(R.id.tvCustomRing);
        lvSystemRing = dialogView.findViewById(R.id.lvSystemRing);
        lvCustomRing = dialogView.findViewById(R.id.lvCustomRing);
        viewAnimator = (ViewAnimator)dialogView.findViewById(R.id.viewAnimator);

        dataSystemRing = new ArrayList<>();
        dataCustomRing = new ArrayList<>();
        adapterSystemRing = new MyAdapter(dataSystemRing);
        adapterCustomRing = new MyAdapter(dataCustomRing);
        lvSystemRing.setAdapter(adapterSystemRing);
        lvCustomRing.setAdapter(adapterCustomRing);

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnGoBack = dialogView.findViewById(R.id.btnGoBack);
        btnOk.setOnClickListener(btnClick);
        btnCancel.setOnClickListener(btnClick);
        btnGoBack.setOnClickListener(btnClick);
        tvSystemRing.setOnClickListener(textViewClick);
        tvCustomRing.setOnClickListener(textViewClick);
        lvSystemRing.setOnItemClickListener(lvSystemRingClick);
        lvCustomRing.setOnItemClickListener(lvCustomRingClick);

        //test
        testView = dialogView.findViewById(R.id.tvTest);

        return dialogView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressDialog();
        LoadSystemInformationTask task = new LoadSystemInformationTask();
        task.execute();
    }

    private void setView(){
        //讀取系統鈴聲資料,設為預設畫面
        setViewSelect(selectID);
        try{
            ringtoneManager = new RingtoneManager(getActivity());
            ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
            Cursor alarmsCursor = ringtoneManager.getCursor();
            int alarmCount = alarmsCursor.getCount();
            if(alarmCount == 0)
                return;
            alarmsCursor.moveToFirst();
            while (!alarmsCursor.isAfterLast()){
                int currentPosition = alarmsCursor.getPosition();
                Uri uri = ringtoneManager.getRingtoneUri(currentPosition);
                String title = ringtoneManager.getRingtone(currentPosition).getTitle(getContext());
                HashMap<String,String> item = new HashMap<>();

                item.put(RING_TITLE,title);
                item.put(RING_URI,""+uri);
                dataSystemRing.add(item);
                alarmsCursor.moveToNext();
            }
        }
        catch (Exception e){}
    }
    //讀取資料夾內部資料,填入畫面
    private void setFileRingView(String currentPath){
        dataCustomRing.clear();
        File[] files = new File(currentPath).listFiles();
        ArrayList<HashMap<String,String>> tempData = new ArrayList<>(); //暫時把檔案類型的item放這裡面
        int index;
        for(File file : files){
            if(file.isHidden())
                continue;
            HashMap<String,String> item = new HashMap<>();
            if(file.isDirectory()){
                index = file.toString().lastIndexOf('/');
                item.put(RING_TITLE,file.toString().substring(index+1));
                item.put(RING_URI,file.getPath());
                item.put(FILE_TYPE,TYPE_DIRECTORY);
                dataCustomRing.add(item);
            }
            else {
                index = file.toString().lastIndexOf('/');
                item.put(RING_TITLE,file.toString().substring(index+1));
                item.put(RING_URI,file.getPath());
                item.put(FILE_TYPE,TYPE_FILE);
                tempData.add(item);
            }
        }
        dataCustomRing.addAll(tempData);
    }
    //畫面設為系統鈴聲,或內建資料夾畫面
    private void setViewSelect(int selectID){
        int colorSystem = selectID == 0? R.color.Wheat : R.color.DarkSalmon;
        int colorCustom = selectID == 0? R.color.DarkSalmon : R.color.Wheat;
        tvSystemRing.setBackgroundColor(getResources().getColor(colorSystem));
        tvCustomRing.setBackgroundColor(getResources().getColor(colorCustom));
        viewAnimator.setDisplayedChild(selectID);
    }
    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btnOk:
                    break;
                case R.id.btnCancel:
                    if(mediaPlayer!= null && mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    RingPickerFragment.this.dismiss();
                    break;
                case R.id.btnGoBack:
                    goBackDirectory();
                    break;
            }

        }
    };
    View.OnClickListener textViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.tvSystemRing:
                    if(selectID == 0)
                        return;
                    selectID = 0;
                    setViewSelect(selectID);
                    break;
                case R.id.tvCustomRing:
                    if(selectID == 1)
                        return;
                    selectID = 1;
                    setViewSelect(selectID);
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener lvSystemRingClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Uri uri = ringtoneManager.getRingtoneUri(position);
            playSound(uri);
        }
    };
    AdapterView.OnItemClickListener lvCustomRingClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String fileType = dataCustomRing.get(position).get(FILE_TYPE);
            if(fileType.equals(TYPE_DIRECTORY)){
                CURRENT_PATH = dataCustomRing.get(position).get(RING_URI);
                setFileRingView(CURRENT_PATH);
                adapterCustomRing.notifyDataSetChanged();
            }
            else if(fileType.equals(TYPE_FILE)){
                String fileName = dataCustomRing.get(position).get(RING_TITLE);
                int index = dataCustomRing.get(position).get(RING_TITLE).lastIndexOf('.');
                String fileAtts = fileName.substring(index+1);
                if(isAudio(fileAtts))
                {
                    Uri uri = Uri.parse(dataCustomRing.get(position).get(RING_URI));
                    playSound(uri);
                }
            }
        }
    };
    private boolean isAudio(String atts){
        for (String type : AUDIO_TYPE){
            if(type.equals(atts))
                return true;
        }
        return false;
    }

    private void playSound(Uri uri){
        if(mediaPlayer!= null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getContext(),uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.sys_cancel_progress_dialog),
                getResources().getString(R.string.sys_loading_file), true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        RingPickerFragment.this.dismiss();
                    }
                });
    }
    //檔案目錄回到上一層
    private void goBackDirectory(){
        String root = Environment.getExternalStorageDirectory().toString();
        if(CURRENT_PATH.equals(root))
            return;
        String temp = CURRENT_PATH;
        int index = CURRENT_PATH.lastIndexOf('/');
        CURRENT_PATH = temp.substring(0,index);
        setFileRingView(CURRENT_PATH);
        adapterCustomRing.notifyDataSetChanged();
    }

    private class LoadSystemInformationTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... args) {
            setView();
            CURRENT_PATH = Environment.getExternalStorageDirectory().toString();
            setFileRingView(CURRENT_PATH);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            adapterSystemRing.notifyDataSetChanged();
            adapterCustomRing.notifyDataSetChanged();
            testView.setText(RingPickerFragment.CURRENT_PATH);
        }
    }

    private class MyAdapter extends BaseAdapter{

        ArrayList<HashMap<String,String>> data;
        public MyAdapter(ArrayList<HashMap<String,String>> data){
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
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
            View view = getLayoutInflater().inflate(R.layout.item_rings_list,null);
            TextView v = view.findViewById(R.id.tvRingTitle);
            v.setText(data.get(position).get(RING_TITLE));
            return view;
        }
    }

}
