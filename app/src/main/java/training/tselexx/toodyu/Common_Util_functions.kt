package com.tselexx.toodyu

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import training.tselexx.toodyu.model.ModelCatEvent
import training.tselexx.toodyu.sqlite.Agenda
import training.tselexx.toodyu.sqlite.Event
import training.tselexx.toodyu.sqlite.MyAppDataBase
import java.util.*
import kotlin.collections.ArrayList

class Common_Util_functions {

    companion object {

        //variables et constantes pour classe RoomSqliteManagement
        var initRoom = false

        var eventList : MutableList<ModelCatEvent> = ArrayList()
        var categorie = -2
        var categoriemodif = -2

        var eventListEvent : MutableList<Event> = ArrayList()
        val REQUEST_PERMISSIONS = 200

        var dayDateOfDay : Int = -1
        var monthDateOfDay : Int = -1
        var yearDateOfDay : Int = -1
        var hourDateOfDay : Int = -1
        var minuteDateOfDay : Int = -1

        var yearDateSelected : Int = -1
        var monthDateSelected : Int = -1
        var dayDateSelected : Int = -1
        var hourDateSelected : Int = -1
        var minuteDateSelected : Int = -1
        var timestampEnreg : Long = -1
        var messageEnreg : String = ""

        var delfromday : Int = -1
        var delfrommonth : Int = -1
        var delfromyear : Int = -1

        var calendarDay : CalendarDay? = null
        var eventdatePrev = ArrayList<CalendarDay>()
        var eventdateCur = ArrayList<CalendarDay>()
        var eventdateJour = ArrayList<CalendarDay>()
        var REQUEST_CODE_CALENDRIER : Int = 100

        var newHour : Int = 0
        var newMinute : Int = 0
        var newDay : Int = 0
        var newMonth : Int = 0
        var newYear : Int = 0
        var newMessage: String = ""

        var backgroundColorAnimator : AnimationDrawable? = null


        var vC : View? = null
        var vL : View? = null
        var vE : View? = null
        var boolBackPressed = false


        var eventListGal : MutableList<Event> = ArrayList()


        var agenda : Agenda? = null
        var myAppDataBase : MyAppDataBase? = null
        var calendar : Calendar? = null
        var contx : Context? = null
        var appContx : Context? = null


        var suiteAgenda = false
        var menuGal : Menu? = null

        var toolbar : Toolbar? = null


    }
}