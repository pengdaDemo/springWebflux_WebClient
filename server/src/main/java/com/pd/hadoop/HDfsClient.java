package com.pd.hadoop;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


//import org.apache.hadoop.hdfs.HdfsConfiguration;
public class HDfsClient {

    public FileSystem fs;

    public HDfsClient(HdfsConfiguration configuration) throws IOException, InterruptedException {
        fs = FileSystem.get(URI.create(configuration.get("fs.defaultFS")), configuration, configuration.get("hdfsUser"));
    }

    public boolean exist(String path) {
        boolean flag = false;
        try {
            flag = fs.exists(new Path(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void upload(String originalFilename, String destPath) throws IOException {
        fs.copyFromLocalFile(new Path(originalFilename), new Path(destPath));
    }

    public void upload(InputStream in, String destPath) throws IOException {
        FSDataOutputStream out = fs.create(new Path(destPath));
        IOUtils.copyBytes(in, out, 8192, true);
    }

    public FSDataInputStream download(String path) throws IOException {
        return fs.open(new Path(path));
    }

    /**
     * 获取hdfs路径下的文件列表
     *
     * @param srcpath hdfs文件路径
     * @return String[]
     */
    public String[] getFileList(String srcpath) {
        try {
            Path path = new Path(srcpath);
            List<String> files = new ArrayList<String>();
            if (fs.exists(path) && fs.getFileStatus(path).isDirectory()) {
                for (FileStatus status : fs.listStatus(path)) {
                    files.add(status.getPath().toString());
                }
            }
            return files.toArray(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
