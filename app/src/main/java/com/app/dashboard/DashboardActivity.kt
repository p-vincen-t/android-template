/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.dashboard

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
import com.app.App.Companion.WALLET_FEATURE_NAME
import com.app.App.Companion.WALLET_FRAGMENT
import com.app.BaseSplitActivity
import com.app.PlaceHolderModuleFragment
import com.app.R
import com.app.domain.session.Account
import com.app.auth.AuthActivity
import com.app.databinding.ActivityDashboardBinding
import com.app.legal.LegalActivity
import com.app.messaging.MessagingActivity
import com.app.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import org.jetbrains.anko.intentFor
import promise.commons.createInstance
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import javax.inject.Inject
import kotlin.reflect.KClass

class DashboardActivity : BaseSplitActivity(),
    PromiseAdapter.Listener<Account>,
    PlaceHolderModuleFragment.OnFragmentInteractionListener {

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
        private var fragment: Fragment = if (app.isWalletModuleInstalled()) createInstance(Class.forName(
            WALLET_FRAGMENT).kotlin) as Fragment
        else PlaceHolderModuleFragment.newInstance(
            WALLET_FEATURE_NAME,
            R.drawable.ic_account_wallet_grey_24dp,
            "Wallet aids organize your finances")
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> mainFragment
            1 -> recentActivitiesFragment
            2 -> fragment
            else -> throw IllegalArgumentException("Only allowed three fragments")
        }

        override fun getCount(): Int = 3
    }

    fun snoozeNotifications(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onRequestedModule(module: String) {
        loadAndLaunchModule(module)
    }
}
