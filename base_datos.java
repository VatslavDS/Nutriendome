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
        //Creando la tabla de estudiantes
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
                " (" + "idReceta" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " idAlimento" + " INTEGER,"
                + " cantidadAlimento" + " INTEGER,"
                + " medidasAlimento" + " TEXT,"
                + "FOREIGN KEY (idAlimento)" + " REFERENCES Alimentos" +  " (idAlimento) "
		   + ");");

        db.execSQL("CREATE TABLE " + "Alimentos" +
                " (" + "idAlimento" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombreAlimento" + " TEXT,"
                + " caloriasAlimento" + " INTEGER,"
                + " grupoAlimento" 
                + " TEXT,"
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
    
    //Metodos wrapper para agregar a un estudiantes
    public long addTemporadas(String nombreTemporada){
        //Creando un CONTENTVALUE
        ContentValues cv = new ContentValues();
        cv.put("nombreTemporada", nombreTemporada);
        
        //Recuperando una base de datos writeable para insertar
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Temporadas","Temporadas", cv);
        return result;
    }
    
    //Metodo de envoltura para insertar un curso
    public long addComidas(String tipoComida, Date fechaComida, int idTemporada){
        ContentValues cv = new ContentValues();
        cv.put("tipoComida", tipoComida);
        cv.put("fechaComida", fechaComida);
        cv.put("idTemporada", idTemporada);
        
        //Recuperando una base de datos writeable para insertar
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Comidas", "Comidas", cv);
        return result;
    }
    
    //Metodo de envoltura para insertar un curso
    public long addRecetas(String tipoComida, Date fechaComida, int idTemporada){
        ContentValues cv = new ContentValues();
        cv.put("tipoComida", tipoComida);
        cv.put("fechaComida", fechaComida);
        cv.put("idTemporada", idTemporada);
        
        //Recuperando una base de datos writeable para insertar
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Comidas", "Comidas", cv);
        return result;
    }

    //Método wrapper para enrolar un estudiante a un curso
    public long addRecetasAlimentos(int cantidadAlimento, String medidasAlimento){
        ContentValues cv = new ContentValues();
        cv.put("cantidadAlimento", cantidadAlimento);
        cv.put("medidasAlimento", medidasAlimento);
        
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Recetas_Alimentos", "Recetas_Alimentos", cv);
        return result;
    }
    
    public long addAlimentos(String nombreAlimento, int caloriasAlimento, String grupoAlimento){
        ContentValues cv = new ContentValues();
        cv.put("nombreAlimento", nombreAlimento);
        cv.put("caloriasAlimento", caloriasAlimento);
        cv.put("grupoAlimento", grupoAlimento);
        
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert("Alimentos", "Alimentos", cv);
        return result;
    }

    ////////////////////////////////////////////////////      Empiezan las funciones ORM //////////////////////////

    //Recuperamos un cursor con la temporada dada en un ID
    //**************************************************************
    public Cursor getTemporadas(int idTemporada){
        SQLiteDatabase sd = getWritableDatabase();
        String[] cols = new String[]{"idTemporada", "nombreTemporada"};
        String[] selecctionArgs = new String[]{String.valueOf(idTemporada)};
        
        //Querying la base de datos
        Cursor c = sd.query("Temporadas", cols, "idTemporada" + "= ? ", selecctionArgs, null, null, null);
        return c;
    }   
    
    //Obtenemos la comida segun sea el caso del ID esto puede cambiar
    // **************************************************************
    public Cursor getComidas(int idComida) {
        SQLiteDatabase sd = getWritableDatabase();
        String[] cols = new String[] { "idComida", "tipoComida"  "fechaComida", "idTemporada" };
        String[] selectionArgs = new String[] {
	    String.valueOf(idComida) };
        Cursor c = sd.query("Comidas", cols,
			    "idComida" + "= ?", selectionArgs, null,null, null);
        return c;
    }

    // Otro tipo de consultas ala base de datos de forma expicita
    // ************************************************************************
    // ************************************************************************
    // ************************************************************************
    // ************************************************************************
    // ************************************************************************
    // ************************************************************************
}
