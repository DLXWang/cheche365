package com.cheche365.cheche.core.util;

import com.cheche365.cheche.core.exception.BusinessException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;

/**
 * Created by chenxiaozhe on 15-7-20.
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 判断目录是否存在，如果不存在则创建
     */
    public static void isNotExistCreateDirPath(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectory(Paths.get(path));
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("create directory fail! path->{}", path);
                }
            }
        }
    }

    /**
     * 判断目录是否存在，如果不存在则创建
     */
    public static void isNotExistCreateDirPaths(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("create directory fail! path->{}", path);
                }
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isExist(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 如果文件存在则删除
     *
     * @param path
     */
    public static void isExistDel(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("delete path file fail! path->{}", path);
            }
        }
    }

    /**
     * 删除文件，可以删除单个文件或文件夹
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.debug(fileName + "文件不存在!");
            return true;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 复制单个文件，如果目标文件存在，则不覆盖
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String descFileName) {
        return copyFileCover(srcFileName, descFileName, false);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @param isCover      如果目标文件已存在，是否覆盖
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFileCover(String srcFileName, String descFileName, boolean isCover) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            logger.debug("复制文件失败，源文件" + srcFileName + "不存在!");
            return false;
        }
        // 判断源文件是否是合法的文件
        else if (!srcFile.isFile()) {
            logger.debug("复制文件失败，" + srcFileName + "不是一个文件!");
            return false;
        }
        File descFile = new File(descFileName);
        // 判断目标文件是否存在
        if (descFile.exists()) {
            // 如果目标文件存在，并且允许覆盖
            if (isCover) {
                logger.debug("目标文件已存在，准备删除!");
                if (!delFile(descFileName)) {
                    logger.debug("删除目标文件" + descFileName + "失败!");
                    return false;
                }
            } else {
                logger.debug("复制文件失败，目标文件" + descFileName + "已存在!");
                return false;
            }
        } else {
            if (!descFile.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建目录
                logger.debug("目标文件所在的目录不存在，创建目录!");
                // 创建目标文件所在的目录
                if (!descFile.getParentFile().mkdirs()) {
                    logger.debug("创建目标文件所在的目录失败!");
                    return false;
                }
            }
        }

        // 准备复制文件
        // 读取的位数
        int readByte = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            // 打开源文件
            ins = new FileInputStream(srcFile);
            // 打开目标文件的输出流
            outs = new FileOutputStream(descFile);
            byte[] buf = new byte[1024];
            // 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
            while ((readByte = ins.read(buf)) != -1) {
                // 将读取的字节流写入到输出流
                outs.write(buf, 0, readByte);
            }
            logger.debug("复制单个文件" + srcFileName + "到" + descFileName + "成功!");
            return true;
        } catch (Exception e) {
            logger.debug("复制文件失败：" + e.getMessage());
            return false;
        } finally {
            // 关闭输入输出流，首先关闭输出流，然后再关闭输入流
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException oute) {
                    oute.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ine) {
                    ine.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制整个目录的内容，如果目标目录存在，则不覆盖
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String descDirName) {
        return copyDirectoryCover(srcDirName, descDirName, false);
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @param coverlay    如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectoryCover(String srcDirName,
                                             String descDirName, boolean coverlay) {
        File srcDir = new File(srcDirName);
        // 判断源目录是否存在
        if (!srcDir.exists()) {
            logger.debug("复制目录失败，源目录" + srcDirName + "不存在!");
            return false;
        }
        // 判断源目录是否是目录
        else if (!srcDir.isDirectory()) {
            logger.debug("复制目录失败，" + srcDirName + "不是一个目录!");
            return false;
        }
        // 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        // 如果目标文件夹存在
        if (descDir.exists()) {
            if (coverlay) {
                // 允许覆盖目标目录
                logger.debug("目标目录已存在，准备删除!");
                if (!delFile(descDirNames)) {
                    logger.debug("删除目录" + descDirNames + "失败!");
                    return false;
                }
            } else {
                logger.debug("目标目录复制失败，目标目录" + descDirNames + "已存在!");
                return false;
            }
        } else {
            // 创建目标目录
            logger.debug("目标目录不存在，准备创建!");
            if (!descDir.mkdirs()) {
                logger.debug("创建目标目录失败!");
                return false;
            }

        }

        boolean flag = true;
        // 列出源目录下的所有文件名和子目录名
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 如果是一个单个文件，则直接复制
            if (files[i].isFile()) {
                flag = copyFile(files[i].getAbsolutePath(), descDirName + File.separator + files[i].getName());
                // 如果拷贝文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 如果是子目录，则继续复制目录
            if (files[i].isDirectory()) {
                flag = copyDirectory(files[i].getAbsolutePath(), descDirName + files[i].getName());
                // 如果拷贝目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("复制目录" + srcDirName + "到" + descDirName + "失败!");
            return false;
        }
        logger.debug("复制目录" + srcDirName + "到" + descDirName + "成功!");
        return true;

    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("删除单个文件" + fileName + "成功!");
                return true;
            } else {
                logger.debug("删除单个文件" + fileName + "失败!");
                return false;
            }
        } else {
            logger.debug(fileName + "文件不存在!");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            logger.debug(dirNames + "目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                // 如果删除文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                // 如果删除子目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            logger.debug("删除目录" + dirName + "成功!");
            return true;
        } else {
            logger.debug("删除目录" + dirName + "失败!");
            return false;
        }
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param contents
     */
    public static void writeFile(String filePath, byte[] contents) {
        int lastIndex = filePath.lastIndexOf(File.separator);
        String parentPath = filePath.substring(0, lastIndex);
        logger.debug("parenPath =========>>>>>" + parentPath);
        isNotExistCreateDirPath(parentPath);
        FileChannel fileChannel = null;
        try {
            //如果文件存在首先删除
            FileUtil.isExistDel(filePath);
            //写文件.
            fileChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            ByteBuffer byteBuffer = ByteBuffer.wrap(contents);
            fileChannel.write(byteBuffer);

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("write " + filePath + " file failed! ");
            }
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "写文件异常" + filePath);
        } finally {
            closeFileChannel(fileChannel);
        }
    }

    /**
     * 追加
     *
     * @param filePath
     * @param contents
     */
    public static void appendFile(String filePath, byte[] contents) {
        int lastIndex = filePath.lastIndexOf(File.separator);
        String parentPath = filePath.substring(0, lastIndex);
        logger.debug("parenPath =========>>>>>" + parentPath);
        isNotExistCreateDirPath(parentPath);
        FileChannel fileChannel = null;
        try {
            //写文件.(追加)
            fileChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            ByteBuffer byteBuffer = ByteBuffer.wrap(contents);
            fileChannel.write(byteBuffer);

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("write " + filePath + " file failed! ");
            }
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "写文件异常" + filePath);
        } finally {
            closeFileChannel(fileChannel);
        }
    }

    private static void closeFileChannel(FileChannel fileChannel){
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }


        } catch (IOException e) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "关闭fileChannel异常");
        }
    }

}
