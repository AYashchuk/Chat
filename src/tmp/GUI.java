package tmp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by admin on 23.12.2014.
 */
public class GUI extends JFrame{
    private JPanel panel1;
    private JButton button1;
    private JCheckBox checkBox1;
    private JTextArea textArea1;
    private JComboBox comboBox1;
    String[] elements = new String[] {"Вася", "Петя",
            "<html><font size = +1 color = yellow>Гена</font>"};
    public GUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(panel1);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBox1.setText("new Text");
                comboBox1.addItem("<html><font  color = yellow>Иван</font>");
                comboBox1.addItem("<html><font  color = red>гена</font>");
                comboBox1.addItem("<html><font  color = blue>Аркадий</font>");
                button1.setText("new Text");
                pack();
            }
        });
        setVisible(true);
        pack();
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel1 = new JPanel();
        button1 = new JButton("Ok");
        checkBox1 = new JCheckBox("PPPP");
        textArea1 = new JTextArea();
        comboBox1 = new JComboBox();
       // button1.setText("sd");
        //System.out.println("createUIComponents");
    }
}
