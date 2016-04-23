
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class FileManager {

    private static final int XUEYANG_LEN = 300;
    private static final int MAIBO_LEN = 300;
    private static final int GUANZHUZHISHU_LEN = 600;
    private static final int TIWEN_LEN = 600;
    private static final int FENCHEN_LEN = 1200;
    private static final int XINDIAN_LEN = 60000;
    private static final int DATA_PACKAGE_LEN = XUEYANG_LEN + MAIBO_LEN + GUANZHUZHISHU_LEN + TIWEN_LEN + FENCHEN_LEN + XINDIAN_LEN;
    private static final String filePath = "/data/data/com.example.health_community/files/";

    private SimpleDateFormat dateFormat;
    private Context context;

    public FileManager(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("yyyy-M-dd");
    }


    //不用filePath因为openFileOutput的默认路径就是/data/data/<package name>/files/
    public synchronized void saveByteFile(int dataType, byte[] buffer) {
        Log.d("存入", getFileName(dataType));
        if (dataType == MConfig.XueYang && buffer.length > 1) {
            saveByteFile(MConfig.XueYang, new byte[]{buffer[0]});
            saveByteFile(MConfig.MaiBo, new byte[]{buffer[1]});
            saveByteFile(MConfig.GuanZhuanZhiShu, new byte[]{buffer[2], buffer[3]});
        }
        FileOutputStream fos = null;
        String fileName = getFileName(dataType);
        String date = dateFormat.format(new Date());
        File pendingFile = new File(filePath + fileName + date);
        //		int fileNum = 1;
        //		while(pendingFile.exists()){
        //			pendingFile = new File(filePath+fileName+date+"-"+fileNum);
        //			fileNum++;
        //		}
        try {
            fos = context.openFileOutput(pendingFile.getName(), Context.MODE_APPEND);
            //写入
            fos.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getByteFile(int fileType, String date) {
        Date date2 = null;
        try {
            date2 = new SimpleDateFormat("yyyy-M-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getByteFile(fileType, date2);
    }

    //把各个数据读入字节数组
    public byte[] getByteFile(int fileType, Date date) {
        Log.d("读取", getFileName(fileType));
        byte[] b = new byte[getLen(fileType)];
        File pendingFile = new File(filePath + getFileName(fileType) + dateFormat.format(date));
        FileInputStream fis = null;
        try {
            //判断是否存在此文件
            if (pendingFile.exists()) {
                fis = context.openFileInput(pendingFile.getName());
                fis.read(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    //判断是否存在此文件
    public void assemblePac(Date date) {
        byte[] pac = new byte[DATA_PACKAGE_LEN];
        byte[] head = new byte[10];
        String dateString = dateFormat.format(date);
        head[0] = (byte) Integer.parseInt(dateString.substring(2, 4));
        head[1] = (byte) Integer.parseInt(dateString.substring(5, 7));
        head[2] = (byte) Integer.parseInt(dateString.substring(8, 10));
        byte[] b1 = getByteFile(MConfig.XueYang, date);
        byte[] b2 = getByteFile(MConfig.MaiBo, date);
        byte[] b3 = getByteFile(MConfig.GuanZhuanZhiShu, date);
        byte[] b4 = getByteFile(MConfig.TiWen, date);
        byte[] b5 = getByteFile(MConfig.FenChen, date);
        byte[] b6 = getByteFile(MConfig.XinDian, date);
        System.arraycopy(head, 0, pac, 0, 10);
        System.arraycopy(b1, 0, pac, 10, b1.length);
        System.arraycopy(b2, 0, pac, b1.length, b2.length);
        System.arraycopy(b3, 0, pac, b2.length, b3.length);
        System.arraycopy(b4, 0, pac, b3.length, b4.length);
        System.arraycopy(b5, 0, pac, b4.length, b5.length);
        System.arraycopy(b6, 0, pac, b5.length, b6.length);
        saveByteFile(0, pac);
        Log.d("组装结果", "成功");
    }

    public byte[] getPackage(Date date) {
        File file = new File(filePath + getFileName(0) + dateFormat.format(date));
        if (!file.exists()) {
            assemblePac(date);
        }
        return getByteFile(0, date);
    }

    public int getLen(int fileType) {
        int len = 0;
        switch (fileType) {
            case 0:
                len = DATA_PACKAGE_LEN;
                break;
            case 1:
                len = XUEYANG_LEN;
                break;
            case 2:
                len = XINDIAN_LEN;
                break;
            case 3:
                len = 0;
                break;
            case 4:
                len = TIWEN_LEN;
                break;
            case 5:
                len = FENCHEN_LEN;
                break;
            case 6:
                len = 0;
                break;
            case 7:
                len = 0;
                break;
            case 8:
                len = MAIBO_LEN;
                break;
            case 9:
                len = GUANZHUZHISHU_LEN;
                break;
        }
        return len;
    }

    public String getFileName(int fileType) {
        String strName = null;
        switch (fileType) {
            case 0:
                strName = "Package";
                break;
            case 1:
                strName = "XueYang";
                break;
            case 2:
                strName = "XinDian";
                break;
            case 3:
                strName = "XueTang"; //貌似另一个文件
                break;
            case 4:
                strName = "TiWen";
                break;
            case 5:
                strName = "FenChen";
                break;
            case 6:
                strName = "NaoDian";//待定
                break;
            case 7:
                strName = "XueYa";  //待定
                break;
            case 8:
                strName = "MaiBo";
                break;
            case 9:
                strName = "GuanZhuZhiShu";
                break;
        }
        return strName;

    }
}
