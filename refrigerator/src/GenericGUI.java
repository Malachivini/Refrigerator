import javax.swing.JFrame;

public abstract class GenericGUI {
    protected JFrame frame;
    protected String frameName;
    protected int frameWidth;
    protected int frameHeight;

    public GenericGUI(String frameName, int frameWidth, int frameHeight) {
        this.frameName = frameName;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frame = new JFrame(frameName);
        this.frame.setSize(frameWidth, frameHeight);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public abstract void load();
    public abstract void show();
    public abstract void close();
    public abstract void save();
}
