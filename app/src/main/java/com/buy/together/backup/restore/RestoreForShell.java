package com.buy.together.backup.restore;

import android.text.TextUtils;

import com.buy.together.backup.BackupConstant;
import com.buy.together.backup.IResponListener;
import com.buy.together.backup.utils.CommandUtil;
import com.safframework.log.L;
import com.utils.common.ThreadUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * description:
 * author: kyXiao
 * created date: 2018/9/12
 */

public class RestoreForShell implements IRestore {
    private static final String TAG = "RestoreForShell";

    private IResponListener mOnResponListener;
    private String mAppUserId = "";
    private int mRestoredCount;
    private int mPackageNameSize;
    private String mRestoreDir;
    //private String mCpDir = "";
    //private String mAppPackageName = "";

    @Override
    public void restore(String packageName, String destDir, IResponListener responListener) {
        if (TextUtils.isEmpty(packageName)) {
            if (responListener != null)
                responListener.onResponFailed("包名不能为空");
            return;
        }

        List<String> packageNameList = new LinkedList<>();
        packageNameList.add(packageName);
        restore(packageNameList, destDir, responListener);
    }

    @Override
    public void restore(List<String> packageNameList, String restoreDir, IResponListener responListener) {
        mOnResponListener = responListener;
        mRestoreDir = restoreDir;

        if (packageNameList == null || packageNameList.size() == 0) {
            getResponListener().onResponFailed("包名不能为空。");
            return;
        }

        restore(packageNameList);
    }

    private void restore(final List<String> packageNameList) {

        ThreadUtils.executeByCached(new ThreadUtils.Task<String>() {
            @Override
            public String doInBackground() throws Throwable {
                try {
                    // Runtime.getRuntime().gc();
                    File baseFile = new File(BackupConstant.getBackupFolder(mRestoreDir));

                    mPackageNameSize = packageNameList.size();
                    mRestoredCount = 0;

                    for (String packageName : packageNameList) {
                        if (baseFile.exists()) {
                            //File backupFile = new File(baseFile.getAbsolutePath() + "/" + packageName);
                            // mCpDir = backupFile.getAbsolutePath();
                            findUserId(packageName);
                        } else {
                            baseFile.mkdirs();
                            findUserId(packageName);
                        }
                    }
                    L.i(TAG, "开始恢复备份》》》》》》》");
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, e.getMessage());
                    restoreFailed(e.getMessage());
                }

                L.i(TAG, "恢复备份完成》》》》》》");
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    /**
     * 先找app的用户id，恢复备份后还要恢复权限和文件所属的用户id
     */
    private void findUserId(final String packageName) {
        ThreadUtils.executeByCached(new ThreadUtils.Task<String>() {
            @Override
            public String doInBackground() throws Throwable {
                String appIdShell = "ls -l /data/data/";

                CommandUtil.sendCommand(appIdShell, new CommandUtil.OnResponListener() {
                    @Override
                    public void onSuccess(List<String> responList) {
                        String appRespon = "";
                        for (int i = 0; i < responList.size(); i++) {
                            L.i(TAG, "data/data/ 目录下返回的文件和权限信息 : " + responList.get(i));
                            String respon = responList.get(i);
                            String[] itemList = respon.split(" ");

                            for (String s : itemList) {
                                if (packageName.equals(s)) {
                                    appRespon = respon;
                                }
                            }
                        }
                        L.i(TAG, "app的文件和权限信息：" + appRespon);
                        String[] appInfoList = appRespon.split(" ");
                        if (appInfoList.length > 1)
                            mAppUserId = appInfoList[1];
                        L.i(TAG, "appUserId = " + mAppUserId);
                        if (!TextUtils.isEmpty(mAppUserId)) {
                            deleteOriginData(packageName);
                        }
                        else restoreFailed("找不到appUserId");
                    }

                    @Override
                    public void onFailed(String msg) {
                        L.e(TAG, "查找包名所属的用户id失败: " + msg);
                        restoreFailed("查找包名所属的用户id失败: " + msg);
                    }
                });

                return null;
            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });

    }

    /**
     * 删除原有的数据
     */
    private void deleteOriginData(final String packageName) {
        String deleteShell = "rm -fr /data/data/" + packageName+"/*";
        CommandUtil.sendCommand(deleteShell, new CommandUtil.OnResponListener() {
            @Override
            public void onSuccess(List<String> responList) {
                restoreData(packageName);
            }

            @Override
            public void onFailed(String msg) {
                L.e(TAG, "删除原有的数据出错：" + msg);
                restoreFailed(msg);
            }
        });
    }

    /**
     * 恢复备份
     *
     * @param packageName
     */
    private void restoreData(final String packageName) {
        String shell = "cp -ar " + BackupConstant.getBackupFolder(mRestoreDir) + packageName + " " + "/data/data/";
        CommandUtil.sendCommand(shell, new CommandUtil.OnResponListener() {
            @Override
            public void onSuccess(List<String> responList) {
                restorePermission(packageName);
            }

            @Override
            public void onFailed(String msg) {
                L.e(TAG, "恢复备份失败：" + msg);
                restoreFailed(msg);
            }
        });
    }

    /**
     * 恢复权限
     *
     * @param packageName
     */
    private void restorePermission(String packageName) {
        String writeReadPermission = "chmod -R 771 /data/data/" + packageName;

        CommandUtil.sendCommand(writeReadPermission);

        String backupPermissionShell = "chown -R " + mAppUserId + ":" + mAppUserId + " /data/data/" + packageName;
        CommandUtil.sendCommand(backupPermissionShell, new CommandUtil.OnResponListener() {
            @Override
            public void onSuccess(List<String> responList) {
                mRestoredCount++;
                if (mRestoredCount == mPackageNameSize)
                    restoreSucceed();
            }

            @Override
            public void onFailed(String msg) {
                L.e(TAG, "恢复权限失败：" + msg);
                restoreFailed(msg);
            }
        });
    }

    private void restoreSucceed() {
        getResponListener().onResponSuccess("");

    }

    private void restoreFailed(final String msg) {
        mRestoredCount = 0;
        getResponListener().onResponFailed(msg);
    }


    private IResponListener getResponListener() {
        if (mOnResponListener == null)
            mOnResponListener = new EmptyResponListener();
        return mOnResponListener;
    }

    private class EmptyResponListener implements IResponListener {

        @Override
        public void onResponSuccess(String msg) {

        }

        @Override
        public void onResponFailed(String msg) {

        }
    }
}
