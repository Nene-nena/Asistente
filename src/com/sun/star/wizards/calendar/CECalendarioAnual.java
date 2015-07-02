/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;

/**
 *
 * @author adrivero
 */
public class CECalendarioAnual extends CECalendario {

    @Override
    public String getEncabezadoDeCelda(Calendar calendario) {
        String encab;

        encab = calendario.get(Calendar.DAY_OF_MONTH) + "-";
        calendario.add(Calendar.DAY_OF_MONTH, 6);
        encab += calendario.get(Calendar.DAY_OF_MONTH);
        return encab;
    }

    @Override
    public String getNombre() {
        return "CalendarioAnual.odt";
    }

    @Override
    public String getHead(Calendar fecha) {
        String Head = "Calendario del año: " + fecha.get(Calendar.YEAR);
        return Head;
    }
}
