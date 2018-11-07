package com.tselexx.toodyu

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.backgroundColorAnimator
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.categorie
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventList
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.messageEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.newDay
import com.tselexx.toodyu.Common_Util_functions.Companion.newHour
import com.tselexx.toodyu.Common_Util_functions.Companion.newMessage
import com.tselexx.toodyu.Common_Util_functions.Companion.newMinute
import com.tselexx.toodyu.Common_Util_functions.Companion.newMonth
import com.tselexx.toodyu.Common_Util_functions.Companion.newYear
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.timestampEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.vE
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.edit_msg_fragment.*
import kotlinx.android.synthetic.main.edit_msg_fragment.view.*
import training.tselexx.toodyu.RoomSqliteManagement
import training.tselexx.toodyu.model.ModelCatEvent
import java.util.*


class EditMsgFragment : Fragment() , IOnBackPressed {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newYear = yearDateSelected
        newMonth = monthDateSelected
        newDay = dayDateSelected
        newHour = hourDateSelected
        newMinute = minuteDateSelected
        newMessage = messageEnreg


    }

    override fun onResume() {

        super.onResume()
        if (backgroundColorAnimator != null && !backgroundColorAnimator!!.isRunning()) {
            // start the animation
            backgroundColorAnimator!!.start()
        }

    }

    override fun onPause() {

        super.onPause()
        if (backgroundColorAnimator != null && !backgroundColorAnimator!!.isRunning()) {
            // start the animation
            backgroundColorAnimator!!.stop()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vE = inflater.inflate(R.layout.edit_msg_fragment, container, false)



        vE!!.editmsg_title.text = getString(R.string.det_event)

        val toto = vE!!.findViewById<TextView>(R.id.editmsg_title)

        backgroundColorAnimator = toto.background as AnimationDrawable

        backgroundColorAnimator!!.setEnterFadeDuration(2000)

        // setting exit fade animation duration to 2 seconds
        backgroundColorAnimator!!.setExitFadeDuration(2000)


        vE!!.edit_msg_textView_date.text = "$dayDateSelected / $monthDateSelected / $yearDateSelected"
                                
        trait_formatage_hhmn(hourDateSelected,minuteDateSelected)

        vE!!.edit_msg_editText.setText(messageEnreg)

        setBackgroundItem()

        vE!!.edit_msg_editText.setHorizontallyScrolling(false)

        vE!!.edit_msg_editText.setMaxLines(500)

        vE!!.edit_floating_action_button.visibility = View.INVISIBLE



        vE!!.edit_msg_textView_date.setOnClickListener {
            choix_date()
        }

        vE!!.edit_msg_textView_hhmn.setOnClickListener {
            callTimePickerDialog()
        }

        vE!!.edit_msg_editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE ||
               actionId ==  KeyEvent.KEYCODE_BACK){
                close_soft_keyboard(v)
                newMessage =  vE!!.edit_msg_editText.text.toString()
                controle_croises()

                true
            }else false
        }

        vE!!.edit_floating_action_button.setOnClickListener {
            modification_message()
        }
        //pour rajouter un item dans le menu
        //va avec onCreateOptionsMenu et onOptionsItemSelected
        setHasOptionsMenu(true)

        return vE
    }

    private fun close_soft_keyboard(v : TextView) {
        val imm = v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.extra_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId)
        {
            R.id.supprimermsg ->
            {
                suppression_message()
            }
        }
        return super.onOptionsItemSelected(item)
    }




    private fun choix_date() {
        val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    newYear = year
                    newMonth = month + 1
                    newDay = day
                    vE!!.edit_msg_textView_date.text = "$newDay / $newMonth / $newYear"
                    controle_croises()
                }

        val datePickerDialog = DatePickerDialog(contx,
                dateSetListener,
                yearDateSelected,
                monthDateSelected - 1,
                dayDateSelected)
        datePickerDialog.setTitle(getString(R.string.mod_event))
        datePickerDialog.show()
    }

    fun callTimePickerDialog()   {
        val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    newHour = hourOfDay
                    newMinute = minute
                    trait_formatage_hhmn(newHour,newMinute)
                    controle_croises()
                }
        val timePickerDialog = TimePickerDialog(activity,
                timeSetListener,
                12,
                0,
                false)
        timePickerDialog.setTitle(getString(R.string.modif_msg))
        timePickerDialog.setMessage(getString(R.string.choose_time))
        timePickerDialog.show()
    }

    private fun trait_formatage_hhmn(hh : Int,mn : Int) {
        var editHH  = ""
        var editMN  = ""
        if(hh in 0..9){
             editHH = "0" + hh.toString()
        }
        else editHH = hh.toString()
        if(mn in 0..9){
            editMN = "0" + mn.toString()
        }
        else editMN = mn.toString()
        vE!!.edit_msg_textView_hhmn.text = "$editHH  H  $editMN"

    }

    private fun controle_croises(){
        edit_floating_action_button.visibility = View.INVISIBLE

        if (!(newYear == yearDateSelected &&
            newMonth == monthDateSelected &&
            newDay == dayDateSelected &&
            newMessage == messageEnreg &&
            newHour == hourDateSelected &&
            newMinute == minuteDateSelected &&
            newMessage == messageEnreg))
        {
            if (((newYear < yearDateOfDay) ||
                (newYear == yearDateOfDay && newMonth < monthDateOfDay) ||
                (newYear == yearDateOfDay && newMonth == monthDateOfDay && newDay < dayDateOfDay)))
            {
                Toast.makeText(contx, getString(R.string.date_mod), Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(newYear == yearDateOfDay && newMonth == monthDateOfDay && newDay == dayDateOfDay)
                {
                    if(controle_time()) {
                        if (newMessage == "") {
                            Toast.makeText(activity, getString(R.string.msg_mandatory), Toast.LENGTH_LONG).show()
                        } else {
                            edit_floating_action_button.visibility = View.VISIBLE
                            vE!!.edit_msg_textView_date.setBackgroundResource(R.drawable.bordermodif)
                            vE!!.edit_msg_editText.setBackgroundResource(R.drawable.bordermodif)
                            vE!!.edit_msg_textView_hhmn.setBackgroundResource(R.drawable.bordermodif)

                        }
                    }
                    else{
                        vE!!.edit_msg_textView_date.setBackgroundResource(R.drawable.bordermodifprev)
                        vE!!.edit_msg_editText.setBackgroundResource(R.drawable.bordermodifprev)
                        vE!!.edit_msg_textView_hhmn.setBackgroundResource(R.drawable.bordermodifprev)
                    }
                }
                else
                {
                    if (newMessage == "") {
                        Toast.makeText(activity, getString(R.string.msg_mandatory), Toast.LENGTH_LONG).show()
                    } else {
                        edit_floating_action_button.visibility = View.VISIBLE
                        vE!!.edit_msg_textView_date.setBackgroundResource(R.drawable.bordermodif)
                        vE!!.edit_msg_editText.setBackgroundResource(R.drawable.bordermodif)
                        vE!!.edit_msg_textView_hhmn.setBackgroundResource(R.drawable.bordermodif)

                    }
                }
            }
        }
        else
        {
            Toast.makeText(activity, getString(R.string.nothing_mod), Toast.LENGTH_LONG).show()
            setBackgroundItem()
        }
    }

    private fun setBackgroundItem() {
        calendar = Calendar.getInstance()
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)

        if((yearDateSelected < yearDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected < dayDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay
                        && hourDateSelected < hourDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay
                        && hourDateSelected == hourDateOfDay && minuteDateSelected <= minuteDateOfDay))
        {
            vE!!.edit_msg_textView_date.setBackgroundResource(R.drawable.bordermodifprev)
            vE!!.edit_msg_editText.setBackgroundResource(R.drawable.bordermodifprev)
            vE!!.edit_msg_textView_hhmn.setBackgroundResource(R.drawable.bordermodifprev)

        }
    }

    fun controle_time() : Boolean {
        calendar = Calendar.getInstance()
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)

        var boolTemp = false
        if((newHour < hourDateOfDay) ||
            (newHour == hourDateOfDay && newMinute <= minuteDateOfDay))
        {
            //on ne peut créer un event que pour un horaire = > horaire courant
            Toast.makeText(contx, contx!!.getString(R.string.time_sup), Toast.LENGTH_SHORT).show()
        }else boolTemp = true
        return boolTemp
    }


    private fun modification_message() {
        if(newYear == yearDateOfDay && newMonth == monthDateOfDay && newDay== dayDateOfDay)
        {
            maj_room_eventList(0)
        }
        else
        {
            maj_room_eventList(1)
        }

        yearDateSelected = newYear
        monthDateSelected = newMonth
        dayDateSelected = newDay
        activity?.supportFragmentManager?.popBackStack()

//
        if (suiteAgenda)
        {
            addToGoogleCalendar (
                    newYear,
                    newMonth ,
                    newDay,
                    newHour,
                    newMinute,
                    newMessage)
        }
    }

    private fun maj_room_eventList(cat : Int) {
        val roomSqliteManagement = RoomSqliteManagement()
        roomSqliteManagement.updateMessage(yearDateSelected,
                monthDateSelected,
                dayDateSelected,
                hourDateSelected,
                minuteDateSelected,
                timestampEnreg,
                newYear,
                newMonth,
                newDay,
                newHour,
                newMinute,
                newMessage)

        //si la date sélectionnée = date du jour
        var catid = 0
        val iterator = eventList.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (it.catEvent_year == yearDateSelected &&
                it.catEvent_month == monthDateSelected &&
                it.catEvent_day == dayDateSelected &&
                it.catEvent_hour == hourDateSelected &&
                it.catEvent_minute == minuteDateSelected &&
                it.catEvent_stamp == timestampEnreg) {
                categorie = it.catEvent_cat
                catid = it.catEvent_id
                iterator.remove()
            }
        }
        val modelCatEvent = ModelCatEvent(
                cat,
                catid,
                newYear,
                newMonth,
                newDay,
                newHour,
                newMinute,
                newMessage,
                timestampEnreg)
        eventList.add(modelCatEvent)
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
                .putExtra(CalendarContract.Events.TITLE, getString(R.string.sync_title))
                .putExtra(CalendarContract.Events.DESCRIPTION, message)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent);
    }


    private fun suppression_message()
    {
        val alert = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        alert.setTitle(getString(R.string.conf_supp))
        alert.setIcon(R.drawable.delete_single)

        var editMN = ""
        if(minuteDateSelected in 0..9){
            editMN = "0" + minuteDateSelected.toString()
        }
        else editMN = minuteDateSelected.toString()


        val sb = StringBuilder()
        sb.append(getString(R.string.lib_du))
                .append (dayDateSelected.toString())
                .append("/")
                .append(monthDateSelected.toString())
                .append("/")
                .append(yearDateSelected.toString())
                .append("   :   ")
                .append(hourDateSelected.toString())
                .append("H")
                .append (editMN)
                .append("\n")
        alert.setMessage(sb.toString())
        val dialogClickListener = DialogInterface.OnClickListener { d, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> sqlite_supprimer_message()
                DialogInterface.BUTTON_NEUTRAL -> d.dismiss()
            }

        }

        alert.setPositiveButton(getString(R.string.SUPPR),dialogClickListener)
        alert.setNeutralButton(getString(R.string.CANCEL),dialogClickListener)
        alert.create()
        alert.show()

    }

    private fun sqlite_supprimer_message() {
        val roomSqliteManagement = RoomSqliteManagement()
        roomSqliteManagement.deleteMessage(yearDateSelected,
                monthDateSelected,
                dayDateSelected,
                hourDateSelected,
                minuteDateSelected,
                timestampEnreg)
        val iterator = eventList.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (it.catEvent_year == yearDateSelected &&
                    it.catEvent_month == monthDateSelected &&
                    it.catEvent_day == dayDateSelected &&
                    it.catEvent_hour == hourDateSelected &&
                    it.catEvent_minute == minuteDateSelected &&
                    it.catEvent_stamp == timestampEnreg) {
                iterator.remove()
            }
        }
        activity?.supportFragmentManager?.popBackStack()
    }


        override fun onBackPressed(): Boolean {
        boolBackPressed = false
        return boolBackPressed
    }

}