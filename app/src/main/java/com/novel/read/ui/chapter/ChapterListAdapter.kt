package com.novel.read.ui.chapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.db.entity.BookChapter
import com.novel.read.databinding.ItemChapterListBinding
import com.novel.read.utils.ext.accentColor
import com.novel.read.utils.ext.getCompatColor

class ChapterListAdapter(val callback: Callback) :
    BaseBindingAdapter<BookChapter, ItemChapterListBinding>() {

    val cacheFileNames = hashSetOf<String>()


    override fun convert(holder: VBViewHolder<ItemChapterListBinding>, item: BookChapter) {

        holder.vb.run {
            val isDur = callback.durChapterIndex() == item.chapterIndex
            if (isDur) {
                tvChapterName.setTextColor(context.accentColor)
            } else {
                tvChapterName.setTextColor(context.getCompatColor(R.color.primaryText))
            }
            tvChapterName.text = item.chapterName

        }

        holder.itemView.run {
            setOnClickListener {
                getItem(holder.layoutPosition).let {
                    callback.openChapter(it)
                }
            }
        }

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemChapterListBinding {
        return ItemChapterListBinding.inflate(inflater,parent,false)
    }

    interface Callback {
        val isLocalBook: Boolean
        fun openChapter(bookChapter: BookChapter)
        fun durChapterIndex(): Int
    }

}