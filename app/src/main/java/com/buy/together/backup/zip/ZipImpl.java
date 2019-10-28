package com.buy.together.backup.zip;

import android.content.Context;
import android.provider.Settings;

import com.buy.together.backup.BackupConstant;
import com.buy.together.backup.utils.ZipUtils;
import com.safframework.log.L;
import com.utils.common.ThreadUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * description:压缩实现类
 * author: kyXiao
 * created date: 2018/9/17
 */

public class ZipImpl implements IZip {
    private static final String TAG = "ZipImpl";

    private static volatile ZipImpl mInstance;
    private WeakReference<Context> mContextWeakReference;
    private IZipListener mZipListener;
    private boolean mIsZipping;

    private String uid;
    private String aid;

    private ZipImpl(Context context) {
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static ZipImpl getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ZipImpl.class) {
                if (mInstance == null) {
                    mInstance = new ZipImpl(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void startZip(final String filePath) {
        if (mIsZipping) {
           /* if (mZipListener != null)
                mZipListener.onZipFailed("正在压缩，请等待完成再执行压缩");*/
            onFailed("正在压缩，请等待完成再执行压缩");
            return;
        }


        ThreadUtils.executeByCached(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                final File sdcardFileList = new File(filePath);
                if (sdcardFileList.exists() && sdcardFileList.isDirectory()) {
                    backupData(sdcardFileList);
                }
                return null;
            }

            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    @Override
    public void setFileNameParams(String uid, String aid) {
        this.uid = uid;
        this.aid = aid;
    }

    /**
     * 备份指定的数据
     *
     * @param fileDir
     */
    private void backupData(File fileDir) {
        File zipFilePath = new File(BackupConstant.ZIP_FILE_PATH);
        //判断之前是否有压缩过，如果有则删除重新创建，无则直接创建
        if (zipFilePath.exists()) {
            //zipFile.mkdir();
            ZipUtils.deleteFile(zipFilePath);
            zipFilePath.mkdirs();
        } else zipFilePath.mkdirs();

        mIsZipping = true;
        File zipFile = zipFile(fileDir);
        mIsZipping = false;
        if (zipFile == null) {
            onFailed("压缩文件过程出现异常");
            return;
        }
        L.i(TAG, "压缩文件成功》》》》》");
        onSucceed(zipFile.getName());
    }

    private File zipFile(File fileDir) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String tempFileName = String.format("%s%s_%s_%s.%s", BackupConstant.ZIP_FILE_PATH, uid, aid,
                dateFormat.format(new Date()), "zip.tmp");

        // tempFileName = BackupConstant.ZIP_FILE_PATH + "uid_aid_20181234567890" + ".zip.tmp";
        L.i(TAG, "zipFile 新文件名字：" + tempFileName);
        try {
            ZipUtils.zip(fileDir, tempFileName);

            File zipedFile;
            new File(tempFileName).renameTo(zipedFile = new File(tempFileName.replace(".tmp", "")));

            return zipedFile;
        } catch (Exception e) {
            e.printStackTrace();
            L.e(TAG, "压缩出错：" + e.getMessage());
        }
        return null;
    }


    private String getAndroidId() {
        String androidId = "";
        if (mContextWeakReference != null) {
            Context context = mContextWeakReference.get();
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }

    @Override
    public boolean isZipping() {
        return mIsZipping;
    }

    @Override
    public void addZipListener(IZipListener zipListener) {
        mZipListener = zipListener;
    }

    private void onFailed(String message) {
        if (mZipListener != null)
            mZipListener.onZipFailed(message);
    }

    private void onSucceed(String zipFileName) {
        if (mZipListener != null)
            mZipListener.onZipSuccess(zipFileName);
    }
}
