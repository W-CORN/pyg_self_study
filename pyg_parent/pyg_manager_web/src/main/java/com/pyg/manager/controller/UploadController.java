package com.pyg.manager.controller;

import com.pyg.common.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName UploadController
 * @Author corn
 * @Date 2019/3/19 11:49
 **/
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String file_server_url;
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        String filename = file.getOriginalFilename();//获取文件名
        String substring = filename.substring(filename.lastIndexOf(".") + 1);//获取拓展名
        try {

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String s = fastDFSClient.uploadFile(file.getBytes(), substring);
            String url=file_server_url+s;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
