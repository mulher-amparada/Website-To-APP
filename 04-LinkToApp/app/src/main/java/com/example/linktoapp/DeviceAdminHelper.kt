package com.linktoapp.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

object DeviceAdminHelper {

    fun bloquearTela(context: Context) {

        val dpm = context.getSystemService(
            Context.DEVICE_POLICY_SERVICE
        ) as DevicePolicyManager


        val admin = ComponentName(
            context,
            AppDeviceAdminReceiver::class.java
        )


        if (dpm.isAdminActive(admin)) {

            dpm.lockNow()

        }

    }

}