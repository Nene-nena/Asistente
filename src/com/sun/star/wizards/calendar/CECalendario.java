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
public abstract class CECalendario {

    protected String nombre;

    public CECalendario() {
    }

    public abstract String getEncabezadoDeCelda(Calendar calendario);

    public abstract String getHead(Calendar fecha);

    public abstract String getNombre();
}
