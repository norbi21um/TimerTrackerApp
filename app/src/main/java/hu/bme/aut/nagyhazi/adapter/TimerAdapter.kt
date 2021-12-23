package hu.bme.aut.nagyhazi.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.nagyhazi.R
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.databinding.ListItemBinding

class TimerAdapter(val c: Context, var timerList:ArrayList<Timer>, private val listener: TimerClickListener): RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {


    interface TimerClickListener {
        fun onItemChanged(item: Timer)
        fun onItemDeleted(item: Timer)
    }

    inner class TimerViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TimerViewHolder (
        ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val item = timerList[position]
        holder.binding.categoryTitleId.text = item.timerName
        holder.binding.descriptionId.text = item.timerDescription
        holder.binding.chronomaterId.base = SystemClock.elapsedRealtime() - timerList[position].timer
        holder.binding.playImage.setOnClickListener {
            playFun(it, holder, position)
        }
        holder.binding.pauseImage.setOnClickListener {
            pauseFun(it, holder, position)
        }
        holder.binding.mMenus.setOnClickListener {
            popupMenus(it,position)
        }
        //Betöltéskor beállítja a futó Timer megfelelő időre
        if(item.isCounting){
            holder.binding.chronomaterId.base = timerList[position].startTimer - timerList[position].timer
            holder.binding.chronomaterId.start()
            timerList[position].isCounting = true //Megmondom, hogy elkezdődött a counting
            holder.binding.pauseImage.visibility = View.VISIBLE
            holder.binding.playImage.visibility = View.GONE
        }
    }

    fun removeItem(item: Timer) {
        timerList.remove(item)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return timerList.size
    }

    fun setData(user: List<Timer>){
        timerList = user as ArrayList<Timer>
        notifyDataSetChanged()
    }

    fun saveAll(){
        for(e in timerList){
            listener.onItemChanged(e)
        }

    }

    fun playFun(v: View, holder: TimerViewHolder, position: Int) {
        var cnt:Int = 0

        for(e in timerList){
            if(e.isCounting){
                cnt++
            }
        }

        //Akkor indítható ha még nincs futó timer.
        if(!(cnt >= 1)){
            holder.binding.chronomaterId.base = SystemClock.elapsedRealtime() - timerList[position].timer
            /**itt beállítom a kezdő pozíciót*/
            timerList[position].startTimer = SystemClock.elapsedRealtime()

            holder.binding.chronomaterId.start()
            timerList[position].isCounting = true //Megmondom, hogy elkezdődött a counting
            holder.binding.pauseImage.visibility = View.VISIBLE
            holder.binding.playImage.visibility = View.GONE
        } else {
            Toast.makeText(c, "You can not run multiple timers at a time.", Toast.LENGTH_LONG).show()
        }

    }


    fun pauseFun(v: View, holder: TimerViewHolder, position: Int) {
        holder.binding.chronomaterId.stop()
        timerList[position].timer = SystemClock.elapsedRealtime() - holder.binding.chronomaterId.base
        timerList[position].isCounting = false
        holder.binding.pauseImage.visibility = View.GONE
        holder.binding.playImage.visibility = View.VISIBLE
        /**Az adatbázist updateli,hogy megváltozott az érték*/
        listener.onItemChanged(timerList[position])
    }

    fun popupMenus(v: View, adapterPosition:Int) {
        val timer = timerList[adapterPosition]
        val popupMenus = PopupMenu(c, v)
        popupMenus.inflate(R.menu.show_menu)
        popupMenus.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editText -> {
                    val v = LayoutInflater.from(c).inflate(R.layout.add_item, null)
                    val name = v.findViewById<EditText>(R.id.timerTitle)
                    val dsp = v.findViewById<EditText>(R.id.timerDescription)
                    name.setText(timer.timerName)
                    dsp.setText(timer.timerDescription)
                    AlertDialog.Builder(c)
                        .setView(v)
                        .setPositiveButton("Ok") { dialog, _ ->
                            timer.timerName = name.text.toString()
                            timer.timerDescription = dsp.text.toString()
                            notifyDataSetChanged()
                            listener.onItemChanged(timer)
                            Toast.makeText(c, "Counter Edited", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()

                        }
                        .create()
                        .show()

                    true
                }
                R.id.delete -> {
                    AlertDialog.Builder(c)
                        .setTitle("Delete")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Are you sure you want to delete the counter?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            removeItem(timer)
                            listener.onItemDeleted(timer)
                            Toast.makeText(c, "Counter Deleted", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                    true
                }
                else -> true
            }

        }
        popupMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
    }
}