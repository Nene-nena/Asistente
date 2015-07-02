/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.calendar;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.container.XIndexAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.datatransfer.UnsupportedFlavorException;
import com.sun.star.datatransfer.XTransferable;
import com.sun.star.datatransfer.XTransferableSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.table.XCellRange;
import com.sun.star.table.XTableRows;
import com.sun.star.text.XSimpleText;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTableCursor;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XSearchDescriptor;
import com.sun.star.util.XSearchable;
import com.sun.star.view.XSelectionSupplier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;

/**
 *
 * @author adrivero
 */
public class Documento {

    private String nombre;
    private String direccionTmp;
    private XComponent xWriterComponent;
    private XTextDocument xTextDocument;
    private XText xText;
    private List<XTextRange> listxtextRangeMarcadores;
    private XComponentContext xRemoteContext;
    private XMultiComponentFactory xRemoteServiceManager;
    private Object desktop;
    private XComponentLoader xComponentLoader;
    private XTextTable xTable;
    private Object table;
    private XPropertySet xTableProps;
    List<XTextTable> allxTables;
    private XMultiComponentFactory xOfficeFactory;
    private XComponentContext xOfficeComponentContext;
    private Object initialObject;
    private XIndexAccess xIndexedTables;

    public Documento(String nombre) throws NoConnectException,
            ConnectionSetupException, com.sun.star.lang.IllegalArgumentException, Exception {
        this.nombre = nombre;

        this.direccionTmp = "Se inicializa en cargaDesdePlantilla";

           // Para utilizarlo  en modo servicios (comando de consola) Start

         XComponentContext xcomponentcontext;

         xcomponentcontext = Bootstrap.createInitialComponentContext(null);


         // create a connector, so that it can contact the office
         XUnoUrlResolver urlResolver = UnoUrlResolver.create(xcomponentcontext);

         initialObject = urlResolver.resolve(
         "uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");

         xOfficeFactory = (XMultiComponentFactory) UnoRuntime.queryInterface(
         XMultiComponentFactory.class, initialObject);

         // retrieve the component context as property (it is not yet exported from the office)
         // Query for the XPropertySet interface.
         XPropertySet xProperySet = (XPropertySet) UnoRuntime.queryInterface(
         XPropertySet.class, xOfficeFactory);

         // Get the default context from the office server.
         Object oDefaultContext = xProperySet.getPropertyValue("DefaultContext");

         // Query for the interface XComponentContext.
         xOfficeComponentContext = (XComponentContext) UnoRuntime.queryInterface(
         XComponentContext.class, oDefaultContext);

         // now create the desktop service
         // NOTE: use the office component context here!
         this.desktop = xOfficeFactory.createInstanceWithContext(
         "com.sun.star.frame.Desktop", xOfficeComponentContext);

         if (null != initialObject) {
         System.out.println("initial object successfully retrieved");
         } else {
         System.out.println("given initial-object name unknown at server side");
         }

        // Para utilizarlo  en modo servicios End        


        //sin modo servicios Start
    /*   // Obtener el jefe de servicio remoto
        xRemoteContext = Bootstrap.bootstrap();
        if (xRemoteContext == null) {
            System.err.println("ERROR: Could not bootstrap default Office.");
        }
        this.xRemoteServiceManager = xRemoteContext.getServiceManager();

        this.desktop = xRemoteServiceManager.createInstanceWithContext(
                "com.sun.star.frame.Desktop", xRemoteContext);
 */       //sin modo servicios End
         
  // Recuperar el objeto de escritorio, necesitamos su XComponentLoader
  this.xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(
                XComponentLoader.class, desktop);

        this.xWriterComponent = CargaNuevaDesdePlantilla("1");
        this.xTextDocument = (XTextDocument) UnoRuntime.queryInterface(
                XTextDocument.class, xWriterComponent);
        this.xText = xTextDocument.getText();
        this.listxtextRangeMarcadores = busca(xTextDocument);

        // get internal service factory of the document
        XMultiServiceFactory xWriterFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, xWriterComponent);

        // insert TextTable
        this.table = xWriterFactory.createInstance("com.sun.star.text.TextTable");
        XTextContent xTextContentTable = (XTextContent) UnoRuntime.queryInterface(
                XTextContent.class, table);

        this.allxTables = new LinkedList<XTextTable>();

    }

    /**
     * Documento de un documento como plantilla
     */
    private XComponent CargaNuevaDesdePlantilla(String loadUrlfi) throws java.lang.Exception {
        String separador = "/";//System.getProperty("file.separator");// cual es el separadpr de tu  pc(/ o \)
        String direccionAbsoluta = System.getProperty("user.dir"); // directorio donde se esta ejecutando java 

        String dirTemp = System.getProperty("java.io.tmpdir") + separador + "adr";      //Creas un nuevo directorio temporal.
        File directorio = new File(dirTemp); //Creas un nuevo directorio temporal.
        String a = directorio.getCanonicalPath();
        directorio.mkdirs();
        directorio.setWritable(true);
        directorio.canWrite();
        directorio.setReadable(true);
        //copias la direccion
        String archivo = directorio.getCanonicalPath() + separador + "miCalendario.odt";
        //nuevo archivo en esa direccion
        File temp = new File(archivo);
        temp.setWritable(true);
        temp.setExecutable(true);
        URL url = this.getClass().getResource(separador + "com/sun/star/wizards/calendar/Plantillas" + separador + nombre);
        String path = url.getPath();
        for (int i = 1; i < 3; i++) {
            InputStream is = this.getClass().getResourceAsStream(separador + "com/sun/star/wizards/calendar/Plantillas" + separador + nombre);

            FileOutputStream archivoDestino = new FileOutputStream(temp);
            FileWriter fw = new FileWriter(temp);


            byte[] buffer = new byte[512 * 1024];
            //lees el archivo hasta que se acabe...
            int nbLectura;
            while ((nbLectura = is.read(buffer)) != -1) {
                archivoDestino.write(buffer, 0, nbLectura);
            }
            //cierras el archivo,el inputS y el FileW
            fw.close();
        }
        // Template / carga con los campos de usuario y marcador

        String os = System.getProperty("os.name");
        String str = "file://";
        if (os.contains("Windows")) {
            str += "/";
        }
        direccionTmp = str + temp.getCanonicalPath();

        // Definir las propiedades de carga según com.sun.star.document.MediaDescriptor
        // La propiedad booleana AsTemplate ordena a la oficina crear un nuevo documento
        // Desde el archivo dado
        PropertyValue[] loadProps = new PropertyValue[1];
        loadProps[0] = new PropertyValue();
        loadProps[0].Name = "AsTemplate";
        loadProps[0].Value = new Boolean(true);
        // Documento
        return xComponentLoader.loadComponentFromURL(direccionTmp, "_blank", 0, loadProps);
    }

    private List<XTextRange> busca(XTextDocument xtextDocument) {
        try {
            XSearchable xSearchable = UnoRuntime.queryInterface(XSearchable.class, xtextDocument);

            XSearchDescriptor sd = xSearchable.createSearchDescriptor();

            sd.setSearchString("#[^#]+#");

            sd.setPropertyValue("SearchRegularExpression", Boolean.TRUE);
            sd.setPropertyValue("SearchWords", Boolean.TRUE);

            XIndexAccess ia = xSearchable.findAll(sd);

            listxtextRangeMarcadores = new ArrayList<XTextRange>(ia.getCount());
            for (int i = 0; i < ia.getCount(); i++) {
                try {
                    listxtextRangeMarcadores.add(UnoRuntime.queryInterface(XTextRange.class, ia.getByIndex(i)));
                } catch (Exception ex) {
                    System.err.println("Nonfatal Error in finding fillins.");
                }
            }
            return listxtextRangeMarcadores;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Fatal Error: Loading template failed: searching fillins failed");
        }
    }

    /**
     * Muestra para el uso de plantillas Este ejemplo utiliza la
     * TextTemplateWithUserFields.odt archivo de la carpeta Samples. El archivo
     * contiene una serie de campos de texto del usuario (- Variables de
     * usuario) y un favorito que se utiliza para rellenar los valores distintos
     *//*
     protected void Cargatemplate() throws java.lang.Exception {
     // Crear una tabla hash pequeño que simula un conjunto de filas con columnas
     Hashtable recipient = new Hashtable();
     recipient.put("Company", "Manatee Books");
     recipient.put("Contact", "Rod Martin");
     recipient.put("ZIP", "34567");
     recipient.put("City", "Fort Lauderdale");
     recipient.put("State", "Florida");

     // Template / carga con los campos de usuario y marcador
     XComponent xTemplateComponent = CargaNuevaDesdePlantilla(
     "file:///home/adrivero/Escritorio/CalendarioSemanal.odt");

     // Obtener las interfaces XTextFieldsSupplier y XBookmarksSupplier de componente del documento

     XTextFieldsSupplier xTextFieldsSupplier = (XTextFieldsSupplier)UnoRuntime.queryInterface(
     XTextFieldsSupplier.class, xTemplateComponent);
     XBookmarksSupplier xBookmarksSupplier = (XBookmarksSupplier)UnoRuntime.queryInterface(
     XBookmarksSupplier.class, xTemplateComponent);
 
     // Acceder a los campos de texto y las colecciones TextFieldMasters
     XNameAccess xNamedFieldMasters = xTextFieldsSupplier.getTextFieldMasters();
     XEnumerationAccess xEnumeratedFields = xTextFieldsSupplier.getTextFields();
 
     // Iterar sobre tabla hash y insertar valores en amos de campo
     java.util.Enumeration keys = recipient.keys();
     while (keys.hasMoreElements()) {
     // Obtener el nombre de la columna
     String key = (String)keys.nextElement();
 
     // Acceso principal campo correspondiente
     Object fieldMaster = xNamedFieldMasters.getByName(
     "com.sun.star.text.fieldmaster.User." + key);
 
     // Consultar la interfaz XPropertySet, tenemos que establecer la propiedad Content
     XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(
     XPropertySet.class, fieldMaster);
 
     // Insertar el valor en la columna principal campo

     xPropertySet.setPropertyValue("Content", recipient.get(key));
     }
 
     // Después tenemos que actualizar la colección de campos de texto

     XRefreshable xRefreshable = (XRefreshable)UnoRuntime.queryInterface(
     XRefreshable.class, xEnumeratedFields);
     xRefreshable.refresh();
 
     // Acceso a la colección Marcadores del documento
     XNameAccess xNamedBookmarks = xBookmarksSupplier.getBookmarks();
 
     // Encuentra el marcador denominado "Suscripción"

     Object bookmark = xNamedBookmarks.getByName("Subscription");
 
     // Necesitamos su XTextRange que está disponible en getAnchor (),
     // Así que consulta para XTextContent
     XTextContent xBookmarkContent = (XTextContent)UnoRuntime.queryInterface(
     XTextContent.class, bookmark);
 
     // Obtener el ancla del marcador (su XTextRange)
     XTextRange xBookmarkRange = xBookmarkContent.getAnchor();
 
     // Establecer cadena en la posición del marcador
     xBookmarkRange.setString("subscription for the Manatee Journal");
     }*/

    /**
     * Store un documento, utilizando el MS Word 97/2000/XP filtro
     */
    protected void storeDocComponent(XComponent xDoc, String storeUrl) throws java.lang.Exception {

        XStorable xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, xDoc);
        PropertyValue[] storeProps = new PropertyValue[0];
        /*  storeProps[0] = new PropertyValue();
         storeProps[0].Name = "FilterName";
         storeProps[0].Value = "MS Word 97"; */
        xStorable.storeToURL(storeUrl, storeProps);
    }

    public String getNombre() {
        return nombre;
    }

    public XComponentContext getxRemoteContext() {
        return xRemoteContext;
    }

    public XMultiComponentFactory getxRemoteServiceManager() {
        return xRemoteServiceManager;
    }

    public Object getDesktop() {
        return desktop;
    }

    public XComponentLoader getxComponentLoader() {
        return xComponentLoader;
    }

    public XTextTable getxTable() {
        return xTable;
    }

    public Object getTable() {
        return table;
    }

    public List<XTextTable> getAllxTables() {
        return allxTables;
    }

    public XPropertySet getxTableProps() {
        return xTableProps;
    }

    public String getDireccionTmp() {
        return direccionTmp;
    }

    public XComponent getxWriterComponent() {
        return xWriterComponent;
    }

    public XTextDocument getxTextDocument() {
        return xTextDocument;
    }

    public XText getxText() {
        return xText;
    }

    public List<XTextRange> getListxtextRangeMarcadores() {
        return listxtextRangeMarcadores;
    }

    public void leeTabla() throws UnknownPropertyException, com.sun.star.lang.IllegalArgumentException, WrappedTargetException, PropertyVetoException, com.sun.star.uno.Exception {

        // first query the XTextTablesSupplier interface from our document
        XTextTablesSupplier xTablesSupplier = (XTextTablesSupplier) UnoRuntime.queryInterface(
                XTextTablesSupplier.class, xWriterComponent);
        // get the tables collection
        XNameAccess xNamedTables = xTablesSupplier.getTextTables();

        // now query the XIndexAccess from the tables collection
        xIndexedTables = (XIndexAccess) UnoRuntime.queryInterface(
                XIndexAccess.class, xNamedTables);
///////////////////////////////////////////////////////////////////////////////
     /*    
         // get internal service factory of the document
         XMultiServiceFactory xWriterFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
         XMultiServiceFactory.class, xWriterComponent);


         // insert TextTable
         Object table1 = xWriterFactory.createInstance("com.sun.star.text.TextTable");
          
         // Create a new table from the document's factory
         XTextTable   xTable1 = (XTextTable) UnoRuntime.queryInterface( 
         XTextTable.class,table1  );
         // Specify that we want the table to have 4 rows and 4 columns


         xTable1.initialize( 2, 2 );

         XTextCursor xTextCursor = createTextCursor(xTextDocument.getText()); 
         // Insert the table into the document
         xText.insertTextContent( xTextCursor, xTable1, false);
         // Get an XIndexAccess of the table rows
         XTableRows xRows1 = xTable1.getRows();

         */

///////////////////////////////////////////////////////////

        // we need properties
        xTableProps = null;

        // get the tables
        for (int i = 0; i < xIndexedTables.getCount(); i++) {
            table = xIndexedTables.getByIndex(i);
            // the properties, please!


            xTableProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, table);
            this.xTable = (XTextTable) UnoRuntime.queryInterface(
                    XTextTable.class, table);
            this.allxTables.add(xTable);
//////////////////
 /*com.sun.star.sdbcx.XDataDescriptorFactory 
             XDataDescriptorFactory= (com.sun.star.sdbcx.XDataDescriptorFactory) UnoRuntime.queryInterface(
             XPropertySet.class, xTable);
             */
//////////////////////////
        }
    }

    protected void TextTableExample() {
        try {

            // first query the XTextTablesSupplier interface from our document
            XTextTablesSupplier xTablesSupplier = (XTextTablesSupplier) UnoRuntime.queryInterface(
                    XTextTablesSupplier.class, xWriterComponent);
            // get the tables collection
            XNameAccess xNamedTables = xTablesSupplier.getTextTables();

            // now query the XIndexAccess from the tables collection
            xIndexedTables = (XIndexAccess) UnoRuntime.queryInterface(
                    XIndexAccess.class, xNamedTables);

            // we need properties
            xTableProps = null;

            // get the tables
            for (int i = 0; i < xIndexedTables.getCount(); i++) {
                table = xIndexedTables.getByIndex(i);
                // the properties, please!
                xTableProps = (XPropertySet) UnoRuntime.queryInterface(
                        XPropertySet.class, table);

                this.xTable = (XTextTable) UnoRuntime.queryInterface(
                        XTextTable.class, table);

                // color the table light green in format 0xRRGGBB
                //  xTableProps.setPropertyValue("BackColor", new Integer(0xC8FFB9));

            }////////////////////////////////////////////////////////////////////////////////

            // get internal service factory of the document
            XMultiServiceFactory xWriterFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, xWriterComponent);

            // insert TextTable
            this.table = xWriterFactory.createInstance("com.sun.star.text.TextTable");
            XTableRows xRows = xTable.getRows();
            // Create a new table from the document's factory

            /*     
             this.xTable = (XTextTable) UnoRuntime.queryInterface( 
             XTextTable.class,table  );*/
            // Specify that we want the table to have 4 rows and 4 columns

            // xTable.initialize( 2, 2 );

            //  XTextCursor xTextCursor = createTextCursor(xTextDocument.getText()); 
            // Insert the table into the document
//        xText.insertTextContent( xTextCursor, xTable, false);
            // Get an XIndexAccess of the table rows
            //    XTableRows xRows = xTable.getRows();

            // Access the property set of the first row (properties listed in service description:
            //com.sun.star.text.TextTableRow)
              /* XPropertySet xRow = (XPropertySet) UnoRuntime.queryInterface( 
             XPropertySet.class, xRows.getByIndex ( 0 ) );*/
            // If BackTransparant is false, then the background color is visible
            // xRow.setPropertyValue( "BackTransparent", xTableProps.getPropertyValue("BackTransparent"));
            // Specify the color of the background to be dark blue
            // Access the property set of the whole table

            // We want visible background colors
            //   xTableProps.setPropertyValue( "BackTransparent", xTableProps.getPropertyValue("BackTransparent"));
            // que salte a otra pagina si no da el largo
            xTableProps.setPropertyValue("Split", Boolean.FALSE);

            // set the text (and text colour) of all the cells in the first row of the table
            //    insertTextIntoCell( "B5", "loca", xTable );
            //   insertTextIntoCell( "B1", "Diaa", xTable );

            // Insert random numbers into the first this three cells of each
            // remaining row
            // xTable.getCellByName( "A2" ).setValue( getRandomDouble() );
            ///   xTable.getCellByName( "B5" ).setValue( getRandomDouble() );
            // xRows.insertByIndex(6,1);

            // Get row 7 by index (interface XIndexAccess)

            xRows.insertByIndex(8, 4);

            for (int i = 0; i < xRows.getCount(); i++) {
                if (i == 6) {
                    i += 2;
                }
                XPropertySet xRowGande = (XPropertySet) UnoRuntime.queryInterface(
                        XPropertySet.class, xRows.getByIndex(i));
                xRowGande.getPropertySetInfo().hasPropertyByName("Height");
                if (i == 0 || i % 2 == 0) {
                    xRowGande.setPropertyValue("BackColor", new Integer(6710932));
                    xRowGande.setPropertyValue("Height", new Integer(10));
                } else {
                    xRowGande.setPropertyValue("Height", new Integer(5000));
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * This method sets the text colour of the cell refered to by sCellName to
     * white and inserts the string sText in it
     */
    public void insertTextIntoCell(String sCellName, String sText, XTextTable xTable) throws UnknownPropertyException, PropertyVetoException, WrappedTargetException, com.sun.star.lang.IllegalArgumentException {
        // Access the XText interface of the cell referred to by sCellName
        XText xCellText = (XText) UnoRuntime.queryInterface(
                XText.class, xTable.getCellByName(sCellName));
        // Set the text in the cell to sText

        xCellText.setString(sText);
    }

    public void addTextIntoCell(String sCellName, String sText, String separator, XTextTable xTable) throws UnknownPropertyException, PropertyVetoException, WrappedTargetException, com.sun.star.lang.IllegalArgumentException {
        // Access the XText interface of the cell referred to by sCellName
        XText xCellText = (XText) UnoRuntime.queryInterface(
                XText.class, xTable.getCellByName(sCellName));
        // Set the text in the cell to sText
        String viejo = xCellText.getString();
        xCellText.setString(viejo + separator + sText);
    }

    /**
     * This method returns a random double which isn't too high or too low
     */
    protected double getRandomDouble() {
        Random r = new Random();
        return (r.nextInt(10));
    }

    public static XTextCursor createTextCursor(Object oCursorContainer) {
        XSimpleText xText = UnoRuntime.queryInterface(XSimpleText.class, oCursorContainer);
        return xText.createTextCursor();
    }

    /**
     * Convenience method for inserting some cells into a table.
     *
     * @param table
     * @param start
     * @param count
     */
    public void insertTableRows(Object table, int start, int count) {

        XTableRows rows = UnoRuntime.queryInterface(XTextTable.class, table).getRows();
        rows.insertByIndex(start, count);

    }

    public boolean DividirCelda(short splitTo, String cellName, XTextTable xtable) throws IndexOutOfBoundsException, WrappedTargetException {

        // Access the XPropertySet of the table
        // Access the XPropertySet of the table

        // Get an XIndexAccess to all table rows
        XIndexAccess xRows =
                xtable.getRows();
        // Get the first row in the table
        XPropertySet xRow;

        xRow = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class,
                xRows.getByIndex(0));

        /*        
         // And let's make it dark blue
         xRow.setPropertyValue( "BackColor",new Integer(6710932));
         // Put a description of the table contents into the first cell
         insertTextIntoCell( "A1", "AutoText Groups", xTable);
         */
        // Create a table cursor pointing at the second cell in the first column
        XTextTableCursor xTableCursor = xtable.createCursorByCellName(cellName);

        // Loop over the group names

        // Get the name of the current cell
        String sCellName = xTableCursor.getRangeName();
        // Get the XText interface of the current cell 
        XText xCellText = (XText) UnoRuntime.queryInterface(
                XText.class, xtable.getCellByName(sCellName));

        // Set the cell contents of the current cell to be

        // Get the titles of each autotext block in this group

        // Split the current cell vertically into two seperate cells
        return xTableCursor.splitRange(splitTo, true);
        /*// Put the cursor in the newly created right hand cell
         //and select it
         xTableCursor.goRight ( (short) 1, false );
         // Split this cell horizontally to make a seperate cell
         // for each Autotext block
         */

        //////////////////////////////////////
    }

    public XPropertySet[] getxCellStyleBaseAll() throws IndexOutOfBoundsException, WrappedTargetException {
        XPropertySet[] xCellStyleBaseAll = new XPropertySet[2];
        XTextTableCursor xTableCursor = getAllxTables().get(0).createCursorByCellName("A1");
        XPropertySet xCellStyleBase = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xTableCursor);
        /*  for (int k = 0; k < xCellStyleBase.getPropertySetInfo().getProperties().length; k++) {

         System.out.println(xCellStyleBase.getPropertySetInfo().getProperties()[k].Name);
         }*/
        xCellStyleBaseAll[0] = xCellStyleBase;
        XTableRows xRows = getAllxTables().get(0).getRows();

        xCellStyleBase = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xRows.getByIndex(2));

        /*  for (int k = 0; k < xCellStyleBase.getPropertySetInfo().getProperties().length; k++) {

         System.out.println(xCellStyleBase.getPropertySetInfo().getProperties()[k].Name);
         }*/
        xCellStyleBaseAll[1] = xCellStyleBase;
        return xCellStyleBaseAll;
    }

    public void AddTables(int count, JLabel label) throws com.sun.star.lang.IllegalArgumentException, UnsupportedFlavorException, IndexOutOfBoundsException, WrappedTargetException {
        //locura
        // query its XDesktop interface, we need the current component
        XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(
                XDesktop.class, desktop);
// retrieve the current component and access the controller
        XComponent xCurrentComponent = xDesktop.getCurrentComponent();
// get the XModel interface from the component
        XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
                xCurrentComponent);
// the model knows its controller
        XController xController = xModel.getCurrentController();
// Part 3:
//XTextViewCursorSupplier iTextViewCursorSupplier = QI.XTextViewCursorSupplier(iController);
        XTextViewCursorSupplier iTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(
                XTextViewCursorSupplier.class, xController);
        XTextViewCursor iTextViewCursor = iTextViewCursorSupplier.getViewCursor();
        iTextViewCursor.gotoEnd(true);

// Part 4:
//XTransferableSupplier iTransferableSupplier = QI.XTransferableSupplier(xController);
        XTransferableSupplier xTransferableSupplier = UnoRuntime.queryInterface(
                XTransferableSupplier.class, xController);

        XTransferable iTransferable = xTransferableSupplier.getTransferable();

// Part 5 (TODO): Move the cursor.
        //   XSelectionSupplier xSelectionSupplier = QI.XSelectionSupplier(iController);
        XSelectionSupplier xSelectionSupplier = UnoRuntime.queryInterface(XSelectionSupplier.class, xController);

        String[] arrCellNames = xTable.getCellNames();
        XTextTableCursor xTextTableCursor = xTable.createCursorByCellName(arrCellNames[0]);
        xTextTableCursor.gotoCellByName(arrCellNames[arrCellNames.length - 1], true);
//XCellRange xTableCellRange = QI.(XCellRange(xTable);
        XCellRange xTableCellRange = UnoRuntime.queryInterface(XCellRange.class, xTable);

        XCellRange xCellRange = xTableCellRange.getCellRangeByName(xTextTableCursor.getRangeName());
        xSelectionSupplier.select(xCellRange);
// Part 6:
        iTextViewCursor.goRight((short) (1), false);

        for (int i = 0; i < count; i++) {
            manipulateText(" ");
            xTransferableSupplier.insertTransferable(iTransferable);
            if (i == count - 1) {
                label.setSize(399, label.getSize().height);
            } else {
                label.setSize(getSizeACrecer(399, i, count), label.getSize().height);
            }
        }
        // get the tables
        allxTables.clear();
        for (int i = 0; i < xIndexedTables.getCount(); i++) {
            table = xIndexedTables.getByIndex(i);
            // the properties, please!

            xTableProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, table);
            this.xTable = (XTextTable) UnoRuntime.queryInterface(
                    XTextTable.class, table);
            this.allxTables.add(xTable);
        }
        this.listxtextRangeMarcadores = busca(xTextDocument);
    }

    public void SetColorCell(XTextTable xtableact, String CellName, Integer color) throws UnknownPropertyException, PropertyVetoException, com.sun.star.lang.IllegalArgumentException, WrappedTargetException {
        XTextTableCursor xTableCursor = xtableact.createCursorByCellName(CellName);
        XPropertySet xCell2 = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xTableCursor);

        xCell2.setPropertyValue("BackColor", color);
    }

    public void SetProtectedCell(String CellName) throws UnknownPropertyException, WrappedTargetException, WrappedTargetException, PropertyVetoException, com.sun.star.lang.IllegalArgumentException {
        XTextTableCursor xTableCursor = xTable.createCursorByCellName(CellName);
        XPropertySet xCell2 = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xTableCursor);

        //boolean bol = (boolean) xCell2.getPropertyValue("IsProtected");
        xCell2.setPropertyValue("IsProtected", Boolean.TRUE);
        // bol = (boolean) xCell2.getPropertyValue("IsProtected");
    }

    public void PushHeadText(int pos, String text) {
        getListxtextRangeMarcadores().get(pos).setString(text);

    }

    public void manipulateText(String text) {

        // Crear cursor para seleccionar y formatear
        XTextCursor xTextCursor = xText.createTextCursor();
        /*     XPropertySet xCursorProps = (XPropertySet) UnoRuntime.queryInterface(
         XPropertySet.class, xTextCursor);

         // Usar el cursor para seleccionar "Estaba tendido" y aplicar cursiva negrita
         xTextCursor.gotoStart(false);
         xTextCursor.goRight((short) 6, true);
         try {
         // A partir CharacterProperties
         xCursorProps.setPropertyValue("CharPosture",
         com.sun.star.awt.FontSlant.ITALIC);
         xCursorProps.setPropertyValue("CharWeight",
         new Float(com.sun.star.awt.FontWeight.BOLD));
         } catch (UnknownPropertyException | PropertyVetoException | com.sun.star.lang.IllegalArgumentException | WrappedTargetException ex) {
         Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
        // Añadir más texto al final del texto utilizando insertString
        xTextCursor.gotoEnd(false);
        xText.insertString(xTextCursor, "\n" + text, false);
    }

    public void setHeightofRow(XTableRows xRows, int pos, Integer heigh) throws IndexOutOfBoundsException, IndexOutOfBoundsException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, com.sun.star.lang.IllegalArgumentException {

        XPropertySet xRow = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xRows.getByIndex(pos));
        xRow.setPropertyValue(
                "Height", heigh);
    }

    public int getSizeACrecer(int totalProgres, int it, int totalIt) {
        return totalProgres * it / totalIt;
    }
}
