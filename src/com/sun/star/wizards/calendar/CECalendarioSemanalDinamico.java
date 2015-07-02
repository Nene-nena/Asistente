/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author adrivero
 */
public class CECalendarioSemanalDinamico extends CECalendario {

    @Override
    public String getEncabezadoDeCelda(Calendar calendario) {
        String encab =
                calendario.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, 
                Locale.getDefault()).toUpperCase()
                + " " + calendario.get(Calendar.DAY_OF_MONTH)
                + "/" + (calendario.get(Calendar.MONTH) + 1)
                + "/" + calendario.get(Calendar.YEAR)
                + " ";
        return encab;
    }

    @Override
    public String getNombre() {
        return "CalendarioSemanalDinamico.odt";
    }

    @Override
    public String getHead(Calendar fecha) {
        String Head = fecha.get(Calendar.YEAR) + " "
                + fecha.getDisplayName(Calendar.MONTH, Calendar.LONG, 
                Locale.getDefault()).substring(0, 1).toUpperCase()
                + fecha.getDisplayName(Calendar.MONTH, Calendar.LONG, 
                Locale.getDefault()).substring(1)
                + ": Semana " + fecha.get(Calendar.WEEK_OF_MONTH);
        return Head;
    }
}
