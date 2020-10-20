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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    @PostMapping("/download")
    public String download(@RequestParam("file") String test) throws Exception{
        return person2.name;
    }
}
