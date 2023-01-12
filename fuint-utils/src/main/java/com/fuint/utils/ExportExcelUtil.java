package com.fuint.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 转载时请保留以下信息，注明出处！
 * 
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
public class ExportExcelUtil<T> {

    public void exportExcel(String title,Collection<T> dataset, OutputStream out) {
        exportExcel(title, null, dataset, out, "yyyy-MM-dd");
    }


    public void exportExcel(String title,
                            String[] headers,
                            Collection<T> dataset,
                            OutputStream out,
                            String pattern) {
        exportExcel(title, headers, dataset, out, pattern);
    }


    public void exportExcel(String title,
                            String[] headers,
                            String[] fields,
                            Collection<T> dataset,
                            OutputStream out) {

        exportExcel(title,
                headers,
                fields,
                dataset,
                out,
                "yyyy-MM-dd");
    }


    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格属性列名数组
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    @SuppressWarnings("unchecked")
    public void exportExcel(String title,
                            String[] headers,
                            String[] ofields,
                            Collection<T> dataset,
                            OutputStream out,
                            String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式

        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        hssfCellStyle.setFont(font);
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            // cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellStyle(hssfCellStyle);
        }

        // 遍历集合数据，产生数据行
        DecimalFormat df = new DecimalFormat("0.00");
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            if (ofields != null) {
                for (int i = 0; i < ofields.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    // cell.setCellStyle(style2);
                    String fieldName = ofields[i];

                    String getMethodName = "get"
                                           + fieldName.substring(0, 1)
                                                      .toUpperCase()
                                           + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Object value = null;
                        try {
                            Method getMethod = tCls.getMethod(getMethodName,
                                                              new Class[]{});
                            value = getMethod.invoke(t, new Object[]{});
                        }
                        catch (Exception e) {
                            // e.printStackTrace();
                        }
                        // 判断值的类型后进行强制类型转换
                        String textValue;
                        if (value == null)
                            continue;
                        textValue = convertTextValue(pattern, df, value);
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);
                                HSSFFont font3 = workbook.createFont();
                                font3.setColor(HSSFColor.BLACK.index);
                                richString.applyFont(font3);
                                cell.setCellValue(richString);
                            }
                        }

                    }
                    catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    finally {
                        // 清理资源
                    }
                }
            } else {
                Field[] fields = t.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    // cell.setCellStyle(style2);
                    Field field = fields[i];
                    String fieldName = field.getName();

                    String getMethodName = "get"
                                           + fieldName.substring(0, 1)
                                                      .toUpperCase()
                                           + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Object value = null;
                        try {
                            Method getMethod = tCls.getMethod(getMethodName,
                                                              new Class[]{});
                            value = getMethod.invoke(t, new Object[]{});
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 判断值的类型后进行强制类型转换
                        String textValue = null;

                        if (value == null)
                            continue;
                        textValue = convertTextValue(pattern, df, value);
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);
                              //  HSSFFont font3 = workbook.createFont();
                                font.setColor(HSSFColor.BLACK.index);
                                richString.applyFont(font);
                                cell.setCellValue(richString);
                            }
                        }

                    }
                    catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    finally {
                        // 清理资源
                    }
                }
            }
        }
        try {
            workbook.write(out);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String convertTextValue(String pattern, DecimalFormat df, Object value) {
        String textValue;
        if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if(value instanceof Timestamp){
            Timestamp timestamp = (Timestamp) value;
            Date date = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if (value instanceof Number) {
            if (((Number) value).intValue() != 0) {
                textValue = df.format((float) ((Number) value).intValue() / 100);
            } else {
                textValue = "0.00";
            }
        }
        /*
         * else if (value instanceof byte[]) { //
         * 有图片时，设置行高为60px; row.setHeightInPoints(60); //
         * 设置图片所在列宽度为80px,注意这里单位的一个换算 sheet.setColumnWidth(i,
         * (short) (35.7 * 80)); // sheet.autoSizeColumn(i);
         * byte[] bsValue = (byte[]) value; HSSFClientAnchor
         * anchor = new HSSFClientAnchor(0, 0, 1023, 255,
         * (short) 6, index, (short) 6, index);
         * anchor.setAnchorType(2);
         * patriarch.createPicture(anchor, workbook.addPicture(
         * bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG)); }
         */
        else {
            // 其它数据类型都当作字符串简单处理
            textValue = value.toString();
        }
        return textValue;
    }

    @SuppressWarnings("unchecked")
    public static void exportExcel(String title,
                                   String[] headers,
                                   Collection<String[]> dataset,
                                   OutputStream out) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                                                                           0,
                                                                           0,
                                                                           0,
                                                                           (short) 4,
                                                                           2,
                                                                           (short) 6,
                                                                           5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("leno");

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        Iterator<String[]> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);// 行
            String[] t = (String[]) it.next();

            for (int i = 0; i < t.length; i++) {
                HSSFCell cell = row.createCell(i);// 行的格
                cell.setCellStyle(style2);// 行的样式
                HSSFRichTextString richString = new HSSFRichTextString(t[i]);
                HSSFFont font3 = workbook.createFont();
                font3.setColor(HSSFColor.BLUE.index);
                richString.applyFont(font3);
                cell.setCellValue(richString);
            }
        }
        try {
            workbook.write(out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
