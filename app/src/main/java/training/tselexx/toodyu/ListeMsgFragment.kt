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
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.REQUEST_CODE_CALENDRIER
import com.tselexx.toodyu.Common_Util_functions.Companion.backgroundColorAnimator
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.categorie
import com.tselexx.toodyu.Common_Util_functions.Companion.categoriemodif
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventList
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListEvent
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.messageEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.timestampEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.vL
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.adapter.ListMsgAdapter
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import com.tselexx.toodyu.model.ModelHmMsg
import kotlinx.android.synthetic.main.list_msg_fragment.view.*
import training.tselexx.toodyu.RoomSqliteManagement
import training.tselexx.toodyu.model.ModelCatEvent
import training.tselexx.toodyu.sqlite.Event
import java.util.*


class ListeMsgFragment : Fragment(), IOnBackPressed {
    val TAG = "ListeMsgFragment"

    lateinit var  myadapter: ListMsgAdapter
    lateinit var  coordinatorLayout: CoordinatorLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"  onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG,"  onCreateView")
        //test pour retour de EditMsgFragment dans le cas de suppression d'un message
        //possibilité qu'il n'y ait plus de message pour la date sélectionnée
        //dans ce cas c'est un retour direct au CalendrierFragment
        test_listEvent_empty_for_selected_date() //test pour retour de EditMsgFragment avec suppression



        vL = inflater.inflate(R.layout.list_msg_fragment, container, false)
        vL!!.listmsg_title.text = getString(R.string.sel_event)


        val toto = vL!!.findViewById<TextView>(R.id.listmsg_title)

        backgroundColorAnimator = toto.background as AnimationDrawable

        backgroundColorAnimator!!.setEnterFadeDuration(2000)

        // setting exit fade animation duration to 2 seconds
        backgroundColorAnimator!!.setExitFadeDuration(2000)

        if(categorie != -1) vL!!.floating_action_button.visibility = View.VISIBLE
        else {
            vL!!.floating_action_button.visibility = View.INVISIBLE
//            var params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//            vL!!.list_date_textview.setLayoutParams(params)
//            params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 9f)
//            vL!!.list_date_recyclerview.setLayoutParams(params)

        }
        vL!!.floating_action_button.setOnClickListener {
            processus_crea_event()

        }
        vL!!.list_date_textview.text = getString((R.string.lib_le)) +" ${dayDateSelected.toString()} / ${monthDateSelected.toString()} / ${yearDateSelected.toString()}"
        setAdapter()
        coordinatorLayout = vL!!.coordinator_list
        return vL
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.e(TAG,"  onAttach")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"  onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
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

    private fun processus_crea_event() {
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
            timePickerDialog.dismiss()
        }
        timePickerDialog.setOnDismissListener {
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


        setAdapter()
        var tempstring = minuteDateSelected.toString()
        if(minuteDateSelected in 0..9){
            tempstring = "0" + tempstring
        }

        Snackbar.make(coordinatorLayout, "Evènement de $hourDateSelected H $tempstring  créé ",Snackbar.LENGTH_SHORT).show()

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
                .putExtra(CalendarContract.Events.DESCRIPTION, message)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent)
    }



    private fun setAdapter() {
        //l'activation de ce fragment implique que msglist n'est jamais vide (au moins 1 msg existe pour la date)

//        msgList = myAppDataBase?.myDao()?.findEventWithDate(yearDateSelected, monthDateSelected, dayDateSelected)
        val listmodelHmMsg : ArrayList<ModelHmMsg> = ArrayList<ModelHmMsg>()
        listmodelHmMsg.clear()
        eventListEvent.clear()

        eventList.forEach {
            if(it.catEvent_year == yearDateSelected &&
               it.catEvent_month == monthDateSelected &&
               it.catEvent_day == dayDateSelected)
            {
                categoriemodif = it.catEvent_cat
                val event = Event(
                        0,
                        it.catEvent_year,
                        it.catEvent_month,
                        it.catEvent_day,
                        it.catEvent_hour,
                        it.catEvent_minute,
                        it.catEvent_message,
                        it.catEvent_stamp
                )
                eventListEvent.add(event)
            }
        }
        val sortedList = eventListEvent.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
//        eventListEvent.clear()
        listmodelHmMsg.clear()

        sortedList.forEach{
            listmodelHmMsg.add(ModelHmMsg(it.event_hour, it.event_minute, it.event_message, it.event_stamp))
        }
//        listmodelHmMsg.clear()
//        eventListEvent.forEach{
//            listmodelHmMsg.add(ModelHmMsg(it.event_hour, it.event_minute, it.event_message, it.event_stamp))
//        }




        myadapter = ListMsgAdapter(listmodelHmMsg) { ModelHmMsg ->

                hourDateSelected = ModelHmMsg.eventhh
                minuteDateSelected = ModelHmMsg.eventmn
                messageEnreg = ModelHmMsg.eventmsg
                timestampEnreg = ModelHmMsg.eventstamp
//                trait_Item_Selected()
                modification_message()
        }



        vL!!.list_date_recyclerview.layoutManager = LinearLayoutManager(contx, LinearLayout.VERTICAL,false)
        vL!!.list_date_recyclerview.layoutManager.scrollToPosition(0)
        vL!!.list_date_recyclerview.adapter = myadapter

    }



    private fun modification_message() {
        val fragment = EditMsgFragment() as Fragment
                fragmentManager!!
                .beginTransaction()
                .replace(R.id.container_main,fragment)
                .addToBackStack(getString(R.string.backstack_listemsg))
                .commit()
    }

    private fun test_listEvent_empty_for_selected_date() {
        var boolExiste = false
        val iterator = eventList.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (it.catEvent_year == yearDateSelected &&
                it.catEvent_month == monthDateSelected &&
                it.catEvent_day == dayDateSelected ) {
                boolExiste = true
            }
        }
        if(!boolExiste){
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onBackPressed(): Boolean {
        boolBackPressed = true
        return boolBackPressed
    }


}