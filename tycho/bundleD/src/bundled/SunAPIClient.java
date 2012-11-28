package bundled;

import java.awt.FontMetrics;
import java.awt.Graphics;

import sun.swing.SwingUtilities2;

public class SunAPIClient
{
    public void useSunAPI()
    {
        FontMetrics metrics = SwingUtilities2.getFontMetrics( null,(Graphics)null );
        System.out.println("Font metrics: " + metrics);
    }
}
