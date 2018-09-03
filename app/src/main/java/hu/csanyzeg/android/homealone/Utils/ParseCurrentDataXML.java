package hu.csanyzeg.android.homealone.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringBufferInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import hu.csanyzeg.android.homealone.Data.SensorRecord;

/**
 * Created by tanulo on 2018. 08. 13..
 */

public class ParseCurrentDataXML {
    public static SimpleDateFormat getDateParser(){
        return new SimpleDateFormat("y'-'M'-'d' 'H':'m':'s");
    }

    public static ArrayList<SensorRecord> parse(String xml){
        ArrayList<SensorRecord> sensorRecords = new ArrayList<>();
        //System.out.println("Parse Current XML");
        //System.out.println(xml);
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
                    Date ts = Calendar.getInstance().getTime();
                    int id = 0;
                    for (int s = 0; s < sen.getLength(); s++) {
                        String txt = sen.item(s).getTextContent().trim().replace("\"", "");
                        switch (sen.item(s).getNodeName().toLowerCase()) {
                            case "#text":
                                break;
                            case "id":
                                id = Integer.parseInt(txt);
                                break;
                            case "ts":
                                //2018-08-11 16:28:46.0
                                SimpleDateFormat simpleDateFormat = getDateParser();
                                ts =  simpleDateFormat.parse(txt);
                                break;
/*
                            case "field":
                                c.field = txt;
                                break;
                            case "value":
                                c.value = Double.parseDouble(txt);
                                break;
                                */
                        }
                    }
                    for (int s = 0; s < sen.getLength(); s++) {
                        String txt = sen.item(s).getTextContent().trim().replace("\"", "");
                        String node = sen.item(s).getNodeName();
                        if (!node.toLowerCase().equals("id") && !node.toLowerCase().equals("#text") && !node.toLowerCase().equals("ts")){
                            sensorRecords.add(new SensorRecord(id, node, Double.parseDouble(txt), ts));
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        //System.out.println(" Data count: " + sensorRecords.size());

        //System.out.println(configs.size());
        //System.out.println(sensorRecords);
        return sensorRecords;
    }

}

/*


<root><data><ID>181</ID><TS>2018-08-13 10:25:31</TS><AL>0</AL><I4>0</I4><I5>0</I5><I6>0</I6><C8>26.0</C8><H8>52</H8><C9>26.7</C9><H9>57</H9><N0>0.047</N0><LP>107</LP><CO>2659</CO><CH>546</CH><N3>0</N3><XC>20.00</XC><X0>0</X0><X1>0</X1><X2>0</X2><X3>0</X3><X4>0</X4><X5>0</X5><X6>0</X6><X7>0</X7></data></root>

 */
