package hu.csanyzeg.android.homealone.Utils;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import hu.csanyzeg.android.homealone.Data.Config;
import hu.csanyzeg.android.homealone.Data.SensorRecord;

/**
 * Created by tanulo on 2018. 08. 12..
 */

public class ParseHistoryDataXML {

    public static SimpleDateFormat getDateParser(){
        return new SimpleDateFormat("y'-'M'-'d' 'H':'m':'s'.'S");
    }

    public static ArrayList<SensorRecord> parse(String xml){
        ArrayList<SensorRecord> sensorRecords = new ArrayList<>();
        System.out.println("Parse Hisroty Data XML");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new StringBufferInputStream(xml));
            //System.out.println(xml);
            doc.normalize();
            Element element=doc.getDocumentElement();
            element.normalize();
            NodeList root = doc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < root.getLength(); i++) {
                if (root.item(i).getNodeName().equals("#text")) {
                    continue;
                }
                if (root.item(i).getNodeName().equals("data")) {
                    NodeList sen = root.item(i).getChildNodes();
                    SensorRecord c = new SensorRecord();
                    for (int s = 0; s < sen.getLength(); s++) {
                        String txt = sen.item(s).getTextContent().trim().replace("\"", "");
                        switch (sen.item(s).getNodeName().toLowerCase()) {
                            case "#text":
                                break;
                            case "id":
                                c.id = Integer.parseInt(txt);
                                break;
                            case "ts":
                                //2018-08-11 16:28:46.0
                                SimpleDateFormat simpleDateFormat = getDateParser();
                                c.ts =  simpleDateFormat.parse(txt);
                                break;
                            case "field":
                                c.field = txt;
                                break;
                            case "value":
                                c.value = Double.parseDouble(txt);
                                break;
                        }
                    }
                    sensorRecords.add(c);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        System.out.println(" Data count: " + sensorRecords.size());

        System.out.println(sensorRecords);
        return sensorRecords;
    }
}
