package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.datasets.DeviceClass;
import com.example.bang.multitapapp.datasets.PortClass;
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
public class MultitapActivity extends Activity implements View.OnClickListener{

    private DeviceClass deviceClass;

    private TextView txtvName;

    private ImageButton ibtnBack;

    private TextView txtvPortCnt;

    private ListView listvMultitap;
    private AdapterPortList adapterPortList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multitap);

        deviceClass = (DeviceClass) getIntent().getExtras().getSerializable("deviceClass");

        Init();
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);

        txtvName = (TextView) findViewById(R.id.txtvMultitapName);
        txtvName.setText(deviceClass.getName() + " 멀티탭");

        txtvPortCnt = (TextView) findViewById(R.id.txtvMultitapCnt);

        listvMultitap = (ListView) findViewById(R.id.listvMultitap);

        ibtnBack.setOnClickListener(this);

        TaskPortList taskPortList = new TaskPortList();
        taskPortList.execute("");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibtnBack.getId()){
            finish();
        }
    }



    public class AdapterPortList extends BaseAdapter {

        private Context context;

        public AdapterPortList(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return deviceClass.getArrPortClases().size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            final PortClass portClass = deviceClass.getArrPortClases().get(position);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.view_multitap, parent, false);

            TextView txtvPort = (TextView) convertView.findViewById(R.id.txtvMultitapPort);
            txtvPort.setText( (position + 1) + " 구");

            ImageView imgvOnOff = (ImageView) convertView.findViewById(R.id.imgvMultitapOnOff);
            if(portClass.getState().equals("0")) {
                imgvOnOff.setImageResource(R.drawable.img_off);
            }
            else{
                imgvOnOff.setImageResource(R.drawable.img_on);
            }

            final int index = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskPortOnOff taskPortOnOff = new TaskPortOnOff(position);
                    taskPortOnOff.execute("");
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(MultitapActivity.this, ReservationActivity.class);
                    intent.putExtra("portClass", portClass);
                    startActivity(intent);

                    return true;
                }
            });

            return convertView;
        }
    }




    public class TaskPortList extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/deviceport.php";

        private ProgressDialog progressDialog;

        public TaskPortList() {
            Log.w("device_id", deviceClass.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MultitapActivity.this);
            progressDialog.setMessage("목록 불러오는 중");
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
                    .addFormDataPart("device_id", deviceClass.getDevice_id())
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
                    JSONArray jsonArrayPortList = jsonObject.getJSONArray("port_list");
                    for(int i=0;i<jsonArrayPortList.length();i++){
                        JSONObject jsonObjectPortList = jsonArrayPortList.getJSONObject(i);

                        String id = jsonObjectPortList.getString("id");
                        String port_number = jsonObjectPortList.getString("port_number");
                        String status = jsonObjectPortList.getString("status");

                        PortClass portClass = new PortClass();
                        portClass.setId(id);
                        portClass.setDevice_id(deviceClass.getDevice_id());
                        portClass.setPort_num(port_number);
                        portClass.setState(status);

                        deviceClass.getArrPortClases().add(portClass);
                    }

                    txtvPortCnt.setText(deviceClass.getArrPortClases().size() + "개");
                    adapterPortList = new AdapterPortList(MultitapActivity.this);
                    listvMultitap.setAdapter(adapterPortList);
                }
                else{
                    Toast.makeText(MultitapActivity.this, "API 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MultitapActivity.this, "JSON 파싱 에러", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class TaskPortOnOff extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/updateport.php";

        private int index;
        private String port_id;
        private String stauts;

        private ProgressDialog progressDialog;

        public TaskPortOnOff(int index) {
            this.index = index;

            port_id = deviceClass.getArrPortClases().get(index).getId();
            if(deviceClass.getArrPortClases().get(index).getState().equals("0")){
                stauts = "1";
            }
            else{
                stauts = "0";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MultitapActivity.this);
            progressDialog.setMessage("제어중");
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
                    .addFormDataPart("port_id", port_id)
                    .addFormDataPart("status", stauts)
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

            //Log.w("s", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");

                if(result.equals("ok")){
                    deviceClass.getArrPortClases().get(index).setState(stauts);
                    adapterPortList.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MultitapActivity.this, "API 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MultitapActivity.this, "JSON 파싱 에러", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
