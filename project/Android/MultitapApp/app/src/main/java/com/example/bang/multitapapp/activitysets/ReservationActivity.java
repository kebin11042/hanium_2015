package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.datasets.PortClass;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by BANG on 2015-11-27.
 */
public class ReservationActivity extends Activity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final String TIME_PATTERN = "HH:mm";

    private PortClass portClass;

    private String Year, Month, Day, Hour, Min;

    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private ImageButton ibtnBack;

    private TextView txtvDate;
    private TextView txtvTime;

    private TextView txtvDateSet;
    private TextView txtvTimeSet;
    private TextView txtvOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation);

        portClass = (PortClass) getIntent().getExtras().getSerializable("portClass");

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        Init();
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);

        txtvDate = (TextView) findViewById(R.id.txtvReservationDate);
        txtvTime = (TextView) findViewById(R.id.txtvReservationTime);

        txtvDateSet = (TextView) findViewById(R.id.txtvReservationDateSet);
        txtvTimeSet = (TextView) findViewById(R.id.txtvReservationTimeSet);

        txtvOk = (TextView) findViewById(R.id.txtvReservationOk);

        ibtnBack.setOnClickListener(this);
        txtvDateSet.setOnClickListener(this);
        txtvTimeSet.setOnClickListener(this);
        txtvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibtnBack.getId()){
            finish();
        }
        else if(v.getId() == txtvDateSet.getId()){
            DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
        }
        else if(v.getId() == txtvTimeSet.getId()){
            TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
        }
        else if(v.getId() == txtvOk.getId()){
            String strDate = txtvDate.getText().toString();
            String strTime = txtvTime.getText().toString();

            if(strDate.equals("0000년 00월 00일") || strTime.equals("00시 00분")){
                Toast.makeText(ReservationActivity.this, "날짜, 시간을 설정해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else{
                TaskReservation taskReservation = new TaskReservation();
                taskReservation.execute("");
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        Year = i + "";
        Month = (i1 + 1) + "";
        Day = i2 + "";

        Log.w("Date", Year + Month + Day);
        txtvDate.setText(Year + "년 " + Month + "월 " + Day + "일");
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
        Hour = i + "";
        Min = i1 + "";

        Log.w("Time", Hour + Min);
        txtvTime.setText(Hour + "시 " + Min + "분");
    }




    public class TaskReservation extends AsyncTask<String, Integer, String> {

        private String strUrl = "http://kebin1104.dothome.co.kr/multi/portreservation.php";

        private ProgressDialog progressDialog;

        public TaskReservation() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ReservationActivity.this);
            progressDialog.setMessage("예약 중");
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
                    .addFormDataPart("device_id", portClass.getDevice_id())
                    .addFormDataPart("port_id", portClass.getId())
                    .addFormDataPart("year", Year)
                    .addFormDataPart("month", Month)
                    .addFormDataPart("day", Day)
                    .addFormDataPart("hour", Hour)
                    .addFormDataPart("min", Min)
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
                    Toast.makeText(ReservationActivity.this, "예약 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(ReservationActivity.this, "API 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ReservationActivity.this, "JSON 파싱 에러", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
