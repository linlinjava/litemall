package org.linlinjava.litemall.core.qcode;

import cn.binarywang.wx.miniapp.api.WxMaService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.linlinjava.litemall.core.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

@Service
public class QCodeService {
    @Autowired
    WxMaService wxMaService;

    @Autowired
    private StorageService storageService;

    /**
     * 创建商品分享图
     *
     * @param goodId
     * @param goodPicUrl
     * @param goodName
     */
    public void createGoodShareImage(String goodId, String goodPicUrl, String goodName) {
        try {
            //创建该商品的二维码
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(goodId, "pages/index/index");
            FileInputStream inputStream = new FileInputStream(file);
            //将商品图片，商品名字画到模版图中
            byte[] imageData = drawPicture(inputStream, goodPicUrl, goodName);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpeg", imageData);
            //存储分享图
            storageService.store(multipartFile, getKeyName(goodId));
        } catch (WxErrorException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getShareImageUrl(String goodId) {
        return storageService.generateUrl(getKeyName(goodId));
    }

    private String getKeyName(String goodId) {
        return "GOOD_QCODE_" + goodId;
    }

    /**
     * 将商品图片，商品名字画到模版图中
     *
     * @param qrCodeImg  二维码图片
     * @param goodPicUrl 商品图片地址
     * @param goodName   商品名称
     * @return
     * @throws IOException
     */
    private byte[] drawPicture(InputStream qrCodeImg, String goodPicUrl, String goodName) throws IOException {
        //底图
        ClassPathResource redResource = new ClassPathResource("back.jpg");
        BufferedImage red = ImageIO.read(redResource.getInputStream());

        //商品图片
        URL avatarUrl = new URL(goodPicUrl);
        BufferedImage goodImage = ImageIO.read(avatarUrl);

        //小程序二维码
//        URL qrCodeUrl = new URL(qrCodeImg);
        BufferedImage qrCodeImage = ImageIO.read(qrCodeImg);


        // --- 画图 ---

        //底层空白 bufferedImage
        BufferedImage baseImage = new BufferedImage(red.getWidth(), red.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        //画上图片
        drawImgInImg(baseImage, red, 0, 0, red.getWidth(), red.getHeight());

        //画上商品图片
        drawImgInImg(baseImage, goodImage, 24, 24, 520, 520);


        //画上小程序二维码
        drawImgInImg(baseImage, qrCodeImage, 143, 770, 280, 280);


        //写上商品名称
        drawTextInImg(baseImage, goodName, 143, 614);


        //转jpg
        BufferedImage result = new BufferedImage(baseImage.getWidth(), baseImage
                .getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        result.getGraphics().drawImage(baseImage, 0, 0, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", bs);

        //最终byte数组
        return bs.toByteArray();
    }

    private void drawTextInImg(BufferedImage baseImage, String textToWrite, int x, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(new Color(167, 136, 69));
        g2D.setFont(new Font("黑体", Font.PLAIN, 42));
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.drawString(textToWrite, x, y);
        g2D.dispose();
    }

    private void drawImgInImg(BufferedImage baseImage, BufferedImage imageToWrite, int x, int y, int width, int heigth) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.drawImage(imageToWrite, x, y, width, heigth, null);
        g2D.dispose();
    }
}
