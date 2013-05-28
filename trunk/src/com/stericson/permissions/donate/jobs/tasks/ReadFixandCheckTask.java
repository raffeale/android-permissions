package com.stericson.permissions.donate.jobs.tasks;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.stericson.RootTools.RootTools;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.PermissionsParserDelegate;
import com.stericson.permissions.donate.jobs.FixPerms;
import com.stericson.permissions.donate.jobs.ReadFixandCheck;
import com.stericson.permissions.donate.service.DBService;
import com.stericson.permissions.donate.service.PreferenceService;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class ReadFixandCheckTask extends BaseTask implements PermissionsParserDelegate {

    ReadFixandCheck job;
    String packageName = "";
    AndroidPackage tmp_package = new AndroidPackage();
    PreferenceService ps;
    DBService db;
    List<AndroidPackage> list;
    List<Permission> changedPermissions;
    PackageManager pm;
    String message = "";

    public ReadFixandCheckTask(ReadFixandCheck job)
    {
        this.job = job;
        this.context = job.getContext();
        this.ps = new PreferenceService(context);
        this.pm = context.getPackageManager();
    }

    public Result executeTask()
    {
        Result result = new Result();
        result.setSuccess(true);

        db = new DBService(context);
        list = new ArrayList<AndroidPackage>();
        changedPermissions = db.getChangedPermissions();

        if (!ps.isLoaded()) {

            try {

                job.publishJobProgress(context.getString(R.string.reading));

                PermissionsXMLFileParserTask.parse(this);

                int index;

                job.publishJobProgress(context.getString(R.string.buildingList));

                for (AndroidPackage p : list) {
                    index = list.indexOf(p);

                    try {
                        job.publishJobProgress(context.getString(R.string.fixingPermissions));
                        FixPerms.fix(context, p.getPackageName());

                        if (!p.isShared())
                        {
                            ApplicationInfo info = pm.getApplicationInfo(p.getPackageName(), PackageManager.GET_META_DATA);
                            list.get(index).setAppName(info.loadLabel(pm).toString());
                            list.get(index).setPackageName(p.getPackageName());
                            list.get(index).setIcon(info.loadIcon(pm));
                            list.get(index).setPermissionCount(p.getPermissionCount());
                            list.get(index).setActiveCount(p.getActiveCount());
                            list.get(index).setDeniedCount(p.getDeniedCount());

                        }
                        else
                        {
                            String[] info2 = pm.getPackagesForUid(p.getUserID());

                            for (String string : info2)
                            {
                                ApplicationInfo info = pm.getApplicationInfo(string, PackageManager.GET_META_DATA);
                                FixPerms.fix(context, info.packageName);
                                list.get(index).getAppNames().add(info.loadLabel(pm).toString());
                                list.get(index).getIcons().add(info.loadIcon(pm));

                                if (list.get(index).getIcon() == null) {
                                    list.get(index).setIcon(info.loadIcon(pm));
                                }

                            }

                            if (list.get(index).getIcon() == null) {
                                list.get(index).setIcon(context.getResources().getDrawable(R.drawable.caution));
                            }

                            list.get(index).setPackageName(p.getPackageName());
                            list.get(index).setPermissionCount(p.getPermissionCount());
                            list.get(index).setActiveCount(p.getActiveCount());
                            list.get(index).setDeniedCount(p.getDeniedCount());
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        //If we can't get the name of the package
                        //TODO handle exception
                    }

                    //insert the row into the database
                    db.insertOrUpdatePackage(list.get(index));
                    db.insertOrUpdateApps(list.get(index));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(changedPermissions.size() > 0 && !ps.getNeedsReboot()) {

            PermissionsXMLFileParserTask.parse(this);

            //update the packages
            for (Permission p : changedPermissions) {
                for (AndroidPackage pa : list) {
                    if (pa.getPackageName().equals(p.getPackageName())) {
                        db.insertOrUpdatePackage(pa);
                        db.insertOrUpdateApps(pa);
                    }
                }
            }
        }

        db.clearChangedPermissions();

        result.setList(db.getPackages());
        result.setMessage(message);
        return result;
    }

    @Override
    public boolean handleStartTag(XmlPullParser xpp) {
        if (xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) {

            tmp_package = new AndroidPackage();
            packageName = xpp.getAttributeValue(0);

            tmp_package.setType(xpp.getName());

            if (xpp.getName().equals("updated-package")) {
                tmp_package.setType("package update");
            }
            else if (xpp.getName().equals("shared-user")) {
                tmp_package.setShared(true);
                tmp_package.setUserID(Integer.parseInt(xpp.getAttributeValue(1)));
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean handleEndTag(XmlPullParser xpp) {
        if (xpp.getName().equals("package") || xpp.getName().equals("updated-package") || xpp.getName().equals("shared-user")) {

            if (!tmp_package.getPackageName().isEmpty() && tmp_package.getPermissionCount() != 0) {
                RootTools.log(tmp_package.getPackageName() + " " + tmp_package.getPermissionCount());
                list.add(tmp_package);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean handlePermission(XmlPullParser xpp) {
        if (tmp_package.getPackageName().isEmpty()) {
            tmp_package.setPackageName(packageName);
        }

        String permission = xpp.getAttributeValue(0);

        if (ps.getNeedsReboot() && changedPermissions.size() > 0) {
            job.publishJobProgress(context.getString(R.string.checkingState));

            for (Permission currentPermission : changedPermissions) {
                if (currentPermission.getPermission().contains(permission) && (currentPermission.getPackageName().isEmpty() || currentPermission.getPackageName().equals(packageName))) {
                    //We are checking to see that a permission that was changed is still correctly set.
                    if (currentPermission.isActive()) {
                        if (permission.contains("stericson.disabled.")) {
                            //The permission is wrong.
                            message = context.getString(R.string.permissionchangefailed);
                        }
                    } else {
                        if (!permission.contains("stericson.disabled.")) {
                            //The permission is wrong.
                            message = context.getString(R.string.permissionchangefailed);
                        }
                    }
                }
            }
        }

        job.publishJobProgress(context.getString(R.string.readingPermissions));


        //count the permissions
        tmp_package.setPermissionCount(tmp_package.getPermissionCount() + 1);
        if (permission.contains("stericson.disabled.")) {
            tmp_package.setDeniedCount(tmp_package.getDeniedCount() + 1);
        } else {
            tmp_package.setActiveCount(tmp_package.getActiveCount() +1);
        }

        return false;
    }
}
