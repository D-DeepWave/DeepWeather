package xyz.deepwave.DeepWeather;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.deepwave.db.City;
import xyz.deepwave.db.County;
import xyz.deepwave.db.DbManager;
import xyz.deepwave.db.Province;
import xyz.deepwave.gen.CityDao;
import xyz.deepwave.gen.CountyDao;
import xyz.deepwave.gen.ProvinceDao;
import xyz.deepwave.util.HttpUtil;
import xyz.deepwave.util.Utility;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseArea extends Fragment {

    private ProgressBar progressBar;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    //控件

    private Province selectProvince;
    private City selectCity;
    private County selectCounty;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    String now;

    //数据
    private void queryProvince() {

        titleText.setText("国家：中国");
        backButton.setVisibility(View.GONE);
        ProvinceDao mProvinceDao = DbManager.getInstance(getContext()).getDaoSession(getContext()).getProvinceDao();

        provinceList = mProvinceDao.loadAll();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            now = "Province";
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"Province");
        }

    }

    private void queryCity() {
        titleText.setText("省份：" + selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        CityDao mCityDao = DbManager.getInstance(getContext()).getDaoSession(getContext()).getCityDao();

        cityList = mCityDao.queryBuilder()
                .where(CityDao.Properties.ProvinceId.eq(selectProvince.getProvinceCode()))
                .orderAsc(CityDao.Properties.Id)
                .list();

        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            now = "City";
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"City");

        }


    }

    private void queryCounty() {
        titleText.setText("城市：" + selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        CountyDao mCountyDao = DbManager.getInstance(getContext()).getDaoSession(getContext()).getCountyDao();

        countyList = mCountyDao.queryBuilder()
                .where(CountyDao.Properties.CityId.eq(selectCity.getCityCode()))
                .orderAsc(CountyDao.Properties.Id)
                .list();

        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            now = "County";
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"County");

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        //一行text
        listView.setAdapter(adapter);
        return view;
    }

    private void queryFromServer(String address, final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type) {
                    case "Province":
                        result = Utility.handleProvinceResponse(responseText,getContext());
                        break;
                    case "City":
                        result = Utility.handleCityResponse(responseText, selectProvince.getProvinceCode(),getContext());
                        break;
                    case "County":
                        result = Utility.handleCountyResponse(responseText, selectCity.getCityCode(),getContext());
                        break;
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            switch (type) {
                                case "Province":
                                    queryProvince();
                                    break;
                                case "City":
                                    queryCity();
                                    break;
                                case "County":
                                    queryCounty();
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (now) {
                    case "Province":
                        selectProvince = provinceList.get(position);
                        queryCity();
                        break;
                    case "City":
                        selectCity = cityList.get(position);
                        queryCounty();
                        break;
                    case "County":
                        String address = countyList.get(position).getWeatherId();
                        if (getActivity() instanceof MainActivity) {

                            Intent intent = new Intent(getActivity(), WeatherActivity.class);   //获取实例
                            intent.putExtra("addressCode", address);
                            startActivity(intent);
                            getActivity().finish();
                        } else if (getActivity() instanceof WeatherActivity) {

                            WeatherActivity activity = (WeatherActivity) getActivity();

                        }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (now .equals( "County")) {
                            queryCity();
                        } else if (now .equals("City")) {
                            queryProvince();
                        }
                    }
                });
            }
        });
        queryProvince();
    }


}
