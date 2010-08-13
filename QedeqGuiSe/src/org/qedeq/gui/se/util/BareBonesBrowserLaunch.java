package org.qedeq.gui.se.util;

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
public class BareBonesBrowserLaunch {

   static final String[] browsers = { "google-chrome", "firefox", "opera",
      "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
   static final String errMsg = "Error attempting to launch web browser";

   /**
    * Opens the specified web page in the user's default browser
    * @param url A web address (URL) of a web page (ex: "http://www.google.com/")
    */
   public static void openURL(String url) {
       System.out.println("openURL"); // FIXME remove me
       if (YodaUtility.existsMethod("java.awt.Desktop", "browse", new Class[] {java.net.URI.class})) {
           try {  //attempt to use Desktop library from JDK 1.6+
               Class d = Class.forName("java.awt.Desktop");
               d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke(
                   d.getDeclaredMethod("getDesktop", new Class[] {}).invoke(null, new Class[] {}),
                   new Object[] {java.net.URI.create(url)});
               return;
               //above code mimicks:  java.awt.Desktop.getDesktop().browse()
          } catch (Exception ignore) {
              ignore.printStackTrace(System.out);    // FIXME
              //library not available or failed
          }
       }
       String osName = System.getProperty("os.name");
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
               for (int i = 0; i < browsers.length; i++) {
                   String b = browsers[i];
                   if (browser == null && Runtime.getRuntime().exec(new String[]
                        {"which", b}).getInputStream().read() != -1) {
                      Runtime.getRuntime().exec(new String[] {browser = b, url});
                   }
               }
               if (browser == null) {
                   throw new Exception(StringUtility.toString(browsers));
               }
            }
       } catch (Exception e) {
            JOptionPane.showMessageDialog(null, errMsg + "\n" + e.toString());
         }
      }

   }
