package ws.ragg.webapp.fileexchange.servlet;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

@SuppressWarnings("serial")
class Util {

    private static abstract class HumanReadableFormat extends Format {
        
        private final String[] sizePrefixes;
        private final double prefixChangeFactor;

        private HumanReadableFormat(String[] sizePrefixes, double factor) {
            this.sizePrefixes = sizePrefixes;
            this.prefixChangeFactor = factor;
        }

        @Override
        public StringBuffer format(Object o, StringBuffer b, FieldPosition pos) {
            Long l;
            if (o instanceof Long) {
                l = (Long) o;
            } else if (o instanceof File) {
                l = ((File)o).length();
            } else {
                throw new IllegalArgumentException(o.toString());
            }
            
            double len = l;
            int suffixIndex = -1;
            
            for (int i = 0; i < sizePrefixes.length; i++) {
                if (len < prefixChangeFactor) {
                    break;
                }
                len = (len / prefixChangeFactor);
                suffixIndex++;
            }
            String suffix = "";
            if (suffixIndex >= 0 && suffixIndex < sizePrefixes.length ) {
                suffix = sizePrefixes[suffixIndex];
            }
            
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            DecimalFormat fmt = new DecimalFormat("0.#", symbols);
            
            b.append(fmt.format(len));
            if (len < 10.0 && ! suffix.isEmpty()) {
                b.append(symbols.getDecimalSeparator()); 
                b.append("0");
            }
            
            b.append(suffix);
            b.append("B");
            return b;
        }
        
        @Override
        public Object parseObject(String s, ParsePosition p) {
            throw new UnsupportedOperationException("feature not implemented");
        }
    }
    
    public static final class MetricByteLengthFormatter extends HumanReadableFormat {
        public MetricByteLengthFormatter() {
            super(new String[] {"K","M","G","T","P","E"}, 1000.0D);
        }
    }
    
    public static final class BinaryLengthFormat extends HumanReadableFormat {
        public BinaryLengthFormat() {
            super(new String[]{"Ki","Mi","Gi","Ti","Pi","Ei"}, 1024.d);
        }
    }
    
    
    public static String getHumanReadableFileSize(File f) {
        return getHumanReadableFileSize(f.length());
    }

    public static String getHumanReadableFileSize(final long lengthInBytes) {
        return new Util.BinaryLengthFormat().format(lengthInBytes);
    }

    public static void main(String[] args) {
        long[] test = {
                   0,   //    0 
                   1,   //    1 
                 999,   //  999 
                1023,   // 1,0K 
                1024,   // 1,0K 
                1025,   // 1,0K 
             1048576,   // 1,0M 
             2097152,   // 2,0M 
             3145728,   // 3,0M 
             4194304,   // 4,0M 
            10485760,   //  10M 
            20971520,   //  20M        
        };
        for (Long i : test) {
            System.out.print("Util.main() i = ");
            for (int j=10-i.toString().length(); j-- >0; System.out.print(" "));
            System.out.println(i.toString()+ " ---> "+getHumanReadableFileSize(i));
        }
    }
}
