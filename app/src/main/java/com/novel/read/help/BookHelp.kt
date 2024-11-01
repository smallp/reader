package com.novel.read.help

import com.novel.read.App
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.utils.StringUtils
import com.novel.read.utils.ext.externalFilesDir
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.util.regex.Pattern

object BookHelp {
    private val downloadDir: File = App.INSTANCE.externalFilesDir

    fun getContent(book: Book, bookChapter: BookChapter): String? {
        val target = File(
            downloadDir,
            book.originName
        )
        val r = RandomAccessFile(target, "r")
        r.seek(bookChapter.from)
        val res = ByteArray((bookChapter.to - bookChapter.from).toInt())
        r.read(res)
        return res.toString(Charset.forName("utf8")).trim()
    }

    fun delBook(book: Book) {
        val target = File(
            downloadDir,
            book.originName
        )
        target.delete()
    }

    private val chapterNamePattern by lazy {
        listOf(
            Pattern.compile("^(.*?第([\\d零〇一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟０-９\\s]+)[章节篇回集])[、，。　：:.\\s]*"),
            Pattern.compile("^\\d+[、\\s]?"),
        )
    }

    fun findTitlePattern(title: String): Pattern? {
        return chapterNamePattern.find {
            it.matcher(title).find()
        }
    }

    fun disposeContent(
        title: String,
        content: String
    ): List<String> {
        val contents = arrayListOf<String>()
        content.split("\n").forEach {
            val str = StringUtils.trim(it)
            if (contents.isEmpty()) {
                contents.add(title)
                if (str.trim() != title && str.isNotEmpty()) {
                    contents.add("${ReadBookConfig.paragraphIndent}$str")
                }
            } else if (str.isNotEmpty()) {
                contents.add("${ReadBookConfig.paragraphIndent}$str")
            }
        }
        return contents
    }
}