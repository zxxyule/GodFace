package com.example.administrator.godface;


import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.godface.db.City;
import com.example.administrator.godface.db.County;
import com.example.administrator.godface.db.Province;
import com.example.administrator.godface.util.Myapplication;
import com.example.administrator.godface.util.ResolveJSON;
import com.example.administrator.godface.util.httpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class choose_area extends Fragment {


  public static final int level_province=0;
    public static final int level_city=1;
    public static final int level_county=2;
    private ProgressDialog progressdialog;
    private TextView titletext;
    private Button backbutton;
    private ListView listview;
    private ListView listview_city;
    private ListView listview_county;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter_city;
    private ArrayAdapter<String> adapter_county;
    private List<String> datalist=new ArrayList<>();
    private List<String> datalist_city=new ArrayList<>();
    private List<String> datalist_county=new ArrayList<>();
    //省列表
    private List<Province> provincelist;
    //市列表
    private List<City> citylist;
    //县列表
    private List<County> countylist;
    //选中的省份
    private Province selectedprovince;
    //选中的市
    private City selectedcity;
    //当前选中的级别
    private int selectedlevel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view=inflater.inflate(R.layout.fragment_choose_area,container,false);
        titletext=(TextView)view.findViewById(R.id.title_text);
        backbutton=(Button)view.findViewById(R.id.back_button);
        listview=(ListView)view.findViewById(R.id.list_view_province);
        listview_city=(ListView)view.findViewById(R.id.list_view_city);
        listview_county=(ListView)view.findViewById(R.id.list_view_county);
        adapter= new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, datalist);
        adapter_city=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,datalist_city);
        adapter_county=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,datalist_county);
        listview.setAdapter(adapter);
        listview_city.setAdapter(adapter_city);
        listview_county.setAdapter(adapter_county);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedprovince=provincelist.get(position);
                    view.setSelected(true);
                    querycity();
            }
        });
        listview_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedcity=citylist.get(position);
                querycounty();
            }
        });
       // selectedlevel=level_province;
    }
    //查询全国所有的省，优先数据库查询，如果数据库中没有，再从服务器请求
    private void queryProvinces(){
        titletext.setText("中国");
        backbutton.setVisibility(View.GONE);
        provincelist= DataSupport.findAll(Province.class);
        if(provincelist.size()>0){
            datalist.clear();
            for(Province province:provincelist){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            selectedprovince=provincelist.get(0);
            querycity();
        }else{
            String address="http://guolin.tech/api/china";
            queryfromServer(address,"province");
        }
    }
    private void querycity(){
        //titletext.setText(selectedprovince.getProvinceName());
       // backbutton.setVisibility(View.VISIBLE);
        citylist=DataSupport.where("province_id=?",selectedprovince.getId()+"").find(City.class);
        if(citylist.size()>0){
            datalist_city.clear();
            for(City city:citylist){
                datalist_city.add(city.getCityName());
            }
            //selectedlevel=level_city;
            adapter_city.notifyDataSetChanged();
            listview_city.setSelection(0);
            selectedcity=citylist.get(0);
            querycounty();
        }else{
            String address="http://guolin.tech/api/china/"+selectedprovince.getProvinceCode();
            queryfromServer(address,"city");
        }
    }
    private void querycounty(){
        //titletext.setText(selectedcity.getCityName());
       // backbutton.setVisibility(View.VISIBLE);
        countylist=DataSupport.where("city_id=?",selectedcity.getId()+"").find(County.class);
        if(countylist.size()>0){
            datalist_county.clear();
            for(County county:countylist){
                datalist_county.add(county.getCountyName());
            }
            adapter_county.notifyDataSetChanged();
            //selectedlevel=level_county;
        }else{
            String address="http://guolin.tech/api/china/"+selectedprovince.getProvinceCode()+"/"+selectedcity.getCityCode();
            queryfromServer(address,"county");
        }
    }
    //根据请求的地址从服务器请求数据
    private void queryfromServer(String address, final String Datatype){
        showpressdialog();
        httpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            closeprogressdialog();
                        Toast.makeText(getContext(),"加载失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    String responseresult=response.body().string();
                    boolean result=false;
                if(Datatype.equals("province")){
                    result= ResolveJSON.handleprovinceResponse(responseresult);
                }else if(Datatype.equals("city")){
                    result=ResolveJSON.handlecityResponse(responseresult,selectedprovince);
                }else if(Datatype.equals("county")){
                    result=ResolveJSON.handlecountyResponse(responseresult,selectedcity);
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeprogressdialog();
                            switch(Datatype){
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    querycity();
                                    break;
                                case "county":
                                    querycounty();
                                    break;
                                default:break;
                            }
                        }
                    });
                }
            }
        });
    }
    private void showpressdialog(){
        if(progressdialog==null){
            progressdialog =new ProgressDialog(getActivity());
            progressdialog.setMessage("正在加载，请稍后....");
            progressdialog.setCanceledOnTouchOutside(false);
        }
        progressdialog.show();
    }
    private void closeprogressdialog(){
        if(progressdialog!=null){
            progressdialog.dismiss();
        }
    }
}
