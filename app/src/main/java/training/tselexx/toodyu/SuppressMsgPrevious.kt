package training.tselexx.toodyu

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.delfromday
import com.tselexx.toodyu.Common_Util_functions.Companion.delfrommonth
import com.tselexx.toodyu.Common_Util_functions.Companion.delfromyear
import com.tselexx.toodyu.Common_Util_functions.Companion.eventList
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.R

class SuppressMsgPrevious {

    public fun choix_date() {

        val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    delfromyear = year
                    delfrommonth = month + 1
                    delfromday = day
                    controle_date()
                }


        val datePickerDialog = DatePickerDialog(contx,
                dateSetListener,
                yearDateOfDay,
                monthDateOfDay - 1,
                dayDateOfDay)

        datePickerDialog.setTitle(contx!!.getString(R.string.supp_event))
        datePickerDialog.setMessage(contx!!.getString(R.string.choose_date))
        datePickerDialog.setOnCancelListener {
//            var datejour = CalendarDay.from(yearDateOfDay, monthDateOfDay - 1, dayDateOfDay)
//
//            vC!!.calendarView.selectedDate = datejour
//            vC!!.calendarView.setSelectedDate(calendar!!.getTime())
            datePickerDialog.dismiss()
        }
        datePickerDialog.setOnDismissListener {
//            vC!!.calendarView.setSelectedDate(calendar!!.getTime())
            datePickerDialog.dismiss()
        }

        datePickerDialog.show()
    }

    private fun controle_date() {
        if (!((delfromyear < yearDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth < monthDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth == monthDateOfDay && delfromday < dayDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth == monthDateOfDay && delfromday == dayDateOfDay))) {
            Toast.makeText(contx,contx!!.getString(R.string.date_del), Toast.LENGTH_SHORT).show()

        } else {
            deleteMessageAnterieurs()
        }
    }

    //suppression au lancement de l'application de tous les messages antérieurs
    private fun deleteMessageAnterieurs() {
        val alertBuilder = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        alertBuilder
                // if the dialog is cancelable
                .setCancelable(true)
                .setIcon(R.drawable.delete_single)
                .setTitle(contx!!.getString(R.string.conf_suppr))
                .setMessage(contx!!.getString(R.string.mind_suppr) + "${delfromday} / ${delfrommonth} / ${delfromyear}")
                // positive button text and action
                .setPositiveButton(contx!!.getString(R.string.ok)) { d, i ->
                    trait_delete_prev()

                }
                // negative button text and action
                .setNegativeButton(contx!!.getString(R.string.CANCEL)) { d, i ->
                    d.dismiss()


                }
                .setOnCancelListener {
                    it.dismiss()

                }
                .create()
                .show()
//        val alert = alertBuilder.create()
//        alert.show()

    }

    private fun trait_delete_prev() {

        val iterator = eventList.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if(it.catEvent_cat == -1){
                if (((it.catEvent_year < delfromyear) ||
                     (it.catEvent_year == delfromyear && it.catEvent_month < delfrommonth) ||
                     (it.catEvent_year == delfromyear && it.catEvent_month == delfrommonth &&
                      it.catEvent_day < delfromday)))
                {
                    iterator.remove()
                }
            }
        }

        val roomSqliteManagement = RoomSqliteManagement()
        roomSqliteManagement.deleteAllPreviousMessage(
                delfromyear,
                delfrommonth,
                delfromday)
    }

}