package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bang.multitapapp.R;

/**
 * Created by BANG on 2015-11-24.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private TextView txtvBT;
    private TextView txtvWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Init();
    }

    public void Init(){
        txtvBT = (TextView) findViewById(R.id.txtvMainBT);
        txtvWeb = (TextView) findViewById(R.id.txtvMainWeb);

        txtvBT.setOnClickListener(this);
        txtvWeb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == txtvBT.getId()){

        }
        else if(v.getId() == txtvWeb.getId()){
            Intent intent = new Intent(this, WeblistActivity.class);
            startActivity(intent);
        }
    }
}
