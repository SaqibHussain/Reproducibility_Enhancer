/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Supreme2
 */
public class MainFrame extends JFrame {

    protected JTabbedPane tabbedPane;
    protected Panel1 panel1;
    protected Panel2 panel2;

    public MainFrame() {
        panel1 = new Panel1(this);
        panel2 = new Panel2(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Run a new computation", panel1);
        tabbedPane.addTab("Re-run an existing computation", panel2);

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(BorderLayout.CENTER, tabbedPane);

        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem aboutMenuItem = new JMenuItem("About");

        fileMenu.add(exitMenuItem);
        helpMenu.add(aboutMenuItem);

        JMenuBar myMenuBar = new JMenuBar();
        myMenuBar.add(fileMenu);
        myMenuBar.add(helpMenu);

        setupLookAndFeelMenu(myMenuBar);

        // register a listener for the "exit" menu item.
        exitMenuItem.addActionListener(new ExitActionListener());
        // register a listener for the "about" menu item
        aboutMenuItem.addActionListener(new AboutActionListener());

        setTitle("Reproducibility Enhancer");
        setSize(1200, 800);
        setLocation(100, 100);
        setJMenuBar(myMenuBar);

        //
        //  ADD LISTENERS
        //
        this.addWindowListener(new WindowCloser());
    }

    protected void setupLookAndFeelMenu(JMenuBar theMenuBar) {

        UIManager.LookAndFeelInfo[] lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
        JMenu lookAndFeelMenu = new JMenu("Options");
        JMenuItem anItem = null;
        LookAndFeelListener myListener = new LookAndFeelListener();

        try {
            for (int i = 0; i < lookAndFeelInfo.length; i++) {
                anItem = new JMenuItem(lookAndFeelInfo[i].getName() + " Look and Feel");
                anItem.setActionCommand(lookAndFeelInfo[i].getClassName());
                anItem.addActionListener(myListener);

                lookAndFeelMenu.add(anItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        theMenuBar.add(lookAndFeelMenu);
    }

    /**
     * Helper method that hides the window and disposes of its resources.
     * Finally, we exit.
     */
    public void exit() {

        setVisible(false);
        dispose();
        System.exit(0);

    }

    // INNER CLASSES //
    /**
     * This class handles action events. The event handler simply exists the
     * program.
     */
    class ExitActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            exit();
        }
    }

    /**
     * This class handles window closing event. The event handler simply exists
     * the program.
     */
    class WindowCloser extends WindowAdapter {

        /**
         * let's call our exit() method defined above
         */
        public void windowClosing(WindowEvent e) {

            exit();
        }
    }

    /**
     * Listener class to set the look and feel at run time.
     *
     * Changing the look and feel is so simple :-) Just call the method:
     *
     * <code>
     *     UIManager.setLookAndFeel(<class name of look and feel>);
     * </code>
     *
     *
     * The available look and feels are:
     *
     * <pre>
     *
     *    NAME             CLASS NAME
     *    --------         -----------
     *    Metal            javax.swing.plaf.metal.MetalLookAndFeel
     *    Windows          com.sun.java.swing.plaf.windows.WindowsLookAndFeel
     *    Motif            com.sun.java.swing.plaf.motif.MotifLookAndFeel
     *
     *  </pre>
     *
     * There is also an additional Mac look and feel that you can download from
     * Sun's web site.
     *
     */
    class LookAndFeelListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            // get the class name to load
            String className = event.getActionCommand();

            try {
                // set the look and feel
                UIManager.setLookAndFeel(className);

                // update our component tree w/ new look and feel
                SwingUtilities.updateComponentTreeUI(MainFrame.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener for the "About" menu item
     */
    class AboutActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String msg = "Reproducibility Enhancer for deploying code inside Docker containers to distrubuted systems";
            JOptionPane.showMessageDialog(MainFrame.this, msg);
        }
    }
}
