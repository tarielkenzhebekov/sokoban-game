import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class AudioControlApp extends JFrame {
    private SoundsRepository soundsRepository =new SoundsRepository();
    private JSlider volumeSlider;
    private final JRadioButton onButton;
    private final JRadioButton offButton;
    private Sound sound =  soundsRepository.getSound("FonSound");

    public AudioControlApp() {

        super("Audio Control");

        setLayout(new FlowLayout());


        onButton = new JRadioButton("On");
        offButton = new JRadioButton("Off");

        ButtonGroup powerButtonGroup = new ButtonGroup();
        powerButtonGroup.add(onButton);
        powerButtonGroup.add(offButton);

        onButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onButton.isSelected()) {
                    sound.loop();
                }
            }
        });

        offButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (offButton.isSelected()) {
                    sound.stop();
                }
            }
        });

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int volume = volumeSlider.getValue();
                sound.setVolume(volume);

            }
        });




        add(new JLabel("Volume:"));
        add(volumeSlider);
        add(onButton);
        add(offButton);

        // Настраиваем основные параметры окна
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(false);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }
}
