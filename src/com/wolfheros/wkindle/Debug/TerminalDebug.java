package com.wolfheros.wkindle.Debug;

import com.wolfheros.wkindle.NewsContent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


public class TerminalDebug {
    /**
     * general system print method.
     * */
    public static void systemPrint(String input){
        System.out.println(input);
    }

    public static void runtimeError(int i){
        System.out.println("runtime error");
    }

    /**
     * print map instance.
     * */
    public static void systemPrintMap(Map map){
        for (Object s: map.keySet()
        ) {
            if (s != null && map.get(s) != null) {
                systemPrint("储存在Map中的ID网址：" + s);
                HashSet nc = ((NewsContent)map.get(s)).getNewsContent();
                Iterator iterable = nc.iterator();
                String content = null;
                while (iterable.hasNext()) {
                    content += (iterable.next() + "\r\n");
                }
                systemPrint("key = " + s + " value = " + content);
            }else {
                systemPrint("发现空键值的map");
            }
        }
    }
}
