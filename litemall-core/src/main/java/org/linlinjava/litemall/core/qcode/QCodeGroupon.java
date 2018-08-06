package org.linlinjava.litemall.core.qcode;

import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class QCodeGroupon extends QCodeBase {
    @Override
    protected String getKeyName(String id) {
        return null;
    }

    public void createGrouponShareImage(String goodName, String goodPicUrl, LitemallGroupon groupon) {
        try {
            BufferedImage qrCodeImage = getQCode("groupon," + groupon.getId(), "pages/index/index");
            //将商品图片，商品名字,商城名字画到模版图中
            byte[] imageData = drawPicture(qrCodeImage, goodPicUrl, goodName, SystemConfig.getMallName());
            saveImage(groupon.getId().toString(), imageData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将商品图片，商品名字画到模版图中
     *
     * @param qrCodeImage 二维码图片
     * @param goodPicUrl  商品图片地址
     * @param goodName    商品名称
     * @return
     * @throws IOException
     */
    private byte[] drawPicture(BufferedImage qrCodeImage, String goodPicUrl, String goodName, String shopName) throws IOException {
        //底图
        ClassPathResource redResource = new ClassPathResource("back.jpg");
        BufferedImage red = ImageIO.read(redResource.getInputStream());


        //商品图片
        URL goodPic = new URL(goodPicUrl);
        BufferedImage goodImage = ImageIO.read(goodPic);

        // --- 画图 ---

        //底层空白 bufferedImage
        BufferedImage baseImage = new BufferedImage(red.getWidth(), red.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);

        //画上图片
        drawImgInImg(baseImage, red, 0, 0, red.getWidth(), red.getHeight());

        //画上商品图片
        drawImgInImg(baseImage, goodImage, 56, 135, 720, 720);

        //画上小程序二维码
        drawImgInImg(baseImage, qrCodeImage, 442, 1006, 340, 340);


        Font font = new Font("Microsoft YaHei", Font.PLAIN, 42);
        Color color = new Color(167, 136, 69);

        //写上商品名称
        drawTextInImg(baseImage, goodName, font, color, 112, 955);

        //写上商城名称
        drawTextInImgCenter(baseImage, shopName, font, color, 98);


        //转jpg
        BufferedImage result = new BufferedImage(baseImage.getWidth(), baseImage
                .getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        result.getGraphics().drawImage(baseImage, 0, 0, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", bs);

        //最终byte数组
        return bs.toByteArray();
    }
}
