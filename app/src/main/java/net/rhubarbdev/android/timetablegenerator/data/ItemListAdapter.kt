package net.rhubarbdev.android.timetablegenerator.data

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.rhubarbdev.android.timetablegenerator.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ItemListAdapter: ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ITEMS_COMPARATOR){

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        val startEndTime = "${formatter.format(current.start)} - ${formatter.format(current.end)}"
        holder.bind(current.day.toString(), current.content, startEndTime, current.colour, current.itemID, current.start, current.end)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTitle: TextView = itemView.findViewById(R.id.itemTitle)
        private val itemStartEndTime: TextView = itemView.findViewById(R.id.startEndTime)
        private val itemContent: TextView = itemView.findViewById(R.id.itemContent)
        private val drawable = itemView.background.mutate() as GradientDrawable

        fun bind(title: String, content: String, time: String, background: Int, id : Int, start: LocalDateTime, end: LocalDateTime) {
            itemTitle.text = title
            itemStartEndTime.text = time
            itemContent.text = content
            drawable.colors = intArrayOf(background, calcTint(background))
            itemView.background = drawable
            itemView.background.alpha = 200
            itemView.tag = Tags.ItemTag(id, start, end, background)
        }

        private fun calcTint(background: Int): Int {
            val red = (background.red * (255 - background.red) * 0.25).toInt()
            val green = (background.green * (255 - background.green) * 0.25).toInt()
            val blue = (background.blue * (255 - background.blue) * 0.25).toInt()
            return Color.rgb(red, green, blue)
        }

        companion object {
            fun create(parent: ViewGroup): ItemViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_item, parent, false)
                return ItemViewHolder(view)
            }
        }
    }

    companion object {
        private val ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }
}
