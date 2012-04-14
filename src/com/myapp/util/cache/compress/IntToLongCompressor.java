package com.myapp.util.cache.compress;

public class IntToLongCompressor {
    
    public static long pack(int x, int y) {
        long xPacked = ((long)x) << 32;
        long yPacked = y & 0xFFFFFFFFL;
        return xPacked | yPacked;
    }

    public static int unpackX(long packed) {
        return (int) (packed >> 32);
    }

    public static int unpackY(long packed) {
        return (int) (packed & 0xFFFFFFFFL);
    }



    public static void main(String[] args) {
        int x = -23664;
        int y = -6543;

        System.out.println("x=    "+Integer.toHexString(x));
        System.out.println("y=    "+Integer.toHexString(y));

        long pack = pack(x,y);
        System.out.println("pack= "+Long.toHexString(pack));

        int x2 = unpackX(pack);
        System.out.println("x2=   "+Integer.toHexString(x2));

        int y2 = unpackY(pack);
        System.out.println("y2=   "+Integer.toHexString(y2));

        testAll();
    }

    private static void testAll() {
        long pack, iteration = 0;
        int iTmp, jTmp;


        for (int i = -100000; i <= 100000; i++) {
            if (i % 100 == 0)
                System.out.println("i = "+i+ ", iterations = "+iteration);

            for (int j = -100000; j <= 100000; j++, iteration++) {
                pack = pack(i, j);
                iTmp = unpackX(pack);
                jTmp = unpackY(pack);

                if (i != iTmp || j != jTmp) {
                    throw new RuntimeException("i="+i+", j="+j);
                }
            }
        }
    }

}
