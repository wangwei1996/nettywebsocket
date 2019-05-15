package link.bosswang.nettywebsocket.controller;

import link.bosswang.nettywebsocket.util.FileOperatonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Controller
@RequestMapping(value = "/uploadfile")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private static final String RELATIVE_PATH = "/resources/upload/";


    @RequestMapping(value = "/nolimit")
    public Map<String, Object> uploadFile(HttpServletRequest request) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(".");
        String ABSOLUTE_PATH = (resource.getPath() + "static/webapp/resources/upload/").substring(1);
        System.err.println(ABSOLUTE_PATH);
        Map<String, Object> map = new LinkedHashMap<>(10);
        MultipartRequest fileRequest = null;

        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getServletContext());
        if (!commonsMultipartResolver.isMultipart(request)) {
            map.put("success", false);
            map.put("msg", "不存在上传文件");
            return map;
        }

        fileRequest = (MultipartRequest) request;
        MultipartFile uploadFile = fileRequest.getFile("uploadFile");
        InputStream inputStream = null;
        String fileName = null;
        try {
            inputStream = uploadFile.getInputStream();
            fileName = FileOperatonUtils.saveFile(uploadFile.getOriginalFilename(), ABSOLUTE_PATH, inputStream);
            map.put("success", true);
            map.put("url", FileUploadController.RELATIVE_PATH + fileName);
        } catch (IOException e) {
            FileUploadController.log.error("文件上传失败: " + e.getMessage());
            map.put("success", false);
            map.put("msg", "文件上传失败");
            return map;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    FileUploadController.log.error("文件关闭异常: " + e.getMessage());
                }
            }
        }
        return map;
    }
}
