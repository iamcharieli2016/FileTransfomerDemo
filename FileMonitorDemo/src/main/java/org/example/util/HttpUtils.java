package org.example.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.File;
import java.io.IOException;

public class HttpUtils {
    public static void httpPost(File file) {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod("http://localhost:8080/distributor");

        postMethod.addRequestHeader("accept", "*/*");
        //设置Content-Type，此处根据实际情况确定
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        //必须设置下面这个Header
        //添加请求参数
//        Map paraMap = new HashMap();
//        paraMap.put("file", "wx");
//        paraMap.put("mchid", "10101");
//        postMethod.addParameter("consumerAppId", "test");
//        postMethod.addParameter("serviceName", "queryMerchantService");
        postMethod.addParameter("file", file.getAbsolutePath());
        String result = "";
        try {
            int code = httpClient.executeMethod(postMethod);
            if (code == 200){
                result = postMethod.getResponseBodyAsString();
                System.out.println("result:" + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File file = new File("/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources");
        httpPost(file);
    }
}
