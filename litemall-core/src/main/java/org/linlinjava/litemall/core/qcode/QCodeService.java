package org.linlinjava.litemall.core.qcode;

import cn.binarywang.wx.miniapp.api.WxMaService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.storage.StorageService;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * 小程序二维码 / 分享图生成服务。
 *
 * <p>生成小程序码，并将商品图片、商品名称绘制到分享图中，
 * 最终保存到存储服务并返回可访问的图片 URL。
 */
@Service
public class QCodeService {
    /** 日志 */
    private final Log logger = LogFactory.getLog(QCodeService.class);
    /** 微信小程序服务 */
    @Autowired
    WxMaService wxMaService;

    /** 存储服务 */
    @Autowired
    private StorageService storageService;


    /**
     * 创建拼团分享图
     *
     * @param goodName   商品名称
     * @param goodPicUrl 商品图片地址
     * @param groupon    拼团信息，用于生成带 groupon 参数的二维码
     * @return 分享图 URL；生成失败时返回空字符串
     */
    public String createGrouponShareImage(String goodName, String goodPicUrl, LitemallGroupon groupon) {
        try {
            //创建该商品的二维码
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit("groupon," + groupon.getId(), "pages" +
                    "/index/index");
            FileInputStream inputStream = new FileInputStream(file);
            //将商品图片，商品名字,商城名字画到模版图中
            byte[] imageData = drawPicture(inputStream, goodPicUrl, goodName);
            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(imageData);
            //存储分享图
            LitemallStorage storageInfo = storageService.store(inputStream2, imageData.length, "image/jpeg",
                    getKeyName(groupon.getId().toString()));

            return storageInfo.getUrl();
        } catch (WxErrorException e) {
            logger.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return "";
    }


    /**
     * 创建商品分享图
     *
     * @param goodId     商品 ID，用于生成带 goods 参数的二维码
     * @param goodPicUrl 商品图片地址
     * @param goodName   商品名称
     * @return 分享图 URL；未开启自动生成或生成失败时返回空字符串
     */
    public String createGoodShareImage(String goodId, String goodPicUrl, String goodName) {
        if (!SystemConfig.isAutoCreateShareImage())
            return "";

        try {
            //创建该商品的二维码
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit("goods," + goodId, "pages/index/index");
            FileInputStream inputStream = new FileInputStream(file);
            //将商品图片，商品名字,商城名字画到模版图中
            byte[] imageData = drawPicture(inputStream, goodPicUrl, goodName);
            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(imageData);
            //存储分享图
            LitemallStorage litemallStorage = storageService.store(inputStream2, imageData.length, "image/jpeg",
                    getKeyName(goodId));

            return litemallStorage.getUrl();
        } catch (WxErrorException e) {
            logger.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return "";
    }

    /**
     * 生成分享图在存储服务中的对象键名
     *
     * @param goodId 商品/拼团 ID
     * @return 存储对象键名，如 GOOD_QCODE_123.jpg
     */
    private String getKeyName(String goodId) {
        return "GOOD_QCODE_" + goodId + ".jpg";
    }

    /**
     * 将商品图片，商品名字画到模版图中
     *
     * @param qrCodeImg  二维码图片
     * @param goodPicUrl 商品图片地址
     * @param goodName   商品名称
     * @return 合成的 JPG 图片字节数组
     * @throws IOException 图片读取失败时抛出
     */
    private byte[] drawPicture(InputStream qrCodeImg, String goodPicUrl, String goodName) throws IOException {
        //底图
        ClassPathResource redResource = new ClassPathResource("back.png");
        BufferedImage red = ImageIO.read(redResource.getInputStream());


        //商品图片
        URL goodPic = new URL(goodPicUrl);
        BufferedImage goodImage = ImageIO.read(goodPic);

        //小程序二维码
        BufferedImage qrCodeImage = ImageIO.read(qrCodeImg);

        // --- 画图 ---

        //底层空白 bufferedImage
        BufferedImage baseImage = new BufferedImage(red.getWidth(), red.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);

        //画上图片
        drawImgInImg(baseImage, red, 0, 0, red.getWidth(), red.getHeight());

        //画上商品图片
        drawImgInImg(baseImage, goodImage, 71, 69, 660, 660);

        //画上小程序二维码
        drawImgInImg(baseImage, qrCodeImage, 448, 767, 300, 300);

        //写上商品名称
        drawTextInImg(baseImage, goodName, 65, 867);

        //转jpg
        BufferedImage result = new BufferedImage(baseImage.getWidth(), baseImage
                .getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        result.getGraphics().drawImage(baseImage, 0, 0, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", bs);

        //最终byte数组
        return bs.toByteArray();
    }

    /**
     * 在图片上绘制文本
     *
     * @param baseImage   目标画布
     * @param textToWrite 要绘制的文本
     * @param x           文本左上角 x 坐标
     * @param y           文本左上角 y 坐标
     */
    private void drawTextInImg(BufferedImage baseImage, String textToWrite, int x, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(new Color(167, 136, 69));

        //TODO 注意，这里的字体必须安装在服务器上
        g2D.setFont(new Font("Microsoft YaHei", Font.PLAIN, 28));
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.drawString(textToWrite, x, y);
        g2D.dispose();
    }

    /**
     * 将一张图片绘制到目标画布的指定区域
     *
     * @param baseImage    目标画布
     * @param imageToWrite 待绘制的源图片
     * @param x            绘制区域左上角 x 坐标
     * @param y            绘制区域左上角 y 坐标
     * @param width        绘制区域宽度
     * @param heigth       绘制区域高度
     */
    private void drawImgInImg(BufferedImage baseImage, BufferedImage imageToWrite, int x, int y, int width,
                              int heigth) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.drawImage(imageToWrite, x, y, width, heigth, null);
        g2D.dispose();
    }
}
