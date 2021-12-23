package hu.bme.aut.nagyhazi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.nagyhazi.HistoryAdapter
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.databinding.FragmentHistoryBinding
import hu.bme.aut.nagyhazi.viewmodel.TimerViewModel


class HistoryFragment : Fragment() {
    lateinit var binding:FragmentHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var timerList:ArrayList<Timer>
    private lateinit var timerAdapter: HistoryAdapter
    private lateinit var mHistoryViewModel: TimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerList = ArrayList()

        recyclerView = binding.RecyclerViewHistory
        timerAdapter = HistoryAdapter( requireContext(),timerList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = timerAdapter

        //Beolvassa a timerListet és tovább adja a timerAdapternek
        //Beállítja az elemek láthatóságát az adatok alapján
        mHistoryViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        mHistoryViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            timer -> timerAdapter.setData(timer)
            timerList= timer as ArrayList<Timer>
            if(timerList.size ==0){
                binding.RecyclerViewHistory.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE
            } else {
                binding.RecyclerViewHistory.visibility = View.VISIBLE
                binding.noDataText.visibility = View.GONE
            }
        })

        setHasOptionsMenu(true)
    }
}