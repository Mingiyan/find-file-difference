package ru;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Uavp {
    public static void main(String[] args) throws Exception {
        Uavp uavp = new Uavp();
        String fullPath = args[0];
        String folder3 = null;
        String message = "";
        String hash1 = "";
        String hash2 = "";
        String folder1 = fullPath.split(";")[0];
        String folder2 = fullPath.split(";")[1];
        try {
            folder3 = fullPath.split(";")[2];
        } catch (Exception e) {
            System.out.println("There is no folder for backup.");
        }
        List<File> list1 = getAllFiles(folder1);

        for (int i = 0; i < list1.size(); i++) {

            String path = list1.get(i).getParent().replace(folder1,"");
            String needFile = filesFinder(folder2 + "/" + path,list1.get(i).getName());
            if (needFile != null) {
                hash1 = uavp.getMD5Checksum(list1.get(i).toString());
                hash2 = uavp.getMD5Checksum(needFile);
                if (hash1.equals(hash2)) {
                    System.out.println("Files " + list1.get(i) + " and " + needFile + " are identical. Not need to copy.");
                } else {
                    message = "Files " + list1.get(i) + " and " + needFile + " are different. Copied...";
                    if (folder3 != null) {
                        message = "Files " + list1.get(i) + " and " + needFile + " are different. There is a backup and copying";
                        System.out.println(message);
                        createDir(folder3 + path);
                        fileCopy(new File(needFile), new File(folder3 + "/" + path + "/" + list1.get(i).getName()));
                    } else {
                        System.out.println(message);
                    }
                    fileCopy(list1.get(i), new File(needFile));
                }
            } else {
                System.out.println(list1.get(i) + " - Do not find such a file. Copied completely to " + folder2);
                createDir(folder2 + path);
                fileCopy(list1.get(i), new File(folder2 + "/" + path + "/" + list1.get(i).getName()));
            }
        }
    }
    
    public static void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static void fileCopy(File in, File out) throws IOException     {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while ( position < size ) {
                position += inChannel.transferTo( position, maxCount, outChannel );
            }
            System.out.println("File Successfully Copied..");
        }
        finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
    
    public static String filesFinder(String directoryName, String fileName) {
        File directory = new File(directoryName);
        File[] list = directory.listFiles();
        String needFile = null;
        try {
            for (File file : list) {
                if (file.isFile()) {
                    if (file.getName().equals(fileName)) {
                        needFile = file.getAbsolutePath();
                    }
                }
            }
        } catch (Exception e) {
            needFile = null;
        }
        return needFile;
    }

    public static List<File> getAllFiles(String directoryName) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        List<File> result = new ArrayList<>();
        for (File file : Objects.requireNonNull(fList)) {
            if (file.isFile()) {
                result.add(file);
            } else {
                result.addAll(getAllFiles(file.getAbsolutePath()));
            }
        }
        return result;
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    public String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
