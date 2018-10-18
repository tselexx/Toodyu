package training.tselexx.toodyu.sqlite;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Event.class, Agenda.class},version = 1)
public abstract class MyAppDataBase extends RoomDatabase {

    public abstract MyDao myDao();
}
