package com.nutriendome

import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SchemaHelper extends SQLiteOpenHelper{
    
    private static final String DATABASE_NAME = "nutriendome.db";
    
    //la version de la base de datos
    private static final int DATABASE_VERSION = 1;
    
    SchemaHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + "Temporadas"
                + " (" + "idTemporada" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombreTemporada" + "TEXT);");
        
        //Crear la tabla de los cursos
        db.execSQL("CREATE TABLE " + "Comidas" + " ("
                + "idComida INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "tipoComida" + " TEXT,"
                + "fechaComida " + "DATE, "
                + "idTemporada" + " INTEGER," 
                + "FOREIGN KEY (idTemporada)" + " REFERENCES " + " Temporadas " + "(idTemporada)"
                + ");");
        
        //Creando el mapeo de clases
        db.execSQL("CREATE TABLE " + " Recetas" +
                " (" + "idReceta" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombreReceta" + " TEXT,"
                + "preparacionReceta" + " TEXT,"
                + "tiempoReceta" + " INTEGER,"
                + "imagenReceta" + " TEXT,"
                + "idComida" + " INTEGER,"
                + "FOREIGN KEY (idComida)" + " REFERENCES " + " Comidas " + "(idComida)"
                + ");");

        //Creando el mapeo de clases
        db.execSQL("CREATE TABLE " + "Recetas_Alimentos" +
                " (" + "idReceta" + " INTEGER,"
                + " idAlimento" + " INTEGER,"
                + " cantidadAlimento" + " INTEGER,"
                + " medidasAlimento" + " TEXT,"
                + "FOREIGN KEY (idAlimento)" + " REFERENCES Alimentos" +  " (idAlimento) "
                + "FOREIGN KEY (idReceta)" + " REFERENCES Recetas" +  " (idAlimento) "
                + ");");

        db.execSQL("CREATE TABLE " + "Alimentos" +
                " (" + "idAlimento" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombreAlimento" + " TEXT,"
                + " caloriasAlimento" + " INTEGER,"
                + " grupoAlimento" + " TEXT,"
                + ");");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
        Log.w("LOG_TAG", "Upgrading database from version "
                + oldVersion + " to " + newVersion + ",which will destroy all old data");
                //KILL PREVIOUS TABLES IF UPGRADED
                db.execSQL("DROP TABLE IF EXISTS " + "Temporadas");
                db.execSQL("DROP TABLE IF EXISTS " + "Comidas");
                db.execSQL("DROP TABLE IF EXISTS " + "Recetas");
                db.execSQL("DROP TABLE IF EXISTS " + "Recetas_Alimentos");
                db.execSQL("DROP TABLE IF EXISTS " + "Alimentos");
                //Creando una nueva instancia del esquema
                onCreate(db);
    }
    
    //Metodos wrapper para agregar temporadas
    public long addTemporadas(String nombreTemporada){
        //Creando un CONTENTVALUE
        ContentValues cv = new ContentValues();
        cv.put("nombreTemporada", nombreTemporada);
        //Recuperando una base de datos writeable para insertar
        SQLiteDatabase bd = getWritableDatabase();
        long result = bd.insert("Temporadas", null, cv);
        bd.close();
    }
    
    //Metodo de envoltura para insertar un curso
    public long addComidas(String tipoComida, Date fechaComida, String nombreTemporada){        
        //Recuperando una base de datos writeable para insertar
        SQLiteDatabase db_write = getWritableDatabase();
        SQLiteDatabase db_read = getReadableDatabase();
        int id_temporadas;
        //Columnas y argumentos
        String[] projection = new String[]{"idTemporada"};
        String[] criteria = new String[]{nombreTemporada};
        //Cursor
        Cursor cursor = db.query("Temporadas", projection, " nombreTemporada = ?", criteria, null, null, null, null);

        if(cursor.moveToFirst()){
            id_temporadas = c.getInt(0);
        }
        ContentValues cv = new ContentValues();
        cv.put("tipoComida", tipoComida);
        cv.put("fechaComida", fechaComida);
        cv.put("idTemporada", id_temporadas);
        long result = sd.insert("Comidas", null, cv);
        db_read.close();
        db_write.close();
        return result;
    }
    
    //Metodo de envoltura para insertar un curso
    public long addRecetas(String nombreReceta, String preparacionReceta, int tiempoReceta, String imagenReceta, int idComida){
        //Objetos accesadores ala bd
        SQLiteDatabase db_write = getWritableDatabase();
        SQLiteDatabase db_read = getReadableDatabase();
        int id_Comidas;
        //Columnas y argumentos
        String[] projection = new String[]{"idComida"};
        String[] criteria = new String[]{idComida};
        //Cursor
        Cursor cursor = db.query("Comidas", projection, " idComida = ?", criteria, null, null, null, null);
        if(cursor.moveToFirst()){
            id_Comidas = c.getInt(0);
        }
        ContentValues cv = new ContentValues();
        cv.put("nombreReceta", nombreReceta);
        cv.put("preparacionReceta", preparacionReceta);
        cv.put("tiempoReceta", tiempoReceta);
        cv.put("imagenReceta", imagenReceta);
        cv.put("idComida", id_Comidas);
        
        long result = sd.insert("Recetas", null, cv);
        db_read.close();
        db_write.close();
        return result;
    }      
    public long addAlimentos(String nombreAlimento, int caloriasAlimento, String grupoAlimento){
        ContentValues cv = new ContentValues();
        cv.put("nombreAlimento", nombreAlimento);
        cv.put("caloriasAlimento", caloriasAlimento);
        cv.put("grupoAlimento", grupoAlimento);
        
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Alimentos", "Alimentos", cv);
        sd.close();
        return result;
    }

    ////////////////////////////////////////////////////      Empiezan las funciones ORM //////////////////////////

    //Recuperamos un cursor con la temporada dada en un ID
    //**************************************************************
    public Cursor getTemporadas(String nombre){
        SQLiteDatabase sd = getReadableDatabase();
        String[] cols = new String[]{"nombreTemporada"};
        String[] selecctionArgs = new String[]{nombre};
        
        //Querying la base de datos
        Cursor c = sd.query("Temporadas", cols, "nombreTemporada" + "= ? ", selecctionArgs, null, null, null);
        return c;
    }   
    
    //Obtenemos la comida segun sea el caso del ID esto puede cambiar
    // **************************************************************
    public Cursor getRecetas(int []consulta, String ingrediente[]) {
        SQLiteDatabase sd = getReadableDatabase();
        //Indice 0 = temporadas
        //0-primavera : 3-invierno
        //INdice 1 = comida (desayuno, comida y cena)
        //Indice 2 = tipo de comida (bebida, sopa, platofuerte )
        //Parametros de consulta dentro de la  receta
        String[] temporada = {"primavera", "verano", "otono", "invierno"};
        String[] comidasTiempos = {"desayuno", "comida", "cena"};
        String[] tipos = {"bebida", "sopa", "platofuerte"};
        ///////////////////////////////////////////
        String query_getIngredientes = "SELECT nombreTemporada, tipoComida, fechaComida, id";


    
    }

    public Cursor getIngredientes(){
        //devuelve todos los ingredientes
        SQLiteDatabase sd = getReadableDatabase();
        String[] projection = new String{"nombreAlimento", "caloriasAlimento", "grupoAlimento"};
        String[] criteria = new String{};
        Cursor cursor = sd.query("Alimentos", projection, null, null, null, null, null);
        return cursor;
    }
}
