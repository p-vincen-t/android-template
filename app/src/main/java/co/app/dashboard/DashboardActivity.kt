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
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.app.App.Companion.WALLET_FEATURE_NAME
import co.app.App.Companion.WALLET_FRAGMENT
import co.app.BaseSplitActivity
import co.app.PlaceHolderModuleFragment
import co.app.R
import co.app.common.UserAccount
import co.app.dashboard.main.MainFragment
import co.app.dashboard.recents.RecentActivitiesFragment
import co.app.databinding.ActivityDashboardBinding
import co.app.dsl.listItems
import co.app.dsl.startActivity
import co.app.legal.LegalActivity
import co.app.messaging.MessagingAndNotificationsActivity
import co.app.photo.PhotoView
import co.app.search.SearchActivity
import co.app.settings.SettingsActivity
import com.allenliu.badgeview.BadgeFactory
import com.allenliu.badgeview.BadgeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import promise.commons.AndroidPromise
import promise.commons.createInstance
import javax.inject.Inject

class DashboardActivity : BaseSplitActivity(),

    PlaceHolderModuleFragment.OnFragmentInteractionListener {

    @Inject
    lateinit var dashboardViewModelFactory: DashboardViewModelFactory

    @Inject
    lateinit var promise: AndroidPromise

    private lateinit var dashboardViewModel: DashboardViewModel

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main -> {
                    viewpager.setCurrentItem(0, true)
                    title = "Today"
                    search_fab.show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_recent_activities -> {
                    viewpager.setCurrentItem(1, true)
                    title = "History"
                    search_fab.show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_wallet -> {
                    viewpager.setCurrentItem(2, true)
                    title = "My Account"
                    search_fab.hide()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        title_text_view.text = title.toString()
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(
            this,
            R.layout.activity_dashboard
        )
        setSupportActionBar(bottom_app_bar)

        DaggerDashboardComponent.factory().create(
            app.reposComponent().searchRepository(), app.accountComponent
        )
            .inject(this)

        dashboardViewModel = ViewModelProvider(this, dashboardViewModelFactory).get(
            DashboardViewModel::class.java
        )
        binding.viewModel = dashboardViewModel
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, bottom_app_bar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        search_fab.setOnClickListener {
            startActivity<SearchActivity>()
        }

        BadgeFactory.create(this)
            .setTextColor(resources.getColor(R.color.color_on_secondary))
            .setWidthAndHeight(15, 15)
            .setBadgeBackground(resources.getColor(R.color.color_secondary))
            .setTextSize(10)
            .setBadgeGravity(Gravity.END or Gravity.TOP)
            .setBadgeCount(3)
            .setShape(BadgeView.SHAPE_CIRCLE)
            .setSpace(5, 5)
            .bind(messages_imageView)

        messages_imageView.setOnClickListener {
            startActivity<MessagingAndNotificationsActivity>()
        }

        bottom_nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        viewpager.adapter = SectionsPagerAdapter(supportFragmentManager)

        dashboardViewModel.accountsResult.observe(this, Observer { accounts ->
            accounts_list.listItems(accounts, R.layout.account_nav_layout,
                bind = { v: View, account: UserAccount.UserChildAccount, _: Int ->
                    val accountImageView: PhotoView =
                        v.findViewById(R.id.account_imageView)
                    accountImageView.setPhoto(account.photo)
                },
                itemClick = { _: UserAccount.UserChildAccount, _: Int ->
                    startAuthActivity()
                })
        })
        dashboardViewModel.fetchAccounts()

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
