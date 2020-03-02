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

package co.app.dashboard

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
import co.app.App.Companion.WALLET_FEATURE_NAME
import co.app.App.Companion.WALLET_FRAGMENT
import co.app.BaseSplitActivity
import co.app.PlaceHolderModuleFragment
import co.app.R
import co.app.common.account.UserChildAccount
import co.app.common.dsl.adapter
import co.app.common.dsl.startActivity
import co.app.databinding.ActivityDashboardBinding
import co.app.legal.LegalActivity
import co.app.messaging.MessagingActivity
import co.app.search.SearchActivity
import co.app.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import promise.commons.Promise
import promise.commons.createInstance
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.reflect.KClass

class DashboardActivity : BaseSplitActivity(),
    PromiseAdapter.Listener<UserChildAccount>,
    PlaceHolderModuleFragment.OnFragmentInteractionListener {

    @Inject
    lateinit var dashboardViewModelFactory: DashboardViewModelFactory

    @Inject
    lateinit var promise: Promise

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var accountsAdapter: PromiseAdapter<UserChildAccount>

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

        DaggerDashboardComponent.builder().accountComponent(app.accountComponent).build()
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

        accountsAdapter = adapter(
            ArrayMap<Class<*>, KClass<out Viewable>>()
                .apply {
                    put(
                        UserChildAccount::class.java,
                        NavigationAccountViewHolder::class
                    )
                },
            this
        ) {
            args = null
        }

        accounts_list.layoutManager = LinearLayoutManager(this)
        accounts_list.adapter = accountsAdapter
        dashboardViewModel.accountsResult.observe(this, Observer {
            accountsAdapter.add(it)
        })
        dashboardViewModel.fetchAccounts()
    }

    override fun onClick(t: UserChildAccount, id: Int) {
        startAuthActivity()
    }

    override fun onBackPressed() =
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()

    fun profileInfoClicked(v: View) =
        executeBeforeAfterOnUi(
            promise,
            { onBackPressed() },
            { startAuthActivity() })

    fun settingsClicked(v: View) =
        executeBeforeAfterOnUi(
            promise,
            { onBackPressed() },
            {
                startActivity<SettingsActivity>()
            })

    fun legalClicked(v: View) =
        executeBeforeAfterOnUi(
            promise,
            { onBackPressed() },
            {
                startActivity<LegalActivity>()
            })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_search -> {
                startActivity<SearchActivity>()
                true
            }
            R.id.action_messages -> {
                startActivity<MessagingActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        private var mainFragment: MainFragment = MainFragment.newInstance()
        private var recentActivitiesFragment: RecentActivitiesFragment =
            RecentActivitiesFragment.newInstance()
        private var fragment: Fragment = if (app.isWalletModuleInstalled()) createInstance(
            Class.forName(
                WALLET_FRAGMENT
            ).kotlin
        ) as Fragment
        else PlaceHolderModuleFragment.newInstance(
            WALLET_FEATURE_NAME,
            R.drawable.ic_account_wallet_grey_24dp,
            "Wallet aids organize your finances"
        )

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> mainFragment
            1 -> recentActivitiesFragment
            2 -> fragment
            else -> throw IllegalArgumentException("Only allowed three fragments")
        }

        override fun getCount(): Int = 3
    }

    fun snoozeNotifications(view: View) {
        startActivity<SettingsActivity>()
    }

    override fun onRequestedModule(module: String) {
        loadAndLaunchModule(module)
    }
}
