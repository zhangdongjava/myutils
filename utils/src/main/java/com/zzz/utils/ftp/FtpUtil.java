package com.zzz.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.FpUtils;

import java.io.*;

public class FtpUtil {

    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);


    /**
     * 获取ftp连接
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static FTPClient connectFtp(FtpConfig f) throws IOException {
        FTPClient ftp = new FTPClient();
        int reply;
        if (f.getPort() == null) {
            ftp.connect(f.getIpAddr(), 21);
        } else {
            ftp.connect(f.getIpAddr(), f.getPort());
        }
        ftp.login(f.getUserName(), f.getPwd());
        ftp.enterLocalPassiveMode();
        ftp.setControlEncoding("utf-8");
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("链接失败!");
        }
        ftp.changeWorkingDirectory(f.getPath());
        return ftp;
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp(FTPClient ftp) {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException e) {
                logger.error("关闭ftp链接异常!");
            }
        }
    }

    /**
     * ftp上传文件
     *
     * @param ftpConfig
     * @param f
     * @throws Exception
     */
    public static boolean upload(FtpConfig ftpConfig, String directory, File f, String fileName) throws Exception {
        FTPClient ftp = connectFtp(ftpConfig);
        if (ftp == null) {
            throw new IOException("链接失败！");
        }
        File file2 = new File(f.getPath());
        FileInputStream input = new FileInputStream(file2);
        if (fileName == null) {
            fileName = f.getName();
        }

        ftp.changeWorkingDirectory(directory);
        boolean res = ftp.storeFile(fileName, input);
        try {
            input.close();
            closeFtp(ftp);
        } finally {
            return res;
        }

    }


    public static boolean makeDirectory(FtpConfig ftpConfig, String directory) throws IOException {
        FTPClient ftp = connectFtp(ftpConfig);
        String[] directories = directory.split("/");
        StringBuilder builder = new StringBuilder("/");
        boolean res = false;
        for (String directory2 : directories) {
            res = ftp.changeWorkingDirectory(builder.toString());
            ftp.makeDirectory(directory2);
            builder.append(directory2);
            builder.append("/");
        }
        return res;
    }

    public static boolean exitesDirectory(FtpConfig ftpConfig, String directory) throws IOException {
        FTPClient ftp = connectFtp(ftpConfig);
        try {
           return ftp.changeWorkingDirectory(directory);
        }catch (Exception e){
            return false;
        }

    }


    public static void main(String[] args) throws Exception {
        FtpConfig f = new FtpConfig();
        f.setIpAddr("192.168.222.220");
        f.setUserName("myuser");
        f.setPwd("mypass");
        f.setPort(2341);
        String dir = "a/v/ba";

        boolean fileRes =  FtpUtil.exitesDirectory(f, dir);
        System.out.println(fileRes);

    }

}