package com.xposed.device.hook.sp;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.xposed.device.hook.PkgConstant;

/**
 * description:preference的内容提供者，允许其他应用读取本应用的xml
 * author: kyXiao
 * date: 2019/4/8
 */
public class PreferenceProvider extends RemotePreferenceProvider {
    /**
     * Initializes the remote preference provider with the specified
     * authority and preference files. The authority must match the
     * {@code android:authorities} property defined in your manifest
     * file. Only the specified preference files will be accessible
     * through the provider.
     */
    public PreferenceProvider() {
        super(PkgConstant.PKG_NAME, new String[]{PkgConstant.SP_DEVICE_PARAMS});
    }
}
