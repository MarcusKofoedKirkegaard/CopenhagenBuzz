package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity () {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate ( savedInstanceState : Bundle ?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}