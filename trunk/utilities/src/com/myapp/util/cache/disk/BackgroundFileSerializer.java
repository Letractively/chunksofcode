package com.myapp.util.cache.disk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.myapp.util.format.Util;


class BackgroundFileSerializer<V extends Serializable> implements Runnable
{
    private static final String NL = System.getProperty("line.separator");
    
    private File targetFile;
    private V value;
    
    public BackgroundFileSerializer(final File targetFile, final V value) {
        this.value = value;
        this.targetFile = targetFile;
    }
    
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        
        try {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(
                  new BufferedOutputStream(
                  new FileOutputStream(targetFile, false)));
            oos.writeObject(value);
            oos.flush();
            oos.close();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        long end = System.currentTimeMillis();

        String time = Util.timespanStr(start,end,3);
        System.out.println("BackgroundFileSerializer.storePersistentValue() time: " + time +NL+
        		           targetFile.getName());
    }
}