package com.myapp.videotools;

import java.io.File;


public interface IPathCalculator {

    String DEFAULT_THUMB_FILE_NAME_PREFIX = "thumb_"; // TODO: store this in videothumbnailer, make implementations dependent from videothumbnailer!
    String DEFAULT_BIG_PICTURE_FILE_NAME_PREFIX = ""; // TODO: store this in videothumbnailer, make implementations dependent from videothumbnailer!


    
    
    String getTargetFile(File source);

    boolean isFailOnOverwrite();
    String getFileNamePrefix();
    String getFileNameSuffix();
    
    void setFailOnOverwrite(boolean fail); 
    void setFileNamePrefix(String fileNamePrefix);
    void setFileNameSuffix(String suffix);
    


    
    

    /////////////////////////////////////
    //   DEFAULT IMPLEMENTATIONS BELOW
    /////////////////////////////////////
    

    
    
    abstract class PrefixSuffixPathCalculator implements IPathCalculator {

        private boolean failOnOverwrite = true;

        protected String prefix = "";
        protected String suffix = ".jpeg";
        

        @Override
        public final String getTargetFile(File source) {
            validateSource(source);
            String target = getTargetFileImpl(source);
            
            if (target == null || ( ! failOnOverwrite && new File(target).exists())) {
                throw new RuntimeException(target+" already exists! source="+source);
            }
            
            return target;
        }

        private void validateSource(File source) {
            if (source.isDirectory() 
                    || ! source.exists() 
                    || ! source.canRead()) {
                throw new IllegalStateException("illegal file:" + source);
            }
            validateSourceImpl(source);
        }
        
        
        protected abstract String getTargetFileImpl(File source);
        
        @SuppressWarnings("unused") // override hook
        protected void validateSourceImpl(File source) {}
        


        @Override
        public final boolean isFailOnOverwrite() {
            return failOnOverwrite;
        }
        @Override
        public final void setFailOnOverwrite(boolean fail) {
            this.failOnOverwrite = fail;
        }
        
        public final String getFileNamePrefix() {
            return prefix;
        }
        public final void setFileNamePrefix(String fileNamePrefix) {
            this.prefix = fileNamePrefix;
        }
        public final String getFileNameSuffix() {
            return suffix;
        }
        public final void setFileNameSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
    
    
    
    
    final class NextToSourceFile extends PrefixSuffixPathCalculator {
        
         @Override
         public String getTargetFileImpl(File source) {
             StringBuilder builder = new StringBuilder();
             builder.append(source.getParentFile().getAbsolutePath());
             
             if (builder.charAt(builder.length() - 1) != File.separatorChar)
                 builder.append(File.separatorChar);
        
             if (prefix != null)
                 builder.append(prefix);
        
             builder.append(source.getName());
        
             if (suffix != null)
                 builder.append(suffix);
        
             File target = new File(builder.toString());
             
             if ( ! isFailOnOverwrite() && target.exists())
                 throw new RuntimeException(target+" already exists! source="+source);
             
             return target.getAbsolutePath();
         }
    }
    
    
    
    
    final class FileHierarchyCopying extends PrefixSuffixPathCalculator {
         
         private String videoRootDir = null;
         private String targetRootDir = null;
         private boolean mkdirs = false;
         
         
         
         public FileHierarchyCopying(File videoRootDir, 
                                     File targetRootDir) {
             setTargetRootDirFile(targetRootDir);
             setVideoRootDirFile(videoRootDir);
         }
         
         public FileHierarchyCopying(File videoRootDir, 
                                     File targetRootDir, 
                                     boolean mkdirs) {
             setTargetRootDirFile(targetRootDir);
             setVideoRootDirFile(videoRootDir);
             this.mkdirs = mkdirs;
         }
        
         
         @Override
         public synchronized String getTargetFileImpl(File source) {
             String parent = source.getParentFile().getAbsolutePath();
             parent = parent.replace(videoRootDir, targetRootDir);
        
             StringBuilder builder = new StringBuilder();
             builder.append(parent);

             if (builder.charAt(builder.length() - 1) != File.separatorChar) {
                 builder.append(File.separatorChar);
             }
             
             builder.append(prefix != null ? prefix : "");
             builder.append(source.getName());
             builder.append(suffix != null ? suffix : "");
        
             File target = new File(builder.toString());
             
             if (mkdirs && ! target.getParentFile().exists()) {
                 boolean y = target.getParentFile().mkdirs();
                 if ( ! y) throw new RuntimeException(target.getParent()+" could not be created! source="+source);
             }
             
             return target.getAbsolutePath();
         }

         @Override
         protected void validateSourceImpl(File source) {
             if ( ! source.getAbsolutePath().contains(videoRootDir)) {
                 throw new IllegalStateException("illegal file:" + source);
             }
         }
         
        
         public void setTargetRootDirFile(File dir) {
             if ( ! (dir.isDirectory() && dir.canWrite()) ) {
                 throw new IllegalArgumentException("targetRootDirFile=" + dir);
             }
             targetRootDir = dir.getAbsolutePath();
         }
         public void setVideoRootDirFile(File dir) {
             if ( ! (dir.isDirectory() && dir.canRead()) ) {
                 throw new IllegalArgumentException("videoRootDirFile=" + dir);
             }
             videoRootDir = dir.getAbsolutePath();
         }
         public String getTargetRootDir() {
             return targetRootDir;
         }
         public String getVideoRootDir() {
             return videoRootDir;
         }
    }
}
