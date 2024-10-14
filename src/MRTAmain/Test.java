package MRTAmain;
import jade.domain.introspection.ACLMessage;

import java.lang.reflect.*;
import java.util.*;

public class Test {

    public static void main(String[] args) throws Exception {
        Map<Character, Method> methodMap = new HashMap<Character, Method>();

        Class[] cArg = new Class[3];
        cArg[0] = Integer.class; 
        cArg[1] = ACLMessage.class; 
        cArg[2] = Class.forName("java.lang.String");
       
        
        
        methodMap.put('h', Test.class.getMethod("showHelp", int.class, ACLMessage.class, String.class));
        methodMap.put('t', Test.class.getMethod("teleport"));

        char cmd = 'h';
        methodMap.get(cmd).invoke(null, 5, new ACLMessage(), "deneme");  // prints "Help"

        cmd = 't';
        methodMap.get(cmd).invoke(null);  // prints "teleport"

    }

    public static void showHelp(int num, ACLMessage msg, String deneme) {
        System.out.println(num);
    }

    public static void teleport() {
        System.out.println("teleport");
    }
}
