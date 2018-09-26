package hu.csanyzeg.android.homealone.Utils;

import android.content.Context;

import hu.csanyzeg.android.homealone.Data.Config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by tanulo on 2018. 06. 27..
 */

@Deprecated
public abstract class ParseConfigXML {

    InputStream inputStream;

    public ParseConfigXML(File file) {
        try {
            this.inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            onFileOpenError(e);
        }
    }

    public ParseConfigXML(int resourceID, Context context) {
        //Log.e("Open","asd");
        this.inputStream = context.getResources().openRawResource(resourceID);

    }

    abstract protected void onFileOpenError(FileNotFoundException e);

    public ArrayList<Config> parse(){
        ArrayList<Config> configs = new ArrayList<Config>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.normalize();
            Element element=doc.getDocumentElement();
            element.normalize();
            NodeList root = doc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < root.getLength(); i++) {
                if (root.item(i).getNodeName().equals("#text")) {
                    continue;
                }
                if (root.item(i).getNodeName().equals("system")) {
                    NodeList sen = root.item(i).getChildNodes();
                    Config c = new Config();
                    c.id = root.item(i).getNodeName();
                    for (int s = 0; s < sen.getLength(); s++) {
                        String txt = sen.item(s).getTextContent().trim().replace("\"", "");
                        switch (sen.item(s).getNodeName()) {
                            case "#text":
                                break;
                            case "name":
                                Config.name = txt;
                                break;
                            case "version":
                                Config.version = txt;
                                break;
                            case "polling":
                                Config.polling = Integer.parseInt(txt);
                                break;
                            case "map":
                                Config.map = txt;
                                break;
                            case "GPS":
                                NodeList gps = sen.item(s).getChildNodes();
                                for (int p = 0; p < gps.getLength(); p++) {
                                    String gpstxt = gps.item(p).getTextContent().trim().replace("\"","");
                                    switch (gps.item(p).getNodeName()) {
                                        case "#text":
                                            break;
                                        case "N":
                                            Config.gpsLatitude = Double.parseDouble(gpstxt);
                                            break;
                                        case "E":
                                            Config.gpsLongitude = Double.parseDouble(gpstxt);
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                }
                //System.out.println(root.item(i).getNodeName());
                NodeList sen = root.item(i).getChildNodes();
                Config c = new Config();
                c.id = root.item(i).getNodeName();
                for (int s = 0; s < sen.getLength(); s++) {
                    String txt = sen.item(s).getTextContent().trim().replace("\"","");
                    switch (sen.item(s).getNodeName()) {
                        case "#text":
                            break;
                        case "display":
                            c.display = txt;
                            break;
                        case "device":
                            c.device = txt;
                            break;
                        case "parent":
                            c.parent = txt;
                            break;
                        case "write":
                            c.write = txt.equals("1");
                            break;
                        case "label":
                            c.label = txt;
                            break;
                        case "enabled":
                            c.enabled = txt.equals("1");
                            break;
                        case "default_value":
                            c.default_value = Double.parseDouble(txt);
                            break;
                        case "min":
                            c.min = Double.parseDouble(txt);
                            break;
                        case "max":
                            c.max = Double.parseDouble(txt);
                            break;
                        case "suffix":
                            c.suffix = txt;
                            break;
                        case "precision":
                            c.precision = Integer.parseInt(txt);
                            break;
                        case "poz":
                            NodeList poz = sen.item(s).getChildNodes();
                            for (int p = 0; p < poz.getLength(); p++) {
                                String poztxt = poz.item(p).getTextContent().trim().replace("\"","");
                                switch (poz.item(p).getNodeName()) {
                                    case "#text":
                                        break;
                                    case "x":
                                        c.pozX = Integer.parseInt(poztxt);
                                        break;
                                    case "y":
                                        c.pozY = Integer.parseInt(poztxt);
                                        break;
                                    case "ikon":
                                        c.icon = poztxt;
                                        break;
                                }
                            }
                            break;
                        case "monostab":
                            c.monostab = Integer.parseInt(txt);
                            break;
                        case "alarm":
                            NodeList alarm = sen.item(s).getChildNodes();
                            //System.out.println(alarm);
                            for (int p = 0; p < alarm.getLength(); p++) {
                                //System.out.println(alarm.item(p).getNodeName());
                                String alarmtxt = alarm.item(p).getTextContent().trim().replace("\"","");
                                switch (alarm.item(p).getNodeName()) {
                                    case "#text":
                                        break;
                                    case "set":
                                        c.alarmSet = alarmtxt.equals("1");
                                        break;
                                    case "switch":
                                        c.alarmSwitch = alarmtxt;
                                        break;
                                    case "minvalue":
                                        c.alarmMinValue = Double.parseDouble(alarmtxt);
                                        break;
                                    case "maxvalue":
                                        c.alarmMaxValue = Double.parseDouble(alarmtxt);
                                        break;
                                    case "text":
                                        c.alarmText = alarmtxt;
                                        break;
                                }
                            }
                            break;
                        case "group":
                            NodeList group = sen.item(s).getChildNodes();
                            //System.out.println(alarm);
                            for (int p = 0; p < group.getLength(); p++) {
                                //System.out.println(alarm.item(p).getNodeName());
                                String grouptxt = group.item(p).getTextContent().trim().replace("\"","");
                                switch (group.item(p).getNodeName()) {
                                    case "#text":
                                        break;
                                    case "valueview":
                                        c.groupValue = grouptxt;
                                        break;
                                    case "issensor":
                                        c.sensor = grouptxt.equals("1");
                                        break;
                                }
                            }
                            break;
                    }

                }
                configs.add(c);
            }
        } catch (Exception e) {e.printStackTrace();}
        //System.out.println(configs.size());
        return configs;
    }

}
