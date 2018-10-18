package training.tselexx.toodyu.sqlite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "events")
public class Event
{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "col_year")
    @NonNull
    private int event_year;

    @ColumnInfo(name = "col_month")
    @NonNull
    private int event_month;

    @ColumnInfo(name = "col_day")
    @NonNull
    private int event_day;

    @ColumnInfo(name = "col_hour")
    @NonNull
    private int event_hour;

    @ColumnInfo(name = "col_minute")
    @NonNull
    private int event_minute;

    @ColumnInfo(name = "col_message")
    @NonNull
    private String event_message;

    @ColumnInfo(name = "col_stamp")
    @NonNull
    private Long event_stamp;

    public Event(int id, @NonNull int event_year, @NonNull int event_month, @NonNull int event_day,
                 @NonNull int event_hour, @NonNull int event_minute, @NonNull String event_message,
                 @NonNull Long event_stamp) {
        this.id = id;
        this.event_year = event_year;
        this.event_month = event_month;
        this.event_day = event_day;
        this.event_hour = event_hour;
        this.event_minute = event_minute;
        this.event_message = event_message;
        this.event_stamp = event_stamp;
    }
    @Ignore
    public Event(@NonNull int event_year, @NonNull int event_month, @NonNull int event_day) {
        this.event_year = event_year;
        this.event_month = event_month;
        this.event_day = event_day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_year() {
        return event_year;
    }

    public void setEvent_year(int event_year) {
        this.event_year = event_year;
    }

    public int getEvent_month() {
        return event_month;
    }

    public void setEvent_month(int event_month) {
        this.event_month = event_month;
    }

    public int getEvent_day() {
        return event_day;
    }

    public void setEvent_day(int event_day) {
        this.event_day = event_day;
    }

    public int getEvent_hour() {
        return event_hour;
    }

    public void setEvent_hour(int event_hour) {
        this.event_hour = event_hour;
    }

    public int getEvent_minute() {
        return event_minute;
    }

    public void setEvent_minute(int event_minute) {
        this.event_minute = event_minute;
    }

    public String getEvent_message() {
        return event_message;
    }

    public void setEvent_message(String event_message) {
        this.event_message = event_message;
    }

    public Long getEvent_stamp() {
        return event_stamp;
    }

    public void setEvent_stamp(Long event_stamp) {
            this.event_stamp = event_stamp;
    }
}
