package com.stericson.permissions.donate.jobs.tasks;

import android.content.Context;
import android.content.pm.PackageManager;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.PermissionsParserDelegate;
import com.stericson.permissions.donate.jobs.LoadPermissionsForPackage;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class LoadPermissionsForPackageTask extends BaseTask implements PermissionsParserDelegate {

    PackageManager pm;
    String packageName;
    Context context;
    ArrayList<Permission> list = new ArrayList<Permission>();


    public LoadPermissionsForPackageTask(LoadPermissionsForPackage job, String packageName)
    {
        this.context = job.getContext();
        this.packageName = packageName;
        pm = context.getPackageManager();
    }

    public Result executeTask()
    {
        Result result = new Result();
        result.setSuccess(true);

        PermissionsXMLFileParserTask.parse(this);

        result.setList(list);
        return result;
    }

    @Override
    public boolean handleStartTag(XmlPullParser xpp) {
        if ((xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) && xpp.getAttributeValue(0).equals(packageName)) {
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

        String description;
        try {
            if (pm.getPermissionInfo(xpp.getAttributeValue(0).toString().replace("stericson.disabled.", ""), 0).loadDescription(pm) == null) {
                description = context.getString(R.string.noDescription);
            } else {
                description = pm.getPermissionInfo(xpp.getAttributeValue(0).toString().replace("stericson.disabled.", ""), 0).loadDescription(pm).toString();
            }

            list.add(new Permission(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), description, pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).packageName, pm.getApplicationInfo(pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).packageName, 0).loadIcon(pm), xpp.getAttributeValue(0).contains("stericson.disabled."), packageName));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
