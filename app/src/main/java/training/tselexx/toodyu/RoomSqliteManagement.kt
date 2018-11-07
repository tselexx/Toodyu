package training.tselexx.toodyu

import android.arch.persistence.room.Room
import com.tselexx.toodyu.Common_Util_functions.Companion.appContx
import com.tselexx.toodyu.Common_Util_functions.Companion.initRoom
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import training.tselexx.toodyu.sqlite.Agenda
import training.tselexx.toodyu.sqlite.Event
import training.tselexx.toodyu.sqlite.MyAppDataBase

class RoomSqliteManagement {

    fun initRoom() {
    //initialisation du contexte Room Sqlite
        if (!initRoom)
        {
            myAppDataBase = Room.databaseBuilder(appContx!! , MyAppDataBase::class.java, "eventDB").allowMainThreadQueries().build()
            initRoom = true
        }
    }

    fun rechercheMessage() : MutableList<Event> {
    //recherche de tous les events stockés
        var events : MutableList<Event> = myAppDataBase?.myDao()?.getEvents()!!
        return events
    }



    fun deleteAllPreviousMessage(year : Int, month : Int, day : Int)  {
        //suppression de tous les events de date < à date donnée
        myAppDataBase?.myDao()?.deletePreviousEvents(year,  month,  day)
    }

    fun addMessage(event : Event) {
        //recherche de tous les events stockés
        myAppDataBase?.myDao()?.addEvent(event)
    }


    fun updateMessage(year: Int,month: Int,day: Int, hour : Int, minute :Int, stamp: Long,
                             newyear: Int, newmonth : Int, newday : Int, newhour : Int, newminute : Int,
                             newmsg : String) {
        //recherche de tous les events stockés
        myAppDataBase?.myDao()?.updateEvent(
                year,
                month,
                day,
                hour,
                minute,
                stamp,
                newyear,
                newmonth,
                newday,
                newhour,
                newminute,
                newmsg)
    }

    fun deleteMessage(year: Int,month: Int,day: Int, hour : Int, minute :Int, stamp: Long) {
        //recherche de tous les events stockés
        myAppDataBase?.myDao()?.deleteEvent(
                year,
                month,
                day,
                hour,
                minute,
                stamp)
    }

    fun rechercheSyncAgenda() : Agenda {
        //recherche de tous les events stockés
        val agenda = myAppDataBase?.myDao()?.getAgenda()!!
        return agenda
    }

    fun updateSyncAgenda(suite : Boolean){
        //recherche de tous les events stockés
        myAppDataBase?.myDao()?.updateAgenda(suite)

    }

}