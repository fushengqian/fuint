package com.fuint.application.util;

import com.fuint.util.StringUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * 二维码生成工具类
 * Created by zach on 2019/09/04.
 */
public class QRCodeUtil {
    public static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    /**
     * 生成包含字符串信息的二维码图片
     *
     * @param outputStream 文件输出流路径
     * @param content      二维码携带信息
     * @param width        宽度
     * @param height       高度
     * @param imageFormat  二维码的格式
     * @param resource     原图
     */
    public static boolean createQrCode(OutputStream outputStream, String content, int width, int height, String imageFormat, String resource) {
        //设置二维码纠错级别
        HashMap<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        width = width + 200;
        height = height + 200;
        try {
            //创建比特矩阵(位矩阵)的QR码编码的字符串
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
            int matrixWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth - 200, matrixWidth - 200, BufferedImage.TYPE_INT_RGB);

            // 使用比特矩阵画并保存图像
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i - 100, j - 100, 1, 1);
                    }
                }
            }

            if (StringUtil.isNotEmpty(resource)) {
                BufferedImage big = getRemoteBufferedImage(resource);

                BufferedImage small = image;
                Graphics2D g = big.createGraphics();

                // 二维码坐标（默认在右上角）
                int x = big.getWidth() - small.getWidth() - 2;
                int y = 2;

                g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
                g.dispose();

                return ImageIO.write(big, imageFormat, outputStream);
            } else {
                return ImageIO.write(image, imageFormat, outputStream);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 读二维码并输出携带的信息
     */
    public static void readQrCode(InputStream inputStream) throws IOException {
        //设置二维码纠错级别
        HashMap<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //从输入流中获取字符串信息
        BufferedImage image = ImageIO.read(inputStream);
        //将图像转换为二进制位图源
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(bitmap, hints);
        } catch (ReaderException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug(result.getText());
    }

    /**
     * 获取远程网络图片信息
     * @param imageURL
     * @return
     */
    public static BufferedImage getRemoteBufferedImage(String imageURL) {
        URL url = null;
        InputStream is = null;
        BufferedImage bufferedImage = null;
        try {
            url = new URL(imageURL);
            is = url.openStream();
            bufferedImage = ImageIO.read(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("imageURL: " + imageURL + ",无效!");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("imageURL: " + imageURL + ",读取失败!");
            return null;
        } finally {
            try {
                if (is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("imageURL: " + imageURL + ",流关闭异常!");
                return null;
            }
        }
        return bufferedImage;
    }

    /**
     * 测试代码
     *
     * @throws WriterException
     */
    public static void main(String[] args) throws IOException, WriterException {
        createQrCode(new FileOutputStream(new File("d:\\tmp\\qrcode.jpg")), "abcdefg_order123456_20171102140750", 120, 120, "png", "D:/tmp/1.png");
    }
}
