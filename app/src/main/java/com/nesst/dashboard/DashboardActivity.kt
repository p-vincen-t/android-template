package com.nesst.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.collection.ArrayMap
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nesst.BaseSplitActivity
import com.nesst.R
import com.nesst.appdomain.session.Account
import com.nesst.auth.AuthActivity
import com.nesst.databinding.ActivityDashboardBinding
import com.nesst.legal.LegalActivity
import com.nesst.messaging.MessagingActivity
import com.nesst.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import org.jetbrains.anko.intentFor
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import javax.inject.Inject
import kotlin.reflect.KClass

class DashboardActivity : BaseSplitActivity(), PromiseAdapter.Listener<Account> {

    @Inject
    lateinit var dashboardViewModelFactory: DashboardViewModelFactory

    lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var accountsAdapter: PromiseAdapter<Account>

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main -> {
                    viewpager.setCurrentItem(0, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_recent_activities -> {
                    viewpager.setCurrentItem(1, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_wallet -> {
                    viewpager.setCurrentItem(2, true)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(
            this,
            R.layout.activity_dashboard
        )

        setSupportActionBar(toolbar)

        DaggerDashboardComponent.builder().sessionComponent(app.sessionComponent).build()
            .inject(this)

        dashboardViewModel = ViewModelProvider(this, dashboardViewModelFactory).get(
            DashboardViewModel::class.java
        )

        binding.viewModel = dashboardViewModel
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)

        toggle.syncState()

        bottom_nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        viewpager.adapter = SectionsPagerAdapter(supportFragmentManager)

        accountsAdapter = PromiseAdapter(ArrayMap<Class<*>, KClass<out Viewable>>().apply {
            put(Account::class.java, NavigationAccountViewHolder::class)
        }, this, null)

        accounts_list.layoutManager = LinearLayoutManager(this)
        accounts_list.adapter = accountsAdapter
        dashboardViewModel.accountsResult.observe(this, Observer {
            accountsAdapter.add(it)
        })
        dashboardViewModel.fetchAccounts()
    }

    override fun onClick(t: Account, id: Int) {
        startActivity(intentFor<AuthActivity>())
    }

    override fun onBackPressed() =
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()

    fun profileInfoClicked(v: View) =
        executeBeforeAfterOnUi(
            { onBackPressed() },
            { startActivity(Intent(this, AuthActivity::class.java)) })

    fun settingsClicked(v: View) =
        executeBeforeAfterOnUi(
            { onBackPressed() },
            { startActivity(Intent(this, SettingsActivity::class.java)) })

    fun legalClicked(v: View) =
        executeBeforeAfterOnUi(
            { onBackPressed() },
            { startActivity(Intent(this, LegalActivity::class.java)) })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_search -> true
            R.id.action_messages -> {
                startActivity(Intent(this, MessagingActivity::class.java))
                //startActivityInMessagingFeature(MESSAGING_ACTIVITY)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        private var mainFragment: MainFragment = MainFragment.newInstance()
        private var recentActivitiesFragment: RecentActivitiesFragment =
            RecentActivitiesFragment.newInstance()
        private var walletFragment: WalletFragment = WalletFragment.newInstance()
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> mainFragment
            1 -> recentActivitiesFragment
            2 -> walletFragment
            else -> throw IllegalArgumentException("Only allowed three fragments")
        }

        override fun getCount(): Int = 3
    }

    fun snoozeNotifications(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
