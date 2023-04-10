package net.rhubarbdev.android.timetablegenerator.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import net.rhubarbdev.android.timetablegenerator.R
import net.rhubarbdev.android.timetablegenerator.TimeTableApplication
import net.rhubarbdev.android.timetablegenerator.data.*
import net.rhubarbdev.android.timetablegenerator.databinding.FragmentMainBinding
import net.rhubarbdev.android.timetablegenerator.notifications.NotificationSchedule
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit


class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val args : MainFragmentArgs by navArgs()
    private val itemViewModel: ItemViewModel by viewModels {
        ItemViewModelFactory((activity?.application as TimeTableApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val recycler = binding.itemRecyclerView
        val adapter = ItemListAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity as Context)
        itemViewModel.allItems.observe(viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }
        val newItem = args.item
        if (newItem != null){
            val item = itemParcelToItem(newItem)
            itemViewModel.insert(item)
            scheduleNotification(newItem.start, "Start Reminder", newItem.content)
            scheduleNotification(newItem.end, "End Reminder", newItem.content)
        }
        binding.addTimetableButton.setOnClickListener{
            val action = MainFragmentDirections.actionMainFragmentToAddFragment()
            this.findNavController().navigate(action)
        }

        recycler.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recycler,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        mailItem(view!!)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}
                })
        )

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id : Int = (viewHolder.itemView.tag as Tags.ItemTag).id
                itemViewModel.delete(id)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recycler)
        return binding.root
    }

    fun mailItem(view: View) {
        val tag : Tags.ItemTag = view.tag as Tags.ItemTag
        val parcel = ItemParcel(
            day = DayOfWeek.valueOf(view.findViewById<TextView>(R.id.itemTitle).text.toString().uppercase()),
            content = view.findViewById<TextView>(R.id.itemContent).text.toString(),
            start = tag.start,
            end = tag.end,
            colour = tag.colour
        )
        val action = MainFragmentDirections.actionMainFragmentToAddFragment(parcel)
        itemViewModel.delete(tag.id)
        this.findNavController().navigate(action)
    }

    private fun scheduleNotification(date: LocalDateTime, tag: String, body: String) {

        val data = Data.Builder().putString("body", body)
        val delay : Long = calculateDelay(date)
        val work =
            OneTimeWorkRequestBuilder<NotificationSchedule>()
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setConstraints(Constraints.Builder().setTriggerContentMaxDelay(0, TimeUnit.SECONDS).build()) // API Level 24
                .setInputData(data.build())
                .addTag(tag)
                .build()
        WorkManager.getInstance(activity as Context).enqueue(work)
    }

    private fun calculateDelay(date: LocalDateTime): Long {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), date)
    }

    private fun itemParcelToItem(parcel: ItemParcel): Item {
        return Item(
            day = parcel.day,
            content = parcel.content,
            start = parcel.start,
            end = parcel.end,
            colour = parcel.colour
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}