package com.fuint.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 小票格式化器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class NoteFormatter {
    /**
     * 纸张宽度（mm），用于选择排版列宽
     */
    public static final Integer PAPER_WIDTH_58 = 58;
    public static final Integer PAPER_WIDTH_80 = 80;

    /**
     * 58mm 系列打印机每行可以打印的字符数
     * 列宽：名称20字节 + 数量6字节 + 单价6字节 = 32字节（GBK，一个汉字2字节）
     */
    private static final Integer ROW_MAX_CHAR_LEN = 32;
    private static final Integer MAX_NAME_CHAR_LEN = 20;
    private static final Integer LAST_ROW_MAX_NAME_CHAR_LEN = 9;
    private static final Integer MAX_QUANTITY_CHAR_LEN = 6;
    private static final Integer MAX_PRICE_CHAR_LEN = 6;

    /**
     * 80mm 系列打印机每行可以打印的字符数
     * 列宽：名称36字节 + 数量6字节 + 单价6字节 = 48字节（GBK，一个汉字2字节）
     */
    private static final Integer ROW_MAX_CHAR_LEN80 = 48;
    private static final Integer MAX_NAME_CHAR_LEN80 = 36;
    private static final Integer MAX_QUANTITY_CHAR_LEN80 = 6;
    private static final Integer MAX_PRICE_CHAR_LEN80 = 6;
    private static final String orderNameEmpty = StringUtils.repeat(" ", MAX_NAME_CHAR_LEN);

    /**
     * 格式化订单数据 80mm打印机使用
     *
     * @param foodName
     * @param quantity
     * @param singlePrice
     * @param price
     * @return
     * @throws Exception
     */
    public static String formatPrintOrderItemBy4Column(String foodName, Integer quantity, Double singlePrice, Double price) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] itemNames = foodName.getBytes("GBK");
        Integer mod = itemNames.length % ROW_MAX_CHAR_LEN;
        String quanityStr = quantity.toString();
        byte[] itemQuans = quanityStr.getBytes("GBK");
        String priceStr = roundByTwo(price);
        byte[] itemPrices = (priceStr).getBytes("GBK");

        if (mod <= LAST_ROW_MAX_NAME_CHAR_LEN) {
            builder.append("<C>").append(foodName).append("</C>");
            // 在同一行,留4个英文字符的空格
            //if  in the same row, fill with 4 spaces at the end of name column
            builder.append(StringUtils.repeat(" ", (MAX_NAME_CHAR_LEN - mod)));
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        } else {
            // 对菜名进行猜分
            builder.append(foodName);
            // 另起新行
            // new line
            builder.append("<BR>");
            builder.append(orderNameEmpty);
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        }
        builder.append("<BR>");
        return builder.toString();
    }

    /**
     * 格式化菜名名称，菜名名称打满一行自动换行
     *
     * @param foodName
     * @param quantity
     * @param price
     * @return
     * @throws Exception
     */
    public static String formatPrintOrderItemByFull(String foodName, Integer quantity, Double price) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] itemNames = foodName.getBytes("GBK");
        Integer mod = itemNames.length % ROW_MAX_CHAR_LEN;
        String quanityStr = quantity.toString();
        byte[] itemQuans = quanityStr.getBytes("GBK");
        String priceStr = roundByTwo(price);
        byte[] itemPrices = (priceStr).getBytes("GBK");

        if (mod <= LAST_ROW_MAX_NAME_CHAR_LEN) {
            builder.append(foodName);
            // 在同一行,留4个英文字符的空格
            //if  in the same row, fill with 4 spaces at the end of name column
            builder.append(StringUtils.repeat(" ", (MAX_NAME_CHAR_LEN - mod)));
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        } else {
            // 对菜名进行猜分
            builder.append(foodName);
            // 另起新行
            // new line
            builder.append("<BR>");
            builder.append(orderNameEmpty);
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        }
        builder.append("<BR>");
        return builder.toString();
    }

    /**
     * 格式化菜品列表（用于58mm打印机）
     * 注意：默认字体排版，若是字体宽度倍大后不适用
     * 58mm打印机一行可打印32个字节 汉字按照2个字节算
     * 分3列： 名称20字节  数量6字节  单价6字节，不足用英文空格填充 名称过长换行
     * Format the dish list (for 58 mm printer)
     * Note: this is  default font typesetting, not applicable if the font width is doubled
     * The 58mm printer can print 32 bytes per line(GBK), a Chinese character takes 2 bytes
     * Divided into 3 columns: name(20 bytes), quanity(6 bytes), price(6 bytes)
     * Long name column will cause auto line break
     *
     * @param foodName 菜品名称
     * @param quantity 数量
     * @param price    价格
     * @throws Exception
     */
    public static String formatPrintOrderItemForNewLine58(String foodName, Integer quantity, Double price) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] itemNames = StringUtils.isNotBlank(foodName) ? foodName.getBytes("GBK") : new byte[0];

        // 名称未超出名称列宽度（20字节）时，数量、单价与名称同一行
        if (itemNames.length <= MAX_NAME_CHAR_LEN) {
            builder.append(StringUtils.isNotBlank(foodName) ? foodName : "");
            builder.append(StringUtils.repeat(" ", (MAX_NAME_CHAR_LEN - itemNames.length)));
            builder.append(fillColumn(quantity.toString(), MAX_QUANTITY_CHAR_LEN));
            builder.append(fillColumn(roundByTwo(price), MAX_PRICE_CHAR_LEN));
        } else {
            getFoodNameSplit58(foodName, builder, quantity, price);
        }
        builder.append("<BR>");
        return builder.toString();
    }

    /**
     * 格式化菜品列表（用于80mm打印机）
     * 注意：默认字体排版，若是字体宽度倍大后不适用
     * 80mm打印机一行可打印48个字节 汉字按照2个字节算
     * 分3列： 名称36字节  数量6字节  单价6字节，不足用英文空格填充 名称过长换行
     * Format the dish list (for 80 mm printer)
     * Note: this is  default font typesetting, not applicable if the font width is doubled
     * The 80mm printer can print 48 bytes per line(GBK), a Chinese character takes 2 bytes
     * Divided into 3 columns: name(36 bytes), quanity(6 bytes), price(6 bytes)
     * Long name column will cause auto line break
     *
     * @param foodName 菜品名称
     * @param quantity 数量
     * @param price    价格
     * @throws Exception
     */
    public static String formatPrintOrderItemForNewLine80(String foodName, Double quantity, Double price) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] itemNames = StringUtils.isNotBlank(foodName) ? foodName.getBytes("GBK") : new byte[0];

        // 名称未超出名称列宽度（36字节）时，数量、单价与名称同一行
        if (itemNames.length <= MAX_NAME_CHAR_LEN80) {
            builder.append(StringUtils.isNotBlank(foodName) ? foodName : "");
            builder.append(StringUtils.repeat(" ", (MAX_NAME_CHAR_LEN80 - itemNames.length)));
            builder.append(fillColumn(quantity.toString(), MAX_QUANTITY_CHAR_LEN80));
            builder.append(fillColumn(roundByTwo(price), MAX_PRICE_CHAR_LEN80));
        } else {
            getFoodNameSplit80(foodName, builder, quantity, price);
        }
        builder.append("<BR>");
        return builder.toString();
    }

    /**
     * 商品列表表头（80mm打印机）：名称36字节 + 数量6字节 + 单价6字节 = 48字节
     * 与 formatPrintOrderItemForNewLine80 使用同一套列宽，保证表头与正文对齐
     * Order item list header (80mm printer), keeps the same column width as the item rows
     *
     * @throws Exception
     */
    public static String formatPrintOrderItemHeader80() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(fillColumn("品名", MAX_NAME_CHAR_LEN80));
        builder.append(fillColumn("数量", MAX_QUANTITY_CHAR_LEN80));
        builder.append(fillColumn("单价", MAX_PRICE_CHAR_LEN80));
        return builder.toString();
    }

    /**
     * 商品列表表头（58mm打印机）：名称20字节 + 数量6字节 + 单价6字节 = 32字节
     * Order item list header (58mm printer)
     *
     * @throws Exception
     */
    public static String formatPrintOrderItemHeader58() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(fillColumn("品名", MAX_NAME_CHAR_LEN));
        builder.append(fillColumn("数量", MAX_QUANTITY_CHAR_LEN));
        builder.append(fillColumn("单价", MAX_PRICE_CHAR_LEN));
        return builder.toString();
    }

    /**
     * 商品列表表头：按纸张宽度选择列宽，与商品行保持一致
     * Order item list header, the column width follows the paper width
     *
     * @param paperWidth 纸张宽度 PAPER_WIDTH_58 / PAPER_WIDTH_80
     * @throws Exception
     */
    public static String formatPrintOrderItemHeader(Integer paperWidth) throws Exception {
        if (PAPER_WIDTH_80.equals(paperWidth)) {
            return formatPrintOrderItemHeader80();
        }
        return formatPrintOrderItemHeader58();
    }

    /**
     * 格式化商品行：按纸张宽度选择列宽
     * Format an order item row, the column width follows the paper width
     *
     * @param foodName   商品名称
     * @param quantity   数量
     * @param price      单价
     * @param paperWidth 纸张宽度 PAPER_WIDTH_58 / PAPER_WIDTH_80
     * @throws Exception
     */
    public static String formatPrintOrderItem(String foodName, Double quantity, Double price, Integer paperWidth) throws Exception {
        if (PAPER_WIDTH_80.equals(paperWidth)) {
            return formatPrintOrderItemForNewLine80(foodName, quantity, price);
        }
        int num = quantity == null ? 0 : quantity.intValue();
        return formatPrintOrderItemForNewLine58(foodName, num, price);
    }

    /**
     * 每行可打印的字节数（GBK）：58mm 为32字节，80mm 为48字节
     * Printable bytes per line(GBK): 32 for 58mm, 48 for 80mm
     *
     * @param paperWidth 纸张宽度 PAPER_WIDTH_58 / PAPER_WIDTH_80
     */
    public static Integer getRowByteLength(Integer paperWidth) {
        return PAPER_WIDTH_80.equals(paperWidth) ? ROW_MAX_CHAR_LEN80 : ROW_MAX_CHAR_LEN;
    }

    private static void getFoodNameSplit58(String foodName, StringBuilder builder, Integer quantity, Double price) throws UnsupportedEncodingException {
        // 按GBK字节切分，每行名称不超过名称列宽度（20字节）
        List<String> foodNames = splitByGbkBytes(foodName, MAX_NAME_CHAR_LEN);
        for (int i = 0; i < foodNames.size(); i++) {
            if (i == 0) {
                builder.append(fillColumn(foodNames.get(i), MAX_NAME_CHAR_LEN));
                builder.append(fillColumn(quantity.toString(), MAX_QUANTITY_CHAR_LEN));
                builder.append(fillColumn(roundByTwo(price), MAX_PRICE_CHAR_LEN));
            } else {
                builder.append(foodNames.get(i));
            }
            if (i < foodNames.size() - 1) {
                builder.append("<BR>");
            }
        }
    }

    private static void getFoodNameSplit80(String foodName, StringBuilder builder, Double quantity, Double price) throws UnsupportedEncodingException {
        // 按GBK字节切分，每行名称不超过名称列宽度（36字节）
        List<String> foodNames = splitByGbkBytes(foodName, MAX_NAME_CHAR_LEN80);
        for (int i = 0; i < foodNames.size(); i++) {
            if (i == 0) {
                builder.append(fillColumn(foodNames.get(i), MAX_NAME_CHAR_LEN80));
                builder.append(fillColumn(quantity.toString(), MAX_QUANTITY_CHAR_LEN80));
                builder.append(fillColumn(roundByTwo(price), MAX_PRICE_CHAR_LEN80));
            } else {
                builder.append(foodNames.get(i));
            }
            if (i < foodNames.size() - 1) {
                builder.append("<BR>");
            }
        }
    }

    /**
     * 按GBK字节数拆分字符串，避免把汉字拆成半个（GBK下一个汉字占2字节）
     * Split the string by GBK bytes, avoid breaking a Chinese character into two halves
     *
     * @param src      待拆分字符串 source string
     * @param maxBytes 每段最大字节数 max bytes of each segment
     */
    private static List<String> splitByGbkBytes(String src, Integer maxBytes) throws UnsupportedEncodingException {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(src) || maxBytes == null || maxBytes <= 0) {
            return result;
        }
        StringBuilder segment = new StringBuilder();
        int bytes = 0;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            int charBytes = String.valueOf(c).getBytes("GBK").length;
            if (bytes + charBytes > maxBytes) {
                result.add(segment.toString());
                segment = new StringBuilder();
                bytes = 0;
            }
            segment.append(c);
            bytes += charBytes;
        }
        if (segment.length() > 0) {
            result.add(segment.toString());
        }
        return result;
    }

    /**
     * 按指定字节宽度补齐一列，内容不足时用英文空格填充，超出时不处理
     * Fill a column to the specified byte width, pad with spaces if not enough
     *
     * @param text     列内容 column content
     * @param maxBytes 列宽度（GBK字节） column width in GBK bytes
     */
    private static String fillColumn(String text, Integer maxBytes) throws UnsupportedEncodingException {
        String value = text == null ? "" : text;
        int fill = maxBytes - value.getBytes("GBK").length;
        return fill > 0 ? value + StringUtils.repeat(" ", fill) : value;
    }

    private static String[] string2StringArray(String src, Integer length) {
        if (StringUtils.isBlank(src) || length <= 0) {
            return null;
        }
        int n = (src.length() + length - 1) / length;
        String[] splits = new String[n];
        for (int i = 0; i < n; i++) {
            if (i < (n - 1)) {
                splits[i] = src.substring(i * length, (i + 1) * length);
            } else {
                splits[i] = src.substring(i * length);
            }
        }
        return splits;
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全 (小数点后保留2位)
     * Format the double as a String with specified decimal places, fill in with 0 if the decimal place is not enough
     *
     * @param v - 需要格式化的数字  Number to be formatted
     * @return 返回指定位数的字符串 Returns a string with a specified number of digits
     */
    public static String roundByTwo(double v) {
        return roundByScale(v, 2);
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     * Format the double as a String with specified decimal places, and use 0 to complete the decimal places
     *
     * @param v     - 需要格式化的数字  number to be formatted
     * @param scale - 小数点后保留几位  places after the decimal point
     * @return 返回指定位数的字符串 Returns a string with a specified number of digits
     */
    public static String roundByScale(double v, int scale) {
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }
}
