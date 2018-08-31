package hu.csanyzeg.android.homealone.Utils;

import android.content.Context;
import android.content.Intent;

import hu.csanyzeg.android.homealone.Data.Config;

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
import java.util.ArrayList;

/**
 * Created by tanulo on 2018. 07. 03..
 */

abstract public class ParseConfigINI {
    InputStream inputStream;

    public ParseConfigINI(File file) {
        try {
            this.inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            onFileOpenError(e);
        }
    }

    public ParseConfigINI(int resourceID, Context context) {
        this.inputStream = context.getResources().openRawResource(resourceID);

    }

    public ParseConfigINI(String ini) {
        this.inputStream = new StringBufferInputStream(ini);
    }

    abstract protected void onFileOpenError(FileNotFoundException e);
    
    public ArrayList<Config> parse(){
        ArrayList<Config> configs = new ArrayList<Config>();
        System.out.println("Parse INI");
        System.out.println(Config.class.getFields().length);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Config config = null;
        String section ="";
        try {
            while (bufferedReader.ready()){
                String[] l = bufferedReader.readLine().trim().split("=");
                String[] line = new String[l.length];
                for(int i=0; i<l.length; i++){
                    line[i] = l[i].trim().replace("\"","");
                }
                if (line.length == 1){
                    if (line[0].startsWith("[") && line[0].endsWith("]")){
                        section = line[0].substring(1,line[0].length()-1);
                        System.out.println(section);
                        switch (section){
                            case "system":

                                break;
                            default:
                                config = new Config();
                                config.id = section;
                                configs.add(config);
                                break;
                        }
                    }
                }
                if (line.length == 2){
                    String key = l[0].trim().replace("\"","").replace(".","").toLowerCase();
                    String value = l[1].trim().replace("\"","");
                    boolean v = false;
                    for (Field f : Config.class.getFields()) {
                        if (Modifier.isPublic(f.getModifiers())) {
                            if (f.getName().toLowerCase().equals(key)) {
                                v = true;
                                //System.out.println(f.getType().toString());
                                String[] types = f.getType().toString().split("\\.");
                                //System.out.println(types.length);
                                switch (types[types.length - 1].toLowerCase()) {
                                    case "boolean":
                                        if (config != null) {
                                            f.setBoolean(config, value.equals("1"));
                                        } else {
                                            if (Modifier.isStatic(f.getModifiers()) && section.equals("system")) {
                                                f.setBoolean(Config.class, value.equals("1"));
                                            }
                                        }
                                        break;
                                    case "int":
                                    case "integer":
                                        if (config != null) {
                                            f.set(config, Integer.parseInt(value));
                                        } else {
                                            if (Modifier.isStatic(f.getModifiers()) && section.equals("system")) {
                                                f.set(Config.class, Integer.parseInt(value));
                                            }
                                        }
                                        break;
                                    case "string":
                                        if (config != null) {
                                            f.set(config, value);
                                        } else {
                                            if (Modifier.isStatic(f.getModifiers()) && section.equals("system")) {
                                                f.set(Config.class, value);
                                            }
                                        }
                                        break;
                                    case "double":
                                        if (config != null) {
                                            f.set(config, Double.parseDouble(value));
                                        } else {
                                            if (Modifier.isStatic(f.getModifiers()) && section.equals("system")) {
                                                f.set(Config.class, Double.parseDouble(value));
                                            }
                                        }
                                        break;
                                    default:
                                        System.out.println("Ismeretlen típus: " + types[types.length - 1]);
                                        break;
                                }
                            }
                        }
                    }
                    if (!v){
                        System.out.println("Ismeretlen INI bejegyzés: " + key + " = " + value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return configs;
    }
}
