package tv.esporter.lurkerstats.util;

import intentbuilder.IntentBuilder;

public class Build {

    public static IntentBuilder intent(String name){
        return new IntentBuilder().action(name);
    }

    private static final String SEPARATOR = "#";

    public static String key(Object... keys){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<keys.length;i++){
            if (i>0) sb.append(SEPARATOR);
            sb.append(keys[i]);
        }

        return sb.toString();
    }

}
