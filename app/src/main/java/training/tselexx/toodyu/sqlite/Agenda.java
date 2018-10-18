package training.tselexx.toodyu.sqlite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "agenda")
public class Agenda
{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "col_sync_agenda")
    @NonNull
    private boolean sync_agenda = false;



    public Agenda(int id,@NonNull boolean sync_agenda) {
        this.id = id;
        this.sync_agenda = sync_agenda;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public boolean getSync_agenda() {
        return sync_agenda;
    }

    public void setSync_agenda(boolean  sync_agenda) {
        this.sync_agenda = sync_agenda;
    }

}
