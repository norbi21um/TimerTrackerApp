package hu.bme.aut.nagyhazi


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.nagyhazi.data.Timer

class HistoryAdapter(val c: Context, var timerList:ArrayList<Timer>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    inner class HistoryViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView
        var mDsp: TextView
        var sdf: TextView

        init {
            name = v.findViewById<TextView>(R.id.display_title)
            mDsp = v.findViewById<TextView>(R.id.display_dsp)
            sdf = v.findViewById<TextView>(R.id.display_timer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.test_row, parent, false)
        return HistoryViewHolder(view)
    }


    override fun getItemCount(): Int {
        return timerList.size
    }

    fun setData(timer: List<Timer>){
        timerList = timer as ArrayList<Timer>
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val newList = timerList[position]
        holder.name.text = newList.timerName
        holder.mDsp.text = newList.timerDescription

        // Milisec átkonvertálása és kijelzése
        val hours = (newList.timer / 3600000).toInt()
        val minutes = ((newList.timer - hours * 3600000) / 60000).toInt()
        val seconds = ((newList.timer - hours * 3600000 - minutes * 60000) / 1000).toInt()
        if(hours>0){
            holder.sdf.text = "$hours hours $minutes mins and $seconds secs"
        } else{
            holder.sdf.text = "$minutes mins and $seconds secs"
        }

    }
}