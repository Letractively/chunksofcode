package com.myapp.util.cache.disk;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.codec.binary.Base64;
import com.myapp.util.file.FileUtils;
import com.myapp.util.format.Util;


public class ObjectStore<V extends Serializable>
{
    
    
    private static final String DEFAULT_NAME = ObjectStore.class.getName();
    private static final FileFilter filesOnlyFilter = new FileUtils.FileFileFilter();
    private static Format DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss.z");
    private static final Set<String> EMPTY = Collections.emptySet();
    
    private final String name;
    private Map<String, V> inMemCache = new HashMap<String, V>();
    private File containerDir;
    
    private final ReentrantLock lock = new ReentrantLock();
    


    
    public static <M extends Serializable> ObjectStore<M> empty() {
        return new ObjectStore<M>() {
            
            public void put(String key, Serializable value) { }
            public Set<String> keys() {  return EMPTY; }
            public M get(String key) { return null; }
        };
    }
    
    public ObjectStore() {
        this(DEFAULT_NAME);
    }
    
    public ObjectStore(String storeName) {
        this.name = storeName;
        
        String path = System.getProperty("user.home");
        path += File.separator;
        path += ".cache";
        File aFile = new File(path);

        if (aFile.exists()) {
            if ( ! aFile.isDirectory()) {
                throw new RuntimeException(path);
            }
        } else if ( ! aFile.mkdirs()) {
            throw new RuntimeException(path);
        }

        path += File.separator;
        path += "com.myapp.objectcache";
        path += File.separator;
        path += name;
        aFile = new File(path);
        
        if (aFile.exists()) {
            if ( ! aFile.isDirectory()) {
                throw new RuntimeException(path);
            }
            Util.log("ObjectStore.ObjectStore() dir found: "+aFile.getAbsolutePath(), Util.now());
        } else if (aFile.mkdirs()) {
            Util.log("ObjectStore.ObjectStore() dir created: "+aFile.getAbsolutePath(), Util.now());
        } else {
            throw new RuntimeException(path);
        }
        
        containerDir = aFile;
    }
    
    
    private void deletePersistentValue(String key) {
        File oldPersistentVal = getPersistentFile(key);
        
        if ( ! oldPersistentVal.delete()) 
            throw new RuntimeException(oldPersistentVal.getAbsolutePath());
        
//        System.out.println("ObjectStore.deletePersistentValue(" + key + ") file: " + oldPersistentVal.getAbsolutePath());
    }
    

    
    private void storePersistentValue(final String key, final V value) {
        final File targetFile = getPersistentFile(key);
        
        try {
            BackgroundFileSerializer<V> serializer;
            serializer = new BackgroundFileSerializer<V>(targetFile, value);
            Thread t = new Thread(serializer);
            String now = DATE_FORMAT.format(new Date());
            t.setName(this.name + " " + now);
            t.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(targetFile.getAbsolutePath(), e);
        }
    }
    
    private V loadObjectFromFile(File targetFile) {
        if ( ! targetFile.isFile()) {
            return null;
        }
        
        try {
            FileInputStream fis = new FileInputStream(targetFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            
            @SuppressWarnings("unchecked")
            V v = (V) o;
            
            // System.out.println("ObjectStore.loadObjectFromFile("+targetFile.getAbsolutePath()+")");
            return v;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(targetFile.getAbsolutePath(), e);
        }
    }
    
    private V loadPersistentValue(String key) {
        File targetFile = getPersistentFile(key);
        V v = loadObjectFromFile(targetFile);
        return v;
    }
    
    public void put(String key, V value) {
        try {
            lock.lock();
            
            if (inMemCache.containsKey(key)) {
                deletePersistentValue(key);
            }
    
            storePersistentValue(key, value);
            inMemCache.put(key, value);
        
        } finally {
            lock.unlock();
        }
    }
    
    public Set<String> keys() {
        File[] entityFiles = containerDir.listFiles(filesOnlyFilter);
        Set<String> allKeys = new HashSet<String>(entityFiles.length + inMemCache.size());
        
        for (int i = 0; i < entityFiles.length; i++) {
            File entityFile = entityFiles[i];
            String key = getMappingKey(entityFile);
            allKeys.add(key);
        }
        
        allKeys.addAll(inMemCache.keySet());
        return allKeys;
    }
    
    public V get(String key) {
        V v = inMemCache.get(key);

        if (v != null) return v;
        
        try {
            lock.lock();
            v = inMemCache.get(key);
            if (v != null) return v;
            
            v = loadPersistentValue(key);
            if (v != null) inMemCache.put(key, v);
            
            return v;
        
        } finally {
            lock.unlock();
        }
    }
    
    
    
    
    public static void main(String[] args) {
        String secret = "andre";
        System.out.println("ObjectStore.main() secret  = "+secret);
        String encodedSecret = encode(secret);
        System.out.println("ObjectStore.main() encoded = "+encodedSecret);
        String decodedSecret = decode(encodedSecret);
        System.out.println("ObjectStore.main() decoded = "+decodedSecret);
        
        ObjectStore<String> store = new ObjectStore<String>("test");
        store.put("programmer", "andre");
        store.put("tester", "anderer");
        store.put("nasen", "hans");

        String[] keys = { "programmer", "tester", "nasen" };
        
        for (int i = 0; i < keys.length; i++) {
            String value = store.get(keys[i]);
            System.out.println("key = " + keys[i] + ", val = " + value);
        }
    }
    
    
    
    

    
    private static String decode(String data) {
        byte[] dataBytes = Base64.decodeBase64(data);
        return new String(dataBytes).trim();
    }
    
    private static String encode(String data) {
        byte[] bytes = Base64.encodeBase64(data.getBytes(), false, true);
        String encoded = new String(bytes);
        return encoded;
    }
    
    @SuppressWarnings("unused")
    private static String getMappingKey(String fileName) {
        return decode(fileName);
    }
    
    private String getMappingKey(File entityFile) {
        return decode(entityFile.getName());
    }
    
    private static String getFileName(String mappingKey) {
        return encode(mappingKey);
    }
    
    private File getPersistentFile(String mappingKey) {
        String fileName = containerDir.getAbsolutePath();
        fileName += File.separator;
        fileName += getFileName(mappingKey);
        return new File(fileName);
    }
}

