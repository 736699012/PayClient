package com.example.luffypclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alipay.ThirdPayAction;
import com.example.alipay.ThirdPayResult;
import com.example.luffypclient.utils.Constant;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView qbNum;
    private Button pay;
    private boolean isBindService;
    private ThirdPayAction thirdPayAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        doBindService();
    }

    /**
     * 绑定支付服务
     */
    private void doBindService() {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_THREEPAY);
        intent.setPackage("com.example.alipay");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        isBindService = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected....");
            thirdPayAction = ThirdPayAction.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected....");
            thirdPayAction =null;
        }
    };

    /**
     * 初始化资源
     */
    private void initView() {
        qbNum = findViewById(R.id.qNum);
        pay = findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Result result = new Result();
                if(result==null){
                    Log.d(TAG,"call ==null ");
                }
                try {
                    if(thirdPayAction!=null){
                        thirdPayAction.requestPay(pay.getText().toString(),100,result);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public  class Result extends ThirdPayResult.Stub {

        @Override
        public void paySuccess() throws RemoteException {
            Log.d(TAG,"充值成功... ");
            qbNum.setText("100Q币");
            Toast.makeText(MainActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void payError(int errorCode, String msg) throws RemoteException {
            Log.d(TAG,"充值失败... ");
        }


    }


    /**
     * 解除服务绑定，释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBindService&&serviceConnection!=null){
            unbindService(serviceConnection);
            serviceConnection=null;
            isBindService =false;
        }
    }
}
