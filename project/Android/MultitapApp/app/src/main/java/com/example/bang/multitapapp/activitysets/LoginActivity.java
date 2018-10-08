package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BANG on 2015-11-24.
 */
public class LoginActivity extends Activity implements View.OnClickListener{

    private UserClass userClass;

    private EditText edtxId, edtxPassword;

    private TextView txtvLogin, txtvJoin;
    private TextView txtvBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Init();
    }

    public void Init(){
        edtxId = (EditText) findViewById(R.id.edtxLoginId);
        edtxPassword = (EditText) findViewById(R.id.edtxLoginPassword);

        txtvLogin = (TextView) findViewById(R.id.txtvLoginLogin);
        txtvJoin = (TextView) findViewById(R.id.txtvLoginJoin);
        txtvBT = (TextView) findViewById(R.id.txtvLoginBT);

        txtvLogin.setOnClickListener(this);
        txtvJoin.setOnClickListener(this);
        txtvBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == txtvLogin.getId()){
            String strId = edtxId.getText().toString();
            String strPassword = edtxPassword.getText().toString();

            TaskLogin taskLogin = new TaskLogin(strId, strPassword);
            taskLogin.execute("");
        }
        else if(v.getId() == txtvJoin.getId()){
            Intent intent = new Intent(this, JoinActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == txtvBT.getId()){
            Intent intent = new Intent(this, BTActivity.class);
            startActivity(intent);
        }

        overridePendingTransition(R.anim.rigth_to_left_in, R.anim.origin_to_left);
        //overridePendingTransition(R.anim.left_to_origin, R.anim.left_to_right_out);
    }

    public static String SHA256(String str){
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }




    public class TaskLogin extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/memberlogin.php";

        private String user_id;
        private String member_password;

        private ProgressDialog progressDialog;

        public TaskLogin(String member_id, String member_password) {
            this.user_id = member_id;
            this.member_password = member_password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            member_password = SHA256(member_password);

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("로그인중");
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
                    .addFormDataPart("member_password", member_password)               //비참여자
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

            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");

                if(result.equals("ok")){
                    userClass = new UserClass();
                    String member_id = jsonObject.getString("member_id");

                    JSONArray jsonArrayDeviceList = jsonObject.getJSONArray("device_list");
                    for(int i=0;i<jsonArrayDeviceList.length();i++){
                        JSONObject jsonObjectDeviceList = jsonArrayDeviceList.getJSONObject(i);

                        String id = jsonObjectDeviceList.getString("id");
                        String device_id = jsonObjectDeviceList.getString("device_id");
                        String name = jsonObjectDeviceList.getString("name");

                        DeviceClass deviceClass = new DeviceClass();
                        deviceClass.setId(id);
                        deviceClass.setDevice_id(device_id);
                        deviceClass.setName(name);

                        userClass.getArrDevice().add(deviceClass);
                    }

                    userClass.setId(member_id);
                    userClass.setUser_id(user_id);
                    userClass.setPassword(member_password);

                    Intent intent = new Intent(LoginActivity.this, WeblistActivity.class);
                    intent.putExtra("userClass", userClass);
                    startActivity(intent);

                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "로그인 실패입니다. 아이디와 패스워드를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
