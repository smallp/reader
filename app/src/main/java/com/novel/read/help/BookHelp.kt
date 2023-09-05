package com.novel.read.help

import com.novel.read.App
import com.novel.read.data.db.entity.Book
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.utils.FileUtils
import com.novel.read.utils.MD5Utils
import com.novel.read.utils.ext.*
import com.spreada.utils.chinese.ZHConverter
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import java.io.File
import java.util.regex.Pattern

object BookHelp {
    private const val cacheFolderName = "book_cache"
    private const val cacheImageFolderName = "images"
    private val downloadDir: File = App.INSTANCE.externalFilesDir
    fun formatChapterName(bookChapter: BookChapter): String {
        return String.format(
            "%05d-%s.nb",
            bookChapter.chapterId,
            MD5Utils.md5Encode16(bookChapter.chapterName)
        )
    }

    fun clearCache() {
        FileUtils.deleteFile(
            FileUtils.getPath(downloadDir, cacheFolderName)
        )
    }

    suspend fun saveImage(book: Book, src: String) {
    }

    fun getImage(book: Book, src: String): File {
        return FileUtils.getFile(
            downloadDir,
            cacheFolderName,
            book.getFolderName(),
            cacheImageFolderName,
            "${MD5Utils.md5Encode16(src)}${getImageSuffix(src)}"
        )
    }

    private fun getImageSuffix(src: String): String {
        var suffix = src.substringAfterLast(".").substringBefore(",")
        if (suffix.length > 5) {
            suffix = ".jpg"
        }
        return suffix
    }

    fun getChapterFiles(book: Book): List<String> {
        val fileNameList = arrayListOf<String>()
        if (book.isLocalBook()) {
            return fileNameList
        }
        FileUtils.createFolderIfNotExist(
            downloadDir,
            subDirs = arrayOf(cacheFolderName, book.getFolderName())
        ).list()?.let {
            fileNameList.addAll(it)
        }
        return fileNameList
    }

    fun getContent(book: Book, bookChapter: BookChapter): String? {
        if (book.isLocalBook()) {
//            return LocalBook.getContext(book, bookChapter)
            //todo epub
            return null
        } else {
            val file = FileUtils.getFile(
                downloadDir,
                cacheFolderName,
                book.getFolderName(),
                formatChapterName(bookChapter)
            )
            if (file.exists()) {
                return file.readText()
            }
        }
        return null
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
        Pattern.compile("^(.*?第([\\d零〇一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟０-９\\s]+)[章节篇回集])[、，。　：:.\\s]*")
    }

    @Suppress("SpellCheckingInspection")
    private val regexOther by lazy {
        // 所有非字母数字中日韩文字 CJK区+扩展A-F区
        return@lazy "[^\\w\\u4E00-\\u9FEF〇\\u3400-\\u4DBF\\u20000-\\u2A6DF\\u2A700-\\u2EBEF]".toRegex()
    }

    private val regexA by lazy {
        return@lazy "\\s".toRegex()
    }

    private val regexB by lazy {
        return@lazy "^第.*?章|[(\\[][^()\\[\\]]{2,}[)\\]]$".toRegex()
    }

    suspend fun disposeContent(
        book: Book,
        title: String,
        content: String
    ): List<String> {
        var title1 = title
        var content1 = content
//        if (book.getReSegment()) {
//            content1 = ContentHelp.reSegment(content1, title1)
//        }
//        if (book.getUseReplaceRule()) {
//            synchronized(this) {
//                if (bookName != book.name || bookOrigin != book.origin) {
//                    bookName = book.name
//                    bookOrigin = book.origin
//                    replaceRules = if (bookOrigin.isNullOrEmpty()) {
//                        App.db.replaceRuleDao().findEnabledByScope(bookName!!)
//                    } else {
//                        App.db.replaceRuleDao().findEnabledByScope(bookName!!, bookOrigin!!)
//                    }
//                }
//            }
//            replaceRules.forEach { item ->
//                item.pattern.let {
//                    if (it.isNotEmpty()) {
//                        try {
//                            content1 = if (item.isRegex) {
//                                content1.replace(it.toRegex(), item.replacement)
//                            } else {
//                                content1.replace(it, item.replacement)
//                            }
//                        } catch (e: Exception) {
//                            withContext(Main) {
//                                App.INSTANCE.toast("${item.name}替换出错")
//                            }
//                        }
//                    }
//                }
//            }
//        }
        try {
            when (AppConfig.chineseConverterType) {
                1 -> {
                    title1=ZHConverter.getInstance(ZHConverter.SIMPLIFIED).convert(title1)
                    content1=ZHConverter.getInstance(ZHConverter.SIMPLIFIED).convert(content1)
                }
                2 -> {
                    title1=ZHConverter.getInstance(ZHConverter.TRADITIONAL).convert(title1)
                    content1=ZHConverter.getInstance(ZHConverter.TRADITIONAL).convert(content1)
                }
            }
        } catch (e: Exception) {
            withContext(Main) {
                App.INSTANCE.toast("简繁转换出错")
            }
        }
        val contents = arrayListOf<String>()
        content1.split("\n").forEach {
            val str = it.replace("^[\\n\\s\\r]+".toRegex(), "")
            if (contents.isEmpty()) {
                contents.add(title1)
                if (str != title1 && str.isNotEmpty()) {
                    contents.add("${ReadBookConfig.paragraphIndent}$str")
                }
            } else if (str.isNotEmpty()) {
                contents.add("${ReadBookConfig.paragraphIndent}$str")
            }
        }
        return contents
    }
}