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
public class CECalendarioMensualPorDia extends CECalendario {

    public CECalendarioMensualPorDia() {
    }

    @Override
    public String getEncabezadoDeCelda(Calendar calendario) {
        String encab;
        encab = String.valueOf(calendario.get(Calendar.DAY_OF_MONTH));
        if (calendario.get(Calendar.DAY_OF_MONTH) == calendario.getActualMinimum
                (Calendar.DAY_OF_MONTH)|| calendario.get(Calendar.DAY_OF_MONTH)
                == calendario.getActualMaximum(Calendar.DAY_OF_MONTH)) {
        encab += " " + calendario.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()).substring(0, 1).toUpperCase()
                     + calendario.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()).substring(1);
        }
        return encab;
    }

    @Override
    public String getNombre() {
        return "CalendarioMensualPorDia(4).odt";
    }

    @Override
    public String getHead(Calendar fecha) {
        String Head = fecha.getDisplayName(Calendar.MONTH, Calendar.LONG, 
                Locale.getDefault()).substring(0, 1).toUpperCase()
                + fecha.getDisplayName(Calendar.MONTH, Calendar.LONG, 
                Locale.getDefault()).substring(1)
                + " de " + fecha.get(Calendar.YEAR);
        return Head;
    }
}
