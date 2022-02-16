package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {
    // 注入storageClient
    @Autowired
    private FastFileStorageClient storageClient;

    // 建立文件类型白名单
    private static final List<String>  CONTENT_TYPES = Arrays.asList("image/gif","image/jpeg","image/png");

    // 建立日志信息
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 1.校验文件类型
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            // 文件不合法
            LOGGER.info("文件类型不合法：{}",originalFilename);
            return null;
        }
        try {
            // 2. 校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                LOGGER.info("文件内容不合法：{}",originalFilename);
                return null;
            }
            // 3， 保存到服务器
            //file.transferTo(new File("C:\\Users\\liaofeifei\\IdeaProjects\\code\\images"+originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            // 3. 返回url,进行回显
           // return "http://image.leyou.com/" + originalFilename;
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
