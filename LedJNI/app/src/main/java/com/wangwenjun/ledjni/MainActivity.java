package com.wangwenjun.ledjni;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button bt = (Button) findViewById(R.id.button);
        Button bt2 = (Button) findViewById(R.id.button2);

        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ledOn();
                Toast.makeText(MainActivity.this, "led打开成功", Toast.LENGTH_SHORT).show();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ledOff();
                Toast.makeText(MainActivity.this, "led关闭成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int ledOn();
    public native int ledOff();
}
