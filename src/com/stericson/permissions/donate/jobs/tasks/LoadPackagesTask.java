package com.stericson.permissions.donate.jobs.tasks;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.PermissionsParserDelegate;
import com.stericson.permissions.donate.jobs.LoadPackages;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class LoadPackagesTask extends BaseTask implements PermissionsParserDelegate {

    Context context;
    PackageManager pm;
    ArrayList<AndroidPackage> pcks = new ArrayList<AndroidPackage>();
    AndroidPackage pck = new AndroidPackage();
    Permission permission;

    public LoadPackagesTask(LoadPackages job, Permission permission) {
        this.context = job.getContext();
        this.pm = context.getPackageManager();
        this.permission = permission;
    }

    public Result executeTask()
    {
        Result result = new Result();
        result.setSuccess(true);


        try {

            PermissionsXMLFileParserTask.parse(this);

            int index;

            for (AndroidPackage p : pcks) {
                index = pcks.indexOf(p);

                if (!p.isShared())
                {
                    ApplicationInfo info = pm.getApplicationInfo(p.getPackageName(), PackageManager.GET_META_DATA);
                    pcks.get(index).setAppName(info.loadLabel(pm).toString());
                    pcks.get(index).setPackageName(p.getPackageName());
                    pcks.get(index).setIcon(info.loadIcon(pm));
                }
                else
                {
                    String[] info2 = pm.getPackagesForUid(p.getUserID());

                    for (String string : info2)
                    {
                        ApplicationInfo info = pm.getApplicationInfo(string, PackageManager.GET_META_DATA);
                        pcks.get(index).getAppNames().add(info.loadLabel(pm).toString());
                        pcks.get(index).getIcons().add(info.loadIcon(pm));
                    }

                    pcks.get(index).setPackageName(p.getPackageName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setList(pcks);
        return result;
    }

    @Override
    public boolean handleStartTag(XmlPullParser xpp) {
        if (xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) {
            pck = new AndroidPackage();

            pck.setPackageName(xpp.getAttributeValue(0));

            pck.setType(xpp.getName());

            if (xpp.getName().equals("updated-package"))
            {
                pck.setType("package update");
            }
            if (xpp.getName().equals("shared-user"))
            {
                pck.setShared(true);
                pck.setUserID(Integer.parseInt(xpp.getAttributeValue(1)));
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean handleEndTag(XmlPullParser xpp) {
        if (xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) {

            return true;
        }
        return false;
    }

    @Override
    public boolean handlePermission(XmlPullParser xpp) {
        if (xpp.getAttributeValue(0).contains(permission.getPermission()))
        {
            pck.setActive(!xpp.getAttributeValue(0).contains("stericson.disabled."));
            pcks.add(pck);

            return true;
        }

        return false;
    }

}
