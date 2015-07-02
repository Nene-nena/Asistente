/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.datatransfer.UnsupportedFlavorException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.table.XTableRows;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTableCursor;
import com.sun.star.uno.UnoRuntime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author adrivero
 */
public class Manager {

    private Documento doc;
    private CECalendario CEcal;
    private List<Tarea> listTareas;

    public Manager() {
    }

    public void IniciarManager(CECalendario CEcal) {
        this.CEcal = CEcal;
    }

    public void AddListaTareas(List<Tarea> listTareas) {
        if (this.listTareas == null) {
            this.listTareas = listTareas;
        } else {
            this.listTareas.addAll(listTareas);

        }
        OrdenarFechasdeTareas();
        OrdenarTareasPorPriFecha();
    }

    public void setListTareas(List<Tarea> listTareas) {
        this.listTareas = listTareas;
    }

    public List<Tarea> getListTareas() {
        return listTareas;
    }

    public Documento getDoc() {
        return doc;
    }

    public CECalendario getCEcal() {
        return CEcal;
    }

    public int addTarea(Tarea tar) {
        if (this.listTareas == null) {
            this.listTareas = new LinkedList<Tarea>();
        }
        listTareas.add(tar);
        OrdenarTareasPorPriFecha();
        return listTareas.indexOf(tar);
    }
    /*
     * 
     */

    public void LLenarPlantilla(int CantTablasAPoner, Calendar fechas, Calendar fechaFinRepet, JLabel jlabel) throws com.sun.star.uno.RuntimeException, NoConnectException, ConnectionSetupException, IllegalArgumentException, Exception {
        if (CEcal.getNombre() == null) {
            {
                return;
            }
        }
        doc = new Documento(CEcal.getNombre());
        doc.leeTabla();

        MyThread mythead = new MyThread(CantTablasAPoner, 100, 0, true, 3000, 100, this, fechas, fechaFinRepet, jlabel);
        mythead.start();
    }

    public String[] getNamesCalendar(int field, int style) {
        String[] arr = new String[7];
        Map<String, Integer> map = Calendar.getInstance().getDisplayNames(field, style, Locale.getDefault());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Integer value = entry.getValue();
            String dia = entry.getKey();
            arr[value - 1] = dia;
            //use key and value
        }
        return arr;
    }

    public final void OrdenarFechasdeTareas() {
        if (listTareas != null) {
            if (!listTareas.isEmpty()) {
                for (int i = 0; i < listTareas.size(); i++) {
                    Tarea tarea = listTareas.get(i);
                    tarea.ordenarFechas();
                }
            }
        }
    }

    public final void OrdenarTareasPorPriFecha() {
        if (listTareas != null) {
            if (!listTareas.isEmpty()) {
                for (int i = 0; i < listTareas.size() - 1; i++) {
                    for (int j = i + 1; j < listTareas.size(); j++) {
                        Tarea tareai = listTareas.get(i);
                        Tarea tareaj = listTareas.get(j);
                        if (tareai.getListafechas().get(0).compareTo(tareaj.getListafechas().get(0)) > 0) {
                            listTareas.add(i, listTareas.get(j));
                            listTareas.remove(i + 1);
                            listTareas.add(j, tareai);
                            listTareas.remove(j + 1);
                        }
                    }
                }
            }
        }
    }

    public int myorNumeroDeTarea() {
        int mayor = -1;
        if (listTareas != null) {
            if (!listTareas.isEmpty()) {
                mayor = listTareas.get(0).getNumero();
                for (int i = 0; i < listTareas.size(); i++) {
                    Tarea tarea = listTareas.get(i);
                    if (mayor < tarea.getNumero()) {
                        mayor = tarea.getNumero();
                    }
                }
            }
        }
        return mayor;
    }

    public void InsertarLeyenda() {
        if (listTareas != null) {
            String leyenda = "Leyenda de Tareas: \n";
            int myorNumeroDeTarea = myorNumeroDeTarea();

            for (int i = 0; i < getListTareas().size(); i++) {
                Tarea tarea = getListTareas().get(i);
                if (tarea.getNumero() != 0) {
                    leyenda += tarea.getNumero() + ": " + tarea.toString();
                    if (tarea.getNumero() != myorNumeroDeTarea) {
                        leyenda += "\n";
                    }
                }
            }
            getDoc().manipulateText(leyenda);
        }
    }

    public void LimpiarNumerosTareas() {
        if (listTareas != null) {
            for (int i = 0; i < listTareas.size(); i++) {
                listTareas.get(i).setNumero(0);
            }
        }
    }

    public String getFechaAString(Calendar cal) {

        String fecha = null;
        fecha = String.valueOf(cal.get(Calendar.HOUR_OF_DAY))
                + ":" + String.valueOf(cal.get(Calendar.MINUTE))
                + " - " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
                + "/" + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + "/" + String.valueOf(cal.get(Calendar.YEAR));
        return fecha;
    }

    public Calendar getStringACalendar(String texto) {
        Calendar cal = Calendar.getInstance();
        String[] arrtodo = texto.split(" - ");
        String[] arrhora = arrtodo[0].split(":");
        String[] arrdate = arrtodo[1].split("/");

        //  cal.set(year, Integer.parseInt(arrdate[2]), Integer.parseInt(arrdate[2]), Integer.parseInt(arrhora[0]), Integer.parseInt(arrhora[1]));
        return cal;
    }
}

class MyThread extends Thread {

    private int max;
    private int min;
    private JLabel jlabel;
    private boolean running;
    private int update;
    private int rate;
    private Manager man;
    private Calendar fecha;
    private Calendar fechaFinRepet;
    private int CantTablasAPoner;

    public MyThread(int CantTablasAPoner, int max, int min, boolean running, int update, int rate, Manager man, Calendar fechas, Calendar fechaFin, JLabel jlabel)
            throws com.sun.star.uno.RuntimeException {
        this.CantTablasAPoner = CantTablasAPoner;
        this.max = max;
        this.min = min;
        this.running = running;
        this.update = update;
        this.rate = rate;
        this.jlabel = jlabel;
        this.man = man;

        //    fechas.add(Calendar.SECOND,86390);
        this.fecha = fechas;
        this.fechaFinRepet = fechaFin;
    }

    public MyThread() throws com.sun.star.uno.RuntimeException, IllegalArgumentException {
    }

    @Override
    public void run() throws com.sun.star.uno.RuntimeException {
        System.out.println("start");
        if (man.getCEcal() instanceof CECalendarioSemanalDinamico) {
            try {
                if (CantTablasAPoner > 0) {
                    man.getDoc().AddTables(CantTablasAPoner, jlabel);
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrappedTargetException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ///////////////////////////////////////////////
        //Obtiene las propiedades de la celda A1 y la fila 2 para usarlas como base 
        XPropertySet[] xCellStyleBaseAll = null;
        try {
            xCellStyleBaseAll = man.getDoc().getxCellStyleBaseAll();
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrappedTargetException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        /////
        if (man.getListTareas() != null) {
            for (int i = 0; i < man.getListTareas().size(); i++) {
                Tarea tarea = man.getListTareas().get(i);
                tarea.repetirFechas(fechaFinRepet);
            }
        }
        if (man.getCEcal() instanceof CECalendarioMensualPorDia) {
            try {
                LLenarTablaMensualPorDia();
            } catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrappedTargetException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownPropertyException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (man.getCEcal() instanceof CECalendarioSemanalDinamico) {

            try {

                LLenarTablaSemanalDinamico(xCellStyleBaseAll);
            } catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownPropertyException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrappedTargetException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (man.getCEcal() instanceof CECalendarioMensual) {
            try {
                LLenarTablaMensual();
            } catch (IndexOutOfBoundsException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrappedTargetException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownPropertyException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (man.getCEcal() instanceof CECalendarioAnual) {
            LLenarTablaAnual();
        }

        System.out.println("end");
    }

    public void LLenarTablaMensualPorDia() throws IndexOutOfBoundsException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException {

        man.getDoc().PushHeadText(0, man.getCEcal().getHead(fecha));
        XTableRows xRows = man.getDoc().getxTable().getRows();
        XPropertySet xRowGande = null;
        //calculo cuantas semanas ocupa este mes
        int total = fecha.get(Calendar.DAY_OF_WEEK) - 2 + fecha.getActualMaximum(Calendar.DAY_OF_MONTH);
        int semanas = SemanasDeMes(fecha);
        int Rowheight = 0;
        //para 4 ----2995
        // para 5 ----2259
        // para  6----1802    
        switch (semanas) {
            case 4:
                Rowheight = 2995; // 2995 ~
                break;
            case 5:
                Rowheight = 2259; /// 2259 ~
                break;
            case 6:
                Rowheight = 1751;//1802 ~ 0.69
                break;

        }
        // inserto filas si hace falta
        xRows.insertByIndex(xRows.getCount(), (semanas - 4) * 2);
        // obtiene la fecha del lunes de esa semana le resto 2 porque si 
        // es miercoles es 4 dia de la semana -2 lunes 
        fecha.add(Calendar.DAY_OF_MONTH, -(fecha.get(Calendar.DAY_OF_WEEK) - 2));
    
        //color a sabado y domingo
        man.getDoc().SetColorCell(man.getDoc().getxTable(), "F1", new Integer(0x99CCFF));
        man.getDoc().SetColorCell(man.getDoc().getxTable(), "G1", new Integer(0xff9f9f));

        for (int i = 2; i < xRows.getCount(); i++) {

            for (int j = 0; j < 7; j++) {

                char col1 = (char) ('A' + j);
                String col = String.valueOf(col1);

                if (i % 2 == 0) {

                    //pongo height alto de fila
                    man.getDoc().setHeightofRow(xRows, i, new Integer(Rowheight));
                    man.getDoc().insertTextIntoCell(col + i, man.getCEcal().getEncabezadoDeCelda(fecha), man.getDoc().getxTable());

                    fecha.add(Calendar.DAY_OF_MONTH, 1);
                    PonerTareasParaFecha(col + (i + 1), man.getDoc().getxTable(), false);

                } else {

                    man.getDoc().setHeightofRow(xRows, i, new Integer(750));
                }
            }
            if (i >= xRows.getCount() - 1) {
                jlabel.setSize(400, jlabel.getSize().height);
            } else {
                jlabel.setSize(man.getDoc().getSizeACrecer(400, i, xRows.getCount()),
                        jlabel.getSize().height);
            }
        }
    }

    public void LLenarTablaMensual() throws IndexOutOfBoundsException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException {
       
        //  man.getDoc().PushHeadText(0, man.getCEcal().getHead(fecha));
        XTableRows xRows = man.getDoc().getxTable().getRows();
        XPropertySet xRowGande = null;
        //calculo cuantas semanas ocupa este mes
        int total = fecha.get(Calendar.DAY_OF_WEEK) - 2 + fecha.getActualMaximum(Calendar.DAY_OF_MONTH);
        int semanas = SemanasDeMes(fecha);
        // inserto filas si hace falta
        xRows.insertByIndex(xRows.getCount() - 6, (semanas - 4) * 2);
        // obtiene la fecha del lunes de esa semana le resto 2 porque si 
        // es miercoles es 4 dia de la semana -2 lunes 
        fecha.add(Calendar.DAY_OF_MONTH, -(fecha.get(Calendar.DAY_OF_WEEK) - 2));
     
       
       
        
                /*  //color a sabado y domingo
         man.getDoc().SetColorCell(man.getDoc().getxTable(), "F1", new Integer(0x99CCFF));
         man.getDoc().SetColorCell(man.getDoc().getxTable(), "G1", new Integer(0xff9f9f));
         */
   
        for (int i = 1,cont=0; i < xRows.getCount() - 3; i += 2) {
            for (int j = 0; j < 7; j++) {

                char col1 = (char) ('A' + j);
                String col = String.valueOf(col1);
                man.getDoc().insertTextIntoCell(col + i, man.getCEcal().getEncabezadoDeCelda(fecha), man.getDoc().getxTable());

                fecha.add(Calendar.DAY_OF_MONTH, 1);
                PonerTareasParaFecha(col + (i + 1), man.getDoc().getxTable(), false);

                 ///////////////////////////////////////////////
     
      if(i>=5 && i<= 5 + (semanas-4)*2){
        XTextTable xTableAct = man.getDoc().getxTable();
                Object xTableCursor = xTableAct.createCursorByCellName(col + 1);
                XPropertySet RowOrig = (XPropertySet) UnoRuntime.queryInterface(
                        XPropertySet.class, xTableCursor);
                xTableCursor = xTableAct.createCursorByCellName(col + i);
                XPropertySet RowNew = (XPropertySet) UnoRuntime.queryInterface(
                        XPropertySet.class, xTableCursor);
                for (Property propertie : RowOrig.getPropertySetInfo().getProperties()) {
                    Object propertyValue = RowOrig.getPropertyValue(propertie.Name);
                    String propertyName = propertie.Name;
                    if (propertyValue != null
                            && !propertyValue.equals("")
                            && !propertyName.equals("TextSection")
                            && !propertyName.equals("NumberFormat")) {
                        RowNew.setPropertyValue(propertie.Name, RowOrig.getPropertyValue(propertie.Name));
                    }
                }
         }
            ////////////////////////////////////////////////////
                
            }

            jlabel.setSize(man.getDoc().getSizeACrecer(400, i, xRows.getCount() - 4),
                    jlabel.getSize().height);
    
        }
    }

    public void LLenarTablaAnual() {
        man.getDoc().PushHeadText(0, man.getCEcal().getHead(fecha));
        //e6e6e6
        XTableRows xRows = man.getDoc().getxTable().getRows();
        // obtiene la fecha del lunes de esa semana le resto 2 porque si 
        // es miercoles es 4 dia de la semana -2 lunes 
        fecha.add(Calendar.DAY_OF_MONTH, -(fecha.get(Calendar.DAY_OF_WEEK) - 2));

        boolean vacio = false;
        for (int fila = 2; fila < xRows.getCount(); fila += 2) {

            vacio = false;
            for (int j = 0; j < 6; j++) {
                char col1 = (char) ('B' + j);
                String col = String.valueOf(col1);
                if (!vacio) {
                    boolean repite = false;

                    if (j >= 3) {
                        // determino  si esta semana es la ultima del mes
                        int maximun = fecha.getActualMaximum(Calendar.DAY_OF_MONTH);
                        int actual = fecha.get(Calendar.DAY_OF_MONTH);
                        int actualfinal = fecha.get(Calendar.DAY_OF_MONTH) + 6;
                        if (maximun >= actual && maximun < actualfinal) {
                            repite = true;
                        } else if (maximun == actualfinal) {
                            vacio = true;
                        }
                    }
                    //////////////////////////////////////
                    Calendar copy = (Calendar) fecha.clone();
                    fecha.add(Calendar.DAY_OF_MONTH, 1);
                    boolean mismaSemama = false;
                    for (int i = 0; i < 7; i++) {
                        int get = fecha.get(Calendar.DAY_OF_MONTH);
                        boolean PonerTareasParaFecha = false;
                        try {
                            PonerTareasParaFecha = PonerTareasParaFecha(col + (fila + 1), man.getDoc().getxTable(), mismaSemama);
                        } catch (UnknownPropertyException ex) {
                            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (WrappedTargetException ex) {
                            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (PonerTareasParaFecha) {
                            mismaSemama = true;
                        }
                        fecha.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    fecha = (Calendar) copy.clone();
                    try {
                        ////////////////////////////////////////////
                        man.getDoc().insertTextIntoCell(col + fila, man.getCEcal().getEncabezadoDeCelda(fecha), man.getDoc().getxTable());
                    } catch (UnknownPropertyException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (PropertyVetoException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WrappedTargetException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (repite) {
                        fecha.add(Calendar.DAY_OF_MONTH, -7);
                        vacio = true;
                    }

                    fecha.add(Calendar.DAY_OF_MONTH, 1);

                } else {
                    try {
                        man.getDoc().SetColorCell(man.getDoc().getxTable(), col + fila, new Integer(0xe7e5e4));
                        man.getDoc().SetColorCell(man.getDoc().getxTable(), col + (fila + 1), new Integer(0xe7e5e4));

                    } catch (UnknownPropertyException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (PropertyVetoException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WrappedTargetException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    /*  man.getDoc().SetProtectedCell(col + fila);
                     man.getDoc().SetProtectedCell(col + (fila + 1));
                     */
                }
            }
            if (fila == xRows.getCount() - 1) {
                jlabel.setSize(400, jlabel.getSize().height);
            } else {
                jlabel.setSize(man.getDoc().getSizeACrecer(400, fila, xRows.getCount()),
                        jlabel.getSize().height);
            }
        }
        man.InsertarLeyenda();
    }

    public void LLenarTablaSemanalDinamico(XPropertySet[] xCellStyleBase) throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, WrappedTargetException, IllegalArgumentException {
        // localizar en que celda caen el sabado y domingo
        int posActual = fecha.get(Calendar.DAY_OF_WEEK);

        for (int it = 0; it < man.getDoc().getAllxTables().size(); it++) {

            man.getDoc().PushHeadText(it, man.getCEcal().getHead(fecha));
            int posDelSab = 7 - posActual;
            XTextTable xTableAct = man.getDoc().getAllxTables().get(it);

            XTableRows xRows = xTableAct.getRows();
            XPropertySet xRow;
            XTextTableCursor xTableCursor;

            for (int j = 0; j < 2; j++) {
                char col1 = (char) ('A' + j);
                String col = String.valueOf(col1);

                for (int i = 1; i <= xRows.getCount(); i++) {

                    if (i % 2 != 0) {
                        if (posDelSab == -4) {
                            i += 2;
                        }
                        if (posDelSab == 0) {
                            boolean sepudo = false;
                            try {
                                sepudo = man.getDoc().DividirCelda((short) 2, col + String.valueOf(i + 1), xTableAct);
                            } catch (WrappedTargetException ex) {
                                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (sepudo) {

                                xRow = null;

                                man.getDoc().insertTextIntoCell(col + i, man.getCEcal().getEncabezadoDeCelda(fecha), xTableAct);
                                fecha.add(Calendar.DAY_OF_MONTH, 1);
                                PonerTareasParaFecha(col + (i + 1), xTableAct, false);
                                man.getDoc().SetColorCell(xTableAct, col + i, new Integer(0x99CCFF));
                                man.getDoc().setHeightofRow(xRows, i, new Integer(3163));
                                i++;
                                man.getDoc().setHeightofRow(xRows, i, new Integer(750));
                                /////////////////////////////////////      
                                i++;
                                man.getDoc().insertTextIntoCell(col + (i), man.getCEcal().getEncabezadoDeCelda(fecha), xTableAct);
                                fecha.add(Calendar.DAY_OF_MONTH, 1);
                                ///////////////////////////////////////////////
                                xTableCursor = xTableAct.createCursorByCellName(col + i);
                                XPropertySet xCell2 = (XPropertySet) UnoRuntime.queryInterface(
                                        XPropertySet.class, xTableCursor);
                                for (int k = 0; k < xCellStyleBase[0].getPropertySetInfo()
                                        .getProperties().length; k++) {
                                    if (xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name.contains("CharF")
                                            || xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name.contains("Para")
                                            || xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name.contains("CharWeight")
                                            || xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name.contains("Border")) {
                                        xCell2.setPropertyValue(xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name, xCellStyleBase[0].getPropertyValue(xCellStyleBase[0].getPropertySetInfo().getProperties()[k].Name));
                                    }
                                }
                                man.getDoc().SetColorCell(xTableAct, col + i, new Integer(0xff9f9f));
                                man.getDoc().setHeightofRow(xRows, i, new Integer(3163));
                            }

                        } else {

                            if (i >= xRows.getCount()) {
                                break;
                            }
                            man.getDoc().insertTextIntoCell(col + i, man.getCEcal().getEncabezadoDeCelda(fecha), xTableAct);

                            fecha.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        posDelSab--;
                    } else {

                        // para las celdas de tareas
                        PonerTareasParaFecha(col + i, xTableAct, false);

                        if (i != xRows.getCount()) {

                            xRow = (XPropertySet) UnoRuntime.queryInterface(
                                    XPropertySet.class, xRows.getByIndex(i));

                            xRow.setPropertyValue(
                                    "Height", xCellStyleBase[1].getPropertyValue("Height"));

                        }
                    }
                }
            }
            try {
                // que salte a otra pagina si no da el largo
                man.getDoc().getxTableProps().setPropertyValue("Split", Boolean.FALSE);
            } catch (UnknownPropertyException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrappedTargetException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            //     xRows.insertByIndex(8, 4);
            int alto = 0;

            if (it == man.getDoc().getAllxTables().size() - 1) {
                jlabel.setSize(400, jlabel.getSize().height);
            } else {
                jlabel.setSize(man.getDoc().getSizeACrecer(400, it, man.getDoc().getAllxTables().size()),
                        jlabel.getSize().height);
            }
        }
    }

    public int SemanasDeMes(Calendar fecha) {
        //calculo cuantas semanas ocupa este mes
        int total = fecha.get(Calendar.DAY_OF_WEEK) - 2 + fecha.getActualMaximum(Calendar.DAY_OF_MONTH);
        int semanas = (int) total / 7;
        if (total % 7 != 0) {
            semanas++;
        }
        // obtiene la fecha del lunes de esa semana le resto 2 porque si
        // es miercoles es 4 dia de la semana -2 lunes
        Calendar clone = (Calendar) fecha.clone();
        clone.add(Calendar.DAY_OF_MONTH, -(clone.get(Calendar.DAY_OF_WEEK) - 2));
        // acotando el error para cuando el 1ro cae domingo
         if (clone.get(Calendar.DAY_OF_MONTH) == 2) {
                 fecha.add(Calendar.DAY_OF_MONTH, -7);
           semanas++;
         }
         
        
        return semanas;
    }
    boolean pri = true;

    private boolean PonerTareasParaFecha(String colFil, XTextTable xTableAct, boolean mismaSemana) throws UnknownPropertyException, PropertyVetoException, WrappedTargetException, IllegalArgumentException {
        boolean res = false;
        if (man.getListTareas() != null) {
            boolean primera = true;
            String operador = "";
            for (int itTar = 0; itTar < man.getListTareas().size(); itTar++) {
                Tarea Taractual = man.getListTareas().get(itTar);
                //obtengo la fecha de un dia antes y restablesco la variable normal
                fecha.add(Calendar.DAY_OF_MONTH, -1);
                //getTextoTareasPraFecha retorna 0 si las fechas no son =.
                String textotareasPraFecha = Taractual.getTextoTareasPraFecha(fecha);
                fecha.add(Calendar.DAY_OF_MONTH, 1);
                if (!textotareasPraFecha.equals("0")) {
                    res = true;
                    if (!(man.getCEcal() instanceof CECalendarioAnual)) {
                        if (primera) {
                            primera = false;
                            man.getDoc().insertTextIntoCell(colFil, textotareasPraFecha, xTableAct);
                        } else {
                            operador = "\n";
                            man.getDoc().addTextIntoCell(colFil, textotareasPraFecha, operador, xTableAct);
                        }
                    }// es anual 
                    else {
                        if (Taractual.getNumero() == 0) {
                            Taractual.setNumero(man.myorNumeroDeTarea() + 1);
                        }

                        if (primera) {
                            primera = false;
                            operador = "";
                        } else {
                            operador = ", ";
                        }
                        if (mismaSemana) {
                            operador = ", ";
                        }
                        man.getDoc().addTextIntoCell(colFil, String.valueOf(Taractual.getNumero()), operador, xTableAct);
                    }
                }
            }
        }
        return res;
    }


}
