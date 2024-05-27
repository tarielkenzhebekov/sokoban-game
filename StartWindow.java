import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StartWindow {

    private final ButtonGroup styleGroup;
    private String style;


    public StartWindow() {
        JFrame frame = new JFrame("Select Style");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font font = null;
        try {
            java.io.File fontFile = new java.io.File("./fonts/WyvernInk Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            final int FONT_SIZE = 17;
            font = font.deriveFont((float) FONT_SIZE);
        } catch (FontFormatException ffe) {
            System.out.println(ffe);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        frame.setFont(font);

        JPanel panel = new JPanel();

        JRadioButton radioDefault = new JRadioButton("Default");
        JRadioButton radioNewYear = new JRadioButton("New Year");
        JRadioButton radioGrinch = new JRadioButton("Grinch");

        radioDefault.setActionCommand("Default");
        radioNewYear.setActionCommand("NewYear");
        radioGrinch.setActionCommand("Grinch");

        radioDefault.setSelected(true);

        styleGroup = new ButtonGroup();
        styleGroup.add(radioDefault);
        styleGroup.add(radioNewYear);
        styleGroup.add(radioGrinch);

        JButton buttonStart = new JButton("Start");

        Dimension buttonSize = new Dimension(120, 50);
        buttonStart.setPreferredSize(buttonSize);

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                style = getSelectedStyle();
                Font font = frame.getFont();
                frame.dispose();
                new Viewer(style, font);
            }
        });

        radioDefault.setFont(font);
        radioNewYear.setFont(font);
        radioGrinch.setFont(font);
        buttonStart.setFont(font);

        panel.setLayout(new FlowLayout());
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(radioDefault);
        panel.add(radioNewYear);
        panel.add(radioGrinch);
        panel.add(buttonStart);

        frame.getContentPane().add(panel);
        frame.setSize(400, 180);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public String getStyle() {
        return style;
    }

    private String getSelectedStyle() {
        if (styleGroup.getSelection() == null) {
            return "";
        } else {
            return styleGroup.getSelection().getActionCommand();
        }
    }
}
