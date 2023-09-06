package com.novel.read.ui.read.provider

import android.graphics.Bitmap
import com.novel.read.data.db.entity.Book
import java.util.concurrent.ConcurrentHashMap

object ImageProvider {

    private val cache = ConcurrentHashMap<Int, ConcurrentHashMap<String, Bitmap>>()
    fun getImage(book: Book, chapterIndex: Int, src: String, onUi: Boolean = false): Bitmap? {
        return null
    }

    fun clearAllCache() {
        cache.forEach { indexCache ->
            indexCache.value.forEach {
                it.value.recycle()
            }
        }
        cache.clear()
    }

    fun clearOut(chapterIndex: Int) {
        cache.forEach { indexCache ->
            if (indexCache.key !in chapterIndex - 1..chapterIndex + 1) {
                indexCache.value.forEach {
                    it.value.recycle()
                }
                cache.remove(indexCache.key)
            }
        }
    }

}
