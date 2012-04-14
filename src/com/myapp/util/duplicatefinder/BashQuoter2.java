package com.myapp.util.duplicatefinder;

class BashQuoter2 {
    public String quoteForBash(String path) { 
        // »/tmp/foo/rock'n roll.mp3«   -->  »'/tmp/foo/rock'"'"'n roll.mp3'«
        return "'" + path.replaceAll("[']", "'\"'\"'") + "'";
    }
    
    public static void main(String[] args) {
        String orig ="/media/datadisk/sound//Brand Nubian - Steal Ya 'Ho.mp3";
        System.out.println("BashQuoter2.main() "+new BashQuoter2().quoteForBash(orig)); 
    }
}