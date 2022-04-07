package com.ssafy.nooni.util

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ssafy.nooni.ui.PermissionDialog

class PermissionUtil (val activity: AppCompatActivity){
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private val notGrantedPermissions = mutableListOf<String>()
    lateinit var permissionListener: PermissionListener

    init {
        registerPermissionLauncher()
    }

    // 여러 개의 권한 중 하나라도 허용되어 있지 않으면 return false
    fun checkPermissions(permissionList: List<String>): Boolean {
        notGrantedPermissions.clear()
        permissionList.forEach {
            if(ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED) {
                notGrantedPermissions.add(it)
            }
        }
        return notGrantedPermissions.size <= 0
    }

    // notGrantedPermissions를 기반으로 권한 요청 실행
    fun requestPermissions() {
        requestPermissionsLauncher.launch(notGrantedPermissions.toTypedArray())
    }

    // 런처 세팅 -> 요청 결과가 false이면 notGrantedPermissions에 등록
    private fun registerPermissionLauncher() {
        requestPermissionsLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val tempNotGrantedPermissions = mutableListOf<String>()
            notGrantedPermissions.forEach { permission ->
                it[permission]?.let { isGranted ->
                    if(!isGranted) {
                        tempNotGrantedPermissions.add(permission)
                    }
                }
            }
            addToNotGrantedPermission(tempNotGrantedPermissions)
            // 요청 결과에 notGrantedPermission이 있다면 다이얼로그 띄우기
            if(notGrantedPermissions.size > 0) {
                showReasonForPermission()
            } else {
                if(this::permissionListener.isInitialized) {
                    permissionListener.run()
                }
            }
        }
    }

    private fun addToNotGrantedPermission(list: List<String>) {
        notGrantedPermissions.clear()
        if(list.isNotEmpty()) {
            notGrantedPermissions.addAll(list)
        }
    }

    // 권한이 필요한 이유를 설명하는 다이얼로그 제공
    // positive button에 권한 설정으로 이동하는 클릭리스너 세팅
    private fun showReasonForPermission() {
        PermissionDialog(activity)
            .setContent("앱이 정상적으로 수행하기 위해\n필요한 권한입니다.")
            .setPositiveButtonText("권한 설정하기")
            .setOnPositiveClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${activity.packageName}")).apply {
                        this.addCategory(Intent.CATEGORY_DEFAULT)
                        this.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    activity.startActivity(intent)
                }
            }).setOnNegativeClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    activity.finish()
                }
            })
            .build().show()
    }

    interface PermissionListener {
        fun run()
    }
}