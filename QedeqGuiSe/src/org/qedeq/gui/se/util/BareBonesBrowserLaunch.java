package org.qedeq.gui.se.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import org.qedeq.base.utility.StringUtility;
import org.qedeq.base.utility.YodaUtility;

/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application
 * in the user's default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code>
 * Latest Version: <a href="http://www.centerkey.com/java/browser/">www.centerkey.com/java/browser</a><br>
 * @author: Dem Pilafian
 * Public Domain Software -- Free to Use as You Like
 * @version 3.1, June 6, 2010
 */
public final class BareBonesBrowserLaunch {

   /** Browser list. */
   private static final String[] BROWSERS = {"google-chrome", "firefox", "opera",
      "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };

   /** Error message. */
   private static final String ERR_MSG = "Error attempting to launch web browser";

   /**
    * Constructor.
    */
   private BareBonesBrowserLaunch() {
       // nothing to do
   }

    /**
     * Opens the specified web page in the user's default browser.
     *
     * @param url A web address (URL) of a web page (ex: "http://www.google.com/")
     */
    public static void openURL(final String url) {
        System.out.println("openURL"); // FIXME remove me
        if (YodaUtility.existsMethod("java.awt.Desktop", "browse", new Class[] {java.net.URI.class})) {
            try {  //attempt to use Desktop library from JDK 1.6+
                Class d = Class.forName("java.awt.Desktop");
                d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke(
                    d.getDeclaredMethod("getDesktop", new Class[] {}).invoke(null, new Class[] {}),
                    new Object[] {java.net.URI.create(url)});
                return;
                //above code mimicks:  java.awt.Desktop.getDesktop().browse()
            } catch (RuntimeException ignore) {
               //library not available or failed
            } catch (ClassNotFoundException ignore) {
                // ignore
            } catch (IllegalAccessException ignore) {
                // ignore
            } catch (InvocationTargetException ignore) {
                // ignore
            } catch (NoSuchMethodException ignore) {
                // ignore
            }
       }
       final String osName = System.getProperty("os.name");
       try {
           if (osName.startsWith("Mac OS")) {
               Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                  "openURL", new Class[] {String.class}).invoke(null,
                  new Object[] {url});
           } else if (osName.startsWith("Windows")) {
               Runtime.getRuntime().exec(
                  "rundll32 url.dll,FileProtocolHandler " + url);
           } else { //assume Unix or Linux
               String browser = null;
               for (int i = 0; i < BROWSERS.length; i++) {
                   String b = BROWSERS[i];
                   if (browser == null && Runtime.getRuntime().exec(new String[]
                        {"which", b}).getInputStream().read() != -1) {
                      Runtime.getRuntime().exec(new String[] {b, url});
                      browser = b;
                   }
               }
               if (browser == null) {
                   throw new Exception(StringUtility.toString(BROWSERS));
               }
            }
       } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ERR_MSG + "\n" + e.toString());
         }
      }

   }
