package org.daisy.printing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.DocAttributeSet;

/**
 * 
 * Printer device class of type DocFlavor.INPUT_STREAM.AUTOSENSE
 * 
 * This class can be used when sending a file to a printer.
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 jul 2008
 * @since 1.0
 */
public class PrinterDevice implements Device {
        private final static DocFlavor FLAVOR = DocFlavor.BYTE_ARRAY.AUTOSENSE;
//      private final static DocFlavor FLAVOR = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
        private PrintService service;
        
        /**
         * Create a device with the provided name.
         * @param deviceName the name of the device
         * @param fuzzyLookup If true, the returned device is any device whose name contains the 
         * supplied deviceName. If false, the returned device name equals the supplied deviceName. 
         * @throws IllegalArgumentException if no device is found.
         */
        public PrinterDevice(String deviceName, boolean fuzzyLookup) {
                PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
                for (PrintService p : printers) {
                        if (p.getName().equals(deviceName)) {
                                service = p;
                                return;
                        }
                }
                if (fuzzyLookup) {
                        PrintService match = null;
                        double currentMatch = 0;
                        for (PrintService p : printers) {
                                if (p.getName().contains(deviceName)) {
                                        double thisMatch = deviceName.length() / (double)p.getName().length();
                                        if (thisMatch > currentMatch) {
                                                currentMatch = thisMatch;
                                                match = p;
                                        }
                                }
                        }
                        if (match != null) {
                                service = match;
                                return;
                        }                       
                }
                throw new IllegalArgumentException("PrinterDevice: Could not find embosser.");
        }
        
        /**
         * List available devices
         * @return returns a list of available devices that accepts DocFlavor.INPUT_STREAM.AUTOSENSE 
         */
        public static PrintService[] getDevices() {
                PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
                return printers;
        }

        /**
         * Transmit a file to the device
         * @param file the file to transmit
         * @throws FileNotFoundException
         * @throws PrintException
         */
        public void transmit(File file) throws PrintException {
                try {
                        transmit(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                        throw new PrintException(e);
                }
        }
        
        private void transmit(InputStream is) throws PrintException {
                InputStreamDoc doc = new InputStreamDoc(is);
                DocPrintJob dpj = service.createPrintJob();
                dpj.print(doc, null);
        }

        private class InputStreamDoc implements Doc {
                private InputStream stream;
                
                /**
                 * Default constructor
                 * @param file
                 * @throws FileNotFoundException
                 */
                public InputStreamDoc(InputStream stream) {
                        this.stream = stream;
                }

                public DocAttributeSet getAttributes() {
                        return null;
                }

                public DocFlavor getDocFlavor() {
                        return FLAVOR;
                }

                public Object getPrintData() throws IOException {
                        return getStreamForBytes();
                }

                public Reader getReaderForText() throws IOException {
                        return null;
                }

                public InputStream getStreamForBytes() throws IOException {
                        return stream;
                }
        }
}
