package hu.bme.aut.nagyhazi.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.aut.nagyhazi.R
import hu.bme.aut.nagyhazi.adapter.TimerAdapter
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.databinding.FragmentTimerBinding
import hu.bme.aut.nagyhazi.viewmodel.TimerViewModel
import kotlin.collections.ArrayList


class TimerFragment : Fragment(), TimerAdapter.TimerClickListener {
    private lateinit var binding:FragmentTimerBinding
    private lateinit var addTimerBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var timerList:ArrayList<Timer>
    private lateinit var timerAdapter: TimerAdapter
    private lateinit var mTimerViewModel: TimerViewModel
    private var numberOfTimers:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerList = ArrayList()
        addTimerBtn = binding.addingBtn
        recyclerView = binding.RecyclerViewId
        timerAdapter = TimerAdapter( requireContext(),timerList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = timerAdapter

        //Beolvassa a timerListet és tovább adja a timerAdapternek
        //Beállítja az elemek láthatóságát az adatok alapján
        mTimerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        mTimerViewModel.readAllData.observe(viewLifecycleOwner, Observer { timer ->
            timerAdapter.setData(timer)
            timerList= timer as ArrayList<Timer>
            if(timerList.size ==0){
                binding.RecyclerViewContainer.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE
            } else {
                binding.RecyclerViewContainer.visibility = View.VISIBLE
                binding.noDataText.visibility = View.GONE
            }
        })

        addTimerBtn.setOnClickListener {
            addInfo()
        }

        setHasOptionsMenu(true)
    }

    //Beolvassa a timer
    private fun addInfo() {
        val inflter = LayoutInflater.from(context)
        val v = inflter.inflate(R.layout.add_item,null)
        val timerName = v.findViewById<EditText>(R.id.timerTitle)
        val timerDsp = v.findViewById<EditText>(R.id.timerDescription)
        val addDialog = AlertDialog.Builder(requireContext())

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val title = timerName.text.toString()
            val description = timerDsp.text.toString()
            /// Itt adom hozzá a listához a új Timer-t
            val timer =  Timer(numberOfTimers,title,description,0,0, false)
            //timer adatbázishoz hozzáadása
            mTimerViewModel.addTimer(timer)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            numberOfTimers++
            // Itt a Timer adapternek szól a változásokrol
            timerAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete){
            deleteAllTimers()
        }
        return super.onOptionsItemSelected(item)
    }

    //Minden stopper kitörlése
    private fun deleteAllTimers() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mTimerViewModel.deleteAllTimers()
            Toast.makeText(
                requireContext(),
                "Successfully removed everything",
                Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }

    override fun onItemChanged(item: Timer) {
        mTimerViewModel.updateTimer(item)
    }

    override fun onItemDeleted(item: Timer) {
        mTimerViewModel.deleteTimer(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timerAdapter.saveAll();
    }
}