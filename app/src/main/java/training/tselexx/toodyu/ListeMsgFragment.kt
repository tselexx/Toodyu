package com.tselexx.toodyu

import android.app.Activity
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.REQUEST_CODE_LISTE
import com.tselexx.toodyu.Common_Util_functions.Companion.aiguillage
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.boolCreaMsg
import com.tselexx.toodyu.Common_Util_functions.Companion.boolaig
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListJour
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListPrev
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.messageEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import com.tselexx.toodyu.Common_Util_functions.Companion.timestampEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.vL
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.adapter.ListMsgAdapter
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import com.tselexx.toodyu.model.ModelHmMsg
import kotlinx.android.synthetic.main.list_msg_fragment.view.*
import training.tselexx.toodyu.sqlite.Event
import java.util.*


class ListeMsgFragment : Fragment(), IOnBackPressed {

    lateinit var  myadapter: ListMsgAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vL = inflater.inflate(R.layout.list_msg_fragment, container, false)

        if(boolCreaMsg) vL!!.list_date_button.visibility = View.VISIBLE
        else {
            vL!!.list_date_button.visibility = View.INVISIBLE
            var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            vL!!.list_date_textview.setLayoutParams(params)
            params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 9f)
            vL!!.list_date_recyclerview.setLayoutParams(params)

        }
        vL!!.list_date_button.setOnClickListener {

            traiter_processus_enreg_voix()
        }
        vL!!.list_date_textview.text = getString((R.string.lib_le)) +" ${dayDateSelected.toString()} / ${monthDateSelected.toString()} / ${yearDateSelected.toString()}"
        setAdapter()

        return vL
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

    }
    public fun controle_time() {
        if(yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay)
        {
            if((hourDateSelected < hourDateOfDay) ||
                    (hourDateSelected == hourDateOfDay && minuteDateSelected <= minuteDateOfDay))
            {
                Toast.makeText(Common_Util_functions.contx, getString(R.string.time_sup), Toast.LENGTH_SHORT).show()
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
            this.startActivityForResult(intent, REQUEST_CODE_LISTE)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(contx, getString(R.string.smartphone_dont_fit), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_CODE_LISTE -> {
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
                dayDateSelected == dayDateOfDay)
        {
            eventListJour.add(event)
        }
        else
        {
            eventListCur.add(event)
        }


        if (Common_Util_functions.suiteAgenda)
            addToGoogleCalendar (
                    yearDateSelected,
                    monthDateSelected ,
                    dayDateSelected,
                    hourDateSelected,
                    minuteDateSelected,
                    messageEnreg)

        setAdapter()
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



    private fun setAdapter(){
        //l'activation de ce fragment implique que msglist n'est jamais vide (au moins 1 msg existe pour la date)

//        msgList = myAppDataBase?.myDao()?.findEventWithDate(yearDateSelected, monthDateSelected, dayDateSelected)
        var listmodelHmMsg : ArrayList<ModelHmMsg> = ArrayList<ModelHmMsg>()

        if ((yearDateSelected < yearDateOfDay) ||
            (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
            (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay &&
            dayDateSelected < dayDateOfDay))
        {   val sortedList = eventListPrev.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
            eventListPrev.clear()
            sortedList.forEach{
                if (it.event_year == yearDateSelected && it.event_month == monthDateSelected &&
                        it.event_day == dayDateSelected)
                {
                    listmodelHmMsg.add(ModelHmMsg(it.event_hour, it.event_minute, it.event_message, it.event_stamp))
                }

                eventListPrev.add(it)
            }
        }
        else
        {
            if (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay &&
                    dayDateSelected == dayDateOfDay)
            {   val sortedList = eventListJour.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
                eventListJour.clear()
                sortedList.forEach{
                    listmodelHmMsg.add(ModelHmMsg(it.event_hour, it.event_minute, it.event_message, it.event_stamp))
                    eventListJour.add(it)
                }
            }
            else
            {   val sortedList = eventListCur.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
                eventListCur.clear()
                sortedList.forEach{
                    if (it.event_year == yearDateSelected && it.event_month == monthDateSelected &&
                            it.event_day == dayDateSelected)
                    {
                        listmodelHmMsg.add(ModelHmMsg(it.event_hour, it.event_minute, it.event_message, it.event_stamp))
                    }
                    eventListCur.add(it)
                }
            }
        }

        myadapter = ListMsgAdapter(listmodelHmMsg) { ModelHmMsg ->

                hourDateSelected = ModelHmMsg.eventhh
                minuteDateSelected = ModelHmMsg.eventmn
                messageEnreg = ModelHmMsg.eventmsg
                timestampEnreg = ModelHmMsg.eventstamp
                trait_Item_Selected()
        }



        vL!!.list_date_recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL,false)
        vL!!.list_date_recyclerview.layoutManager.scrollToPosition(0)
        vL!!.list_date_recyclerview.adapter = myadapter

    }

    private fun trait_Item_Selected() {
        val alert = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        if (boolCreaMsg) alert.setTitle(getString(R.string.modif_suppr_msg))
        else alert.setTitle(getString(R.string.conf_supp))
        val editMN = trait_formatage_hhmn()

        val sb = StringBuilder()
        sb.append(getString(R.string.lib_du))
                .append (dayDateSelected.toString())
                .append (getString(R.string.slash))
                .append(monthDateSelected.toString())
                .append (getString(R.string.slash))
                .append(yearDateSelected.toString())
                .append(getString(R.string.lib_pour))
                .append(hourDateSelected.toString())
                .append(getString(R.string.lib_h))
                .append (editMN)
                .append("\n")
        alert.setMessage(sb.toString())
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when(which){

                DialogInterface.BUTTON_POSITIVE -> modification_message()
                DialogInterface.BUTTON_NEGATIVE -> confirmation_suppression()

            }

        }
        if (boolCreaMsg) alert.setPositiveButton(getString(R.string.EDIT),dialogClickListener)

        alert.setNegativeButton(getString(R.string.SUPPR),dialogClickListener)
        alert.setNeutralButton(getString(R.string.CANCEL),dialogClickListener)
        // Dialog
        val dialog = alert.create()
        dialog.show()
    }

    private fun modification_message() {
        val fragment = EditMsgFragment() as Fragment
                fragmentManager!!
                .beginTransaction()
                .replace(R.id.container,fragment)
                .addToBackStack(getString(R.string.backstack_listemsg))
                .commit()
    }
    private fun confirmation_suppression(){
//        val alert = AlertDialog.Builder(context!!,R.style.AlertDialogTheme)
        val alert = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        alert.setTitle(getString(R.string.conf_suppr))
        alert.setIcon(R.mipmap.ic_delete_foreground)

        val editMN = trait_formatage_hhmn()

        val sb = StringBuilder()
        sb.append(getString(R.string.lib_du))
                .append (dayDateSelected.toString())
                .append (getString(R.string.slash))
                .append(monthDateSelected.toString())
                .append (getString(R.string.slash))
                .append(yearDateSelected.toString())
                .append(getString(R.string.lib_pour))
                .append(hourDateSelected.toString())
                .append(getString(R.string.lib_h))
                .append (editMN)
                .append("\n")
        alert.setMessage(sb.toString())

        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> sqlite_supprimer_message()
            }

        }
        alert.setPositiveButton(getString(R.string.ok),dialogClickListener)
        alert.setNeutralButton(getString(R.string.CANCEL),dialogClickListener)
        // Dialog
        val dialog = alert.create()
        dialog.show()
    }

    fun sqlite_supprimer_message(){
        myAppDataBase?.myDao()?.deleteEvent(yearDateSelected,
                monthDateSelected,
                dayDateSelected,
                hourDateSelected,
                minuteDateSelected,
                timestampEnreg)




        var iterator = eventListGal.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (it.event_year == yearDateSelected &&
                    it.event_month == monthDateSelected &&
                    it.event_day == dayDateSelected &&
                    it.event_hour == hourDateSelected &&
                    it.event_minute == minuteDateSelected &&
                    it.event_stamp == timestampEnreg)
                iterator.remove()
        }

        boolaig = false

        if ((yearDateSelected < yearDateOfDay) ||
            (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
            (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay &&
            dayDateSelected < dayDateOfDay))
        {
            var iterator = eventListPrev.iterator()
            while (iterator.hasNext()) {
                val it = iterator.next()
                if (it.event_year == yearDateSelected &&
                        it.event_month == monthDateSelected &&
                        it.event_day == dayDateSelected &&
                        it.event_hour == hourDateSelected &&
                        it.event_minute == minuteDateSelected &&
                        it.event_stamp == timestampEnreg)
                {
                    iterator.remove()
                    aiguillage = 1
                }
            }
        }
        else
        {
            if (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay &&
                                    dayDateSelected == dayDateOfDay)
            {
                var iterator = eventListJour.iterator()
                while (iterator.hasNext())
                {
                    val it = iterator.next()
                    if (it.event_year == yearDateSelected &&
                            it.event_month == monthDateSelected &&
                            it.event_day == dayDateSelected &&
                            it.event_hour == hourDateSelected &&
                            it.event_minute == minuteDateSelected &&
                            it.event_stamp == timestampEnreg)
                    {
                        iterator.remove()
                        aiguillage = 2

                    }
                }

            }
            else
            {
                var iterator = eventListCur.iterator()
                while (iterator.hasNext()) {
                    val it = iterator.next()
                    if (it.event_year == yearDateSelected &&
                            it.event_month == monthDateSelected &&
                            it.event_day == dayDateSelected &&
                            it.event_hour == hourDateSelected &&
                            it.event_minute == minuteDateSelected &&
                            it.event_stamp == timestampEnreg) {
                        iterator.remove()
                        aiguillage = 3

                    }
                }
            }
        }

        when(aiguillage) {
            1 -> {
                eventListPrev.forEach {
                    if (it.event_year == yearDateSelected &&
                    it.event_month == monthDateSelected &&
                    it.event_day == dayDateSelected )
                    {
                        boolaig = true
                    }
                }
            }
            2 -> {
                eventListJour.forEach {
                    if (it.event_year == yearDateSelected &&
                    it.event_month == monthDateSelected &&
                    it.event_day == dayDateSelected )
                    {
                        boolaig = true
                    }
                }
            }
            3 -> {
                eventListCur.forEach {
                    if (it.event_year == yearDateSelected &&
                    it.event_month == monthDateSelected &&
                    it.event_day == dayDateSelected )
                    {
                        boolaig = true
                    }
                }
            }

        }

        if (boolaig)
        {
            setAdapter()
        }
        else
        {
            val calendrierFragment = CalendrierFragment()
            calendrierFragment.trait_affichage_date()
            fragmentManager!!.popBackStack()
        }

    }

    private fun trait_formatage_hhmn() : String {
        var editMN : String = ""

        if(minuteDateSelected == 0 ||
                minuteDateSelected == 1 ||
                minuteDateSelected == 2 ||
                minuteDateSelected == 3 ||
                minuteDateSelected == 4 ||
                minuteDateSelected == 5 ||
                minuteDateSelected == 6 ||
                minuteDateSelected == 7 ||
                minuteDateSelected == 8 ||
                minuteDateSelected == 9){
            editMN = "0" + minuteDateSelected.toString()
        }else editMN = minuteDateSelected.toString()


        return editMN
    }

    override fun onBackPressed(): Boolean {
        boolBackPressed = true
        return boolBackPressed
    }


}