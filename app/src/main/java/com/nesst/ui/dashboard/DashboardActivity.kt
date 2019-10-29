package com.nesst.ui.dashboard

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
import com.google.android.material.navigation.NavigationView
import com.nesst.R
import com.nesst.databinding.ActivityDashboardBinding
import com.nesst.ui.*
import com.nesst.ui.auth.AuthActivity
import com.nesst.ui.legal.LegalActivity
import com.nesst.ui.settings.SettingsActivity
import com.nesst.ui.viewHolders.NavigationAccountViewHolder
import com.nesstbase.auth.Account
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import net.steamcrafted.materialiconlib.MaterialMenuInflater
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import javax.inject.Inject
import kotlin.reflect.KClass

class DashboardActivity : BaseSplitActivity(), NavigationView.OnNavigationItemSelectedListener,
    PromiseAdapter.Listener<Account> {

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
                R.id.navigation_orders -> {
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
        /*val binding = ActivityDashboardBinding()*/
        val binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(
            this,
            R.layout.activity_dashboard
        )

        setSupportActionBar(toolbar)

        DaggerUiComponent.builder().appComponent(app.appComponent).build().inject(this)

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

        nav_view.setNavigationItemSelectedListener(this)

        bottom_nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        viewpager.adapter = SectionsPagerAdapter(supportFragmentManager)

        accountsAdapter = PromiseAdapter(ArrayMap<Class<*>, KClass<out Viewable>>().apply {
            put(Account::class.java, NavigationAccountViewHolder::class)
        }, this)

        accounts_list.layoutManager = LinearLayoutManager(this)
        accounts_list.adapter = accountsAdapter
        dashboardViewModel.accountsResult.observe(this, Observer {
            accountsAdapter.add(it)
        })
        dashboardViewModel.fetchAccounts()
    }

    override fun onClick(t: Account, id: Int) {

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

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
        MaterialMenuInflater
            .with(this) // Provide the activity context
            // Set the fall-back color for all the icons. Colors set inside the XML will always have higher priority
            // Inflate the menu
            .inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
// Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> true
            R.id.action_messages -> {
                startActivityInMessagingFeature(MESSAGING_ACTIVITY)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        executeBeforeAfterOnUi({
            drawer_layout.closeDrawer(GravityCompat.START)
        }, {
            when (item.itemId) {

            }
        })
        return true
    }

    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        private var mainFragment: MainFragment = MainFragment.newInstance()
        private var ordersFragment: OrdersFragment = OrdersFragment.newInstance()
        private var walletFragment: WalletFragment = WalletFragment.newInstance()
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> mainFragment
            1 -> ordersFragment
            2 -> walletFragment
            else -> throw IllegalArgumentException("Only allowed three fragments")
        }

        override fun getCount(): Int = 3
    }
}
