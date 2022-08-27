package com.joy.hellogrpc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import dmax.dialog.SpotsDialog;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloReply;


public class HelloWorldActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnConnect;

    private final String SERVER_ADDRESS = "";
    private final int PORT = 8082;

    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld);

        findViews();

        dialog = new SpotsDialog(this, R.style.LoadingDialog);
        dialog.setCancelable(false);

        btnConnect.setOnClickListener((View v) -> {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_ADDRESS, PORT)
                    .usePlaintext()
                    .build();
            requestExampleService(channel);
        });
    }

    private void requestExampleService(ManagedChannel channel) {
        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);

        HelloRequest helloRequest = HelloRequest.newBuilder().setName("test").build();
        dialog.show();

        Thread backgroundThread = new Thread(() -> {
            try {
                HelloReply helloReply = stub.sayHello(helloRequest);
                showResultMessage("Response: " + helloReply.getMessage());
            }
            catch(Exception e){
                showResultMessage("Error " + e.getMessage());
                Log.e("app", "exception", e);
            }
        });

        backgroundThread.start();

    }


    private void showResultMessage(String message) {
        runOnUiThread(() -> {
            tvResult.setText(message);
            dialog.dismiss();
        });
    }

    /**
     * find all components from view
     */
    private void findViews() {
        tvResult = findViewById(R.id.tv_result);
        btnConnect = findViewById(R.id.btn_server_connect);
    }



}
