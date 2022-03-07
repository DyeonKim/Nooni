package com.ssafy.nooni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ssafy.nooni.databinding.ActivityRegisterAllergyBinding
import com.ssafy.nooni.util.SharedPrefArrayListUtil

private const val TAG = "RegisterAllergy"
class RegisterAllergyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAllergyBinding
    var sharePrefArrayListUtil = SharedPrefArrayListUtil()
    val list = listOf<String>("갑각류", "견과", "달걀", "땅콩", "밀", "생선", "우유", "조개", "콩")
    val allergyList = ArrayList<String>()
    var cnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.tvAllergyAType.text = list[cnt]

        binding.btnAllergyANo.setOnClickListener {
            Log.d(TAG, "init: cnt = $cnt")
            if(++cnt >= list.size) save()
            else binding.tvAllergyAType.text = list[cnt]
        }
        binding.btnAllergyAYes.setOnClickListener {
            allergyList.add(list[cnt])

            Log.d(TAG, "init: cnt = $cnt")
            if(++cnt >= list.size) save()
            else binding.tvAllergyAType.text = list[cnt]
        }

    }

    private fun save(){
        sharePrefArrayListUtil.setStringArrayPref(this, "allergies", allergyList)
        finish()
    }
}