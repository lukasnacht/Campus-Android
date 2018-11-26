package de.tum.`in`.tumcampusapp.component.ui.transportation.widget

import android.app.Activity
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Switch
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.general.RecentsDao
import de.tum.`in`.tumcampusapp.component.other.generic.activity.ActivityForSearching
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.NoResultsAdapter
import de.tum.`in`.tumcampusapp.component.ui.transportation.MVVStationSuggestionProvider
import de.tum.`in`.tumcampusapp.component.ui.transportation.di.TransportModule
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.efa.StationResult
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.efa.WidgetDepartures
import de.tum.`in`.tumcampusapp.component.ui.transportation.repository.TransportLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.transportation.repository.TransportRemoteRepository
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MVVWidgetConfigureActivity : ActivityForSearching<Unit>(
        R.layout.activity_mvv_widget_configure, MVVStationSuggestionProvider.AUTHORITY, 3) {

    private var appWidgetId: Int = 0
    private lateinit var listViewResults: ListView
    private lateinit var adapterStations: ArrayAdapter<StationResult>
    private lateinit var recentsDao: RecentsDao

    private lateinit var widgetDepartures: WidgetDepartures

    @Inject
    lateinit var transportRemoteRepository: TransportRemoteRepository

    @Inject
    lateinit var transportLocalRepository: TransportLocalRepository

    @Inject
    lateinit var widgetController: MVVWidgetController

    @Inject
    lateinit var database: TcaDb

    private val disposable = CompositeDisposable()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.transportComponent()
                .transportModule(TransportModule(this))
                .build()
                .inject(this)

        recentsDao = database.recentsDao()

        // Setup cancel button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)
            val closeIcon = ContextCompat.getDrawable(this, R.drawable.ic_action_cancel)
            val color = ContextCompat.getColor(this, R.color.tum_blue)
            closeIcon?.setTint(color)
            actionBar.setHomeAsUpIndicator(closeIcon)
        }

        // Get appWidgetId from intent
        appWidgetId = intent.extras?.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
                ?: INVALID_APPWIDGET_ID

        widgetDepartures = widgetController.getWidget(appWidgetId)

        val autoReloadSwitch = findViewById<Switch>(R.id.mvv_widget_auto_reload)
        autoReloadSwitch.isChecked = widgetDepartures.autoReload
        autoReloadSwitch.setOnCheckedChangeListener { _, checked ->
            widgetDepartures.autoReload = checked
        }
        // TODO add handling for use location

        listViewResults = findViewById(R.id.activity_transport_listview_result)
        listViewResults.setOnItemClickListener { adapterView, _, position, _ ->
            val (station, stationId) = adapterView.adapter.getItem(position) as StationResult
            widgetDepartures.station = station
            widgetDepartures.stationId = stationId
            saveAndReturn()
        }

        // Initialize stations adapter
        val recentQueries = recentsDao.getAll(RecentsDao.STATIONS) ?: emptyList()
        val stations = transportLocalRepository.getRecentStations(recentQueries)
        adapterStations = ArrayAdapter(this, android.R.layout.simple_list_item_1, stations)

        if (adapterStations.isEmpty) {
            openSearch()
            return
        }

        listViewResults.adapter = adapterStations
    }

    override fun onStartSearch() {
        val recents = recentsDao.getAll(RecentsDao.STATIONS)
        if (recents == null) {
            listViewResults.adapter = NoResultsAdapter(this)
            return
        }

        val stations = transportLocalRepository.getRecentStations(recents)
        displayStations(stations)
    }

    override fun onStartSearch(query: String) {
        disposable += transportRemoteRepository
                .fetchStationsByPrefix(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayStations) {
                    // Something went wrong
                    Utils.showToast(this, R.string.something_wrong)
                    Utils.log(it)
                    onStartSearch()
                }
    }

    private fun displayStations(stations: List<StationResult>) {
        showLoadingEnded()

        if (stations.isEmpty()) {
            listViewResults.adapter = NoResultsAdapter(this)
            return
        }

        adapterStations.clear()
        adapterStations.addAll(stations)

        adapterStations.notifyDataSetChanged()
        listViewResults.adapter = adapterStations
    }

    /**
     * Setup cancel and back action
     *
     * @param item the menu item which has been pressed (or activated)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                cancelAndReturn()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Saves the selection to the database, triggers a widget update and closes this activity
     */
    private fun saveAndReturn() {
        // save the settings
        widgetController.addWidget(appWidgetId, widgetDepartures)

        // update widget
        val reloadIntent = Intent(this, MVVWidget::class.java)
        reloadIntent.action = MVVWidget.MVV_WIDGET_FORCE_RELOAD
        reloadIntent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        sendBroadcast(reloadIntent)

        // return to widget
        val resultValue = Intent()
        resultValue.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    /**
     * Cancel the widget creation and close this activity
     */
    private fun cancelAndReturn() {
        val resultValue = Intent()
        if (!(widgetDepartures.station.isEmpty() || widgetDepartures.stationId.isEmpty())) {
            saveAndReturn()
        } else {
            resultValue.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}
