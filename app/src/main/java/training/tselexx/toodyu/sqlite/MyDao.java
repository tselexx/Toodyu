package training.tselexx.toodyu.sqlite;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MyDao {

    @Insert (onConflict = REPLACE)
    public void addEvent(Event event);

    @Query("select * from events")
    public List<Event> getEvents();


    @Query("SELECT * FROM events WHERE col_year = :year  AND " +
            " col_month = :month  AND  col_day = :day " +
            " ORDER BY col_hour, col_minute, col_stamp" )
    public List<Event> findEventWithDate(int year, int month, int day);



    @Query("DELETE FROM events WHERE " +
            "(col_year < :year)" +
            " OR " +
            "(col_year = :year AND col_month < :month)" +
            " OR " +
            "(col_year = :year AND col_month = :month AND  col_day < :day)"
    )
    public void deletePreviousEvents(int year, int month, int day);



    @Query("DELETE FROM events WHERE col_year = :year  AND " +
            " col_month = :month  AND  col_day = :day  AND " +
            " col_hour = :hour AND col_minute = :minute AND " +
            " col_stamp = :stamp")
    public void deleteEvent(int year, int month, int day, int hour, int minute, Long stamp);


    @Query("UPDATE events SET col_hour= :newhour, col_minute = :newminute, col_message = :newmessage " +
            "WHERE col_year = :year AND col_month = :month " +
            "AND col_day = :day AND col_hour = :hour " +
            "AND col_minute = :minute AND col_stamp = :stamp")
    public void updateEvent(int year, int month, int day, int hour, int minute, Long stamp,
                            int newhour, int newminute, String newmessage);


    @Insert (onConflict = REPLACE)
    public void addAgenda(Agenda agenda);


    @Query("select * from agenda")
    public Agenda getAgenda();

    @Query("UPDATE agenda SET col_sync_agenda = :new_sync_agenda")
    public void updateAgenda(boolean new_sync_agenda);

}

