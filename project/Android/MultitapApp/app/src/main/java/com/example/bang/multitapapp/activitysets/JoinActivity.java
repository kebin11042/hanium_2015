package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.datasets.UserClass;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BANG on 2015-11-24.
 */
public class JoinActivity extends Activity implements View.OnClickListener{

    private ImageButton ibtnBack;

    private EditText edtxId, edtxPassword, edtxPasswordCon;

    private TextView txtvJoinOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join);

        Init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.left_to_origin, R.anim.left_to_right_out);
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);

        edtxId = (EditText) findViewById(R.id.edtxJoinId);
        edtxPassword = (EditText) findViewById(R.id.edtxJoinPassword);
        edtxPasswordCon = (EditText) findViewById(R.id.edtxJoinPasswordCon);

        txtvJoinOk = (TextView) findViewById(R.id.txtvJoinOk);

        ibtnBack.setOnClickListener(this);
        txtvJoinOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibtnBack.getId()){
            finish();
        }
        else if(v.getId() == txtvJoinOk.getId()){
            String strId = edtxId.getText().toString();
            String strPassword = edtxPassword.getText().toString();
            String strPasswordCon = edtxPasswordCon.getText().toString();

            if(!strPassword.equals(strPasswordCon) || strId.length() == 0){

            }
            else{
                TaskJoin taskJoin = new TaskJoin(strId, strPassword);
                taskJoin.execute("");
            }
        }
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



    public class TaskJoin extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/memberjoin.php";

        private String member_id;
        private String member_password;

        private ProgressDialog progressDialog;

        public TaskJoin(String member_id, String member_password) {
            this.member_id = member_id;
            this.member_password = member_password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            member_password = SHA256(member_password);

            progressDialog = new ProgressDialog(JoinActivity.this);
            progressDialog.setMessage("회원가입중");
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
                    .addFormDataPart("member_id", member_id)
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
                    Toast.makeText(JoinActivity.this, "가입하신 계정으로 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if(result.equals("overlap")){
                    Toast.makeText(JoinActivity.this, "아이디 중복입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(JoinActivity.this, "API 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
