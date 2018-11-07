package com.tselexx.toodyu.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateSelected
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateSelected
import com.tselexx.toodyu.R
import com.tselexx.toodyu.model.ModelHmMsg
import java.util.*


class ListMsgAdapter(val modelhmmsg : ArrayList<ModelHmMsg>,
                     val itemListener  : (ModelHmMsg) -> Unit)
    : RecyclerView.Adapter<ListMsgAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemview = LayoutInflater.from(parent.context).inflate(
                R.layout.item_hhmn_msg,
                parent,
                false
        )
        return MyViewHolder(itemview)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {

        holder.bindItem(modelhmmsg[position])

    }

    override fun getItemCount(): Int = modelhmmsg.size


    //    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    {
        private var hmaff = itemView.findViewById<TextView>(R.id.item_date)
        private var msgaff =  itemView.findViewById<TextView>(R.id.item_msg)
        private var llayout =  itemView.findViewById<LinearLayout>(R.id.item_layout)


        fun bindItem(modelhmmsg: ModelHmMsg) = with (itemView){
            var tempstring = modelhmmsg.eventmn.toString()
            if(modelhmmsg.eventmn in 0..9){
                tempstring = modelhmmsg.eventhh.toString()  + " H " + "0" + tempstring
            }else
                tempstring = modelhmmsg.eventhh.toString()  + " H " + tempstring

            setBackgroundItem(llayout,modelhmmsg.eventhh, modelhmmsg.eventmn)

            hmaff.setText(tempstring)
            msgaff.setText(modelhmmsg.eventmsg)
            setOnClickListener { itemListener(modelhmmsg)}
        }
    }
    private fun setBackgroundItem(llayout : LinearLayout, hh: Int, mn : Int) {
        calendar = Calendar.getInstance()
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)

        if((yearDateSelected < yearDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected < monthDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected < dayDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay
                        && hh < hourDateOfDay) ||
                (yearDateSelected == yearDateOfDay && monthDateSelected == monthDateOfDay && dayDateSelected == dayDateOfDay
                        && hh == hourDateOfDay && mn <= minuteDateOfDay))
        {
            llayout.setBackgroundResource(R.drawable.borderitemprev)
        }
    }
}
