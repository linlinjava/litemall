package org.linlinjava.litemall.core.qcode;

import cn.binarywang.wx.miniapp.api.WxMaService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.linlinjava.litemall.core.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class QCodeBase {

    @Autowired
    protected WxMaService wxMaService;

    @Autowired
    protected StorageService storageService;

    protected abstract String getKeyName(String id);

    /**
     * 获取图片地址
     *
     * @param id
     * @return
     */
    public String getShareImageUrl(String id) {
        return storageService.generateUrl(getKeyName(id));
    }

    protected BufferedImage getQCode(String scene, String page) {
        //创建该商品的二维码
        File file = null;
        try {
            file = wxMaService.getQrcodeService().createWxaCodeUnlimit(scene, page);
            FileInputStream inputStream = new FileInputStream(file);
            BufferedImage qrCodeImage = ImageIO.read(inputStream);
            return qrCodeImage;
        } catch (WxErrorException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void saveImage(String id, byte[] imageData) {
//        MultipartFile multipartFile = new MockMultipartFile(getKeyName(id), getKeyName(id), "image/jpeg", imageData);
//        //存储分享图
//        storageService.store(multipartFile, getKeyName(id));
    }

    /**
     * 居中写文字
     *
     * @param baseImage
     * @param textToWrite
     * @param font
     * @param color
     * @param y
     */
    protected void drawTextInImgCenter(BufferedImage baseImage, String textToWrite, Font font, Color color, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(color);

        g2D.setFont(font);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算文字长度，计算居中的x点坐标
        FontMetrics fm = g2D.getFontMetrics(font);
        int textWidth = fm.stringWidth(textToWrite);
        int widthX = (baseImage.getWidth() - textWidth) / 2;
        // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。

        g2D.drawString(textToWrite, widthX, y);
        // 释放对象
        g2D.dispose();
    }

    /**
     * 写上文字
     *
     * @param baseImage
     * @param textToWrite
     * @param font
     * @param color
     * @param x
     * @param y
     */
    protected void drawTextInImg(BufferedImage baseImage, String textToWrite, Font font, Color color, int x, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(color);

        g2D.setFont(font);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.drawString(textToWrite, x, y);
        g2D.dispose();
    }

    /**
     * 画中画
     *
     * @param baseImage
     * @param imageToWrite
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void drawImgInImg(BufferedImage baseImage, BufferedImage imageToWrite, int x, int y, int width, int height) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.drawImage(imageToWrite, x, y, width, height, null);
        g2D.dispose();
    }
}
