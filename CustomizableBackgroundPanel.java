import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CustomizableBackgroundPanel extends JPanel {

    public enum BackgroundMode { SOLID, GRADIENT, IMAGE }

    private BackgroundMode mode = BackgroundMode.SOLID;
    private Color solidColor = Color.WHITE;
    private Color gradientStart = Color.WHITE;
    private Color gradientEnd = Color.LIGHT_GRAY;
    private Image backgroundImage;
    private float imageOpacity = 1.0f; // 0.0 (invisible) - 1.0 (fully opaque)

    public CustomizableBackgroundPanel() {
        setOpaque(false); // we handle painting ourselves
    }

    // --- Setters used to customize the background at runtime ---

    public void setSolidColor(Color color) {
        this.solidColor = color;
        this.mode = BackgroundMode.SOLID;
        repaint();
    }

    public void setGradient(Color start, Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        this.mode = BackgroundMode.GRADIENT;
        repaint();
    }

    public void setBackgroundImage(String imagePath) throws IOException {
        this.backgroundImage = new ImageIcon(new File(imagePath).getAbsolutePath()).getImage();
        this.mode = BackgroundMode.IMAGE;
        repaint();
    }

    public void setImageOpacity(float opacity) {
        this.imageOpacity = Math.max(0f, Math.min(1f, opacity));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int width = getWidth();
        int height = getHeight();

        switch (mode) {
            case SOLID:
                g2d.setColor(solidColor);
                g2d.fillRect(0, 0, width, height);
                break;

            case GRADIENT:
                GradientPaint gp = new GradientPaint(0, 0, gradientStart, 0, height, gradientEnd);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
                break;

            case IMAGE:
                if (backgroundImage != null) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, imageOpacity));
                    g2d.drawImage(backgroundImage, 0, 0, width, height, this);
                }
                break;
        }

        g2d.dispose();
    }
}
