package hu.csanyzeg.android.homealone.Utils;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class HttpByteArrayDownloadUtil extends AsyncTask<String, String, byte[]>{
    @Override
    protected byte[] doInBackground(String... strings) {
        URL website = null;
        try {
            website = new URL(strings[0]);
            BufferedInputStream in = new BufferedInputStream(website.openStream());
            byte dataBuffer[] = new byte[4096];
            int bytesRead;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int size = 0;
            while ((bytesRead = in.read(dataBuffer, size, 1024)) != -1) {
                //System.out.println(bytesRead);
                if (dataBuffer.length - 2048 <size){
                    byte[] dataBuffer2 = new byte[dataBuffer.length + 4096];
                    System.arraycopy(dataBuffer, 0, dataBuffer2, 0, dataBuffer.length);
                    System.out.println(dataBuffer.length + " - - - - " + dataBuffer2.length);
                    dataBuffer = dataBuffer2;
                }
                size += bytesRead;
            }

            System.out.println("-----------------" + size);
            return Arrays.copyOf(dataBuffer,size);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
