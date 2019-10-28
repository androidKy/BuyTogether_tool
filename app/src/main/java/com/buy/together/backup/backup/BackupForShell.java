package com.buy.together.backup.backup;

import android.text.TextUtils;

import com.buy.together.backup.BackupConstant;
import com.buy.together.backup.IResponListener;
import com.safframework.log.L;
import com.utils.common.CMDUtil;
import com.utils.common.ThreadUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * description: 备份管理
 * author: kyXiao
 * created date: 2018/9/12
 */

public class BackupForShell implements IBackup {
    private static final String TAG = "BackupForShell";

    private IResponListener mOnResponListener;
    private int mBackupCounted;
    private int mRemovedCount;

    @Override
    public void startBackup(String packageName, String destDir, IResponListener responListener) {
        if (TextUtils.isEmpty(packageName)) {
            if (responListener != null)
                responListener.onResponFailed("包名不能为空");
            return;
        }
        List<String> packageNameList = new LinkedList<>();
        packageNameList.add(packageName);
        startBackup(packageNameList, destDir, responListener);
    }

    @Override
    public void startBackup(List<String> packNameList, String destDir, IResponListener responListener) {
        this.mOnResponListener = responListener;
        if (packNameList == null || packNameList.size() == 0) {
            backupFailed("包名列表不能为空");
            return;
        }
        backupFiles(packNameList, destDir);
    }

    @Override
    public void removeBackup(String packageName, String destDir, IResponListener responListener) {
        if (TextUtils.isEmpty(packageName)) {
            if (responListener != null)
                responListener.onResponFailed("包名不能为空");
            return;
        }

        List<String> packageNameList = new LinkedList<>();
        packageNameList.add(packageName);
        removeBackup(packageNameList, destDir, responListener);
    }

    @Override
    public void removeBackup(List<String> packNameList, String destDir, IResponListener responListener) {
        this.mOnResponListener = responListener;
        if (packNameList == null || packNameList.size() == 0) {
            getResponListener().onResponFailed("包名不能为空。");
            return;
        }
        removeBackup(packNameList, destDir);
    }

    private void removeBackup(final List<String> packageNameList, final String destDir) {
        ThreadUtils.executeByCached(new ThreadUtils.Task<String>() {
            @Override
            public String doInBackground() throws Throwable {
                try {
                    String backupFileDir = BackupConstant.getBackupFolder(destDir);
                    L.i(TAG, "开始删除备份 备份存储的路径：" + backupFileDir);

                    File baseFile = new File(backupFileDir);
                    if (baseFile.exists()) {
                        mRemovedCount = 0;
                        StringBuilder deleteShell = new StringBuilder();
                        for (String packageName : packageNameList) {
                            String packageDir = baseFile.getAbsolutePath() + "/" + packageName;

                            deleteShell.append("rm -r ").append(packageDir).append(";");
                        }
                        String shell = deleteShell.toString();
                        L.i("deleteShell: " + shell);
                        String result = new CMDUtil().execCmd(shell);

                        return result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, e.getMessage());
                    backupFailed(e.getMessage());
                }
                return null;
            }

            @Override
            public void onSuccess(String result) {
                L.i("删除备份结果：" + result);
                if (!TextUtils.isEmpty(result))
                    backupSucceed();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    private void backupFiles(final List<String> packageNameList, final String destDir) {

        ThreadUtils.executeByCached(new ThreadUtils.Task<String>() {

            @Override
            public String doInBackground() throws Throwable {
                try {
                    // removeBackup(packageName, destDir);
                    int packageNameCount = packageNameList.size();
                    L.i(TAG, "开始备份>>>>>>>");
                    String backupFileDir = BackupConstant.getBackupFolder(destDir);

                    L.i(TAG, "开始备份 存储的路径：" + backupFileDir);
                    File backupFile = new File(backupFileDir);
                    if (!backupFile.exists()) {
                        boolean mkdirResult = backupFile.mkdirs();
                    }
                    StringBuilder sbShell = new StringBuilder();
                    for (int i = 0; i < packageNameCount; i++) {
                        String packageName = packageNameList.get(i);

                        sbShell.append("cp -ar /data/data/").append(packageName).append(" ").append(backupFileDir);
                    }

                    String shell = sbShell.toString();
                    L.i("copyDataShell: " + shell);
                    String result = new CMDUtil().execCmd(shell);

                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, "备份失败 : " + e.getMessage());
                    backupFailed(e.getMessage());
                }
                L.i(TAG, "备份完成>>>>>>>");
                return null;
            }

            @Override
            public void onSuccess(String result) {
                L.i("备份结果：" + result);
              //  if (!TextUtils.isEmpty(result))
                    backupSucceed();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    private void backupFailed(final String msg) {
        mBackupCounted = 0;
        mRemovedCount = 0;
        getResponListener().onResponFailed(msg);
    }

    private void backupSucceed() {
        mBackupCounted = 0;
        mRemovedCount = 0;
        getResponListener().onResponSuccess("");
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
