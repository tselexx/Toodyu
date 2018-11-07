package com.tselexx.toodyu


import android.Manifest
import android.animation.ObjectAnimator.ofPropertyValuesHolder
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.tselexx.toodyu.Common_Util_functions.Companion.REQUEST_PERMISSIONS
import com.tselexx.toodyu.Common_Util_functions.Companion.agenda
import com.tselexx.toodyu.Common_Util_functions.Companion.categorie
import com.tselexx.toodyu.Common_Util_functions.Companion.appContx
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.eventList
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.menuGal
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.toolbar
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.activity_main.*
import training.tselexx.toodyu.DisplayWayOfUse
import training.tselexx.toodyu.RoomSqliteManagement
import training.tselexx.toodyu.SuppressMsgPrevious
import training.tselexx.toodyu.model.ModelCatEvent
import training.tselexx.toodyu.sqlite.Agenda
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"  onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialisation du calendrier
        calendar = Calendar.getInstance()
        dayDateOfDay = calendar!!.get(Calendar.DAY_OF_MONTH)
        monthDateOfDay = calendar!!.get(Calendar.MONTH) + 1
        yearDateOfDay = calendar!!.get(Calendar.YEAR)
        hourDateOfDay = calendar!!.get(Calendar.HOUR_OF_DAY)
        minuteDateOfDay = calendar!!.get(Calendar.MINUTE)
        contx = this
        eventList.clear()
        eventListGal.clear()

        //permission explicite si nécessaire
        testPermission()
        //Initialisation de ROOM sqlite
        initRoom()
        //recherche si existe au moins un event
        rechercheMessage()
        //implementarion de la toolbarth
        toolbar = toolbar_main
        setSupportActionBar(toolbar_main)
//        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        toolbar!!.setLogo(R.mipmap.ic_toodyu)
        //affiche le 1er fragment (Calendrier)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            image_animation(toolbar!!.logo)
        }

        afficheCalendrierFragment()
    }
    override fun onResume() {
        val calendrierFragment = CalendrierFragment()
        calendrierFragment.trait_affichage_date()

        super.onResume()
        Log.e(TAG,"  onResume")
    }
    override fun onStart() {
        super.onStart()
        Log.e(TAG,"  onStart")
    }
    override fun onPause() {
        super.onPause()
        Log.e(TAG,"  onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"  onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG,"  onRestart")
    }

    //traitement des options du menu à partir du fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.mode_emploi ->
            {
                val displayWayOfUse = DisplayWayOfUse()
                displayWayOfUse.displayInfo()

            }
            R.id.supprimer ->
            {
                val suppressMsgPrevious = SuppressMsgPrevious()
                suppressMsgPrevious.choix_date()

            }

            R.id.syncAgenda ->
            {
                val roomSqliteManagement = RoomSqliteManagement()
                agenda = roomSqliteManagement.rechercheSyncAgenda()
                if (agenda!!.sync_agenda) {
                    menuGal!!.findItem(R.id.syncAgenda).setChecked(false)
//                    item.setChecked(false)
                    suiteAgenda = false
                } else {
                    menuGal!!.findItem(R.id.syncAgenda).setChecked(true)
//                    item.setChecked(true)
                    suiteAgenda = true
                }
                roomSqliteManagement.updateSyncAgenda(suiteAgenda)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun image_animation(drawable: Drawable) {

        val animator = ofPropertyValuesHolder(drawable,
                PropertyValuesHolder.ofInt("alpha", 0))
        animator.setTarget(drawable)
        animator.duration = 1500
        animator.repeatMode = REVERSE
        animator.repeatCount = INFINITE
        animator.setupStartValues()
        animator.start()


    }


    //fonctions SQLITE / ROOM
    private fun initRoom() {
        appContx = applicationContext
        val roomSqliteManagement = RoomSqliteManagement()
        roomSqliteManagement.initRoom()
//        myAppDataBase = Room.databaseBuilder(applicationContext, MyAppDataBase::class.java, "eventDB").allowMainThreadQueries().build()
    }


    //Recherche des events stockés et indication si < ou = ou > date du jour
    private fun rechercheMessage() {
        val roomSqliteManagement = RoomSqliteManagement()
        eventListGal.clear()
        eventList.clear()

        eventListGal = roomSqliteManagement.rechercheMessage()
        // eventListGal est renseigné
        if (!eventListGal.isEmpty()) {
            eventListGal.forEach { it ->
                categorie = -2

                if (((it.event_year < yearDateOfDay) ||
                (it.event_year == yearDateOfDay && it.event_month < monthDateOfDay) ||
                (it.event_year == yearDateOfDay && it.event_month == monthDateOfDay &&
                it.event_day < dayDateOfDay))) {
                    categorie = -1
                }
                else
                {
                    if (it.event_year == yearDateOfDay && it.event_month == monthDateOfDay &&
                                            it.event_day == dayDateOfDay)
                    {
                        categorie = 0
                    }
                    else
                    {
                        categorie = 1
                    }
                }
                val modelCatEvent = ModelCatEvent(
                        categorie,
                        it.id,
                        it.event_year,
                        it.event_month,
                        it.event_day,
                        it.event_hour,
                        it.event_minute,
                        it.event_message,
                        it.event_stamp)
                eventList.add(modelCatEvent)
            }
        }
    }
    //affichage du fragment gérant le calendrier
    private fun afficheCalendrierFragment() {

        supportFragmentManager
                .beginTransaction()
                .add(R.id.container_main, CalendrierFragment())
                .commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        menuGal = menu

        try {
            agenda = myAppDataBase?.myDao()?.getAgenda()!!
            suiteAgenda = agenda!!.sync_agenda
        } catch (a: NullPointerException) {
            suiteAgenda = false
            agenda = Agenda(0, suiteAgenda)
            myAppDataBase?.myDao()?.addAgenda(agenda)
            agenda = myAppDataBase?.myDao()?.getAgenda()!!
            suiteAgenda = agenda!!.sync_agenda
        }


        if (suiteAgenda) {
            menuGal!!.findItem(R.id.syncAgenda).setChecked(true)
        } else {
            menuGal!!.findItem(R.id.syncAgenda).setChecked(false)
        }

        return true
    }

    private fun testPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            if (((ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) !=
                            PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Please Grant Permissions",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) {
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                REQUEST_PERMISSIONS)
                    }.show()
                } else {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSIONS)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:" + packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(intent)
                    }.show()
                }
                return
            }
        }
    }


    override fun onBackPressed() {

        val fragment = this.supportFragmentManager.findFragmentById(R.id.container_main)
        if (!(fragment as? IOnBackPressed)?.onBackPressed()!!.not()) {
            rechercheMessage()
            afficheCalendrierFragment()
//            eventListGal = myAppDataBase?.myDao()?.getEvents()!!
//            val calendrierFragment = CalendrierFragment()
//            calendrierFragment.trait_affichage_date()
        }
        super.onBackPressed()

    }


}








