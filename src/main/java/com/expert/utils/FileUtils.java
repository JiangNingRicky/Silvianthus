package com.expert.utils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FileUtils {

    private static AtomicLong totalLetter = new AtomicLong();
    private static AtomicLong totalFile = new AtomicLong();
    private static AtomicLong totalFileRead = new AtomicLong();

    private static ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(20,20,30,TimeUnit.SECONDS,new ArrayBlockingQueue(1000));

    private static CountDownLatch totalFileCountdownLatch = new CountDownLatch(0);

    public static void main(String[] args) throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        //先统计一遍文件总数，用于监控进度
        letterCountDir("/Users/valtechwh/Documents/N132","pdf",true);
        System.out.println("--------------------------【START】There are ["+totalFile+"] files to be read");
        totalFileCountdownLatch = new CountDownLatch(totalFile.intValue());
        letterCountDir("/Users/valtechwh/Documents/N132","pdf",false);
        totalFileCountdownLatch.await();
        System.out.println("--------------------------【END】Letter count:["+totalLetter+"],cost ["+(System.currentTimeMillis()-startTime)+"]ms");
        threadPoolExecutor.shutdown();

    }


    public static void letterCountDir(String path,String format,boolean justCountFile){
        File file = new File(path);
        //只遍历文件夹
        if(!file.isDirectory()){return;}
        if(file.listFiles()!=null){
            for(File file1:file.listFiles()){
                if(file.getName().equals("zh") && file1.isFile()
                        && getFileFormat(file1.getAbsolutePath()).equals(format)){
                    if(justCountFile){
                        totalFile.incrementAndGet();
                    }else{
                        threadPoolExecutor.execute(()->{
                            letterCount(file1);
                            totalFileCountdownLatch.countDown();
                        });
                    }
                }else if(file1.isDirectory()){
                    letterCountDir(file1.getAbsolutePath(),format,justCountFile);
                }

            }
        }
    }

    public static long letterCount(File file){
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static String replaceUselessLetters(String content){
        return content
                .replaceAll("[^\\u4E00-\\u9FA5|^\\u2F00-\\u2FD5]","");
    }

    public static Integer countText(PdfReader reader) throws InterruptedException {
        try {
            AtomicInteger characterCount = new AtomicInteger();

            PdfDocument pdfDoc = new PdfDocument(reader);
            // get the number of pages in PDF
            int noOfPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= noOfPages; i++) {
                    // Extract content of each page
                    String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                    //TODO 替换无用字符
                    contentOfPage = replaceUselessLetters(contentOfPage);
                    //统计数量
                    characterCount.addAndGet(contentOfPage.length());
            }
            pdfDoc.close();
            //单个文件全部统计完成后再进行汇总
            totalLetter.addAndGet(characterCount.longValue());
            System.out.println("Progress:"+totalFile+"/"+totalFileRead.incrementAndGet()+"("+NumberUtils.calculatePercentage(totalFileRead.longValue(),totalFile.longValue())+")");
            return characterCount.intValue();
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
