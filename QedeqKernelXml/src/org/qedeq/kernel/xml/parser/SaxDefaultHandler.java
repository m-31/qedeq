/* $Id: SaxDefaultHandler.java,v 1.1 2008/07/26 08:00:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
 *
 * "Hilbert II" is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package org.qedeq.kernel.xml.parser;

import java.util.Stack;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourcePosition;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Default SAX handler. Delegates SAX events to a
 * {@link org.qedeq.kernel.xml.parser.AbstractSimpleHandler}
 * which could also delegate events to other
 * {@link org.qedeq.kernel.xml.parser.AbstractSimpleHandler}s.
 * <p>
 * Before anything is parsed the method {@link #setExceptionList(DefaultSourceFileExceptionList)}
 * must be called.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class SaxDefaultHandler extends SimpleHandler {

    /** This class. */
    private static final Class CLASS = SaxDefaultHandler.class;

    /** Delegate currently to this handler. */
    private AbstractSimpleHandler currentHandler;

    /** Stack of previous {@link AbstractSimpleHandler}s. */
    private Stack handlerStack = new Stack();

    /** Top level handler. This handler is activated after the begin of the document. */
    private AbstractSimpleHandler basisHandler;

    /** Collect errors in this object. */
    private DefaultSourceFileExceptionList errorList;

    /** Buffer for combining character events. */
    private StringBuffer buffer = new StringBuffer(2000);

    /** Tag level for current handler. */
    private int level;

    /** Tag level for previous handlers. */
    private Stack levelStack = new Stack();

    /** Current tag name. Could be <code>null</code>. */
    private String currentElementName;

    /**
     * Constructor.
     */
    public SaxDefaultHandler() {
        super();
    }

    /**
     * Set parse exception list. This list collects occurring parsing errors.
     *
     * @param   errorList  Collect errors here.
     */
    public void setExceptionList(final DefaultSourceFileExceptionList errorList) {
        this.errorList = errorList;
    }

    /**
     * Set basis handler for documents.
     *
     * @param   handler Basis handler for documents. This handler might also pass control to
     * another handler via the
     * {@link AbstractSimpleHandler#changeHandler(AbstractSimpleHandler, String, SimpleAttributes)}
     * method.
     */
    public final void setBasisDocumentHandler(final AbstractSimpleHandler handler) {
        basisHandler = handler;
        currentHandler = handler;
        handlerStack.clear();
        level = 0;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public final void startDocument() throws SAXException {
        sendCharacters();
        currentHandler = basisHandler;
        handlerStack.clear();
        level = 0;
        currentElementName = null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public final void endDocument() throws SAXException {
        sendCharacters();
        currentElementName = null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String,
     * java.lang.String, org.xml.sax.Attributes)
     */
    public final void startElement(final String uri, final String localName, final String qName,
            final Attributes amap) throws SAXException {
        final String method = "startElement";
        try {
            Trace.param(CLASS, this, method, "currentHandler", currentHandler.getClass().getName());
            Trace.param(CLASS, this, method, "localName", localName);
            Trace.param(CLASS, this, method, "qName", qName);
            if (handlerStack.empty() && level == 0) {
                currentHandler.init();
            }
            level++;
            Trace.param(CLASS, this, method, "level", level);
            sendCharacters();
            currentElementName = localName;
            final SimpleAttributes attributes = new SimpleAttributes();
            for (int i = 0; i < amap.getLength(); i++) {
                attributes.add(amap.getQName(i), amap.getValue(i));
            }
            Trace.param(CLASS, this, method, "attributes", attributes);
            currentHandler.startElement(qName, attributes);
        } catch (XmlSyntaxException e) {
            Trace.trace(CLASS, this, method, e);
            setLocationInformation(e);
            errorList.add(new SourceFileException(e, createSourceArea(), null));
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
            final XmlSyntaxException ex = XmlSyntaxException.createByRuntimeException(e);
            setLocationInformation(ex);
            final SourceFileException sfe = new SourceFileException(ex.getErrorCode(),
                ex.getMessage(), ex,
                createSourceArea(),
                null);
            errorList.add(sfe);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public final void endElement(final String uri, final String localName, final String qName)
            throws SAXException {
        sendCharacters();
        final String method = "endElement";
        try {
            Trace.param(CLASS, this, method, "currentHandler", currentHandler.getClass().getName());
            Trace.param(CLASS, this, method, "localName", localName);
            currentHandler.endElement(localName);
        } catch (XmlSyntaxException e) {
            Trace.trace(CLASS, this, method, e);
            setLocationInformation(e);
            errorList.add(new SourceFileException(e, createSourceArea(), null));
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
            final XmlSyntaxException ex = XmlSyntaxException.createByRuntimeException(e);
            setLocationInformation(ex);
            errorList.add(new SourceFileException(ex, createSourceArea(), null));
        }
        try {
            currentElementName = null;
            level--;
            Trace.param(CLASS, this, method, "level", level);
            if (level <= 0) {
                restoreHandler(localName);
            }
        } catch (XmlSyntaxException e) {
            Trace.trace(CLASS, this, method, e);
            setLocationInformation(e);
            final SourceFileException sfe = new SourceFileException(e.getErrorCode(),
                e.getMessage(), e,
                createSourceArea(),
                null);
            errorList.add(sfe);
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
            final XmlSyntaxException ex = XmlSyntaxException.createByRuntimeException(e);
            setLocationInformation(ex);
            errorList.add(new SourceFileException(ex, createSourceArea(), null));
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public final void characters(final char[] ch, final int start, final int length) {
        buffer.append(ch, start, length);
    }

    /**
     * Sends <code>characters</code> event to current handler.
     */
    private void sendCharacters() {
        try  {
            if (buffer.length() > 0) {
                final String str = buffer.toString().trim();
                buffer.setLength(0);
                if (str.length() > 0) {
                    currentHandler.characters(currentElementName, str);
                }
            }
        } catch (XmlSyntaxException e) {
            Trace.trace(CLASS, this, "sendCharacters", e);
            setLocationInformation(e);
            errorList.add(new SourceFileException(e, createSourceArea(), null));
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, "sendCharacters", e);
            final XmlSyntaxException ex = XmlSyntaxException.createByRuntimeException(e);
            setLocationInformation(ex);
            errorList.add(new SourceFileException(ex, createSourceArea(), null));
        }
    }

    /**
     * Change current handler to new one. The new handler is initialized by calling
     * {@link AbstractSimpleHandler#init()}.
     * The new handler also gets a {@link AbstractSimpleHandler#startElement(String,
     * SimpleAttributes)} event.
     * The current handler is stacked. After the new handler gets the appropriate endElement
     * event, the control is switched back to the old handler.
     * <p>
     * The switch back is also done, if the tag level gets back to the same number. That means
     * if for example the new handler starts with the <code>&lt;banana&gt;</code> tag, the
     * old handler is restored when the misspelled <code>&lt;/bnana&gt</code> tag occurs:
     * <p>
     * <pre>
     * &lt;banana&gt;
     *      &lt;one /&gt;
     *      &lt;two &gt;
     *          &lt;one /&gt;
     *          &lt;one /&gt;
     *      &lt;/two &gt;
     * &lt;/bnana&gt
     * </pre>
     *
     * @param  newHandler  This handler gets the new events.
     * @param  elementName Element name.
     * @param  attributes  Element attributes.
     * @throws XmlSyntaxException   New Handler detected a semantic problem.
     */
    public final void changeHandler(final AbstractSimpleHandler newHandler,
            final String elementName, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        handlerStack.push(currentHandler);
        levelStack.push(new Integer(level));
        currentHandler = newHandler;
        level = 0;
        level++;
        Trace.param(CLASS, this, "changeHandler", "level", level);
        currentHandler.init();
        currentHandler.startElement(elementName, attributes);
    }

    /**
     * Restore previous handler if there is any. An endElement event is also send to the restored
     * handler.
     *
     * @param   elementName
     * @throws  XmlSyntaxException
     */
    private final void restoreHandler(final String elementName) throws XmlSyntaxException {
        while (level <= 0 && !handlerStack.empty()) {
            currentHandler = (AbstractSimpleHandler) handlerStack.pop();
            Trace.param(CLASS, this, "restoreHandler", "currentHandler", currentHandler);
            level = ((Integer) levelStack.pop()).intValue();
            currentHandler.endElement(elementName);
            level--;
            Trace.param(CLASS, this, "restoreHandler", "level", level);
        }
        if (handlerStack.empty()) {
            Trace.trace(CLASS, this, "restoreHandler", "no handler to restore");
        }
    }

    /**
     * Get current level.
     *
     * @return  Current level.
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Wraps exception in new {@link SAXParseException} including parsing position information.
     *
     * @param   e   Exception to wrap.
     * @return  Exception to throw.
     */
    public final SAXParseException createSAXParseException(final Exception e) {
        return new SAXParseException(null, getLocator(), e);
    }

    /**
     * Creates new {@link SAXParseException} including parsing position information.
     *
     * @param   message Problem description.
     * @return  Exception to throw.
     */
    public final SAXParseException createSAXParseException(final String message) {
        return new SAXParseException(message, getLocator());
    }

    /**
     * Set current location information within an {@link XmlSyntaxException}.
     *
     * @param   e   Set location information within this exception.
     */
    private final void setLocationInformation(final XmlSyntaxException e) {
        if (getLocator() != null && getUrl() != null) {
            e.setErrorPosition(new SourcePosition(getUrl(), getLocator().getLineNumber(),
                getLocator().getColumnNumber()));
        }
    }

    /**
     * Create current source area.
     *
     * @return  Current area.
     */
    private final SourceArea createSourceArea() {
        if (getLocator() != null && getUrl() != null) {
            return new SourceArea(getUrl(), new SourcePosition(getUrl(),
                getLocator().getLineNumber(), 1),
                new SourcePosition(getUrl(), getLocator().getLineNumber(),
                getLocator().getColumnNumber()));
        }
        return new SourceArea(getUrl(), new SourcePosition(getUrl(), 1 , 1),
            new SourcePosition(getUrl(), 1 , 1));
    }

}
