package hu.bme.aut.nagyhazi.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import hu.bme.aut.nagyhazi.R
import hu.bme.aut.nagyhazi.databinding.ActivityMainBinding
import hu.bme.aut.nagyhazi.fragments.ChartFragment
import hu.bme.aut.nagyhazi.fragments.HistoryFragment
import hu.bme.aut.nagyhazi.fragments.TimerFragment

class MainActivity : AppCompatActivity(){
    private lateinit var binding:ActivityMainBinding
    private var pageCounter:Int = 1

    private var timerFragment = TimerFragment()
    private var historyFragment = HistoryFragment()
    private var chartFragment = ChartFragment()

    private lateinit var appSettingsPrefs: SharedPreferences
    private lateinit var sharedPrefsEdit: SharedPreferences.Editor
    var isNightModeOn: Boolean = false

    //Navigation Drawer-höz
    lateinit var toggle:ActionBarDrawerToggle

    //Eltárolom melyik Fragment van jelenleg az Activityn
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("page", pageCounter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Preferencesben eltárolom, hogy Dark módban van-e az eszköz
        appSettingsPrefs = getSharedPreferences("appSettingsPrefs", 0)
        sharedPrefsEdit = appSettingsPrefs.edit()
        isNightModeOn  = appSettingsPrefs.getBoolean("DarkMode", false)

        //Dark/Light mód beállítása
        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //A PageCounter beállítása, eltárolt adat alapján
        if(savedInstanceState!=null){
            pageCounter = savedInstanceState.getInt("page", 1)
            replaceFragment(pageCounter)
        } else{
            pageCounter = 1
            replaceFragment(1)
        }

        //Navigation Drawer beállítása
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_main -> replaceFragment(1)
                R.id.ic_history -> replaceFragment(2)
                R.id.ic_chart -> replaceFragment(3)
            }
            true
        }
    }

    //Toolbarban lévő elemek cselekvésének beállítása
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        } else if(item.itemId == R.id.dark_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefsEdit.putBoolean("DarkMode", true)
            sharedPrefsEdit.apply()
            return true
        } else if(item.itemId == R.id.light_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefsEdit.putBoolean("DarkMode", false)
            sharedPrefsEdit.apply()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //Fragmentek kicserélése Pagenumber alapján
    private fun replaceFragment(number:Int){
        lateinit var fragment:Fragment

        pageCounter = number
        if(number==1){
            fragment = timerFragment
        }else if(number == 2){
            fragment = historyFragment
        } else if(number == 3){
            fragment = chartFragment
        }

        if(fragment!=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
        binding.drawerLayout.closeDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dark_ligh_mode_menu, menu)
        return true
    }
}