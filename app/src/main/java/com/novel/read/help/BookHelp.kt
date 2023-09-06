package com.novel.read.help

import com.novel.read.App
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.utils.FileUtils
import com.novel.read.utils.MD5Utils
import com.novel.read.utils.StringUtils
import com.novel.read.utils.ext.externalFilesDir
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.util.regex.Pattern

object BookHelp {
    private const val cacheFolderName = "book_cache"
    private val downloadDir: File = App.INSTANCE.externalFilesDir
    fun formatChapterName(bookChapter: BookChapter): String {
        return String.format(
            "%05d-%s.nb",
            bookChapter.chapterId,
            MD5Utils.md5Encode16(bookChapter.chapterName)
        )
    }

    fun getChapterFiles(book: Book): List<String> {
        return emptyList()
    }

    fun getContent(book: Book, bookChapter: BookChapter): String? {
        val target = File(
            downloadDir,
            book.originName
        )
        val r = RandomAccessFile(target, "r")
        r.seek(bookChapter.from)
        val res = ByteArray((bookChapter.to - bookChapter.from).toInt())
        r.read(res)
        return res.toString(Charset.forName("utf8"))
    }

    fun delContent(book: Book, bookChapter: BookChapter) {
        if (book.isLocalBook()) {
            return
        } else {
            FileUtils.createFileIfNotExist(
                downloadDir,
                cacheFolderName,
                book.getFolderName(),
                formatChapterName(bookChapter)
            ).delete()
        }
    }


    private val chapterNamePattern by lazy {
        listOf(
            Pattern.compile("^(.*?第([\\d零〇一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟０-９\\s]+)[章节篇回集])[、，。　：:.\\s]*"),
            Pattern.compile("^\\s*\\d+[、\\s]"),
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
                if (str != title && str.isNotEmpty()) {
                    contents.add("${ReadBookConfig.paragraphIndent}$str")
                }
            } else if (str.isNotEmpty()) {
                contents.add("${ReadBookConfig.paragraphIndent}$str")
            }
        }
        return contents
    }
}