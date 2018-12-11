package xyz.deepwave.DeepWeather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;


public class WeatherActivity extends AppCompatActivity {
    String addressCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        addressCode = getIntent().getStringExtra("addressCode");
        heInit();


    }
    public void heInit() {
        HeConfig.init("HE1812071834161929", "498c7cad8e804d408f98c4c804971f4d");
        HeConfig.switchToFreeServerNode();
    }

}
