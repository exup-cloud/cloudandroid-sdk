package com.follow.order.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class StringUtil {

    /**
     * json 格式化
     *
     * @param json
     * @return
     */
    public static String jsonFormat(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content";
        }
        String message;
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = jsonObject.toString(4);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = jsonArray.toString(4);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }
        return message;
    }

    /**
     * xml 格式化
     *
     * @param xml
     * @return
     */
    public static String xmlFormat(String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content";
        }
        String message;
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            message = xml;
        }
        return message;
    }

    public static double formatNum(float num) {
        BigDecimal b = new BigDecimal(num);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String formatBalance(String num) {
        try {
            BigDecimal b = new BigDecimal(num);
            return b.toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 格式化止盈止损百分比
     *
     * @param num
     * @return
     */
    public static String formatPercent(double num) {
        NumberFormat nf = new DecimalFormat("#.##");
        return nf.format(num);
    }

    public static float getChartYOffset(float max, float min) {
        return Math.abs(max - min);
    }

    public static BigDecimal getMin(float yOffset, float valueMin) {
        BigDecimal offset = new BigDecimal(yOffset);
        BigDecimal min = new BigDecimal(valueMin);
        BigDecimal sum = min.subtract(offset).setScale(2, BigDecimal.ROUND_DOWN);
        int result = sum.compareTo(BigDecimal.ZERO);
        BigDecimal sumMin = result == 1 ? BigDecimal.ZERO : sum;
        return sumMin;
    }

    public static BigDecimal getMax(float yOffset, float valueMin, float valueMax) {
        BigDecimal offset = new BigDecimal(yOffset);
        BigDecimal min = new BigDecimal(valueMin);
        BigDecimal max = new BigDecimal(valueMax);
        BigDecimal sum = min.subtract(offset);
        int result = sum.compareTo(BigDecimal.ZERO);
        BigDecimal sumMax = (result == 1) ? max.add(min).subtract(offset) : max.add(offset);
        return sumMax;
    }
}
