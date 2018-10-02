package hu.csanyzeg.android.homealone.Utils;

import android.os.AsyncTask;

import com.google.android.gms.fido.u2f.api.common.ErrorCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class HttpByteArrayDownloadUtil extends AsyncTask<String, String, HttpByteArrayDownloadUtil.Result>{


    private int maxBufferSize = 1024*1024*16;
    private int minBufferSize = 1024*128;
    private int incBufferSize = 1024*64;
    private int readBufferSize = 1460;

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public int getMinBufferSize() {
        return minBufferSize;
    }

    public void setMinBufferSize(int minBufferSize) {
        this.minBufferSize = minBufferSize;
    }

    public int getIncBufferSize() {
        return incBufferSize;
    }

    public void setIncBufferSize(int incBufferSize) {
        this.incBufferSize = incBufferSize;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public static enum ErrorCode{
        OK,
        TIMEOUT,
        BUFFER_OVERFLOW,
        MALFORMED_URL
    }

    public static class Result{
        public byte[] bytes;
        public ErrorCode errorCode;
    }

    @Override
    protected Result doInBackground(String... strings) {
        URL website = null;
        Result result = new Result();
        try {
            website = new URL(strings[0]);
            BufferedInputStream in = new BufferedInputStream(website.openStream());
            byte dataBuffer[] = new byte[minBufferSize];
            int bytesRead;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int size = 0;
            while ((bytesRead = in.read(dataBuffer, size, readBufferSize)) != -1) {
                //System.out.println(bytesRead);
                if (dataBuffer.length - incBufferSize/2 <size){
                    if (dataBuffer.length>maxBufferSize){
                        result.errorCode = ErrorCode.BUFFER_OVERFLOW;
                        return result;
                    }
                    byte[] dataBuffer2 = new byte[dataBuffer.length + incBufferSize];
                    System.arraycopy(dataBuffer, 0, dataBuffer2, 0, dataBuffer.length);
                    System.out.println(dataBuffer.length + " - - - - " + dataBuffer2.length);
                    dataBuffer = dataBuffer2;
                }
                size += bytesRead;
            }


            System.out.println("-----------------" + size);
            result.errorCode = ErrorCode.OK;
            result.bytes = Arrays.copyOf(dataBuffer,size);
            return result;
        } catch (MalformedURLException e) {
            result.errorCode = ErrorCode.MALFORMED_URL;
            e.printStackTrace();
        } catch (IOException e) {
            result.errorCode = ErrorCode.TIMEOUT;
            e.printStackTrace();
        }
        return result;
    }
}
