package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bang.multitapapp.BS.IntentIntegrator;
import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.datasets.DeviceClass;
import com.example.bang.multitapapp.datasets.PortClass;
import com.example.bang.multitapapp.datasets.UserClass;
import com.example.bang.multitapapp.dialogsets.DeviceAddDialog;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BANG on 2015-11-24.
 */
public class WeblistActivity extends Activity implements View.OnClickListener{

    private final int REQCODE_ADD_DEVICE = 30;

    private UserClass userClass;

    private ImageButton ibtnBack;
    private ImageButton ibtnPlus;
    private ImageButton ibtnBT;

    private ListView listvWebList;
    private AdapterWebList adapterWebList;

    private TextView txtvNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_weblist);

        userClass = (UserClass) getIntent().getExtras().getSerializable("userClass");

        Init();
    }

    public void Init(){

        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);
        ibtnPlus = (ImageButton) findViewById(R.id.ibtnWeblistPlus);
        ibtnBT = (ImageButton) findViewById(R.id.ibtnWeblistBT);

        listvWebList = (ListView) findViewById(R.id.listvWeblistList);

        txtvNothing = (TextView) findViewById(R.id.txtvWeblistNothing);

        ibtnBack.setOnClickListener(this);
        ibtnPlus.setOnClickListener(this);
        ibtnBT.setOnClickListener(this);

        setTxtvNothingVisible();

        adapterWebList = new AdapterWebList(WeblistActivity.this);
        listvWebList.setAdapter(adapterWebList);
    }

    public void setTxtvNothingVisible(){
        if(userClass.getArrDevice().size() != 0){
            txtvNothing.setVisibility(View.GONE);
        }
        else{
            txtvNothing.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibtnBack.getId()){
            finish();
        }
        else if(v.getId() == ibtnPlus.getId()){
            DeviceAddDialog deviceAddDialog = new DeviceAddDialog(WeblistActivity.this, userClass.getId());
            deviceAddDialog.show();
        }
        else if(v.getId() == ibtnBT.getId()){
            Intent intent = new Intent(WeblistActivity.this, BTActivity.class);
            startActivity(intent);
        }

        overridePendingTransition(R.anim.rigth_to_left_in, R.anim.origin_to_left);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.left_to_origin, R.anim.left_to_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 바코드스캐너로부터 응답.
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");   //mac주소가 옴
                //Log.w("scanresult", contents);

                Intent intent = new Intent(WeblistActivity.this, AddDeviceActivity.class);
                intent.putExtra("member_id", userClass.getId());
                intent.putExtra("mode", 1);
                intent.putExtra("device_mac", contents);
                startActivityForResult(intent, REQCODE_ADD_DEVICE);
            }
        }
        //멀티탭 추가 응답
        if(requestCode == REQCODE_ADD_DEVICE) {
            if(resultCode == RESULT_OK) {
                DeviceClass deviceClass = (DeviceClass) data.getExtras().getSerializable("deviceClass");
                userClass.getArrDevice().add(deviceClass);

                adapterWebList = new AdapterWebList(WeblistActivity.this);
                listvWebList.setAdapter(adapterWebList);

                setTxtvNothingVisible();
            }
        }
    }




    public class AdapterWebList extends BaseAdapter {

        private Context context;

        public AdapterWebList(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return userClass.getArrDevice().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final DeviceClass deviceClass = userClass.getArrDevice().get(position);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.view_weblist, parent, false);

            TextView txtvDeviceName = (TextView) convertView.findViewById(R.id.txtvWeblistDeviceName);
            txtvDeviceName.setText(deviceClass.getName() + " 멀티탭");

            final int index = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeblistActivity.this, MultitapActivity.class);
                    intent.putExtra("deviceClass", userClass.getArrDevice().get(index));
                    startActivity(intent);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(WeblistActivity.this);
                    builder.setTitle("삭제하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TaskDeleteDevice taskDeleteDevice = new TaskDeleteDevice(index);
                            taskDeleteDevice.execute("");
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();



                    return true;
                }
            });

            return convertView;
        }
    }




    public class TaskDeleteDevice extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/deletedevice.php";

        private ProgressDialog progressDialog;

        private int index;

        public TaskDeleteDevice(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(WeblistActivity.this);
            progressDialog.setMessage("삭제하는 중");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String ret = "";

            OkHttpClient client = new OkHttpClient();


            //전송할 포스트 변수 준비

            RequestBody requestBody;
            requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("member_device_id", userClass.getArrDevice().get(index).getId())
                    .build();


            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    ret = response.body().string();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            Log.w("s", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");

                if(result.equals("ok")){
                    userClass.getArrDevice().remove(index);
                    adapterWebList.notifyDataSetChanged();
                    setTxtvNothingVisible();
                }
                else{
                    Toast.makeText(WeblistActivity.this, "API 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(WeblistActivity.this, "JSON 파싱 에러", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
