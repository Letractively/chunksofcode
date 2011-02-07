package com.myapp.util.log.unixcolors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.pattern.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.PatternLayoutBase;


public class                UnixTerminalColorizedPatternLayout extends PatternLayoutBase<ILoggingEvent> {
    
    public static final class ColorizedMessageConverter extends MessageConverter {
        @Override
        public String convert(ILoggingEvent event) {
            String string = super.convert(event);
            return colorize(string, event);
        }
    }
    
    public static final class ColorizedLevelConverter extends LevelConverter {
        @Override
        public String convert(ILoggingEvent event) {
            String string = super.convert(event);
            return colorize(string, event);
        }
    }
    
    public static final class ColorizedLineOfCallerConverter extends LineOfCallerConverter {
        @Override
        public String convert(ILoggingEvent event) {
            String string = super.convert(event);
            return UnixStringColorizer.paintGreen(string);
        }
    }
    
    public static final class CustomClassOfCallerConverter extends ClassOfCallerConverter {
        @Override
        public String convert(ILoggingEvent event) {
            String string = super.convert(event);
            return UnixStringColorizer.paintBlack(string);
        }
    }
    
    
    
    private static final Map<String, String> converterMap;
    
    static {
        Map<String, String> m = new HashMap<String, String>(PatternLayout.defaultConverterMap);
        m.put("m",          ColorizedMessageConverter.class.getName());
        m.put("msg",        ColorizedMessageConverter.class.getName());
        m.put("message",    ColorizedMessageConverter.class.getName());

        m.put("level",      ColorizedLevelConverter.class.getName());
        m.put("le",         ColorizedLevelConverter.class.getName());
        m.put("p",          ColorizedLevelConverter.class.getName());

        m.put("L",          ColorizedLineOfCallerConverter.class.getName());
        m.put("line",       ColorizedLineOfCallerConverter.class.getName());
        
        converterMap = Collections.unmodifiableMap(m);
    }
    
    
    

    public UnixTerminalColorizedPatternLayout() {
      postCompileProcessor = new EnsureExceptionHandling();
    }
    
    
    

    @Override
    public Map<String, String> getDefaultConverterMap() {
        return converterMap;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if ( ! isStarted()) {
            return CoreConstants.EMPTY_STRING;
        }
        return writeLoopOnConverters(event);
    }
    
    private static String colorize(String s, ILoggingEvent e) {
        Level level = e.getLevel();
        
             if (level == Level.DEBUG) return UnixStringColorizer.paintYellow(s);
        else if (level == Level.TRACE) return UnixStringColorizer.paintCyan(s);
        else if (level == Level.WARN)  return UnixStringColorizer.paintRed(s);
        else if (level == Level.ERROR) return UnixStringColorizer.paintRed(s);
        else if (level == Level.INFO)  return UnixStringColorizer.paintBlue(s);

        return s;
    }
}
