package xyz.deepwave.DeepWeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

import interfaces.heweather.com.interfacesmodule.bean.air.Air;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.deepwave.db.WeatherAddress;
import xyz.deepwave.entity.AqiAdapter;
import xyz.deepwave.entity.ForecastAdapter;
import xyz.deepwave.entity.LifeAdapter;
import xyz.deepwave.util.HttpUtil;


public class WeatherActivity extends AppCompatActivity {
    public WeatherAddress address;
    private ImageView backGround;
    private Button chooseArea;
    private TextView title_city;
    private TextView last_update_time;
    private TextView degree_text;
    private TextView weather_info_text;
    private List<Weather> myWeather;
    private List<Air> myAir;
    private List<String> aqi_info;
    private List<String> mlife;
    private List<ForecastBase> mforecast;
    private RecyclerView aqi_list;
    private RecyclerView forecast_list;
    private RecyclerView life_list;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AqiAdapter aqi_adapter;
    private ForecastAdapter forecast_adapter;
    private LifeAdapter life_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_weather);

       // address = (WeatherAddress) getIntent().getSerializableExtra("address");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("addressCode", null);

            WeatherAddress temp = (WeatherAddress) getIntent().getSerializableExtra("address");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("addressCode", temp.getAddressCode());
            editor.putString("addressName",temp.getAddressName());
            editor.apply();

        address.setAddressCode(weatherString);
        initView();
        backPic();
        getAllView(address);
    }

    public void initView() {
        HeConfig.init("HE1812071834161929", "498c7cad8e804d408f98c4c804971f4d");
        HeConfig.switchToFreeServerNode();

        /*** FindViewById***/
        degree_text = findViewById(R.id.degree_text);
        weather_info_text = findViewById(R.id.weather_info_text);
        title_city = findViewById(R.id.title_city);
        last_update_time = findViewById(R.id.last_update_time);
        chooseArea = findViewById(R.id.chooseArea);
        aqi_list = findViewById(R.id.aqi_list);
        forecast_list = findViewById(R.id.forecast_list);
        life_list = findViewById(R.id.suggestion_list_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.other_settings);
        backGround = findViewById(R.id.background_picture);

        myWeather = new ArrayList<>();
        aqi_info = new ArrayList<>();
        mforecast = new ArrayList<>();
        mlife = new ArrayList<>();
        myAir = new ArrayList<>();
        chooseArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //aqi
        LinearLayoutManager aqi_layoutManager = new LinearLayoutManager(this);
        aqi_layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        aqi_adapter = new AqiAdapter(aqi_info);
        aqi_list.setLayoutManager(aqi_layoutManager);
        aqi_list.setAdapter(aqi_adapter);

        //forecast
        LinearLayoutManager forecast_layoutManager = new LinearLayoutManager(this);
        forecast_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        forecast_list.setLayoutManager(forecast_layoutManager);
        forecast_adapter = new ForecastAdapter(mforecast);
        forecast_list.setAdapter(forecast_adapter);

        //life
        LinearLayoutManager life_layoutManager = new LinearLayoutManager(this);
        life_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        life_list.setLayoutManager(life_layoutManager);
        life_adapter = new LifeAdapter(mlife);
        life_list.setAdapter(life_adapter);

        //swipeRefresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllView(address);
                Toast.makeText(WeatherActivity.this, "刷新成功", Toast.LENGTH_LONG).show();
                backPic();
                swipeRefresh.setRefreshing(false);
            }
        });
        swipeRefresh.setColorSchemeColors(Color.parseColor("#3300FF"),Color.parseColor("#e1e1e1"));
        if(swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.account_login:
                        Toast.makeText(WeatherActivity.this,"Login",Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(WeatherActivity.this,UserLoginActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.cipher_text:
                        break;
                    case R.id.hot_news:
                        Intent intent3 = new Intent(WeatherActivity.this,WeiboHotActivity.class);
                        startActivity(intent3);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }

    public void getAllView(WeatherAddress A) {
        title_city.setText(A.getAddressName());
        getHeWeather(A.getAddressCode());
        getHeAir(A.getAddressCode());
        if(swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
    }

    public void getHeWeather(String loc) {
        HeWeather.getWeather(WeatherActivity.this, loc, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(List<Weather> list) {
                myWeather.clear();
                mforecast.clear();
                mlife.clear();
                life_adapter.notifyDataSetChanged();
                forecast_adapter.notifyDataSetChanged();

                myWeather.addAll(list);
                mforecast.addAll(myWeather.get(0).getDaily_forecast());
                showWeatherInfo();
                showForecastInfo();
                showLifeInfo();
            }
        });
    }

    public void getHeAir(String loc) {
        HeWeather.getAir(WeatherActivity.this, loc, new HeWeather.OnResultAirBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(WeatherActivity.this, "获取空气信息失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(List<Air> list) {
                myAir.clear();
                aqi_info.clear();
                aqi_adapter.notifyDataSetChanged();
                myAir.addAll(list);
                showAirInfo();
            }
        });
    }

    public void showWeatherInfo() {
        Weather w = myWeather.get(0);
        last_update_time.setText(String.format(getResources().getString(R.string.last_update_time), w.getUpdate().getLoc()));
        degree_text.setText(String.format(getResources().getString(R.string.degree_text), w.getNow().getTmp()));
        weather_info_text.setText(String.format(getResources().getString(R.string.weather_info),
                w.getNow().getCond_txt(), w.getDaily_forecast().get(0).getTmp_max(),
                w.getDaily_forecast().get(0).getTmp_min()));
    }

    public void showAirInfo() {
        Air a = myAir.get(0);
        aqi_info.add("站点检测时间\n" + a.getAir_now_city().getPub_time());
        aqi_info.add("空气质量监测站\n" + a.getAir_now_station().get(0).getAir_sta());
        aqi_info.add("空气质量指数\n" + a.getAir_now_city().getAqi());
        aqi_info.add("空气主要污染物\n" + a.getAir_now_city().getMain());
        aqi_info.add("PM2.5指数\n" + a.getAir_now_city().getPm25());
        aqi_info.add("NO2 指数\n" + a.getAir_now_city().getNo2());
        aqi_info.add("SO2 指数\n" + a.getAir_now_city().getSo2());
        aqi_info.add("CO  指数\n" + a.getAir_now_city().getCo());
        aqi_adapter.notifyDataSetChanged();

    }

    public void showForecastInfo() {
       life_adapter.notifyDataSetChanged();
    }

    public void showLifeInfo() {
        List<LifestyleBase> L = new ArrayList<>(myWeather.get(0).getLifestyle());
        for (LifestyleBase e : L) {
            mlife.add(e.getBrf() + '\n'+'\n' + e.getTxt());
        }
       life_adapter.notifyDataSetChanged();
    }

    private void backPic() {
        String backPicAddress = "https://source.unsplash.com/random";
        Glide.with(WeatherActivity.this).load(backPicAddress).into(backGround);
    }

}
