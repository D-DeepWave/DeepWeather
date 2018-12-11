package xyz.deepwave.DeepWeather;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import xyz.deepwave.gen.DaoMaster;
import xyz.deepwave.gen.DaoSession;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // Intent intent = new Intent(this,WeatherActivity.class);
       // startActivity(intent);
    }

}
