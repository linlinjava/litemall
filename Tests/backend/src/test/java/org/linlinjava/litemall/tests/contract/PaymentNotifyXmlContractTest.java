package org.linlinjava.litemall.tests.contract;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PaymentNotifyXmlContractTest {

    @Test
    public void mallPayAc04Contract001_wechatPaymentNotifyXmlKeepsRequiredFields() throws Exception {
        Path xml = Paths.get("..", "contracts", "wechat-pay-notify-success.xml").toAbsolutePath().normalize();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml.toFile());

        assertEquals("SUCCESS", text(document, "return_code"));
        assertEquals("SUCCESS", text(document, "result_code"));
        assertTrue(text(document, "out_trade_no").length() > 0);
        assertTrue(text(document, "transaction_id").startsWith("wxpay-transaction"));
        assertEquals("10000", text(document, "total_fee"));
    }

    @Test
    public void mallPayAc04Contract002_wechatPaymentNotifyXmlCanLoadIntoSdkNotifyResult() throws Exception {
        Path xml = Paths.get("..", "contracts", "wechat-pay-notify-success.xml").toAbsolutePath().normalize();
        String xmlBody = new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);

        WxPayOrderNotifyResult result = WxPayOrderNotifyResult.fromXML(xmlBody);

        assertEquals("SUCCESS", result.getReturnCode());
        assertEquals("SUCCESS", result.getResultCode());
        assertEquals("20260711000001", result.getOutTradeNo());
        assertEquals("wxpay-transaction-001", result.getTransactionId());
        assertEquals(Integer.valueOf(10000), result.getTotalFee());
    }

    private String text(Document document, String tag) {
        return document.getElementsByTagName(tag).item(0).getTextContent();
    }
}
