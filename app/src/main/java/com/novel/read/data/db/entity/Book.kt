package com.novel.read.data.db.entity

import com.novel.read.constant.AppPattern
import com.novel.read.constant.BookType
import org.litepal.crud.LitePalSupport
import kotlin.math.max

data class Book(
    val bookId: Long,
    val bookName: String,
    var totalChapterNum: Int = 0,               // 书籍目录总数
    var durChapterTitle: String? = null,        // 当前章节名称
    var durChapterIndex: Int = 0,               // 当前章节索引
    var durChapterPos: Int = 0,                 // 当前阅读的进度(首行字符的索引位置)
    var durChapterTime: Long = System.currentTimeMillis(),               // 最近一次阅读书籍的时间(打开正文的时间)
    var origin: String = BookType.local,
    var originName: String = "",
) : LitePalSupport() {

    fun isLocalBook(): Boolean {
        return origin == BookType.local
    }

    fun getFolderName(): String {
        return bookName.replace(AppPattern.fileNameRegex, "")
    }

    fun isEpub(): Boolean {
        return originName.endsWith(".epub", true)
    }

    fun canUpdate(): Boolean {
        return false
    }

    fun getUnreadChapterNum() = max(totalChapterNum - durChapterIndex - 1, 0)
}