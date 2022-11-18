package com.expert.utils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.IOException;

public class FileUtils {

    public static void main(String[] args) {
        System.out.println(letterCount("/Users/valtechwh/Documents/2022111600917_c.pdf"));
    }

    public static long letterCount(String filePath){
        String suffix = getFileFormat(filePath);
        switch(suffix){
            case "pdf": return letterCountPDF(filePath);
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
                // Extract content of each page
                String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                contentOfPage = contentOfPage.replaceAll("\\p{C}", "").replaceAll(" ", "");
                characterCount += contentOfPage.length();
            }
            pdfDoc.close();
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
