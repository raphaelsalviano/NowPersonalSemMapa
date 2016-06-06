package br.com.ufpb.nowpersonal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.com.ufpb.nowpersonal.model.Usuario;

public class DBCoreORM extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "NowPersonalDB";
    private static final int DB_VERSION = 1;

    public DBCoreORM(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Usuario.class);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try{
            TableUtils.dropTable(connectionSource, Usuario.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
