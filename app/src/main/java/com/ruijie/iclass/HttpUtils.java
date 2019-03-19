package com.ruijie.iclass;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream; 
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.URLEncoder;
import java.io.ByteArrayOutputStream;

//import android.os.StrictMode;

public class HttpUtils {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(3000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            Log.e("error","发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    /*
     * Function  :   发送Post请求到服务器
     * Param     :   params请求体内容，encode编码格式
     */
    public static String submitPostData(String strUrlPath,Map<String, String> params, String encode) {
//    	   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
//           .detectDiskReads()  
//           .detectDiskWrites()  
//           .detectNetwork()  
//           .penaltyLog()  
//           .build());   
//    	   StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
//           .detectLeakedSqlLiteObjects()  
//           .detectLeakedClosableObjects()  
//           .penaltyLog()  
//           .penaltyDeath()  
//           .build()); 
        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {            
            
            URL url = new URL(strUrlPath);  
             
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/json");//application/x-www-form-urlencoded
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            outputStream.close();
            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            System.out.println("response code:" + response);
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                String result = dealResponseResult(inptStream);
                inptStream.close();
//                httpURLConnection.setDoInput(false);                  
//                httpURLConnection.setDoOutput(false);  
                  return result;								 		 //处理服务器的响应结果
//                return dealResponseResult(inptStream);                    
                
            }
        } catch (Exception e) {
            e.printStackTrace();
//        	System.out.println("err: " + e.getMessage().toString());
            return "网络异常";
        }

        return "-1";
    }
    public static String submitPostJsonData(String strUrlPath,String params, String encode, Integer readTimeout) {
// 	   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()
//        .penaltyLog()
//        .build());
// 	   StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()
//        .detectLeakedClosableObjects()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());
        byte[] data = params.getBytes();//获得请求体
        try {
            URL url = new URL(strUrlPath);
            System.out.println(url + "@" + params);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setReadTimeout(readTimeout);
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/json");//application/x-www-form-urlencoded
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            outputStream.close();
            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            //Log.e("time","response Length:" + httpURLConnection.getContentLength());
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                String result = dealResponseResult(inptStream);

//                Long strat = System.currentTimeMillis();
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                byte[] databyte = new byte[1024];
//                int len = 0;
//                while((len = inptStream.read(databyte)) != -1) {
//                    byteArrayOutputStream.write(databyte, 0, len);
//                }
//                Log.e("time","dealResponseResult "+(System.currentTimeMillis()-strat));
//                String result = byteArrayOutputStream.toString();
//                byteArrayOutputStream.close();
//                Log.e("time","dealResponseResult resultData");

                inptStream.close();
//             httpURLConnection.setDoInput(false);
//             httpURLConnection.setDoOutput(false);

                return result;								 		 //处理服务器的响应结果
//             return dealResponseResult(inptStream);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "网络异常";
        }

        return "-1";
    }

    public static String submitPostJsonData(String strUrlPath,String params, String encode) {
        return submitPostJsonData(strUrlPath,params,encode,5000);
    }
    
    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
   public static StringBuffer getRequestData(Map<String, String> params, String encode) {
      StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
      try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                      .append(":")
                      .append(URLEncoder.encode(entry.getValue(), encode))
                      .append("&");
            }
           stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
           e.printStackTrace();
       }
       return stringBuffer;
    }
    
   /*
    * Function  :   处理服务器的响应结果（将输入流转化成字符串）
    * Param     :   inputStream服务器的响应输入流
    */
    public static String dealResponseResult(InputStream inputStream) {

        try {
            Long strat = System.currentTimeMillis();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len = 0;
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);

            }

            String resultData = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();
  
            return resultData;

//            Long strat = System.currentTimeMillis();
//            InputStreamReader reader = null;
//            reader = new InputStreamReader(inputStream, "UTF-8");
//            BufferedReader br = new BufferedReader(reader);
//            StringBuilder sb = new StringBuilder();
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            Log.e("time","dealResponseResult "+(System.currentTimeMillis()-strat));
//            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
   }



    public static String uploadFile(String urlString,String fileName) {
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String boundary = "*****";
            // 服务器的域名
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);     //设置连接超时时间
            conn.setReadTimeout(5000);
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // 上传文件
            File file = new File(fileName);
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(boundary);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + "pd.txt" + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());

            // 数据输入流,用于读取文件数据
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            // 最后添加换行
            out.write(newLine.getBytes());
            in.close();

            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + boundary + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            int response = conn.getResponseCode();            //获得服务器的响应码
            //Log.e("time","response Length:" + httpURLConnection.getContentLength());
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = conn.getInputStream();
                String result = dealResponseResult(inptStream);
                inptStream.close();

                return result;								 		 //处理服务器的响应结果
//             return dealResponseResult(inptStream);

            }

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return null;
    }
}
