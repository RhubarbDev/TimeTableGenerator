package net.rhubarbdev.android.timetablegenerator.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.rhubarbdev.android.timetablegenerator.R
import net.rhubarbdev.android.timetablegenerator.data.ItemParcel
import net.rhubarbdev.android.timetablegenerator.databinding.FragmentAddBinding
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddFragment: Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val args : AddFragmentArgs by navArgs()
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private var startTime : LocalDateTime? = null
    private var endTime : LocalDateTime? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addTimeButton.setOnClickListener{
            val content = binding.textbox.text.toString()
            val colour = Color.rgb(binding.setColourRed.progress, binding.setColourGreen.progress, binding.setColourBlue.progress)
            var action = AddFragmentDirections.actionAddFragmentToMainFragment()
            if(startTime != null && endTime != null){
                val dayOfWeek = DayOfWeek.valueOf(binding.datespinner.selectedItem.toString().uppercase())
                startTime!!.with(dayOfWeek)
                endTime!!.with(dayOfWeek)
                val parcel = ItemParcel(
                    day = dayOfWeek,
                    content = content,
                    start = startTime!!,
                    end = endTime!!,
                    colour = colour
                )
                action = AddFragmentDirections.actionAddFragmentToMainFragment(parcel)
            }else{
                Toast.makeText(activity as Context, "Invalid Item", Toast.LENGTH_SHORT).show()
            }
            this.findNavController().navigate(action)
        }
        val spinner = binding.datespinner
        ArrayAdapter.createFromResource(
            activity as Context,
            R.array.Days,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val parcel = args.item
        if(parcel != null){
            binding.textbox.setText(parcel.content)
            binding.datespinner.setSelection(getIndex(binding.datespinner, parcel.day.toString().uppercase()))
            binding.startTimeButton.text = formatter.format(parcel.start)
            binding.endTimeButton.text = formatter.format(parcel.end)
            startTime = parcel.start
            endTime = parcel.end
            binding.setColourRed.progress = parcel.colour.red
            binding.setColourGreen.progress = parcel.colour.green
            binding.setColourBlue.progress = parcel.colour.blue
            binding.selectedColor.setBackgroundColor(parcel.colour)
        }

        binding.startTimeButton.setOnClickListener{
            getHourMinute(binding.startTimeButton)
        }

        binding.endTimeButton.setOnClickListener{
            getHourMinute(binding.endTimeButton)
        }

        val sliderRed = binding.setColourRed
        val sliderGreen = binding.setColourGreen
        val sliderBlue = binding.setColourBlue

        sliderRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(slider: SeekBar?, value: Int, p2: Boolean) {
                updateColour(value, sliderGreen.progress, sliderBlue.progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        sliderGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(slider: SeekBar?, value: Int, p2: Boolean) {
                updateColour(sliderRed.progress, value, sliderBlue.progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        sliderBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(slider: SeekBar?, value: Int, p2: Boolean) {
                updateColour(sliderRed.progress, sliderGreen.progress, value)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun updateColour(red: Int, green: Int, blue: Int){
        binding.selectedColor.setBackgroundColor(Color.rgb(red, green, blue))
    }

    private fun getIndex(spinner: Spinner, string: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if ((spinner.getItemAtPosition(i) as String).uppercase() == string) {
                index = i
            }
        }
        return index
    }

    private fun getHourMinute(button: Button) {
        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            activity as Context,
            { _, hourOfDay, minute ->
                var localDateTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute).withSecond(0)
                if (button == binding.startTimeButton){
                    startTime = localDateTime
                    button.text = formatter.format(startTime)
                }else{
                    endTime = localDateTime
                    button.text = formatter.format(endTime)
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}