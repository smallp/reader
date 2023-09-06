package com.novel.read.ui.main.bookshelf

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.VMBaseFragment
import com.novel.read.constant.EventBus
import com.novel.read.constant.IntentAction
import com.novel.read.constant.PreferKey
import com.novel.read.data.db.entity.Book
import com.novel.read.databinding.DialogBookshelfConfigBinding
import com.novel.read.databinding.FragmentBookShelfBinding
import com.novel.read.help.IntentDataHelp
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.okButton
import com.novel.read.ui.main.bookshelf.arrange.ArrangeBookActivity
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.BooksDiffCallBack
import com.novel.read.utils.ext.*
import com.novel.read.utils.viewbindingdelegate.viewBinding
import org.jetbrains.anko.startActivity


class BookshelfFragment : VMBaseFragment<BookViewModel>(R.layout.fragment_book_shelf), BaseBookAdapter.CallBack  {

    override val viewModel: BookViewModel by viewModels()
    private val binding by viewBinding(FragmentBookShelfBinding::bind)
    private lateinit var booksAdapter: BaseBookAdapter
    private var bookshelfLiveData = MutableLiveData<List<Book>>()
    private lateinit var selectBook: Book

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(binding.titleBar.toolbar)
        initRecycleView()
        upRecyclerData()
    }

    override fun onCompatCreateOptionsMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main_bookshelf, menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem) {
        super.onCompatOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_add -> addBook()
            R.id.menu_bookshelf_layout -> configBookshelf()
            R.id.menu_arrange_bookshelf -> startActivity<ArrangeBookActivity>()
        }
    }

    private fun initRecycleView() {
        ATH.applyEdgeEffectColor(binding.rlvBookShelf)

        binding.refreshLayout.setColorSchemeColors(accentColor)
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
        }

        val bookshelfLayout = getPrefInt(PreferKey.bookshelfLayout)
        if (bookshelfLayout == 0) {
            binding.rlvBookShelf.layoutManager = LinearLayoutManager(context)
            booksAdapter = BookShelfAdapter(this)
        } else {
            binding.rlvBookShelf.layoutManager = GridLayoutManager(context, bookshelfLayout + 2)
            booksAdapter = BooksAdapterGrid(this)
        }
        binding.rlvBookShelf.adapter = booksAdapter
        booksAdapter.setEmptyView(R.layout.view_empty)
        booksAdapter.setDiffCallback(BooksDiffCallBack())

        booksAdapter.setOnItemClickListener { adapter, view, position ->
            selectBook = adapter.data[position] as Book
            activity?.startActivity<ReadBookActivity>(
                Pair(IntentAction.bookId, selectBook.bookId),
                Pair("key", IntentDataHelp.putData(selectBook))
            )
//            ProcessLifecycleOwner.get().lifecycle.addObserver(App.ApplicationObserverInRead())
        }
    }


    private fun upRecyclerData() {
        bookshelfLiveData.removeObservers(this)
        bookshelfLiveData.value = App.db.getBookDao().getAllBooks()

        bookshelfLiveData.observe(viewLifecycleOwner) { list ->
            Log.e("BookFragment", "observeLiveBus: 开始更新")
            booksAdapter.isUseEmpty = list.isEmpty()
            val books = when (getPrefInt(PreferKey.bookshelfSort)) {
                1 -> list.sortedByDescending { it.lastUpdateChapterDate }
                2 -> list.sortedBy { it.bookName }
                else -> list.sortedByDescending { it.durChapterTime }
            }

            booksAdapter.setList(books.toMutableList())
        }

    }

    fun addBook() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 100)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val contentResolver = requireActivity().applicationContext.contentResolver

                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                // Check for the freshest data.
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                val cursor: Cursor? = contentResolver.query(
                    uri, null, null, null, null, null
                )

                cursor?.use {
                    // moveToFirst() returns false if the cursor has 0 rows. Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (it.moveToFirst()) {

                        // Note it's called "Display Name". This is
                        // provider-specific, and might not necessarily be the file name.
                        val displayName: String =
                            it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                        Log.d("small", "$displayName ${sizeIndex}")
                    }
                }

            }
        }
    }

    @SuppressLint("InflateParams")
    private fun configBookshelf() {
        requireContext().alert(titleResource = R.string.bookshelf_layout) {
            val bookshelfLayout = getPrefInt(PreferKey.bookshelfLayout)
            val bookshelfSort = getPrefInt(PreferKey.bookshelfSort)
            val alertBinding =
                DialogBookshelfConfigBinding.inflate(layoutInflater)
                    .apply {
                        rgLayout.checkByIndex(bookshelfLayout)
                        rgSort.checkByIndex(bookshelfSort)
                    }

            customView = alertBinding.root
            okButton {
                alertBinding.apply {
                    var changed = false
                    if (bookshelfLayout != rgLayout.getCheckedIndex()) {
                        putPrefInt(PreferKey.bookshelfLayout, rgLayout.getCheckedIndex())
                        changed = true
                    }
                    if (bookshelfSort != rgSort.getCheckedIndex()) {
                        putPrefInt(PreferKey.bookshelfSort, rgSort.getCheckedIndex())
                        changed = true
                    }
                    if (changed) {
                        activity?.recreate()
                    }
                }
            }
            noButton()
        }.show().applyTint()
    }

    override fun observeLiveBus() {
        super.observeLiveBus()
        observeEvent<Long>(EventBus.UP_BOOK) {
            bookshelfLiveData.value = App.db.getBookDao().getAllBooks()
        }
        observeEvent<Long>(EventBus.UPDATE_BOOK) {
            booksAdapter.notification(it)
        }
        observeEvent<String>(EventBus.SHOW_AD) {
            activity?.startActivity<ReadBookActivity>(
                Pair(IntentAction.bookId, selectBook.bookId),
                Pair("key", IntentDataHelp.putData(selectBook))
            )
        }

    }

    override fun isUpdate(bookId: Long): Boolean {
        return false
    }
}