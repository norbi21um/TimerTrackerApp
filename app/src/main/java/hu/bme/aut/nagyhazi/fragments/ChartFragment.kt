package hu.bme.aut.nagyhazi.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.databinding.FragmentChartBinding
import hu.bme.aut.nagyhazi.ui.MainActivity
import hu.bme.aut.nagyhazi.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.fragment_chart.*


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var timerList:ArrayList<Timer>
    private lateinit var mChartViewModel: TimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerList = ArrayList()

        //Beolvassa a timerListet és az adatokat megjeleníti a képernyőn
        mChartViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        mChartViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            timerList = it as ArrayList<Timer>
            setVisibility()
            loadTimerChart()
            sumUpTimers()
        })

        setHasOptionsMenu(true)
    }

    //Megadja, hogy van-e felhasználható adat a timerListben
    //Felhasználható addat == Van olyan timer, aminek az állása nagyobb 0-nál
    private fun timerListHasUsableData():Boolean{
        var tmp: Boolean = false
        for(timer in timerList){
            if(timer.timer>0){
                tmp = true
            }
        }
        return tmp
    }

    //Beállítja a UI komponensek láthatóságát
    private fun setVisibility(){
        if(timerList.size == 0 || !timerListHasUsableData()){
            binding.piechart.visibility = View.GONE
            binding.sumOfTimersCardview.visibility = View.GONE
            binding.noDataText.visibility = View.VISIBLE
        } else{
            binding.piechart.visibility = View.VISIBLE
            binding.sumOfTimersCardview.visibility = View.VISIBLE
            binding.noDataText.visibility = View.GONE
        }
    }

    //Megjeleníti az adatokat a PieChartban
    private fun loadTimerChart(){
        val dataEntries = ArrayList<PieEntry>()

        for(timer in timerList){
            if(timer.timer>0){
                dataEntries.add(PieEntry(timer.timer.toFloat(), timer.timerName))
            }
        }

        val colors: ArrayList<Int> = ArrayList()

        //Light/Night mód esetén külön színpalettát használ a PieChart
        if((activity as MainActivity).isNightModeOn){
            colors.add(Color.parseColor("#3F3351"))
            colors.add(Color.parseColor("#864879"))
            colors.add(Color.parseColor("#E9A6A6"))
            colors.add(Color.parseColor("#5C3D2E"))
            colors.add(Color.parseColor("#B85C38"))
            binding.piechart.setCenterTextColor(Color.BLACK)
        } else{
            colors.add(Color.parseColor("#3c78e5"))
            colors.add(Color.parseColor("#cdd63e"))
            colors.add(Color.parseColor("#b0b06c"))
            colors.add(Color.parseColor("#22ae8e"))
            colors.add(Color.parseColor("#7d6dcb"))
        }

        val dataSet = PieDataSet(dataEntries, "Timers")
        val data = PieData(dataSet)

        setUpPieChart()

        data.setValueFormatter(PercentFormatter(piechart))
        dataSet.sliceSpace = 4f
        dataSet.colors = colors
        binding.piechart.data = data
        data.setValueTextSize(15f)
        binding.piechart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.piechart.animateY(1400, Easing.EaseInOutQuad)
    }

    //PieChart kinézetének a beállítása
    private fun setUpPieChart(){
        binding.piechart.holeRadius = 58f
        binding.piechart.transparentCircleRadius = 61f
        binding.piechart.isDrawHoleEnabled = true

        if((activity as MainActivity).isNightModeOn){
            binding.piechart.setHoleColor(Color.DKGRAY)
            binding.piechart.setEntryLabelColor(Color.WHITE)
            binding.piechart.setCenterTextColor(Color.WHITE)
            binding.piechart.legend.textColor = Color.WHITE
        } else{
            binding.piechart.setHoleColor(Color.WHITE)
            binding.piechart.setEntryLabelColor(Color.BLACK)
        }

        binding.piechart.setUsePercentValues(true)
        binding.piechart.setEntryLabelTextSize(12f)
        binding.piechart.centerText = "Timers"
        binding.piechart.setCenterTextSize(24f)
    }

    //Az össze mért idő megjelenítése és kiszámolása
    private fun sumUpTimers(){
        var sumMilisec:Long = 0

        for(timer in timerList){
            sumMilisec += timer.timer
        }

        val hours = (sumMilisec / 3600000).toInt()
        val minutes = ((sumMilisec - hours * 3600000) / 60000).toInt()
        val seconds = ((sumMilisec - hours * 3600000 - minutes * 60000) / 1000).toInt()

        if(hours>0){
            binding.displayTimer.text = "$hours hours $minutes mins and $seconds secs"
        } else{
            binding.displayTimer.text = "$minutes mins and $seconds secs"
        }
    }
}

