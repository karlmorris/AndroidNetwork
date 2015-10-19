package edu.temple.androidnetwork;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class BrowserActivity extends Activity {

    EditText urlTextView;
    TextView display;
    Button goButton;

    Handler showContent = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            display.setText((String) msg.obj);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1234);

        display = (TextView) findViewById(R.id.display);
        urlTextView = (EditText) findViewById(R.id.url_textfield);
        goButton = (Button) findViewById(R.id.go_button);

        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread loadContent = new Thread() {
                    @Override
                    public void run() {

                        if (isNetworkActive()) {

                            URL url;

                            try {
                                url = new URL(urlTextView.getText().toString());
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                                url.openStream()));

                                String response = "", tmpResponse;

                                tmpResponse = reader.readLine();
                                while (tmpResponse != null) {
                                    response = response + tmpResponse;
                                    tmpResponse = reader.readLine();
                                }

                                Message msg = Message.obtain();

                                msg.obj = response;

                                showContent.sendMessage(msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(BrowserActivity.this, "Please connect to a network", Toast.LENGTH_SHORT).show();

                        }
                    }
                };

                loadContent.start();
            }
        });
    }

    public boolean isNetworkActive() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
