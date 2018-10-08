package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.datasets.DeviceClass;
import com.example.bang.multitapapp.datasets.UserClass;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by BANG on 2015-11-25.
 */
public class AddDeviceActivity extends Activity implements View.OnClickListener{

    private DeviceClass deviceClass;
    private String member_id;
    private int mode;                   //0이면 mac, 1 바코드 스캔
    private String device_mac;

    private ImageButton ibtnBack;

    private EditText edtxMac;
    private EditText edtxName;

    private TextView txtvOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_device);

        member_id = getIntent().getExtras().getString("member_id");
        mode = getIntent().getExtras().getInt("mode");

        if(mode == 1){
            device_mac = getIntent().getExtras().getString("device_mac");
        }

        Init();
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);

        edtxMac = (EditText) findViewById(R.id.edtxAddDeviceMAC);
        edtxName = (EditText) findViewById(R.id.edtxAddDeviceName);

        txtvOk = (TextView) findViewById(R.id.txtvAddDeviceOk);

        if(mode == 1){
            edtxMac.setText(device_mac);
            edtxMac.setEnabled(false);
        }

        ibtnBack.setOnClickListener(this);
        txtvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibtnBack.getId()){
            finish();
        }
        else if(v.getId() == txtvOk.getId()){
            String device_mac = edtxMac.getText().toString();
            String device_name = edtxName.getText().toString();
            TaskAddDevice taskAddDevice = new TaskAddDevice(member_id, device_mac, device_name);
            taskAddDevice.execute("");
        }
    }



    public class TaskAddDevice extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/plusdevice.php";

        private String user_id;
        private String device_mac;
        private String device_name;

        private ProgressDialog progressDialog;

        public TaskAddDevice(String member_id, String device_mac, String device_name) {
            this.user_id = member_id;
            this.device_mac = device_mac;
            this.device_name = device_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(AddDeviceActivity.this);
            progressDialog.setMessage("멀티탭 추가 중");
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
                    .addFormDataPart("member_id", user_id)
                    .addFormDataPart("device_mac", device_mac)
                    .addFormDataPart("device_name", device_name)
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

            //Log.w("json", s);

            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");

                if(result.equals("ok")){
                    deviceClass = new DeviceClass();

                    JSONObject jsonObjectDeviceIntfo = jsonObject.getJSONObject("member_device_info");
                    String id = jsonObjectDeviceIntfo.getString("id");
                    String device_id = jsonObjectDeviceIntfo.getString("device_id");
                    String name = jsonObjectDeviceIntfo.getString("name");

                    deviceClass.setId(id);
                    deviceClass.setDevice_id(device_id);
                    deviceClass.setName(name);

                    Intent intent = new Intent();
                    intent.putExtra("deviceClass", deviceClass);

                    setResult(RESULT_OK, intent);

                    finish();
                    overridePendingTransition(R.anim.left_to_origin, R.anim.left_to_right_out);
                    Toast.makeText(AddDeviceActivity.this, "멀티탭 추가 완료", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("nothing")){
                    Toast.makeText(AddDeviceActivity.this, "데이터베이스에 등록된 멀티탭이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("already")){
                    Toast.makeText(AddDeviceActivity.this, "이미 추가하신 멀티탭 입니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
