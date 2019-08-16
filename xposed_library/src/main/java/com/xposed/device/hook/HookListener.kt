package com.xposed.device.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage

interface HookListener {
    fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam)
}