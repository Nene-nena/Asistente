/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author adrivero
 */
public class HiloReloj extends Thread {

    private JLabel jshowDay;
    private JLabel showDay1;
    private JLabel showDate;
    private JLabel showDate1;
    private JProgressBar prgresBar;
    private JLabel label;

    public HiloReloj(JLabel jshowDay, JLabel showDay1, JLabel showDate, JLabel showDate1) {
        this.jshowDay = jshowDay;
        this.showDay1 = showDay1;
        this.showDate = showDate;
        this.showDate1 = showDate1;
    }

    public HiloReloj(JProgressBar prgresBar) {
        this.prgresBar = prgresBar;
    }

    public HiloReloj(JLabel label) {
        this.label = label;
    }

    @Override
    public void run() {
        System.out.println("start");

        if (label == null) {
            while (true) {
                Calendar cal = Calendar.getInstance(Locale.getDefault());

                String tex = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":"
                        + String.valueOf(cal.get(Calendar.MINUTE)) + ":"
                        + String.valueOf(cal.get(Calendar.SECOND));
                jshowDay.setText(tex);

                showDate1.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
                String date = cal.get(Calendar.DAY_OF_MONTH)
                        + " de " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                        + " de " + cal.get(Calendar.YEAR);

                showDate.setText(date);

                //   showDate1.setText(date);
                try {
                    sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HiloReloj.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            /*for (int i = 1; i <= 170; i++) {
             label.setSize(i, label.getHeight());
             try {
             sleep(50);
             } catch (InterruptedException ex) {
             Logger.getLogger(HiloReloj.class.getName()).log(Level.SEVERE, null, ex);
             }
             }*/
        }
        System.out.println("end");
    }
}
