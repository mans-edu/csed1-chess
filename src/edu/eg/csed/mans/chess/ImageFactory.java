package edu.eg.csed.mans.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageFactory {
    private static HashMap<String, Image> s_Images;
    private static ArrayList<String> s_InvalidPathnames;
    static { // static initializer
        s_Images = new HashMap<>();
        s_InvalidPathnames = new ArrayList<>();
    }

    public static Image GetImage(String pathname) {
        if (s_Images.containsKey(pathname)) {
            return s_Images.get(pathname);
        } else {
            // tried to load this image earlier
            // failed to load
            if (s_InvalidPathnames.contains(pathname)) {
                return null;
            }

            // loading the image for the first time
            var image = LoadImage(pathname);

            // error occurred while loading the image
            if (image == null) {
                s_InvalidPathnames.add(pathname);
                return null;
            }

            // put the image so that we don't have to load it later
            s_Images.put(pathname, image);
            return image;
        }
    }

    private static Image LoadImage(String pathname) {
        try {
            return ImageIO.read(new File(pathname));
        } catch (Throwable e) {
            return null;
        }
    }

}
