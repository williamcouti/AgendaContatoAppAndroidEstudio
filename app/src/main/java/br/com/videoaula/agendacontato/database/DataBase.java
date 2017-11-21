package br.com.videoaula.agendacontato.database;

/**
 * Created by Paulo on 28/03/2015.
 */

import android.content.Context;
import android.database.sqlite.*;

public class DataBase extends SQLiteOpenHelper{

    public DataBase(Context context)
    {
        super(context, "AGENDA", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

       db.execSQL( ScriptSQL.getCreateContato() );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
