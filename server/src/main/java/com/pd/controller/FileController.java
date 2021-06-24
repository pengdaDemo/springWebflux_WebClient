package com.pd.controller;

import com.google.common.hash.Hashing;
import com.pd.entity.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;

import static com.fasterxml.jackson.core.Base64Variants.MIME;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    Person person2;
    @PostMapping("/upload")
    public String upload(MultipartHttpServletRequest request) throws Exception{
        Collection<MultipartFile> files = request.getFileMap().values();
        files.parallelStream().forEach((uploadFile) -> {
            try{
                System.out.println(request.getHeader("Content-Type"));
                uploadFile.getInputStream();
                uploadFile.getBytes();
                System.out.println(uploadFile.getBytes().length);
//                System.out.println(uploadFile.getContentType());
//                System.out.println(uploadFile.getName());
//                System.out.println(uploadFile.getOriginalFilename());
//                System.out.println(uploadFile.getSize());
            }catch (Exception e) {
                e.printStackTrace();
            }

        });

        return "成功";
    }
    @GetMapping(path = "/download")
    public HttpServletResponse download(HttpServletResponse response) throws Exception{
        try {
            File file = new File("/data/dev/Documents/mongoData.zip");
            if (file.exists()) {
                String dfileName = file.getName();
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                response.reset();
                response.setContentType("application/x-download");
                response.addHeader("Content-Disposition","attachment;filename="+ new String(dfileName.getBytes(),"iso-8859-1"));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                byte[] buffer = new byte[1024 * 1024 * 4]; //使用缓存慢慢读取，支持大文件下载
                int i = -1;
                while ((i = fis.read(buffer)) != -1) {
                    toClient.write(buffer, 0, i);
                }
                fis.close();
                toClient.flush();
                toClient.close();
                try {
                    response.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                PrintWriter out = response.getWriter();
                out.print("<script>");
                out.print("alert(\"未找到文件\")");
                out.print("</script>");
            }
        } catch (IOException ex) {
            PrintWriter out = response.getWriter();
            out.print("<script>");
            out.print("alert(\"未找到文件\")");
            out.print("</script>");
        }
        return response;
    }
}
