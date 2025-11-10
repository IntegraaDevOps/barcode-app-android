package net.integraa.read;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import net.integraa.read.scanditLayout.FullscreenScanFragmentContainerActivity;
import net.integraa.read.R;

public class MainActivity extends AppCompatActivity {

    public Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;
        Intent intent = new Intent(MainActivity.this, FullscreenScanFragmentContainerActivity.class);
        startActivity(intent);

    }



}