package com.tselexx.toodyu

import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.boolBackPressed
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListJour
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.messageEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import com.tselexx.toodyu.Common_Util_functions.Companion.newHour
import com.tselexx.toodyu.Common_Util_functions.Companion.newMessage
import com.tselexx.toodyu.Common_Util_functions.Companion.newMinute
import com.tselexx.toodyu.Common_Util_functions.Companion.timestampEnreg
import com.tselexx.toodyu.Common_Util_functions.Companion.vE
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.edit_msg_fragment.*
import kotlinx.android.synthetic.main.edit_msg_fragment.view.*
import training.tselexx.toodyu.sqlite.Event
import java.util.*

class EditMsgFragment : Fragment() , IOnBackPressed {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newHour = hourDateSelected
        newMinute = minuteDateSelected
        newMessage = messageEnreg
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vE = inflater.inflate(R.layout.edit_msg_fragment, container, false)

        vE!!.edit_msg_textView_title.text = getString(R.string.modif_msg) +"\n" +
                                "$dayDateSelected / $monthDateSelected / $yearDateSelected "
        trait_formatage_hhmn(hourDateSelected,minuteDateSelected)
        vE!!.edit_msg_textView_hhmn.setOnClickListener {
            callTimePickerDialog()
        }
        vE!!.edit_msg_editText.setText(messageEnreg)
        vE!!.edit_msg_editText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edit_msg_button.visibility = View.VISIBLE
            }

        })
        vE!!.edit_msg_button.setOnClickListener {
            modification_message()
        }
        return vE
    }


    private fun trait_formatage_hhmn(hh : Int,mn : Int) {
        var editHH : String = ""
        var editMN : String = ""
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

    fun callTimePickerDialog()   {
        val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    newHour = 12
                    newMinute = 0
                    if (newHour != hourDateSelected ||
                        newMinute != minuteDateSelected)
                    {
                       edit_msg_button.visibility = View.VISIBLE
                    }else{
                        edit_msg_button.visibility = View.INVISIBLE
                    }

                    trait_formatage_hhmn(newHour,newMinute)
                }
        TimePickerDialog(activity,
                timeSetListener,
                hourDateSelected,
                minuteDateSelected,
                false).show()
    }


    private fun modification_message() {
        newMessage = vE!!.edit_msg_editText.text.toString()

        var tempList : MutableList<Event> = ArrayList()

        if (newMessage == ""){
            Toast.makeText(activity,getString(R.string.msg_mandatory), Toast.LENGTH_LONG).show()
        }else
        {
            if (newMessage == messageEnreg &&
                newHour == hourDateSelected &&
                newMinute == minuteDateSelected)
            {
                Toast.makeText(activity,getString(R.string.update_nothing), Toast.LENGTH_LONG).show()

            }else
            {
                // si appartien à eventlistjour
                // si appartient à eventlistCur
                //faire replace

                myAppDataBase?.myDao()?.updateEvent(
                        yearDateSelected,
                        monthDateSelected,
                        dayDateSelected,
                        hourDateSelected,
                        minuteDateSelected,
                        timestampEnreg,
                        newHour,
                        newMinute,
                        newMessage)

                //mettre à jour la eventlistjour ou la eventlistcur
                if(yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay
                        && dayDateSelected == dayDateOfDay)
                {
                    var iterator = Common_Util_functions.eventListJour.iterator()
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
                            val event = Event(0,
                                    yearDateSelected,
                                    monthDateSelected,
                                    dayDateSelected,
                                    newHour,
                                    newMinute,
                                    newMessage,
                                    timestampEnreg)
                            tempList.add(event)
                            continue
                        }
                    }
                    eventListJour.add(tempList[0])
                    tempList.clear()
                    val sortedList = eventListJour.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
                    eventListJour.clear()
                    sortedList.forEach{
                        eventListJour.add(it)
                    }

                }
                else
                {
                    val iterator = eventListCur.iterator()
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
                            val event = Event(0,
                                    yearDateSelected,
                                    monthDateSelected,
                                    dayDateSelected,
                                    newHour,
                                    newMinute,
                                    newMessage,
                                    timestampEnreg)
                            tempList.add(event)
                            continue
                        }
                    }
                    eventListCur.add(tempList[0])
                    tempList.clear()
                    val sortedList = eventListCur.sortedWith(compareBy<Event> { it.event_hour }.thenBy { it.event_minute }.thenBy { it.event_stamp })
                    eventListCur.clear()
                    sortedList.forEach{
                        eventListCur.add(it)
                    }

                }

                activity?.supportFragmentManager?.popBackStack()
                //on revient à ListeMsgFragment
                //on effectue adapter.notifyDataSetChanged() dans ListeMsgFragment
            }
        }
    }
    override fun onBackPressed(): Boolean {
        boolBackPressed = false
        return boolBackPressed
    }

}