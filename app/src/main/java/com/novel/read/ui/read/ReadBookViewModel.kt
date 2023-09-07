package com.novel.read.ui.read

import android.app.Application
import android.content.Intent
import com.novel.read.App
import com.novel.read.base.BaseViewModel
import com.novel.read.data.db.entity.Book
import com.novel.read.help.IntentDataHelp
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.service.help.ReadBook

class ReadBookViewModel(application: Application) : BaseViewModel(application) {
    var isInitFinish = false
    var searchContentQuery = ""

    fun initData(intent: Intent) {
        execute {
            ReadBook.inBookshelf = intent.getBooleanExtra("inBookshelf", true)
            IntentDataHelp.getData<Book>(intent.getStringExtra("key"))?.let {
                initBook(it)
            } ?: intent.getStringExtra("bookId")?.let {
                App.db.getBookDao().getBook(it)?.let { book ->
                    initBook(book)
                }
            } ?: App.db.getBookDao().lastReadBook().let {
                initBook(it)
            }
        }.onFinally {
            if (ReadBook.inBookshelf) {
                ReadBook.saveRead()
            }
        }
    }

    private fun initBook(book: Book) {
        if (ReadBook.book?.bookId != book.bookId) {
            ReadBook.resetData(book)
            isInitFinish = true
            ReadBook.chapterSize = App.db.getChapterDao().getChapterCount(book.bookId)
            if (ReadBook.durChapterIndex > ReadBook.chapterSize - 1) {
                ReadBook.durChapterIndex = ReadBook.chapterSize - 1
            }
            ReadBook.loadContent(resetPageOffset = true)
        } else {
            ReadBook.book = book
            if (ReadBook.durChapterIndex != book.durChapterIndex) {
                ReadBook.durChapterIndex = book.durChapterIndex
                ReadBook.durPageIndex = book.durChapterPos
                ReadBook.prevTextChapter = null
                ReadBook.curTextChapter = null
                ReadBook.nextTextChapter = null
            }
            ReadBook.titleDate.postValue(book.bookName)
            isInitFinish = true
            ReadBook.chapterSize = App.db.getChapterDao().getChapterCount(book.bookId)
            if (ReadBook.curTextChapter != null) {
                ReadBook.callBack?.upContent(resetPageOffset = false)
            } else {
                ReadBook.loadContent(resetPageOffset = true)
            }
        }
    }

    fun openChapter(index: Int, pageIndex: Int = 0) {
        ReadBook.prevTextChapter = null
        ReadBook.curTextChapter = null
        ReadBook.nextTextChapter = null
        ReadBook.callBack?.upContent()
        if (index != ReadBook.durChapterIndex) {
            ReadBook.durChapterIndex = index
            ReadBook.durPageIndex = pageIndex
        }
        ReadBook.saveRead()
        ReadBook.loadContent(resetPageOffset = true)
    }

    fun removeFromBookshelf(success: (() -> Unit)?) {
        execute {
            ReadBook.book?.delete()
        }.onSuccess {
            success?.invoke()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (BaseReadAloudService.pause) {
            ReadAloud.stop(context)
        }
    }


}