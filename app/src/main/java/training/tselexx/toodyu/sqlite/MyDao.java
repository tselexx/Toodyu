package training.tselexx.toodyu.sqlite;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MyDao {

    @Insert (onConflict = REPLACE)
    void addEvent(Event event);

    @Query("select * from events")
    List<Event> getEvents();


    @Query("SELECT * FROM events WHERE col_year = :year  AND " +
            " col_month = :month  AND  col_day = :day " +
            " ORDER BY col_hour, col_minute, col_stamp" )
    List<Event> findEventWithDate(int year, int month, int day);



    @Query("DELETE FROM events WHERE " +
            "(col_year < :year)" +
            " OR " +
            "(col_year = :year AND col_month < :month)" +
            " OR " +
            "(col_year = :year AND col_month = :month AND  col_day < :day)"
    )
    void deletePreviousEvents(int year, int month, int day);



    @Query("DELETE FROM events WHERE col_year = :year  AND " +
            " col_month = :month  AND  col_day = :day  AND " +
            " col_hour = :hour AND col_minute = :minute AND " +
            " col_stamp = :stamp")
    void deleteEvent(int year, int month, int day, int hour, int minute, Long stamp);


    @Query("UPDATE events SET " +
            "col_year = :newyear, col_month = :newmonth, col_day = :newday, " +
            "col_hour = :newhour, col_minute = :newminute, col_message = :newmessage " +
            "WHERE col_year = :year AND col_month = :month " +
            "AND col_day = :day AND col_hour = :hour " +
            "AND col_minute = :minute AND col_stamp = :stamp")
    void updateEvent(int year, int month, int day, int hour, int minute, Long stamp,
                            int newyear, int newmonth, int newday,
                            int newhour, int newminute, String newmessage);

    @Insert (onConflict = REPLACE)
    void addAgenda(Agenda agenda);


    @Query("select * from agenda")
    Agenda getAgenda();

    @Query("UPDATE agenda SET col_sync_agenda = :new_sync_agenda")
    void updateAgenda(boolean new_sync_agenda);

}

