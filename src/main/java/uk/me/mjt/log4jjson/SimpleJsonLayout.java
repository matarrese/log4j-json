package uk.me.mjt.log4jjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Michael Tandy
 */
public class SimpleJsonLayout extends Layout {
    
    private final Gson gson = new GsonBuilder().create();
    private final String hostname = getHostname().toLowerCase();
    private final String username = System.getProperty("user.name").toLowerCase();

    @Override
    public String format(LoggingEvent le) {
        HashMap<String,Object> r = new HashMap();
        r.put("timestamp", le.timeStamp);
        r.put("date", new Date(le.timeStamp));
        r.put("hostname", hostname);
        r.put("username", username);
        r.put("level", le.getLevel().toString());
        r.put("thread", le.getThreadName());
        r.put("ndc",le.getNDC());
        r.put("classname", le.getLocationInformation().getClassName());
        r.put("filename", le.getLocationInformation().getFileName());
        r.put("linenumber", Integer.parseInt(le.getLocationInformation().getLineNumber()));
        r.put("methodname", le.getLocationInformation().getMethodName());
        r.put("message", le.getMessage());
        r.put("throwable", formatThrowable(le) );
        
        after(le,r);
        return gson.toJson(r)+"\n";
    }
    
    /**
     * Method called near the end of formatting a LoggingEvent in case users
     * want to override the default object fields. 
     * @param le the event being logged
     * @param r the map which will be output.
     */
    public void after(LoggingEvent le, Map<String,Object> r) {
        
    }
    
    /**
     * If a throwable is present, format it with newlines between stack trace
     * elements. Otherwise return null.
     */
    private String formatThrowable(LoggingEvent le) {
        if (le.getThrowableInformation() == null || 
                le.getThrowableInformation().getThrowable()==null)
            return null;
        
        return mkString(le.getThrowableStrRep(),"\n");
    }
    
    private String mkString(Object[] parts,String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i=0 ; ; i++) {
            sb.append(parts[i]);
            if (i==parts.length)
                return sb.toString();
            sb.append("\n");
        }
    }
    
    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {
        
    }
    
    private static String getHostname() {
        String hostname;
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostname = "Unknown, "+e.getMessage();
        }
        return hostname;
    }
    
}
