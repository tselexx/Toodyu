package com.tselexx.toodyu


import android.Manifest
import android.animation.ObjectAnimator.ofPropertyValuesHolder
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.tselexx.toodyu.Common_Util_functions.Companion.agenda
import com.tselexx.toodyu.Common_Util_functions.Companion.calendar
import com.tselexx.toodyu.Common_Util_functions.Companion.contx
import com.tselexx.toodyu.Common_Util_functions.Companion.dayDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.delfromday
import com.tselexx.toodyu.Common_Util_functions.Companion.delfrommonth
import com.tselexx.toodyu.Common_Util_functions.Companion.delfromyear
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListGal
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListJour
import com.tselexx.toodyu.Common_Util_functions.Companion.eventListPrev
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdateCur
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdateJour
import com.tselexx.toodyu.Common_Util_functions.Companion.eventdatePrev
import com.tselexx.toodyu.Common_Util_functions.Companion.hourDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.menuGal
import com.tselexx.toodyu.Common_Util_functions.Companion.minuteDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.monthDateOfDay
import com.tselexx.toodyu.Common_Util_functions.Companion.myAppDataBase
import com.tselexx.toodyu.Common_Util_functions.Companion.suiteAgenda
import com.tselexx.toodyu.Common_Util_functions.Companion.toolbar
import com.tselexx.toodyu.Common_Util_functions.Companion.vC
import com.tselexx.toodyu.Common_Util_functions.Companion.yearDateOfDay
import com.tselexx.toodyu.R.id.*
import com.tselexx.toodyu.interfaçage.IOnBackPressed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.calendrier_fragment.view.*
import training.tselexx.toodyu.sqlite.Agenda
import training.tselexx.toodyu.sqlite.MyAppDataBase
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
        eventdateCur.clear()
        eventdatePrev.clear()
        eventdateJour.clear()
        eventListPrev.clear()
        eventListJour.clear()
        eventListCur.clear()

        //permission explicite si nécessaire
        testPermission()
        //Initialisation de ROOM sqlite
        initRoom()
        //recherche si existe au moins un event
        rechercheMessage()
        //implementarion de la toolbar
        toolbar = toolbar_main
        setSupportActionBar(toolbar_main)
//        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        toolbar!!.setLogo(R.mipmap.ic_toodyu)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            image_animation(toolbar!!.logo)
        }
        //affiche le 1er fragment (Calendrier)
        afficheCalendrierFragment()
    }


    private fun image_animation(drawable: Drawable) {

        var animator = ofPropertyValuesHolder(drawable,
                PropertyValuesHolder.ofInt("alpha", 0))
        animator.setTarget(drawable)
        animator.setDuration(2000)
        animator.repeatMode = REVERSE
        animator.repeatCount = INFINITE
        animator.start()
    }


    //fonctions SQLITE / ROOM
    private fun initRoom() {
        myAppDataBase = Room.databaseBuilder(applicationContext, MyAppDataBase::class.java, "eventDB").allowMainThreadQueries().build()
    }


    //Recherche d'au moins 1 message toutes dates confondues
    private fun rechercheMessage() {
        eventListGal = myAppDataBase?.myDao()?.getEvents()!!

        if (!eventListGal.isEmpty()) {
            eventListGal.forEach { it ->
                if (((it.event_year < yearDateOfDay) ||
                (it.event_year == yearDateOfDay && it.event_month < monthDateOfDay) ||
                (it.event_year == yearDateOfDay && it.event_month == monthDateOfDay &&
                it.event_day < dayDateOfDay))) {

                    eventListPrev.add(it)

                } else {
                    if (it.event_year == yearDateOfDay && it.event_month == monthDateOfDay &&
                                            it.event_day == dayDateOfDay) {
                        eventListJour.add(it)
                    }
                    else {

                        eventListCur.add(it)
                    }
                }
            }
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            mode_emploi -> {
                displayInfo()
            }
            supprimer -> {
                choix_date()
            }

            syncAgenda -> {
                agenda = myAppDataBase?.myDao()?.getAgenda()!!
                if (agenda!!.sync_agenda) {
                    menuGal!!.findItem(R.id.syncAgenda).setChecked(false)
//                    item.setChecked(false)
                    suiteAgenda = false
                } else {
                    menuGal!!.findItem(R.id.syncAgenda).setChecked(true)
//                    item.setChecked(true)
                    suiteAgenda = true
                }
                myAppDataBase?.myDao()?.updateAgenda(suiteAgenda)
            }

        }

        return super.onOptionsItemSelected(item)

    }


    //affichage du fragment gérant le calendrier
    private fun afficheCalendrierFragment() {

        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, CalendrierFragment())
                .commit()
    }

    private fun displayInfo() {
        val alert = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        with(alert) {
            setTitle(getString(R.string.explications))
            setMessage(getString(R.string.info100))
            setPositiveButton("OK") { dialog, whichButton ->
                dialog.dismiss()
            }
        }
        // Dialog
        val dialog = alert.create()
        dialog.show()
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

    private fun choix_date() {

        val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    delfromyear = year
                    delfrommonth = month
                    delfromday = day
                    controle_date()
                }


        val datePickerDialog = DatePickerDialog(contx,
                dateSetListener,
                yearDateOfDay,
                monthDateOfDay,
                dayDateOfDay)

        datePickerDialog.setTitle(getString(R.string.supp_event))
        datePickerDialog.setMessage(getString(R.string.choose_date))
        datePickerDialog.setOnCancelListener {
            vC!!.calendarView.setSelectedDate(calendar!!.getTime())

        }
        datePickerDialog.setOnDismissListener {
            vC!!.calendarView.setSelectedDate(calendar!!.getTime())
        }

        datePickerDialog.show()
    }

    private fun controle_date() {
        if (!((delfromyear < yearDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth < monthDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth == monthDateOfDay && delfromday < dayDateOfDay) ||
            (delfromyear == yearDateOfDay && delfrommonth == monthDateOfDay && delfromday == dayDateOfDay))) {
            Toast.makeText(contx, getString(R.string.date_del), Toast.LENGTH_SHORT).show()

        } else {
            deleteMessageAnterieurs()
        }


    }

    //suppression au lancement de l'application de tous les messages antérieurs
    private fun deleteMessageAnterieurs() {
        var alertBuilder = AlertDialog.Builder(contx!!, R.style.AlertDialogTheme)
        alertBuilder
                // if the dialog is cancelable
                .setCancelable(true)
                .setIcon(R.mipmap.ic_delete_foreground)
                .setTitle(getString(R.string.conf_suppr))
                .setMessage(getString(R.string.mind_suppr) + "$delfromday / $delfrommonth / $delfromyear")
                // positive button text and action
                .setPositiveButton(getString(R.string.ok)) { d, i ->
                    trait_delete_prev()
                }
                // negative button text and action
                .setNegativeButton(getString(R.string.cancel)) { d, i ->
                    vC!!.calendarView.setSelectedDate(calendar!!.getTime())

                }
        val alert = alertBuilder.create()
        alert.show()


    }

    private fun trait_delete_prev() {


        var iterator = eventListGal.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (((it.event_year < delfromyear) ||
                (it.event_year == delfromyear && it.event_month < delfrommonth) ||
                (it.event_year == delfromyear && it.event_month == delfrommonth &&
                it.event_day < delfromday))) {
                iterator.remove()
            }
        }


        iterator = eventListPrev.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (((it.event_year < delfromyear) ||
                (it.event_year == delfromyear && it.event_month < delfrommonth) ||
                (it.event_year == delfromyear && it.event_month == delfrommonth &&
                it.event_day < delfromday))) {
                iterator.remove()
            }
        }

        myAppDataBase!!.myDao().deletePreviousEvents(
                delfromyear,
                delfrommonth,
                delfromday)

        CalendrierFragment().trait_affichage_date()

    }


    companion object {
        const val TAG = ">>> TODYU2 "
        private val REQUEST_PERMISSIONS = 200

    }

    override fun onBackPressed() {

        val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
        if (!(fragment as? IOnBackPressed)?.onBackPressed()!!.not()) {
            eventListGal = myAppDataBase?.myDao()?.getEvents()!!
            val calendrierFragment = CalendrierFragment()
            calendrierFragment.trait_affichage_date()
        }
        super.onBackPressed()

    }
}








