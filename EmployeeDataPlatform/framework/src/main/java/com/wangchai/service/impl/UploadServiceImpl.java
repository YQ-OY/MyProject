package com.wangchai.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.wangchai.domain.ResponseResult;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.service.UploadService;
import com.wangchai.utils.PathUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@Data
@ConfigurationProperties(prefix = "oss")    // 把配置文件里 oss.xxx 的配置，自动赋值到注解所在类的对应字段中
public class UploadServiceImpl implements UploadService {

    // 允许的文件扩展名（和前端保持一致，小写）
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg", "webp");
    // 文件最大大小：2MB（字节），和配置文件、前端保持一致
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {

        // 判断文件类型和文件大小
        // 获取文件名
        String originalFilename = img.getOriginalFilename();

        if (img.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.FILE_IS_EMPTY);
        }

        // 2. 校验文件大小
        if (img.getSize() > MAX_FILE_SIZE) {
            return ResponseResult.errorResult(AppHttpCodeEnum.FILE_SIZE_EXCEED);
        }

        // 3. 校验文件扩展名（处理大小写、兼容多后缀名）
        // 防止文件名为空（比如某些异常场景）
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ResponseResult.errorResult(AppHttpCodeEnum.FILE_NAME_INVALID);
        }
        // 提取扩展名（转小写，兼容 xxx.PNG、xxx.Jpg 等情况）
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.FILE_NAME_INVALID);
        }

        // 如果判断通过 上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = OssUpload(img, filePath);

        return ResponseResult.okResult(url);

    }

    private String accessKey;
    private String secretKey;
    private String bucket;

    // 这里参考了官方文档代码(https://developer.qiniu.com/kodo/1239/java#upload-stream)
    private String OssUpload(MultipartFile imgFile, String filePath) {

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = Configuration.create(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        // 存储文件路径
        String key = filePath;

        try {
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return "http://t7giez8zm.hb-bkt.clouddn.com/" + key;
            } catch (QiniuException ex) {
                ex.printStackTrace();
                if (ex.response != null) {
                    System.err.println(ex.response);

                    try {
                        String body = ex.response.toString();
                        System.err.println(body);
                    } catch (Exception ignored) {
                    }
                }
            }

        } catch (Exception e) {
        }
        return "www";

    }

}
