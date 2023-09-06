package com.novel.read.data.db.entity

import org.litepal.crud.LitePalSupport

data class BookChapter(
    val chapterId: Long,
    val bookId: Long,
    val chapterIndex: Int,
    var chapterName: String,
    val createTimeValue: Long,
    val from: Long,
    val to: Long,
) : LitePalSupport()