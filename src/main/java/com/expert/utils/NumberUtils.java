package com.expert.utils;

import java.text.NumberFormat;

public class NumberUtils {

    public static String calculatePercentage(long current,long target){

         String percentStr;
         Double percentNumber;
         if(target == 0){
             //被除数为0的情况
             percentNumber = 0.0;
         }else{
             //先转换成double再除
             percentNumber = current*1.0/target;
         }
         //百分比
        NumberFormat nf = NumberFormat.getPercentInstance();
        //控制保留小数点后几位
        nf.setMinimumFractionDigits(2);
        percentStr = nf.format(percentNumber);

        return percentStr;
    }
}
