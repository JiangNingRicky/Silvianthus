package com.expert.utils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FileUtils {

    private static AtomicLong totalLetter = new AtomicLong();
    private static AtomicLong totalFile = new AtomicLong();
    private static AtomicLong totalFileRead = new AtomicLong();

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        //先统计一遍文件总数，用于监控进度
        letterCountDir("/Users/valtechwh/Documents/N132","pdf",true);
        System.out.println("--------------------------【START】There are ["+totalFile+"] files to be read");
        letterCountDir("/Users/valtechwh/Documents/N132","pdf",false);
        System.out.println("--------------------------【END】Letter count:["+totalLetter+"],cost ["+(System.currentTimeMillis()-startTime)+"]ms");
    }


    public static void letterCountDir(String path,String format,boolean justCountFile){
        File file = new File(path);
        //非空文件夹
        if(file.isDirectory() && file.listFiles()!=null){
            for(File f : file.listFiles()){
                letterCountDir(f.getAbsolutePath(),format,justCountFile);
            }
        }else if(file.isFile() && getFileFormat(file.getAbsolutePath()).equals(format)){
            if(justCountFile){
                //仅统计文件个数
                totalFile.incrementAndGet();
            }else{
                //如果是指定格式的文件，统计字数
                letterCount(file);
            }
        }
    }

    public static long letterCount(File file){
        //System.out.print(file.getAbsolutePath()+",");
        String suffix = getFileFormat(file.getAbsolutePath());
        switch(suffix){
            case "pdf": return letterCountPDF(file.getAbsolutePath());
        }
        return -1;
    }

    public static long letterCountPDF(String filePath){
        try {
            PdfReader reader = new PdfReader(filePath);
            return countText(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Integer countText(PdfReader reader) {
        try {
            int characterCount = 0;

            PdfDocument pdfDoc = new PdfDocument(reader);
            // get the number of pages in PDF
            int noOfPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= noOfPages; i++) {
                //TODO 多线程
                // Extract content of each page
                String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                //TODO 替换无用字符
                contentOfPage = contentOfPage.replaceAll("\\p{C}", "").replaceAll(" ", "");
                //统计数量
                characterCount += contentOfPage.length();
            }
            pdfDoc.close();
            //System.out.print(characterCount+",");
            totalLetter.addAndGet(characterCount);
            System.out.println("Progress:"+totalFile+"/"+totalFileRead.incrementAndGet()+"("+NumberUtils.calculatePercentage(totalFileRead.longValue(),totalFile.longValue())+")");
            return characterCount;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // do noting
            }
        }
    }

    public static String getFileFormat(String filePath){
        return filePath.substring(filePath.lastIndexOf(".")+1);
    }

}
