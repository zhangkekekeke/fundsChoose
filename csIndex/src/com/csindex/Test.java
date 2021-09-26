package com.csindex;

import com.csindex.model.IndustryModule;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        IndustryModule model = new IndustryModule();
//        20210903
        int year = 2020;
        int month = 9;
        int day = 3;

        for (int i = 1; i <= 12; i++) {
            String date;

            if (i < 10) {
                date = String.format("%d0%d0%d", year, i, day);
            } else {
                date = String.format("%d%d0%d", year, i, day);
            }

            model.downloadIndustryPE(date, new MyCallBack(day,i,year,date,model));
        }
    }

    static class MyCallBack implements Callback<ResponseBody>{
        int limit = 0;
        int myDay ;
        int month;
        int year ;
        String myDate ;
        private IndustryModule model;

        public MyCallBack(int myDay, int month, int year ,String myDate, IndustryModule model) {
            this.myDay = myDay;
            this.month = month;
            this.year = year;
            this.myDate = myDate;
            this.model = model;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> p1) {
            System.out.println("" + p1.code() + " " + myDate);
            if (p1.code() == 200 && p1.body() != null) {
                try {
                    byte[] bytes = p1.body().bytes();
                    model.saveDate(myDate, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(limit>3)return;
                String date;
                if (month < 10) {
                    date = String.format("%d0%d0%d", year, month, ++myDay);
                } else {
                    date = String.format("%d%d0%d", year, month, ++myDay);
                }
                myDate = date;
                model.downloadIndustryPE(myDate,this);
                limit++;
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {

        }
    }
}
