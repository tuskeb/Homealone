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
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        //return new SimpleDateFormat("y'-'M'-'d' 'H':'m':'s");
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
                System.out.println(root.item(i).getNodeName());
                if (root.item(i).getNodeName().equals("#text")) {
                    continue;
                }
                if (root.item(i).getNodeName().equals("data") || root.item(i).getNodeName().equals("startdata") || root.item(i).getNodeName().equals("stopdata")) {
                    NodeList data = root.item(i).getChildNodes();
                    for (int s = 0; s < data.getLength(); s++) {
                        //String txt = data.item(s).getTextContent().trim().replace("\"", "");
                        //System.out.println(data.item(s).getTextContent());
//                        System.out.println(data.item(s).getNodeName());
                        if (data.item(s).getNodeName().equals("#text")) {
                            continue;
                        }
                        if (data.item(s).getAttributes()!=null) {
                            SensorRecord c = new SensorRecord();
                            c.ts = new Date(Long.parseLong(data.item(s).getAttributes().getNamedItem("TS").getTextContent(), 16) * 1000);
                            c.field = data.item(s).getNodeName();
                            c.value = Double.parseDouble(data.item(s).getTextContent().trim().replace("\"", ""));
                            c.id = 0;
                            sensorRecords.add(c);
//                            System.out.println(c);
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        //System.out.println(" Data count: " + sensorRecords.size());
        //System.out.println(sensorRecords);
        return sensorRecords;
    }

    public static void main(String[] args) {
        ArrayList<SensorRecord> sr = parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><startdata><AL TS=\"5B82E8DE\">0</AL><C8 TS=\"5B85454A\">26.0</C8><C9 TS=\"5B85454A\">24.0</C9><CH TS=\"5B85454D\">77607</CH><CO TS=\"5B85454D\">1623528</CO><H8 TS=\"5B85454A\">44</H8><H9 TS=\"5B85454A\">53</H9><I4 TS=\"5B84D7A8\">1</I4><I5 TS=\"5B798B55\">0</I5><I6 TS=\"5B7C32D6\">0</I6><LP TS=\"5B85454D\">11060</LP><N0 TS=\"5B85472E\">0.062</N0><X0 TS=\"5B7C5700\">0</X0><X1 TS=\"5B817DF6\">0</X1><X4 TS=\"5B7587FE\">1</X4><X5 TS=\"5B84ECBF\">1</X5><X6 TS=\"5B82E8B4\">0</X6><X7 TS=\"5B758814\">0</X7><XC TS=\"5B854548\">20.00</XC></startdata><stopdata><AL TS=\"5B857B94\">1</AL><C8 TS=\"5B855727\">26.0</C8><C9 TS=\"5B855727\">24.6</C9><CH TS=\"5B855727\">77570</CH><CO TS=\"5B855727\">1622517</CO><H8 TS=\"5B855727\">44</H8><H9 TS=\"5B855727\">52</H9><I4 TS=\"5B8568CB\">1</I4><LP TS=\"5B855727\">11055</LP><N0 TS=\"5B855567\">0.052</N0><X1 TS=\"5B856396\">1</X1><X5 TS=\"5B857BC6\">1</X5><XC TS=\"5B855727\">20.00</XC></stopdata><data><N0 TS=\"5B854754\">0.057</N0><N0 TS=\"5B85475A\">0.063</N0><N0 TS=\"5B85475F\">0.059</N0><N0 TS=\"5B854789\">0.062</N0><N0 TS=\"5B85478F\">0.058</N0><N0 TS=\"5B854791\">0.061</N0><N0 TS=\"5B854793\">0.066</N0><N0 TS=\"5B854798\">0.059</N0><N0 TS=\"5B8547A7\">0.063</N0><N0 TS=\"5B8547C1\">0.055</N0><N0 TS=\"5B8547C9\">0.060</N0><N0 TS=\"5B8547CD\">0.055</N0><N0 TS=\"5B8547D3\">0.060</N0><N0 TS=\"5B8547E4\">0.056</N0><N0 TS=\"5B8547EA\">0.061</N0><N0 TS=\"5B8547EE\">0.056</N0><N0 TS=\"5B8547F8\">0.057</N0><N0 TS=\"5B85480A\">0.061</N0><N0 TS=\"5B85480F\">0.055</N0><N0 TS=\"5B854815\">0.060</N0><N0 TS=\"5B85481C\">0.056</N0><N0 TS=\"5B85483A\">0.062</N0><N0 TS=\"5B85483D\">0.054</N0><N0 TS=\"5B854841\">0.059</N0><N0 TS=\"5B854847\">0.056</N0><N0 TS=\"5B85484D\">0.060</N0><N0 TS=\"5B854853\">0.055</N0><N0 TS=\"5B854857\">0.593</N0><N0 TS=\"5B85485F\">0.055</N0><N0 TS=\"5B854863\">0.060</N0><N0 TS=\"5B8548DB\">0.056</N0><N0 TS=\"5B8549A1\">0.063</N0><N0 TS=\"5B8549A2\">0.058</N0><N0 TS=\"5B8549B7\">0.061</N0><N0 TS=\"5B8549BD\">0.057</N0><N0 TS=\"5B8549D2\">0.056</N0><N0 TS=\"5B8549DB\">0.061</N0><N0 TS=\"5B8549DE\">0.057</N0><N0 TS=\"5B8549E8\">0.057</N0><N0 TS=\"5B8549F2\">0.060</N0><N0 TS=\"5B8549F4\">0.056</N0><N0 TS=\"5B8549F8\">0.059</N0><N0 TS=\"5B8549FE\">0.055</N0><N0 TS=\"5B854A07\">0.061</N0><N0 TS=\"5B854A09\">0.056</N0><N0 TS=\"5B854A0E\">0.062</N0><N0 TS=\"5B854A14\">0.058</N0><N0 TS=\"5B854A31\">0.061</N0><N0 TS=\"5B854A35\">0.056</N0><N0 TS=\"5B854A3A\">0.059</N0><N0 TS=\"5B854A3B\">0.062</N0><N0 TS=\"5B854A3D\">0.058</N0><N0 TS=\"5B854A3E\">0.061</N0><N0 TS=\"5B854A40\">0.055</N0><N0 TS=\"5B854A47\">0.060</N0><N0 TS=\"5B854A49\">0.063</N0><N0 TS=\"5B854A4B\">0.057</N0><N0 TS=\"5B854A50\">0.062</N0><N0 TS=\"5B854A56\">0.056</N0><N0 TS=\"5B854A5F\">0.060</N0><N0 TS=\"5B854A76\">0.054</N0><N0 TS=\"5B854A78\">0.057</N0><N0 TS=\"5B854A93\">0.055</N0><N0 TS=\"5B854AB5\">0.056</N0><N0 TS=\"5B854AD1\">0.059</N0><N0 TS=\"5B854ADA\">0.056</N0><N0 TS=\"5B854AE1\">0.061</N0><N0 TS=\"5B854AFB\">0.057</N0><N0 TS=\"5B854BE8\">0.060</N0><N0 TS=\"5B854BEA\">0.056</N0><N0 TS=\"5B854C03\">0.060</N0><N0 TS=\"5B854C04\">0.056</N0><N0 TS=\"5B854C0D\">0.061</N0><N0 TS=\"5B854C0F\">0.056</N0><N0 TS=\"5B854C38\">0.047</N0><N0 TS=\"5B854C3B\">0.050</N0><N0 TS=\"5B854C43\">0.053</N0><N0 TS=\"5B854C56\">0.047</N0><N0 TS=\"5B854C60\">0.054</N0><N0 TS=\"5B854C74\">0.046</N0><N0 TS=\"5B854C7C\">0.051</N0><N0 TS=\"5B854C81\">0.060</N0><N0 TS=\"5B854D5C\">0.056</N0><N0 TS=\"5B854DAE\">0.055</N0><N0 TS=\"5B854DB4\">0.058</N0><N0 TS=\"5B854DC0\">0.055</N0><N0 TS=\"5B854E0D\">0.058</N0><N0 TS=\"5B854E22\">0.055</N0><N0 TS=\"5B854E43\">0.058</N0><N0 TS=\"5B854E60\">0.055</N0><N0 TS=\"5B854E72\">0.052</N0><N0 TS=\"5B854E96\">0.058</N0><N0 TS=\"5B854F48\">0.051</N0><N0 TS=\"5B854F7D\">0.057</N0><N0 TS=\"5B855029\">0.054</N0><N0 TS=\"5B85507C\">0.051</N0><N0 TS=\"5B85507F\">0.056</N0><N0 TS=\"5B85518A\">0.052</N0><N0 TS=\"5B85518D\">0.057</N0><N0 TS=\"5B8551AB\">0.052</N0><N0 TS=\"5B8551AE\">0.055</N0><N0 TS=\"5B8551EC\">0.052</N0><N0 TS=\"5B855219\">0.057</N0><N0 TS=\"5B85521F\">0.053</N0><N0 TS=\"5B855245\">0.056</N0><N0 TS=\"5B85524B\">0.050</N0><N0 TS=\"5B855281\">0.053</N0><N0 TS=\"5B8552AF\">0.049</N0><N0 TS=\"5B8552C3\">0.049</N0><N0 TS=\"5B8552C5\">0.052</N0><N0 TS=\"5B855349\">0.049</N0><N0 TS=\"5B855350\">0.053</N0><N0 TS=\"5B855356\">0.049</N0><N0 TS=\"5B85535B\">0.054</N0><N0 TS=\"5B85535F\">0.051</N0><N0 TS=\"5B855371\">0.054</N0><N0 TS=\"5B855377\">0.050</N0><N0 TS=\"5B855386\">0.053</N0><N0 TS=\"5B85539B\">0.049</N0><N0 TS=\"5B85539C\">0.054</N0><N0 TS=\"5B8553AB\">0.049</N0><N0 TS=\"5B8553B2\">0.054</N0><N0 TS=\"5B8553B8\">0.050</N0><N0 TS=\"5B8553CA\">0.053</N0><N0 TS=\"5B8553CB\">0.049</N0><N0 TS=\"5B8553CF\">0.052</N0><N0 TS=\"5B8553E2\">0.049</N0><N0 TS=\"5B855400\">0.053</N0><N0 TS=\"5B855429\">0.052</N0><N0 TS=\"5B85544A\">0.049</N0><N0 TS=\"5B85545F\">0.052</N0><N0 TS=\"5B855470\">0.049</N0><N0 TS=\"5B85547F\">0.052</N0><N0 TS=\"5B8554A9\">0.049</N0><N0 TS=\"5B8554C3\">0.049</N0><N0 TS=\"5B85551A\">0.052</N0><N0 TS=\"5B855539\">0.052</N0></data></root>");

        for(SensorRecord s : sr){
            System.out.println(s);
        }
    }
}
