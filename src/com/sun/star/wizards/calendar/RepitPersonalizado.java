/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.List;

/**
 *
 * @author adriam
 */
public abstract class RepitPersonalizado {
 protected int repitCada;
 protected int conFechaFinal;
 protected int cantRepeticiones;
 protected Calendar repetirHastaDia;

    public RepitPersonalizado(int repitCada, int cantRepeticiones, Calendar repetirHastaDia, int conFechaFinal) {
        this.repitCada = repitCada;
      
        this.cantRepeticiones = cantRepeticiones;
        this.repetirHastaDia = repetirHastaDia;
          this.conFechaFinal = conFechaFinal;
    }

   public abstract void ReetirFechasPersonalizado(List<Calendar> fechaIni, Calendar fechaFin);

   

    public int getRepitCada() {
        return repitCada;
    }

   

    public int getCantRepeticiones() {
        return cantRepeticiones;
    }

    public Calendar getRepetirHastaDia() {
        return repetirHastaDia;
    }

    public void setRepitCada(int repitCada) {
        this.repitCada = repitCada;
    }

    public int getConFechaFinal() {
        return conFechaFinal;
    }

    public void setConFechaFinal(int conFechaFinal) {
        this.conFechaFinal = conFechaFinal;
    }

   

    public void setCantRepeticiones(int cantRepeticiones) {
        this.cantRepeticiones = cantRepeticiones;
    }

    public void setRepetirHastaDia(Calendar repetirHastaDia) {
        this.repetirHastaDia = repetirHastaDia;
    }
 

}
