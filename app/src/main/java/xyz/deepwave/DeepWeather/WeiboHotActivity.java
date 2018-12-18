package xyz.deepwave.DeepWeather;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import xyz.deepwave.entity.AqiAdapter;
import xyz.deepwave.entity.WeiboAdapter;
import xyz.deepwave.util.HttpUtil;

public class WeiboHotActivity extends AppCompatActivity {
    private String Html;
    private final String address = "https://s.weibo.com/top/summary?cate=realtimehot";
    private Button getWeibo;
    private Button weiboReturn;
    private List<String> hotInfo;
    private List<String> link;
    private RecyclerView weiboList;
    private WeiboAdapter weibo_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_hot);
        initView();
    }

    public void initView() {
        hotInfo = new ArrayList<>();
        link = new ArrayList<>();

        weiboList = findViewById(R.id.weibo_list);
        weiboReturn = findViewById(R.id.weibo_return);
        getWeibo = findViewById(R.id.get_weibo);

        weiboReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeiboHotActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager weibo_layoutManager = new LinearLayoutManager(this);
        weibo_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        weibo_adapter = new WeiboAdapter(hotInfo);
        weibo_adapter.setOnItemClickListener(new WeiboAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                openWeiboBrowser(WeiboHotActivity.this,link.get(position));
            }
            @Override
            public void onLongClick(int position) {
                Toast.makeText(WeiboHotActivity.this,"您长按点击了"+position+"行",Toast.LENGTH_SHORT).show();
            }
        });
        weiboList.setLayoutManager(weibo_layoutManager);
        weiboList.setAdapter(weibo_adapter);

        getWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHtml();
            }
        });
    }

    public void getHtml() {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeiboHotActivity.this, "无法访问微博", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Html = response.body().string();
                showHot();
            }
        });
        weibo_adapter.notifyDataSetChanged();
    }

    public void showHot() {
        List<String> temp = new ArrayList<>();
        Pattern pattern = Pattern.compile("td class=\\\"td-02\\\">\\n.*<a href=\\\"\\/weibo\\?q=(.*)&Refer=top\\\"");
        Matcher matcher = pattern.matcher(Html);

        while (matcher.find()) {
            temp.add(matcher.group(0));
        }
        hotInfo.clear();
        link.clear();
        for (String elemnt : temp) {
            try {
                link.add("https://s.weibo.com/"+splitIt(elemnt,'w','"'));
                elemnt = URLDecoder.decode(splitIt(elemnt,'%','&'),"utf-8");
                hotInfo.add(elemnt);
            }catch (UnsupportedEncodingException t) {
                // TODO Auto-generated catch block
                t.printStackTrace();
            }
        }
    }

    public String splitIt(String s,char c,char e){
        String res="";
        int i = 0;
        for(;s.charAt(i)!=c;i++);
        for(;s.charAt(i)!=e;i++)
            res+=s.charAt(i);
        return res;
    }

    public static void openWeiboBrowser(Activity activity,String url){
        if(activity==null){
            return;
        }
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("sinaweibo://browser?url="+url));
        activity.startActivity(intent);
    }

}
