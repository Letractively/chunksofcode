package com.myapp.util.xml;

import java.io.File;
import java.net.URI;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;



@SuppressWarnings("restriction")
public class XmlValidator {
    
    
    
    private static class MyErrorHandler implements ErrorHandler {
        
        private boolean _error = false;
        
        void reset() {
            _error = false;
        }
        boolean getFatal() {
            return _error;
        }
        public void error(SAXParseException e) throws SAXException {
            System.err.println("error: " + e);
            _error = true;
        }
        public void fatalError(SAXParseException e) throws SAXException {
            System.err.println("fatalError: " + e);
            _error = true;
        }
        public void warning(SAXParseException e) throws SAXException {
            System.err.println("warning: " + e);
        }
    }
    
    
    
    private SAXParser parser;
    private File schemaFile = null;
    private MyErrorHandler errorHandler = new MyErrorHandler();
    
    
    
    public XmlValidator() {
        try {
            parser = new SAXParser();
            parser.setFeature("http://xml.org/sax/features/" +
                              "validation", true);
            
            parser.setFeature("http://apache.org/xml/features/" +
                              "validation/schema", true);
            
            parser.setFeature("http://apache.org/xml/features/" +
                              "validation/schema-full-checking", true);

            parser.setErrorHandler(errorHandler);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public File getSchemaFile() {
        return schemaFile;
    }
    
    public void setSchemaFile(File schemaFile) throws SAXException {
        synchronized (this) {
            this.schemaFile = schemaFile;
            URI uri = schemaFile.toURI();
            // System.out.println("URI set to: " + uri);
            parser.setProperty("http://apache.org/xml/properties/schema/" +
                               "external-noNamespaceSchemaLocation",
                               uri.toString());
        }
    }
    
    public boolean validate(File xmlFile) {
        try {
            return validateImpl(xmlFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private boolean validateImpl(File xmlFile) throws Exception {
        synchronized (this) {
            errorHandler.reset();
            parser.parse(xmlFile.toURI().toString());
            boolean fatalErrorsOccured = errorHandler.getFatal();
            return  ( ! fatalErrorsOccured);
        }
    }
    
    public static void main(String[] args) throws Exception {
        String path = "/media/personal/workspace/new/playground/tool-utils/test-resources/";
        
        XmlValidator validator = new XmlValidator();
        File xsd = new File(path + "test.xsd");
        validator.setSchemaFile(xsd);
        
        File xmlOk = new File(path + "test.xml");
        boolean ok = validator.validate(xmlOk);
        System.out.println("test.xml valid:               " + ok);
        
        File xmlInvalid = new File(path + "test-negative.xml");
        ok = validator.validate(xmlInvalid);
        System.out.println("test-negative.xml valid:      " + ok);
    }
}
