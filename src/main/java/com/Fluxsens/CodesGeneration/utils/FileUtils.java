package com.Fluxsens.CodesGeneration.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtils
{

    private static final Log LOG = LogFactory.getLog(FileUtils.class);

    public static boolean createDirectory(String path)
    {
        File directory = new File(path);
        if (!directory.exists())
        {
            try
            {
                directory.mkdir();
                return true;
            }
            catch (Exception se)
            {
                return false;
            }
        }
        return true;
    }

    public static byte[] convertingFileToByte(File file)
    {
        byte[] b = new byte[(int) file.length()];
        try
        {
            @SuppressWarnings("resource")
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
        }
        catch (FileNotFoundException e)
        {
            LOG.info("File Not Found : " + file.getAbsolutePath());
        }
        catch (IOException e1)
        {
            LOG.info("Error Reading The File.");
        }
        return b;
    }

    public static void moveFile(String sourcePath, String destinationPath)
    {
        InputStream inStream = null;
        OutputStream outStream = null;

        try
        {
            File afile = new File(sourcePath);
            File bfile = new File(destinationPath);

            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(bfile);

            byte[] buffer = new byte[1024];

            int length;
            // copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0)
            {
                outStream.write(buffer, 0, length);
            }

            inStream.close();
            outStream.close();

            // delete the original file
            afile.deleteOnExit();
            afile.delete();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static BufferedImage resizeImage(String imageName, final Image image, int width, int height)
    {
        String ext = imageName.split("\\.")[1];
        BufferedImage bufferedImage = null;
        if (ext.equals("png"))
        {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        else
        {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setBackground(Color.white);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    /**
     * @author rabindranath.s
     * @param path
     * @return {boolean}
     */
    public static boolean createDirs(String path)
    {
        try
        {
            File directory = new File(path);
            if (!directory.exists()) return directory.mkdirs();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

}
