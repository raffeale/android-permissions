package com.stericson.permissions.donate.jobs.tasks;

import android.content.pm.PackageManager;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.PermissionsParserDelegate;
import com.stericson.permissions.donate.jobs.LoadAllPermissions;
import com.stericson.permissions.donate.service.DBService;
import com.stericson.permissions.donate.service.PreferenceService;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class LoadAllPermissionsTask extends BaseTask implements PermissionsParserDelegate {

    String packageName = "";
    PackageManager pm;
    ArrayList<String> tmp = new ArrayList <String>();
    ArrayList<Permission> list = new ArrayList<Permission>();
    DBService db;

    public LoadAllPermissionsTask(LoadAllPermissions job) {

        db = new DBService(context);
        context = job.getContext();
        db = new DBService(context);
        pm = context.getPackageManager();
    }

    public Result executeTask()
    {
        Result result = new Result();
        result.setSuccess(true);

        PreferenceService ps = new PreferenceService(context);

        if (!ps.isLoaded()) {

            PermissionsXMLFileParserTask.parse(this);

        }

        ps.setLoaded(true);
        result.setList(db.getPermissions());
        return result;
    }

    @Override
    public boolean handleStartTag(XmlPullParser xpp) {
        if (xpp.getName().equals("perms"))
        {
            return true;
        }
        else if (xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) {
            packageName = xpp.getAttributeValue(0);
        }
        return false;
    }

    @Override
    public boolean handleEndTag(XmlPullParser xpp) {
        if (xpp.getName().equals("perms")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handlePermission(XmlPullParser xpp) {
        try {
            String description;
            if (pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).loadDescription(pm) == null) {
                description = context.getString(R.string.noDescription);
            } else {
                description = pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).loadDescription(pm).toString();
            }

            if (!tmp.contains(xpp.getAttributeValue(0).replace("stericson.disabled.", "")))
            {
                tmp.add(xpp.getAttributeValue(0).replace("stericson.disabled.", ""));
                Permission perm = new Permission(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), description, pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).packageName, pm.getApplicationInfo(pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disabled.", ""), 0).packageName, 0).loadIcon(context.getPackageManager()), false, packageName);
                list.add(perm);
                db.insertOrUpdatePermission(perm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
