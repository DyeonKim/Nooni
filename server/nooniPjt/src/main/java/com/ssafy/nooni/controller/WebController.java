package com.ssafy.nooni.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ResourceLoader;

@RestController
@RequestMapping("/web")
public class WebController {
	 @Autowired
	 ResourceLoader resourceLoader;

    @GetMapping(value = "/download")
    public void downloadFile(HttpServletResponse response) throws Exception{

        String fileName = "nooni_v1.0.0.apk";
//        Resource resource = resourceLoader.getResource("classpath:/"+ fileName);	
//		File file = resource.getFile();
        ClassPathResource classPathResource = new ClassPathResource(fileName);
       // InputStream inputStream = classPathResource.getInputStream();
        File file = classPathResource.getFile();
        //File file = File.createTempFile("nooni_v1.0.0", ".apk");
        // 클라이언트에서 아래의 이름으로 파일이 받아진다.
        String newFileName = "n_nooni_v1.0.0.apk";
//        try {
//        	FileUtils.copyInputStreamToFile(inputStream, file);
//        } finally {
//        	IOUtils.closeQuietly(inputStream);
//        }
        
        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream out = response.getOutputStream()
        ){
            // 응답이 파일 타입이라는 것을 명시
            response.addHeader("Content-Disposition", "attachment;filename=\""+newFileName+"\"");
            // 응답 크기 명시
            response.setContentLength((int)file.length());

            int read = 0;

            while((read = bis.read()) != -1) {
                out.write(read);
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
