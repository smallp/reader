package com.novel.read.ui.main.mail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.novel.read.R
import com.novel.read.base.VMBaseFragment
import com.novel.read.constant.AppConst
import com.novel.read.data.model.*
import com.novel.read.lib.ATH
import com.novel.read.ui.main.mail.pick.PickAdapter
import com.novel.read.ui.search.SearchActivity
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.fragment_mail.rlv_home
import kotlinx.android.synthetic.main.view_title_bar.*
import java.util.ArrayList

class MailFragment : VMBaseFragment<MailViewModel>(R.layout.fragment_mail) {

    override val viewModel: MailViewModel
        get() = getViewModel(MailViewModel::class.java)
    private lateinit var adapter: PickAdapter<MultiItemEntity>
    private val data: MutableList<MultiItemEntity> = ArrayList()
    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(toolbar)
        ATH.applyEdgeEffectColor(rlv_home)
        initRecycleView()
        upRecyclerData()
    }

    private fun initRecycleView() {
        rlv_home.layoutManager = LinearLayoutManager(context)
        adapter = PickAdapter(data)
        rlv_home.adapter = adapter
    }

    private fun upRecyclerData() {
        onRefresh()
        viewModel.run {
            homeResp.observe(viewLifecycleOwner) {
                data.clear()
                val hotEntity = HotEntity(it.starRank)
                val clickEntity = ClickEntity(it.wordNumRank)
                val recommendEntity = RecommendEntity(it.recommendRank)
                val endEntity = EndEntity(it.clickRank)
                data.add(TypeEntity())
                data.add(hotEntity)
                data.add(clickEntity)
                data.add(recommendEntity)
                data.add(endEntity)
                adapter.setList(data)
            }

            refreshStatus.observe(viewLifecycleOwner) {
                when (it) {
                    AppConst.loading -> {
                        adapter.isUseEmpty = true
                    }
                    AppConst.complete -> {
                        adapter.isUseEmpty = false
                    }
                    else -> {
                        adapter.setEmptyView(getErrorView())
                        adapter.isUseEmpty = true
                    }
                }
            }

        }
    }

    private fun onRefresh() {
        adapter.setEmptyView(R.layout.view_loading)
        viewModel.getAll()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, rlv_home, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

    override fun onCompatCreateOptionsMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main_mail, menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem) {
        super.onCompatOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_search -> startActivity<SearchActivity>()
        }
    }


}