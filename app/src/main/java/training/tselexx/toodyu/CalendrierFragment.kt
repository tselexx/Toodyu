package com.tselexx.toodyu

import android.app.Activity
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.tselexx.toodyu.Common_Util_functions.Companion.REQUEST_CODE_CALENDRIER
import com.tselexx.toodyu.Common_Util_functions.Companion.backgroundColorAnimator
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.calendarDay
import com.tselexx.toodyu.Common_Util_functions.Companion.categorie
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventList
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
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
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.vC
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.calendrier_fragment.view.*
import training.tselexx.toodyu.RoomSqliteManagement
import training.tselexx.toodyu.model.ModelCatEvent
import training.tselexx.toodyu.sqlite.Event
import java.util.*


class CalendrierFragment : Fragment(), IOnBackPressed


{
    val TAG = "CalendrierFragment"
//    interface CalendrierListener{
//        fun calendrier_voice_recording()
//    }
//    var calendrierlistener : CalendrierListener? = null

    override fun onBackPressed(): Boolean {
        boolBackPressed = false
        return boolBackPressed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"  onCreate")
        super.onCreate(savedInstanceState)


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.e(TAG,"  onAttach")
        yearDateSelected  = -1
        monthDateSelected  = -1
        dayDateSelected  = -1
        hourDateSelected = -1
        minuteDateSelected  = -1
        messageEnreg  = ""
        calendar = Calendar.getInstance()
        dayDateOfDay = calendar!!.get(Calendar.DAY_OF_MONTH)
        monthDateOfDay = calendar!!.get(Calendar.MONTH) + 1
        yearDateOfDay = calendar!!.get(Calendar.YEAR)
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"  onActivityCreated")
    }

    override fun onResume() {
        Log.e(TAG,"  onResume  setCurrentDate")
        super.onResume()
        vC!!.calendarView.setCurrentDate(calendar!!.getTime())
        vC!!.calendarView.setSelectedDate(calendar!!.getTime())
        if (backgroundColorAnimator != null && !backgroundColorAnimator!!.isRunning()) {
            // start the animation
            backgroundColorAnimator!!.start()
        }

        Log.e(TAG,"  onResume")
    }

    override fun onPause() {
        super.onPause()
        if (backgroundColorAnimator != null && !backgroundColorAnimator!!.isRunning()) {
            // start the animation
            backgroundColorAnimator!!.stop()
        }

        Log.e(TAG,"  onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"  onStop")
    }
    override fun onStart() {
        super.onStart()
        Log.e(TAG,"  onStart")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG,"  onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"  onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG,"  onDetach")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG,"  onCreateView")
//
//

        vC = inflater.inflate(R.layout.calendrier_fragment, container, false)

//        val title = container!!.findViewById<TextView>(R.id.main_title)
//        title.text = "SELECTION D'UNE DATE"
////        container.main_title.text = "SELECTION D'UNE DATE"

        vC!!.calendrier_title.text = getString(R.string.sel_date)

        val toto = vC!!.findViewById<TextView>(R.id.calendrier_title)

        backgroundColorAnimator = toto.background as AnimationDrawable

        backgroundColorAnimator!!.setEnterFadeDuration(2000)

        // setting exit fade animation duration to 2 seconds
        backgroundColorAnimator!!.setExitFadeDuration(2000)


        trait_affichage_date()
        vC!!.calendarView.setOnDateChangedListener {
            _, date, _ ->
            traitement_date_selected(date)
//            calendrierDateListener?.onClickDate(date)

        }

        return vC
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        try {
//            calendrierlistener =  context as CalendrierListener
//        } catch (castException : ClassCastException) {
//            Toast.makeText(contx, "errreur castException + $castException", Toast.LENGTH_LONG).show()
//        }
//    }


    public fun trait_affichage_date() {
        vC!!.calendarView.removeDecorators()
        Log.e(TAG,"  trait_affichage_date  setCurrentDate")
        vC!!.calendarView.setCurrentDate(calendar!!.getTime())
        vC!!.calendarView.setSelectedDate(calendar!!.getTime())
        //toutes les décorations éventuelles sont supprimées (raz)
//
        eventdateCur.clear()
        eventdatePrev.clear()
        eventdateJour.clear()



        if (eventList.isNotEmpty() )
        {
            val sortedList = eventList
                    .sortedWith(compareBy<ModelCatEvent> { it.catEvent_year }.thenBy { it.catEvent_month }.thenBy { it.catEvent_day })
                    .distinctBy { Triple(it.catEvent_year,it.catEvent_month,it.catEvent_day)}

            sortedList.forEach {
                calendarDay = CalendarDay.from(it.catEvent_year,it.catEvent_month - 1,it.catEvent_day)

                when(it.catEvent_cat)
                {
                    -1 -> //indique les events < date du jour
                    {
                        eventdatePrev.add(calendarDay!!)
                    }
                    0 ->  //indique les events = date du jour
                    {
                        eventdateJour.add(calendarDay!!)
                    }
                    1 ->  //indique les events > date du jour
                    {
                        eventdateCur.add(calendarDay!!)
                    }
                    else -> {
                        Toast.makeText(contx, "Erruer Event avec catégorie = $categorie", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            if (eventdateJour.isEmpty())
            {
                val datejour = CalendarDay.from(yearDateOfDay, monthDateOfDay - 1, dayDateOfDay)
                eventdateJour.add(datejour)
                vC!!.calendarView.addDecorator(DateDecorator(2, 0, eventdateJour))
            }
            else{
                vC!!.calendarView.addDecorator(DateDecorator(3, 0, eventdateJour))
            }
            if(eventdatePrev.isNotEmpty())
            {
                vC!!.calendarView.addDecorator(DateDecorator(4, 0, eventdatePrev))
            }
            if(eventdateCur.isNotEmpty())
            {
                vC!!.calendarView.addDecorator(DateDecorator(1, 0, eventdateCur))
            }
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
                1 ->
                {
                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration)!!)
                }

                2 ->
                {
                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_jour)!!)
                }
                3 ->
                {
                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_enter)!!)
                }
                4 ->
                {
                    dayDate.setBackgroundDrawable(getDrawable(contx!!, R.drawable.date_decoration_prev)!!)
                }
            }
        }
    }


    private fun traitement_date_selected(date: CalendarDay) {
        yearDateSelected = date.year
        monthDateSelected = date.month + 1
        dayDateSelected = date.day
        categorie = -2 // initialisation bidon
        eventList.forEach {
            if(it.catEvent_year == yearDateSelected &&
                it.catEvent_month == monthDateSelected &&
                it.catEvent_day == dayDateSelected)
            {
                categorie = it.catEvent_cat
            }
        }

        if(categorie != -2) //un event au moins a été trouvé pour la date sélectionnée
        {
            //affichage de la liste des events pour la date séléctionnée
            activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_main, ListeMsgFragment())
                    .addToBackStack(getString(R.string.backstack_calendrier))
                    .commit()

        }
        else // pas d'event pour la date sélectionnée
        {
            if((yearDateSelected < yearDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected < dayDateOfDay))
            {
                // une date sélectionnée sans event et < date du jour ne peut pas être traitée
                yearDateSelected = -1
                monthDateSelected = -1
                dayDateSelected = -1
                Toast.makeText(contx, getString(R.string.date_sup), Toast.LENGTH_SHORT).show()
                vC!!.calendarView.setSelectedDate(calendar!!.getTime())

            }
            else {
                if (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay)
                    categorie = 0
                else
                    categorie = 1


                processus_crea_event()

            }
        }
    }

    private fun processus_crea_event() {
        calendar = Calendar.getInstance()
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)

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

        timePickerDialog.setTitle(contx!!.getString(R.string.cre_event))
        timePickerDialog.setMessage(contx!!.getString(R.string.choose_time))
        timePickerDialog.setOnCancelListener {

            vC!!.calendarView.setSelectedDate(calendar!!.getTime())

            timePickerDialog.dismiss()
        }
        timePickerDialog.setOnDismissListener {

            vC!!.calendarView.setSelectedDate(calendar!!.getTime())

            timePickerDialog.dismiss()
        }

        timePickerDialog.show()
    }

    fun controle_time() {
        if(categorie == 0) // la date sélectonnée est = date du jour
        {
            if((hourDateSelected < hourDateOfDay) ||
                    (hourDateSelected == hourDateOfDay && minuteDateSelected <= minuteDateOfDay))
            {
                //on ne peut créer un event que pour un horaire = > horaire courant
                Toast.makeText(contx, contx!!.getString(R.string.time_sup), Toast.LENGTH_SHORT).show()
            }
            else getSpeechInput()

        }else getSpeechInput() // la date sélectonnée est > date du jour
    }




    private fun getSpeechInput(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().displayLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, contx!!.getString(R.string.to_speek))

        try {
            startActivityForResult(intent, REQUEST_CODE_CALENDRIER)

        } catch (a : ActivityNotFoundException) {
            throw ActivityNotFoundException(getString(R.string.smartphone_dont_fit))
//            Toast.makeText(contx, contx!!.getString(R.string.smartphone_dont_fit), Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_CALENDRIER -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    val stringresult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
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

        val roomSqliteManagement = RoomSqliteManagement()
        roomSqliteManagement.addMessage(event)

        val modelCatEvent = ModelCatEvent(
                categorie,
                0,
                event.event_year,
                event.event_month,
                event.event_day,
                event.event_hour,
                event.event_minute,
                event.event_message,
                event.event_stamp)
        eventList.add(modelCatEvent)

        val modelEvent = Event(
                0,
                event.event_year,
                event.event_month,
                event.event_day,
                event.event_hour,
                event.event_minute,
                event.event_message,
                event.event_stamp)
        eventListGal.add(modelEvent)

        activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_main, ListeMsgFragment())
                .addToBackStack(getString(R.string.backstack_calendrier))
                .commit()
        if (suiteAgenda) {
            addToGoogleCalendar(
                    yearDateSelected,
                    monthDateSelected,
                    dayDateSelected,
                    hourDateSelected,
                    minuteDateSelected,
                    messageEnreg)
        }
    }




    private fun addToGoogleCalendar(year : Int, month : Int, day : Int, hour : Int, minute : Int, message : String) {

        val beginTime = Calendar.getInstance()
        beginTime.set(year, month-1, day, hour, minute)
        val endTime = Calendar.getInstance()
        endTime.set(year, month-1, day, hour+1, minute)
        val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, message)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent)
    }
}

