package com.tselexx.toodyu

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.tselexx.toodyu.Common_Util_functions.Companion.REQUEST_CODE_CALENDRIER
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.boolCreaMsg
import com.tselexx.toodyu.Common_Util_functions.Companion.boolfind
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.calendarDay
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListJour
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListPrev
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdateCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdateJour
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdatePrev
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.messageEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.vC
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.calendrier_fragment.view.*
import training.tselexx.toodyu.sqlite.Event
import java.util.*


class CalendrierFragment : Fragment(), IOnBackPressed

{
    override fun onBackPressed(): Boolean {
        boolBackPressed = false
        return boolBackPressed
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vC = inflater.inflate(R.layout.calendrier_fragment, container, false)

//        Traitement de l'affichage des dates

        trait_affichage_date()
        vC!!.calendarView.setOnDateChangedListener {
            _, date, _ ->
            traitement_date_selected(date)
//            calendrierDateListener?.onClickDate(date)

        }
        //ici mettre la logique sur MaterialCalendar

        return vC
    }


    public fun trait_affichage_date() {
        vC!!.calendarView.setSelectedDate(calendar!!.getTime())
        //rajout
        vC!!.calendarView.removeDecorators()
//
        eventdateCur.clear()
        eventdatePrev.clear()
        eventdateJour.clear()



        var datejour = CalendarDay.from(yearDateOfDay, monthDateOfDay - 1, dayDateOfDay)

        if (!eventListCur.isEmpty() )
        {
            eventListCur.forEach {
                calendarDay = CalendarDay.from(it.event_year, it.event_month - 1, it.event_day)
                eventdateCur.add(calendarDay!!)
            }
            vC!!.calendarView.addDecorator(DateDecorator(1, 0, eventdateCur))
        }
        if (!eventListJour.isEmpty() )
        {
            eventdateJour.add(datejour)
            vC!!.calendarView.addDecorator(DateDecorator(3, 0, eventdateJour))
        }
        else
        {
            eventdateJour.add(datejour)
            vC!!.calendarView.addDecorator(DateDecorator(2, 0, eventdateJour))
        }
        if (!eventListPrev.isEmpty())
        {
            eventListPrev.forEach {
                calendarDay = CalendarDay.from(it.event_year,it.event_month - 1,it.event_day)
                eventdatePrev.add(calendarDay!!)
            }

            vC!!.calendarView.addDecorator(DateDecorator(4, 0, eventdatePrev))
        }
    }

    inner class DateDecorator constructor(cdecor:Int, ccolor: Int, cdates: Collection<CalendarDay>) : DayViewDecorator {
        private var dates = HashSet<CalendarDay>()
        var colored  = 0
        init {
            dates = HashSet(cdates)
            colored = cdecor
        }


        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day);         }

        override fun decorate(dayDate: DayViewFacade) {
            when(colored) {
                1 -> {
                    val drawablebg = getDrawable(contx!!, R.drawable.date_decoration)
                    dayDate.setBackgroundDrawable(drawablebg!!)
                }

                2 -> {

                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_jour)!!)

                }
                3 -> {

                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_enter)!!)
                }
                4 -> {

                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_prev)!!)
                }
            }
        }
    }

//    }


    private fun traitement_date_selected(date: CalendarDay) {
        yearDateSelected = date.year
        monthDateSelected = date.month + 1
        dayDateSelected = date.day
        boolfind = false

        if((yearDateSelected < yearDateOfDay) ||
        (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
        (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected < dayDateOfDay))
        {
            eventListPrev.forEach {
                if(it.event_year == yearDateSelected &&
                        it.event_month == monthDateSelected &&
                        it.event_day == dayDateSelected)
                {
                    boolfind = true
                }
            }
            if(boolfind)
            {
                boolCreaMsg = false
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ListeMsgFragment())
                        .addToBackStack(getString(R.string.backstack_calendrier))
                        .commit()
            }
            else
            {
                yearDateSelected = -1
                monthDateSelected = -1
                dayDateSelected = -1
                vC!!.calendarView.setSelectedDate(calendar!!.getTime())
                Toast.makeText(contx, getString(R.string.date_sup), Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            if(yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay)
            {
                eventListJour.forEach {
                    if(it.event_year == yearDateSelected &&
                            it.event_month == monthDateSelected &&
                            it.event_day == dayDateSelected)
                    {
                        boolfind = true
                    }
                }
                if(boolfind)
                {
                    boolCreaMsg = true
                    activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, ListeMsgFragment())
                            .addToBackStack(getString(R.string.backstack_calendrier))
                            .commit()
                }
                else
                {
                    boolCreaMsg = true
                    traiter_processus_enreg_voix()
                }
            }
            else
            {
                eventListCur.forEach {
                    if(it.event_year == yearDateSelected &&
                            it.event_month == monthDateSelected &&
                            it.event_day == dayDateSelected)
                    {
                        boolfind = true
                    }
                }
                if(boolfind)
                {
                    boolCreaMsg = true
                    activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, ListeMsgFragment())
                            .addToBackStack(getString(R.string.backstack_calendrier))
                            .commit()
                }
                else
                {
                    boolCreaMsg = true
                    traiter_processus_enreg_voix()
                }
            }
        }

   }
    private fun traiter_processus_enreg_voix() {
        hourDateSelected = hourDateOfDay
        minuteDateSelected = minuteDateOfDay

        val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                hourDateSelected = hourOfDay
                minuteDateSelected = minute
                controle_time()
                }


        val timePickerDialog = TimePickerDialog(contx,
                timeSetListener,
                12,
                0,
                false)

        timePickerDialog.setTitle(getString(R.string.choose_time))
        timePickerDialog.setOnCancelListener {
            vC!!.calendarView.setSelectedDate(calendar!!.getTime())

        }
        timePickerDialog.setOnDismissListener {
            vC!!.calendarView.setSelectedDate(calendar!!.getTime())
        }

        timePickerDialog.show()
    }
    fun controle_time() {
        if(yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay)
        {
            if((hourDateSelected < hourDateOfDay) ||
                    (hourDateSelected == hourDateOfDay && minuteDateSelected <= minuteDateOfDay))
            {
                Toast.makeText(contx, getString(R.string.time_sup), Toast.LENGTH_SHORT).show()
            }else trait_autorise_enreg()

        }else trait_autorise_enreg()
    }


    fun trait_autorise_enreg() {
        //affichage de la boîte de dialogue pour enregistrer
        var alertBuilder = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        alertBuilder
                // if the dialog is cancelable
                .setCancelable(true)
                .setIcon(R.drawable.microphone_50)
                .setTitle(getString(R.string.recording))
                .setMessage(getString(R.string.recording_button_ok))
                // positive button text and action
                .setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener {
                    _, _ ->
                    getSpeechInput()
                })
                // negative button text and action
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener {
                    _, _ ->
                    vC!!.calendarView.setSelectedDate(calendar!!.getTime())

                })
        val alert = alertBuilder.create()
        alert.show()
    }




    private fun getSpeechInput(){
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().displayLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.to_speek))

        try {
            this.startActivityForResult(intent, REQUEST_CODE_CALENDRIER)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(contx, getString(R.string.smartphone_dont_fit), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_CODE_CALENDRIER -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    var stringresult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    messageEnreg = stringresult!![0]
                    enregOK()
                }
            }
        }
    }
    private fun enregOK() {
        calendar = Calendar.getInstance()
        val event = Event(0,
                yearDateSelected,
                monthDateSelected,
                dayDateSelected,
                hourDateSelected,
                minuteDateSelected,
                messageEnreg,
                calendar!!.timeInMillis)
        myAppDataBase?.myDao()?.addEvent(event)
        eventListGal.add(event)
        if (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay &&
                dayDateSelected == dayDateOfDay) {
            eventListJour.add(event)
        }
        else
        {
            eventListCur.add(event)
        }
        if (suiteAgenda)
            addToGoogleCalendar (
                    yearDateSelected,
                    monthDateSelected ,
                    dayDateSelected,
                    hourDateSelected,
                    minuteDateSelected,
                    messageEnreg)

        activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ListeMsgFragment())
                .addToBackStack(getString(R.string.backstack_calendrier))
                .commit()

    }

    private fun addToGoogleCalendar(year : Int, month : Int, day : Int, hour : Int, minute : Int, message : String) {

        var beginTime = Calendar.getInstance();
        beginTime.set(year, month-1, day, hour, minute);
        var endTime = Calendar.getInstance();
        endTime.set(year, month-1, day, hour+1, minute);
        val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, getString(R.string.sync_title))
                .putExtra(CalendarContract.Events.DESCRIPTION, message)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent);
    }



}