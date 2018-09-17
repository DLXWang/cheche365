package com.cheche365.cheche.manage.common.util;

import com.cheche365.cheche.core.util.FileUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by xu.yelong on 2016/8/19.
 */
public class ImageUploadUtil {

    public static String upload(MultipartFile file, String descPath) throws IOException {
        AssertUtil.notNull(file, "文件不可为空");
        FileUtil.isNotExistCreateDirPath(descPath);
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String imgeArray[] = {".jpg", ".jpeg", ".png", ".gif"};
        boolean suffixeq = false;
        for (int i = 0; i < imgeArray.length; i++) {
            if (suffix.equals(imgeArray[i]))
                suffixeq = true;
        }
        if (!suffixeq)
            return null;
        fileName = System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(10).toUpperCase() + suffix;
        File targetFile = new File(descPath, fileName);
        AssertUtil.notExists(targetFile, "已存在相同名字的文件");
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        FileUtil.writeFile(targetFile.getAbsolutePath(), file.getBytes());
        return fileName;
    }
}
