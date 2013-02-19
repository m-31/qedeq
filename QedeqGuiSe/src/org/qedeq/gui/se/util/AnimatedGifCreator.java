package org.qedeq.gui.se.util;

// 
//  GifSequenceWriter.java
//  
//  Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.ImageIcon;

import org.qedeq.base.io.IoUtility;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import furbelow.AnimatedIcon;

public class AnimatedGifCreator {

    private String firstIconName;
    private String secondIconName;
    private BufferedImage firstIcon;
    private BufferedImage secondIcon;
    protected ImageWriter writer;
    protected ImageWriteParam writeParam;
    protected IIOMetadata imageMetaData;

    /**
     * Creates a new AnimatedGifCreator.
     *
     * @param   outputStream    Write resulting data herein.
     * @param   delay   Time between frames in milliseconds.
     * @param   repeat  Should the animation repeat from the beginning?
     */
    public AnimatedGifCreator(final ImageOutputStream outputStream, final String firstIconName,
                    final String secondIconName,
                    final int delay, final boolean repeat) throws IOException {
        writer = ImageIO.getImageWritersByFormatName("gif").next();
        writeParam = writer.getDefaultWriteParam();
        this.firstIconName = firstIconName;
        this.secondIconName = secondIconName;
        firstIcon = getImage(firstIconName);
        secondIcon = getImage(secondIconName);
//        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier
//            .createFromBufferedImageType(firstIcon.getType());
        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier
//            .createFromBufferedImageType(BufferedImage.TYPE_3BYTE_BGR);
            .createFromBufferedImageType(BufferedImage.TYPE_4BYTE_ABGR);

        imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, writeParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode gce = getCreateNode(root, "GraphicControlExtension");

        gce.setAttribute("disposalMethod", "none");
        gce.setAttribute("userInputFlag", "FALSE");
        gce.setAttribute("transparentColorFlag", "FALSE");
        gce.setAttribute("delayTime", Integer.toString(delay / 10));
        gce.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode appEntensionsNode = getCreateNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int l = (repeat ? 0 : 1);

        child.setUserObject(new byte[] {0x1, (byte) (l & 0xFF), (byte) ((l >> 8) & 0xFF) });
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);
        writer.setOutput(outputStream);
        writer.prepareWriteSequence(null);
    }

    private IIOMetadataNode getCreateNode(final IIOMetadataNode rootNode, final String name) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (name.compareToIgnoreCase(rootNode.item(i).getNodeName()) == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(name);
        rootNode.appendChild(node);
        return node;
    }

    private BufferedImage getImage(final String name) throws IOException {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
//        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.TRANSLUCENT);
//        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.BITMASK);
        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.TRANSLUCENT);
        Graphics2D gImg = (Graphics2D) image.getGraphics();
        URL resource = AnimatedGifCreator.class
                        .getResource("/images/qedeq/16x16/" + name);
        Image originalImage = ImageIO.read(resource);
        gImg.setColor(Color.white);
        gImg.fillRect(0, 0, 16, 16);
        gImg.drawImage(originalImage, 0, 0, 16, 16, null);
        gImg.dispose();
        return image;
    }

    private BufferedImage getImage2(final String name) throws IOException {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.TRANSLUCENT);
//        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.BITMASK);
//        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.OPAQUE);
        Graphics2D gImg = (Graphics2D) image.getGraphics();
        URL resource = AnimatedGifCreator.class
                        .getResource("/images/qedeq/16x16/" + name);
        Image originalImage = ImageIO.read(resource);
//        gImg.setColor(Color.white);
//        gImg.fillRect(0, 0, 16, 16);
        gImg.drawImage(originalImage, 0, 0, 16, 16, null);
        gImg.dispose();
        return image;
    }

    private BufferedImage getEmptyImage() throws IOException {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(16, 16, Transparency.TRANSLUCENT);
        return image;
    }

    public void writeSequence() throws IOException {
        writer.writeToSequence(new IIOImage(getImage(firstIconName), null, imageMetaData), writeParam);
        int len = 10;
        for (int i = 1; i < len; i++) {
            writeFrame(0.0f + (float) i / len);
        }
        for (int i = 1; i < len; i++) {
            writeFrame(1.0f - (float) i / len);
        }
        writer.endWriteSequence();

    }

    private void writeFrame(final float alpha) throws IOException {
        BufferedImage nextImage = getEmptyImage();

        Graphics g = nextImage.getGraphics();
        int dx = 0, dy = 0;
        Graphics2D gFade = (Graphics2D) g.create();
        gFade.setColor(Color.white);
        gFade.setComposite(AlphaComposite.Src);
        gFade.fillRect(0, 0, 16, 16);

        AlphaComposite newComposite1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        gFade.setComposite(newComposite1);
        gFade.drawImage(getImage2(firstIconName), dx, dy, null);
        gFade.setComposite(AlphaComposite.Src);
        AlphaComposite newComposite2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        gFade.setComposite(newComposite2);
//        gFade.drawImage(secondIcon, 8, 8, null);
//        gFade.drawImage(getImage2(secondIconName), 8, 8, null);
        gFade.drawImage(getImage2(secondIconName),
            2, 2,
            12, 12, null);
        gFade.dispose();
        writer.writeToSequence(new IIOImage(nextImage, null, imageMetaData), writeParam);
    }

    public static AnimatedIcon createAnimatedIcon(final String firstIconName,
            final String secondIconName) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIcon icon = new ImageIcon(output.toByteArray());
        try {
            AnimatedGifCreator writer = new AnimatedGifCreator(
                new MemoryCacheImageOutputStream(output), firstIconName,
                secondIconName, 10, true);
            writer.writeSequence();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IoUtility.close(output);
        return new AnimatedIcon(icon);
    }
    public static void main(final String[] args) throws Exception {
        // create a new BufferedOutputStream with the last argument
        ImageOutputStream output = new FileImageOutputStream(new File("output.gif"));
        AnimatedGifCreator writer = new AnimatedGifCreator(output, "module_checked2.gif",
             "module_checked.gif", 10, true);
        writer.writeSequence();
        output.close();
    }

    /*
     * public byte[] createImage() throws Exception {
     *
     * ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();
     *
     * ByteArrayOutputStream os = new ByteArrayOutputStream(); ImageOutputStream
     * ios = ImageIO.createImageOutputStream(os); iw.setOutput(ios);
     * iw.prepareWriteSequence(null); int i = 0;
     *
     * for (AnimationFrame animationFrame : frameCollection) {
     *
     * BufferedImage src = animationFrame.getImage(); ImageWriteParam iwp =
     * iw.getDefaultWriteParam(); IIOMetadata metadata =
     * iw.getDefaultImageMetadata( new ImageTypeSpecifier(src), iwp);
     *
     * configure(metadata, "" + animationFrame.getDelay(), i);
     *
     * IIOImage ii = new IIOImage(src, null, metadata); iw.writeToSequence(ii,
     * null); i++; }
     *
     * iw.endWriteSequence(); ios.close(); return os.toByteArray(); }
     */
}
