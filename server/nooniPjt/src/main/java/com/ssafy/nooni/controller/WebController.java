package com.ssafy.nooni.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
//        Path root = Paths.get(URI.create(path + fileName));
        // 프로젝트 폴더의 temp.jpg 파일 로드
        String fileName = "nooni_v1.0.0.apk";
        Resource resource = resourceLoader.getResource("classpath:/"+ fileName);	
		File file = resource.getFile();
  
        // 클라이언트에서 아래의 이름으로 파일이 받아진다.
        String newFileName = "new_nooni_v1.0.0.apk";

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
