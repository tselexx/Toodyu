package com.tselexx.toodyu.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tselexx.toodyu.R
import com.tselexx.toodyu.model.ModelHmMsg


class ListMsgAdapter(val modelhmmsg : ArrayList<ModelHmMsg>,
                     val itemListener  : (ModelHmMsg) -> Unit):
        RecyclerView.Adapter<ListMsgAdapter.MyViewHolder>() {



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

        fun bindItem(modelhmmsg: ModelHmMsg) = with (itemView){
            var tempstring = modelhmmsg.eventmn.toString()
            if (tempstring == "0") tempstring = "00"
            if (tempstring == "1") tempstring = "01"
            if (tempstring == "2") tempstring = "02"
            if (tempstring == "3") tempstring = "03"
            if (tempstring == "4") tempstring = "04"
            if (tempstring == "5") tempstring = "05"
            if (tempstring == "6") tempstring = "06"
            if (tempstring == "7") tempstring = "07"
            if (tempstring == "8") tempstring = "08"
            if (tempstring == "9") tempstring = "09"
            tempstring = modelhmmsg.eventhh.toString()  + " H " + tempstring
            hmaff.setText(tempstring)
            msgaff.setText(modelhmmsg.eventmsg)
            setOnClickListener { itemListener(modelhmmsg)}

        }
    }
}
