package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOSSUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        log.info("文件上传", file);
        String uploadObjectUrl = aliOSSUtils.upload(file);
        return Result.success(uploadObjectUrl);
    }
}
