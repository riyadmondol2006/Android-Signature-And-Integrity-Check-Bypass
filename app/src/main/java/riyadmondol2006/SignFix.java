package riyadmondol2006;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.Map;

public class SignFix {
	    public static String HookApk() {
        return "HookApk";
    }

    public static final File fileStreamPath = new File((HookApk()));
	
	   public static String SignData() {
        return "signature data";
    }


   public static void SignFix(Context context) {
        try {
		   DataInputStream is = new DataInputStream(new ByteArrayInputStream(Base64.decode(SignData(), 0)));
            final byte[][] originalSigns = new byte[(is.read() & 255)][];
            for (int i = 0; i < originalSigns.length; i++) {
                originalSigns[i] = new byte[is.readInt()];
                is.readFully(originalSigns[i]);
            }
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null);
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            final Object sPackageManager = sPackageManagerField.get(currentActivityThread);
            Class<?> iPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
            final String packageName = context.getPackageName();
            Object proxy = Proxy.newProxyInstance(iPackageManagerClass.getClassLoader(), new Class[]{iPackageManagerClass}, new InvocationHandler() {

                @Override 
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    PackageInfo info;
                    if (method.getName().equals("getPackageInfo") && (args[0] instanceof String) && args[0].toString() == packageName && (info = (PackageInfo) method.invoke(sPackageManager, args)) != null) {
                        if (info.signatures != null) {
                            info.signatures = new Signature[originalSigns.length];
                            for (int i = 0; i < info.signatures.length; i++) {
                                info.signatures[i] = new Signature(originalSigns[i]);
                            }
                        }
                        if (info.applicationInfo != null) {
                            info.applicationInfo.sourceDir = fileStreamPath.getPath();
                            info.applicationInfo.publicSourceDir = fileStreamPath.getPath();
                        }
                        return info;
                    } else if (!method.getName().equals("getApplicationInfo") || !(args[0] instanceof String) || args[0].toString() != packageName) {
                        if (method.getName().equals("getPackageArchiveInfo") && (args[0] instanceof String) && args[0].toString().contains(".apk") && args[0].toString() == packageName) {
                            args[0] = fileStreamPath.getPath();
                        }
                        return method.invoke(sPackageManager, args);
                    } else {
                        ApplicationInfo info2 = (ApplicationInfo) method.invoke(sPackageManager, args);
                        if (info2 != null) {
                            info2.sourceDir = fileStreamPath.getPath();
                            info2.publicSourceDir = fileStreamPath.getPath();
                        }
                        return info2;
                    }
                }
            });
            sPackageManagerField.set(currentActivityThread, proxy);
            PackageManager pm = context.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void HookApkLoader(Context context) {
        int i = 0;
        try {
            final File fileStreamPath = context.getFileStreamPath(HookApk());
            if (!fileStreamPath.exists()) {
                final InputStream open = context.getAssets().open(HookApk());
                final FileOutputStream fileOutputStream = new FileOutputStream(fileStreamPath);
                for (byte[] array = new byte[1024]; i != -1; i = open.read(array)) {
                    fileOutputStream.write(array, 0, i);
                    fileOutputStream.flush();
                }
                open.close();
                fileOutputStream.close();
            }
            if (fileStreamPath != null && fileStreamPath.exists()) {
                String path = fileStreamPath.getPath();
                context.getClassLoader();
                Field declaredField = ClassLoader.getSystemClassLoader().loadClass("android.app.ActivityThread").getDeclaredField("sCurrentActivityThread");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(null);
                Field declaredField2 = obj.getClass().getDeclaredField("mPackages");
                declaredField2.setAccessible(true);
                Object obj2 = ((WeakReference) ((Map) declaredField2.get(obj)).get(context.getPackageName())).get();
                Field declaredField3 = obj2.getClass().getDeclaredField("mAppDir");
                declaredField3.setAccessible(true);
                declaredField3.set(obj2, path);
                Field declaredField4 = obj2.getClass().getDeclaredField("mApplicationInfo");
                declaredField4.setAccessible(true);
                ApplicationInfo applicationInfo = (ApplicationInfo) declaredField4.get(obj2);
                applicationInfo.publicSourceDir = path;
                applicationInfo.sourceDir = path;
            }
    } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void attachBaseContext(final Context context) {
        SignFix(context);
        HookApkLoader(context);
        
    }
}
