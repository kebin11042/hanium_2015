package com.example.bang.multitapapp.dialogsets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bang.multitapapp.BS.IntentIntegrator;
import com.example.bang.multitapapp.R;
import com.example.bang.multitapapp.activitysets.AddDeviceActivity;

/**
 * Created by BANG on 2015-11-25.
 */
public class DeviceAddDialog extends Dialog implements View.OnClickListener{

    private final int REQCODE_ADD_DEVICE = 30;

    private String member_id;

    private Context context;

    private TextView txtvMac, txtvBC;

    public DeviceAddDialog(Context context, String member_id) {
        super(context);

        this.context = context;
        this.member_id = member_id;

        this.setTitle("등록 방법 선택");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_add_device);

        txtvMac = (TextView) findViewById(R.id.txtvAddDeviceDlgMAC);
        txtvBC = (TextView) findViewById(R.id.txtvAddDeviceDlgBC);

        txtvMac.setOnClickListener(this);
        txtvBC.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == txtvMac.getId()){
            Intent intent = new Intent(context, AddDeviceActivity.class);
            intent.putExtra("member_id", member_id);
            intent.putExtra("mode", 0);
            ((Activity) context).startActivityForResult(intent, REQCODE_ADD_DEVICE);
        }
        else if(v.getId() == txtvBC.getId()){
            new IntentIntegrator((Activity)context).initiateScan(IntentIntegrator.QR_CODE_TYPES);
        }
        this.dismiss();
    }


}
