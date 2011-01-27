package org.qedeq.kernel.xml.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.xerces.parsers.SAXParser;
import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.StringUtility;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlNormalizerHandler extends DefaultHandler {
    
    private Locator locator;

    private Map namespaces = new HashMap();

    private StringBuffer buffer = new StringBuffer();

    private StringBuffer tabs = new StringBuffer();

    final SAXParser parser = new SAXParser();
        
    Stack hasSubs = new Stack();

    private OutputStream os;

    private PrintStream ps;


    public final void parse(final InputStream is, final OutputStream os) throws SAXException, IOException {
        this.os = os;
        ps = new PrintStream(os, false, "UTF-8");
        parser.setContentHandler(this);
        parser.parse(new InputSource(is));
    }

    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException {
        tabs.setLength(0);
        namespaces.clear();
        hasSubs.clear();
        hasSubs.push(new Enumerator());
    }

    public void endDocument() throws SAXException {
    }

    /**
     * Receive notification of the start of a Namespace mapping.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the start of
     * each Namespace prefix scope (such as storing the prefix mapping).</p>
     *
     * @param prefix The Namespace prefix being declared.
     * @param uri The Namespace URI mapped to the prefix.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startPrefixMapping
     */
     public void startPrefixMapping (String prefix, String uri) {
        namespaces.put(prefix, uri);
        System.out.println(prefix + ":" + uri);
     }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (((Enumerator) hasSubs.peek()).getNumber() == 0) {
          println(">");
        }
        buffer.setLength(0);
        ((Enumerator) hasSubs.peek()).increaseNumber();
        hasSubs.push(new Enumerator());
        print(tabs.toString());
        print("<" + qName);
        int len = attributes.getLength();
        if (len > 0) {
            final SortedMap map = new TreeMap();
            for (int i = 0; i < len; i++) {
                map.put(attributes.getQName(i), attributes.getValue(i));
            }
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                print(" ");
                final Entry entry = (Entry) it.next();
                final String key = (String) entry.getKey();
                final String value = (String) entry.getValue();
                print(key);
                print("=\"");
                print(StringUtility.escapeXml(value));
                print("\"");
            }
        }
        tabs.append("  ");
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName)
            throws SAXException {
        tabs.setLength(tabs.length() - 2);
        if (buffer.length() > 0 && buffer.toString().trim().length() > 0) {
//            int i = 0;
//            while (buffer.charAt(i) == 10 || buffer.charAt(i) == 13) {
//                i++;
//            }
//            buffer.delete(0, i);
//            i = buffer.length() - 1;
//            while (buffer.charAt(i) == 10 || buffer.charAt(i) == 13) {
//                i--;
//            }
//            buffer.delete(i + 1, buffer.length());
            if (((Enumerator) hasSubs.peek()).getNumber() == 0) {
                println(">");
            }
            print(tabs.toString());
            print("  ");
            println(buffer.toString().trim());
            print(tabs.toString());
            println("</" + qName + ">");
            buffer.setLength(0);
        } else {
            if (((Enumerator) hasSubs.peek()).getNumber() == 0) {
                println("/>");
            } else {
                print(tabs.toString());
                println("</" + qName + ">");
            }
        }
        hasSubs.pop();
    }

    public void characters(char[] ch, int start, int length) {
        buffer.append(ch, start, length);
    }

    private void print(final String text) {
//        System.out.print(text);
        ps.print(text);
    }

    private void println(final String text) {
//        System.out.println(text);
        ps.println(text);
    }

}
