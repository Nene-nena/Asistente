/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author adrivero
 */
public class Tarea {

    private List<Calendar> listafechas;
    private String titulo;
    private String descripcion;
    private TipeTarea categoria;
    private String Lugar;
    private TipeRepit repit;
    private int numero;
    private RepitPersonalizado rePersonal;

    public Tarea(Calendar fechaIni, String titulo, String Lugar, String descripcion, TipeRepit repit1, TipeTarea categoria, RepitPersonalizado repitpersonal) {

        if (this.listafechas == null) {
            this.listafechas = new LinkedList<Calendar>();
        }
        this.listafechas.add(fechaIni);

        this.categoria = categoria;
        this.repit = repit1;
        this.Lugar = Lugar;
        this.titulo = titulo;
        this.descripcion = descripcion;
        numero = 0;

      
            this.rePersonal = repitpersonal;
        
    }

    public RepitPersonalizado getRePersonal() {
        return rePersonal;
    }

    public void setRePersonal(RepitPersonalizado rePersonal) {
        this.rePersonal = rePersonal;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public TipeTarea getCategoria() {
        return categoria;
    }

    public String getLugar() {
        return Lugar;
    }

    public TipeRepit getRepit() {
        return repit;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    void ordenarFechas() {
        for (int i = 0; i < listafechas.size() - 1; i++) {
            for (int j = i + 1; j < listafechas.size(); j++) {
                Calendar fecha = listafechas.get(i);
                if (fecha.compareTo(listafechas.get(j)) > 0) {
                    listafechas.add(i, listafechas.get(j));
                    listafechas.remove(i + 1);
                    listafechas.add(j, fecha);
                    listafechas.remove(j + 1);
                }

            }

        }

    }

    public List<Calendar> getListafechas() {
        return listafechas;
    }

    String getTextoTareasPraFecha(Calendar fecha) {

        for (int itFech = 0; itFech < listafechas.size(); itFech++) {
            //  if (fecha.compareTo(listafechas.get(itFech)) >= 0) {
            if (fecha.get(Calendar.DAY_OF_MONTH) == listafechas.get(itFech).get(Calendar.DAY_OF_MONTH)
                    && fecha.get(Calendar.MONTH) == listafechas.get(itFech).get(Calendar.MONTH)
                    && fecha.get(Calendar.YEAR) == listafechas.get(itFech).get(Calendar.YEAR)) {
                String texto = listafechas.get(itFech).get(Calendar.HOUR_OF_DAY)
                        + ":" + listafechas.get(itFech).get(Calendar.MINUTE);

                if (!categoria.equals(TipeTarea.Ninguna)) {
                    texto += " " + categoria;
                }
                if (!Lugar.equals("")) {
                    texto += " En: " + Lugar+".";
                }
                    texto += " " + titulo;
               
                if (!descripcion.equals("")) {
                    texto += " : " + descripcion;
                }

                return texto;
            }/*
             } else {
             break;
             }*/
        }
        return "0";
    }

    public void repetirFechas(Calendar limite) {
        if (listafechas.size() > 1) {
            while (listafechas.size() > 1) {
                listafechas.remove(listafechas.size() - 1);
            }
        }
        if (repit.equals(TipeRepit.Personalizar)) {
            //rePersonal.metodoQueDevuelva la lista de fechas  dado listafechas.get(0),limite
            rePersonal.ReetirFechasPersonalizado(listafechas, limite);

        } else {
            int i = 0;
            boolean bandera = true;
            while (bandera) {
                bandera = listafechas.get(i).compareTo(limite) < 0;
                if (!bandera) {
                    break;
                }
                int fecha = listafechas.get(i).get(Calendar.DAY_OF_MONTH);
                int limit = limite.get(Calendar.DAY_OF_MONTH);
                Calendar copy = (Calendar) listafechas.get(i).clone();
                int cop = copy.get(Calendar.DAY_OF_MONTH);
                switch (repit) {
                    case Diariamente:
                        copy.add(Calendar.DAY_OF_MONTH, 1);
                        break;
                    case Semanalmente:
                        copy.add(Calendar.DAY_OF_MONTH, 7);
                        break;
                    case Mensualmente:
                        copy.add(Calendar.MONTH, 1);
                        break;
                    case Anualmente:
                        copy.add(Calendar.YEAR, 1);
                        break;
                    case No_repetir:
                        bandera = false;
                        break;

                }

                listafechas.add(copy);
                i++;
            }
        }
    }

    @Override
    public String toString() {
        return titulo + ", " + Lugar;
    }

    public void setListafechas(List<Calendar> listafechas) {
        this.listafechas = listafechas;
    }

    public boolean Modifi(int posatrib, Object atrib) {

        switch (posatrib) {
            case 0:
                listafechas = new LinkedList<Calendar>();
                listafechas.add((Calendar) atrib);
                if (rePersonal.getRepetirHastaDia().compareTo((Calendar) atrib) < 0) {
                    rePersonal.setRepetirHastaDia((Calendar) atrib);
                }

                return true;
            case 1:
                titulo = (String) atrib;
                return true;
            case 2:
                Lugar = (String) atrib;
                return true;
            case 3:
                categoria = (TipeTarea) atrib;
                return true;
            case 4:
                repit = (TipeRepit) atrib;
                return true;
            case 5:
                descripcion = (String) atrib;
                return true;
            case 6:
                rePersonal = (RepitPersonalizado) atrib;
                return true;
        }
        return false;
    }
}
