package com.myapp.videotools.cli;

import static com.myapp.videotools.IVideoThumbnailer.DEFAULT_BIG_PIC_COLS;/*{{{*/
import static com.myapp.videotools.IVideoThumbnailer.DEFAULT_BIG_PIC_ROWS;
import static com.myapp.videotools.IVideoThumbnailer.DEFAULT_THUMB_HEIGHT;
import static com.myapp.videotools.IVideoThumbnailer.DEFAULT_THUMB_WIDTH;
import static com.myapp.videotools.cli.Parameters.CMD_CAPTURE_FRAME;
import static com.myapp.videotools.cli.Parameters.CMD_CREATE_BIG_PICTURE;
import static com.myapp.videotools.cli.Parameters.CMD_CREATE_THUMB_SERIES;
import static com.myapp.videotools.cli.Parameters.FLAG_COLORED_OUTPUT;
import static com.myapp.videotools.cli.Parameters.FLAG_DEBUG_OUTPUT;
import static com.myapp.videotools.cli.Parameters.FLAG_HELP;
import static com.myapp.videotools.cli.Parameters.FLAG_RECURSIVE;
import static com.myapp.videotools.cli.Parameters.FLAG_TRACE_OUTPUT;
import static com.myapp.videotools.cli.Parameters.PARAM_BIG_PICTURE_COLS;
import static com.myapp.videotools.cli.Parameters.PARAM_BIG_PICTURE_ROWS;
import static com.myapp.videotools.cli.Parameters.PARAM_BIG_PIC_FILE_NAME_PREFIX;
import static com.myapp.videotools.cli.Parameters.PARAM_BIG_PIC_ROOT_DIR;
import static com.myapp.videotools.cli.Parameters.PARAM_IMAGE_HEIGTH;
import static com.myapp.videotools.cli.Parameters.PARAM_IMAGE_WIDTH;
import static com.myapp.videotools.cli.Parameters.PARAM_INPUT_FILE;
import static com.myapp.videotools.cli.Parameters.PARAM_OUTPUT_FILE;
import static com.myapp.videotools.cli.Parameters.PARAM_SERIES_COUNT;
import static com.myapp.videotools.cli.Parameters.PARAM_VIDEO_ROOT_DIR;
import static com.myapp.videotools.cli.Parameters.printParam;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.util.log.unixcolors.LogFileSelector;
import com.myapp.videotools.AppStatistics;
import com.myapp.videotools.IPathCalculator;
import com.myapp.videotools.IVideoFileParser;
import com.myapp.videotools.IVideoThumbnailer;
import com.myapp.videotools.StatisticsShutDownHook;
import com.myapp.videotools.Util;
import com.myapp.videotools.VideoFile;
import com.myapp.videotools.impl.FFMPEG;/*}}}*/



public final class CommandLineInterface {

    
    private static final int INVALID_ARGS_EXIT_VAL = -17;
    static final Logger log = LoggerFactory.getLogger(CommandLineInterface.class);
    static final String NL = System.getProperty("line.separator");

    
    private final StatisticsShutDownHook printStatsShutDownHook = new StatisticsShutDownHook();
    private Parameters params;
    private IVideoThumbnailer nailer = null;       
    private boolean shutDownVmOnExit = false;
    



    public static void main(String[] args) throws IOException {
        AppStatistics.getInstance().setApplicationStart();
        CommandLineInterface cli = new CommandLineInterface();
        cli.shutDownVmOnExit = true;
        cli.process(args);
    }
    
    public CommandLineInterface() {
    }

    public CommandLineInterface(String[] commandLineArgs) {
        process(commandLineArgs);
    }
    

    
    public void process(String[] commandLineArgs) {
        params = new Parameters(commandLineArgs);
        applyLogConfig();
        
        if (log.isDebugEnabled()) {
            log.debug("new CLI initiated with arguments: {}", Arrays.toString(commandLineArgs));
            log.debug(Util.getOsInfoString());
            FFMPEG.getInstance(); // create ffmpeg instance at program start
            FFMPEG.getInstance().printFfmpegDebugInfo();
        }
        
        if (params.isFlagSet(FLAG_HELP)) {
            printUsage(out);
            exit(0);
        }
        
        try {
            params.failIfEmpty();
            executeAction();
            
        } catch (IllegalArgumentException e) {
            log.error("INVALID ARGUMENTS ({}), call VideoCommandlineTool with '{}' to display a help text",e.getMessage(),FLAG_HELP);
            log.debug("stacktrace: ", e);
            exit(INVALID_ARGS_EXIT_VAL);
        
        } catch (Throwable e) {
            log.error(e.getMessage());
            log.debug("stacktrace: ", e);
            exit(INVALID_ARGS_EXIT_VAL);
        }
    }
    
    private void exit(int status) {
        if (shutDownVmOnExit)
            System.exit(status);
    }
    
    
    private void executeAction() {
        final String command = params.getFirstArgument();
        log.debug("  command: " + printParam(command));

        if (    params.isFirstArgument(FLAG_DEBUG_OUTPUT)
             || params.isFirstArgument(FLAG_TRACE_OUTPUT)) {
            printUsage(out);
            return;
        }

        if (params.isFirstArgument(CMD_CAPTURE_FRAME)){
            captureFrame();

        } else if (params.isFirstArgument(CMD_CREATE_THUMB_SERIES)) {
            createThumbnails();

        } else if (params.isFirstArgument(CMD_CREATE_BIG_PICTURE)) {
            createBigPicture();
        }
        
        throw new IllegalArgumentException("unknown command: "+command);
    }
    
    private void createBigPicture() {
        nailer = FFMPEG.getInstance().createVideoThumbnailer();
        params.applyWidthAndHeight(nailer);
        params.applyRowsAndCols(nailer);
        
        if (params.isFlagSet(FLAG_RECURSIVE)) {
            log.info("  creating big pictures recursively...");
            File vidRoot = params.readableDirAtParameter(PARAM_VIDEO_ROOT_DIR);
            nailer.setVideoRootDir(vidRoot);
            log.info("  video root dir: {}",vidRoot.getAbsolutePath());
            
            if (params.isFlagSet(PARAM_BIG_PIC_ROOT_DIR)) {
                File root = params.writableDirAtParameter(PARAM_BIG_PIC_ROOT_DIR);//FIXME
                IPathCalculator fhc = new IPathCalculator.FileHierarchyCopying(vidRoot, root);
                nailer.setPathCalculatingAlgorithm(fhc);
                params.applyBigPicPrefix(nailer);
                
            } else {
                nailer.setPathCalculatingAlgorithm(new IPathCalculator.NextToSourceFile());
                params.applyBigPicPrefix(nailer);
            }

            Runtime.getRuntime().addShutdownHook(printStatsShutDownHook);
            nailer.createBigPictureRecursively();
            
        } else {
            File inputFile= params.getReadableFileAt(PARAM_INPUT_FILE);
            File outputFile = params.getFileAt(PARAM_OUTPUT_FILE);
            log.info("  creating a big picture for video: '{}'", inputFile);
            log.info("  big picture target:               '{}'", outputFile);
            
            try {
                VideoFile videoFile = new VideoFile(inputFile);
                IVideoFileParser parser = FFMPEG.getInstance().createVideoFileParser();
                videoFile.parse(parser);
                nailer.setVideoFile(videoFile);
                nailer.createBigPicture(outputFile);
                
            } catch (IOException e) {
                log.error("could not read video info for file {} ({})",inputFile.getName(),e.getMessage());
                throw new RuntimeException(e);
            }
        }
        
        exit(0);
    }

    private void createThumbnails() {
        nailer = FFMPEG.getInstance().createVideoThumbnailer();
        params.applyWidthAndHeight(nailer);
        params.applyRowsAndCols(nailer);
        
        File inputFile= params.getReadableFileAt(PARAM_INPUT_FILE);
        log.info("  creating a set of thumbnails for video: '{}'",inputFile);
        
        File outputFile = params.getFileAt(PARAM_OUTPUT_FILE);
        log.info("  thumbnails output files:                '{}'",outputFile);
        
        int amount = params.getIntAt(PARAM_SERIES_COUNT);
        log.info("  number of thumbs to be created:         '{}'",amount);
        
        try {
            VideoFile videoFile = new VideoFile(inputFile);
            nailer.setVideoFile(videoFile);
            nailer.createThumbnailSeries(amount, outputFile);
            
        } catch (IOException e) {
            log.error("could not read video info for file {} ({})",inputFile.getName(),e.getMessage());
            throw new RuntimeException(e);
        }

        // TODO Auto-generated method stub
        throw new RuntimeException("not yet implemented");
    }

    private void captureFrame() {
        // TODO doc WHEN
        // TODO Auto-generated method stub
        throw new RuntimeException("not yet implemented");
    }
    
    /**package visible because testing*/
    void applyLogConfig() {
        if (params.isFlagSet(FLAG_TRACE_OUTPUT)) {
            if (params.isFlagSet(FLAG_COLORED_OUTPUT))
                LogFileSelector.setUnixColoredTraceLogLevel();
            else
                LogFileSelector.setTraceLogLevel();
        } else if (params.isFlagSet(FLAG_DEBUG_OUTPUT)) {
            LogFileSelector.setDebugLogLevel();
        } else {
            LogFileSelector.setDefaultLogLevel();
        }
    }
    
    private static void printUsage(PrintStream pOut) {
        String usage =
"USAGE of VideoCommandLineTool: "+NL+
// TODO: implement!
//NL+
//"*** CREATE A SINGLE FRAME SNAPSHOT IMAGE OF A VIDEOFILE: "+NL+//TODO offset parameter
//"    command: "+printParam(CMD_CAPTURE_FRAME)+NL+
//"      "+printParam(PARAM_INPUT_FILE)+       "          <file>  path of the videofile where we want to get a snapshot"+NL+
//"      "+printParam(PARAM_OUTPUT_FILE)+       "         <file>  name of the file where the captured image will be written to"+NL+
//"     ("+printParam(PARAM_IMAGE_HEIGTH)+ "              <pixel> height of the captured image, default: "+DEFAULT_THUMB_HEIGHT+" )"+NL+
//"     ("+printParam(PARAM_IMAGE_WIDTH)+ "               <pixel> width  of the captured image, default: "+DEFAULT_THUMB_WIDTH+" )"+NL+
//NL+
//NL+
//"*** CREATE A SERIES OF THUMBNAIL IMAGES OF A VIDEOFILE: "+NL+
//"    command: "+printParam(CMD_CREATE_THUMB_SERIES)+NL+
//"      "+printParam(PARAM_INPUT_FILE)+       "          <file>  path of the videofile where we want to get thumbs"+NL+
//"      "+printParam(PARAM_OUTPUT_FILE)+       "         <dir>   name of the directory where the thumbnails will be stored"+NL+
//"      "+printParam(PARAM_SERIES_COUNT)+"               <int>   number of thumbnail pictures to create. the program will"+NL+
//                      "                                         try to create thumbs in equal time periods."+NL+
//"     ("+printParam(PARAM_IMAGE_HEIGTH)+ "              <pixel> height of the thumbnails, default: "+DEFAULT_THUMB_HEIGHT+" )"+NL+
//"     ("+printParam(PARAM_IMAGE_WIDTH)+ "               <pixel> width of the thumbnails, default: "+DEFAULT_THUMB_WIDTH+" )"+NL+
//NL+
NL+
"*** CREATE A BIG PICTURE OF THUMBNAILS FOR A VIDEO FILE:"+NL+
"    command: "+printParam(CMD_CREATE_BIG_PICTURE)+""+NL+
"      "+printParam(PARAM_INPUT_FILE)+            "          <file>  path of the videofile where we want to get thumbs"+NL+
"      "+printParam(PARAM_OUTPUT_FILE)+            "         <file>  path of the file where the big picture will be stored"+NL+
"     ("+printParam(PARAM_BIG_PICTURE_ROWS)+"                <int>   rows of the thumbs grid, default: "+DEFAULT_BIG_PIC_ROWS+" )"+NL+
"     ("+printParam(PARAM_BIG_PICTURE_COLS)+   "             <int>   columns of the thumbs grid, default: "+DEFAULT_BIG_PIC_COLS+" )"+NL+
"     ("+printParam(PARAM_IMAGE_HEIGTH)+      "              <pixel> height of the thumbnails, default: "+DEFAULT_THUMB_HEIGHT+" )"+NL+
"     ("+printParam(PARAM_IMAGE_WIDTH)+      "               <pixel> width of the thumbnails, default: "+DEFAULT_THUMB_WIDTH+" )"+NL+
NL+
NL+
"*** RECURSE INTO A DIRECTORY OF VIDEOS AND"+NL+
"    CREATE A BIG PICTURE FOR EACH SUPPORTED VIDEO FILES:"+NL+
"    command: "+printParam(CMD_CREATE_BIG_PICTURE)+NL+
"      "+printParam(FLAG_RECURSIVE)+NL+
"      "+printParam(PARAM_VIDEO_ROOT_DIR)+             "        <dir>   root directory where the videos are stored"+NL+
"     ("+printParam(PARAM_BIG_PICTURE_ROWS)+"                   <int>   rows of the thumbs grid, default: "+DEFAULT_BIG_PIC_ROWS+" )"+NL+
"     ("+printParam(PARAM_BIG_PICTURE_COLS)+   "                <int>   columns of the thumbs grid, default: "+DEFAULT_BIG_PIC_COLS+" )"+NL+
"     ("+printParam(PARAM_IMAGE_HEIGTH)+      "                 <pixel> height of the thumbnails, default: "+DEFAULT_THUMB_HEIGHT+" )"+NL+
"     ("+printParam(PARAM_IMAGE_WIDTH)+      "                  <pixel> width of the thumbnails, default: "+DEFAULT_THUMB_WIDTH+" )"+NL+
"     ("+printParam(PARAM_BIG_PIC_ROOT_DIR)+                  " <dir>   directory where the big picture"+NL+
                           "                                            files will be stored. if not specified, "+NL+
                           "                                            the big-pictures will be stored "+NL+
                           "                                            in the same dir where the video file is )"+NL+
"     ("+printParam(PARAM_BIG_PIC_FILE_NAME_PREFIX)+       "    <str>   prefix of the big picture files "+NL+
                           "                                            default: "+IPathCalculator.DEFAULT_BIG_PICTURE_FILE_NAME_PREFIX+" )"+NL+
NL+
NL+
"*** LOG LEVELS: you may alter the log verbosity with flags:"+NL+
"      "+printParam(FLAG_TRACE_OUTPUT)+", "+printParam(FLAG_DEBUG_OUTPUT)+" ("+printParam(FLAG_COLORED_OUTPUT)+")"+NL+
"     ( you may edit logger configurations in ./conf/*.xml )"+NL+
"     ( you may add flag "+FLAG_COLORED_OUTPUT+" if you want to colorize the output, when "+FLAG_TRACE_OUTPUT+" is enabled. )"
        
        ;
        
        pOut.println(usage);
    }
}
