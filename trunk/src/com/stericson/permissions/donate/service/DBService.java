package com.stericson.permissions.donate.service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import com.stericson.RootTools.RootTools;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;

import java.util.ArrayList;
import java.util.List;

public class DBService
{
	private static final String DATABASE_NAME = "permissions_pro_db";
	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

    //Permissions table columns
    public static final String TBL_PERMISSION = "permissions";
    private static final String KEY_PERMISSION_ROWID = "permissions_id";
    private static final String KEY_PERMISSION_PACKAGE_NAME = "permissions_package_name";
    private static final String KEY_PERMISSION_NAME = "name";
    private static final String KEY_PERMISSION_ICON = "icon";
    private static final String KEY_PERMISSION_DESCRIPTION = "description";
    private static final String KEY_PERMISSION_OWNER = "owner";
    private static final String KEY_PERMISSION_ACTIVE = "active";

    //package table columns
    public static final String TBL_PACKAGE = "package";
    private static final String KEY_PACKAGE_ROWID = "package_id";
    private static final String KEY_PACKAGE_NAME = "name";
    private static final String KEY_PACKAGE_APP_NAME = "app_name";
    private static final String KEY_PACKAGE_TYPE = "type";
    private static final String KEY_PACKAGE_ICON = "icon";
    private static final String KEY_PACKAGE_PERMISSIONS_COUNT = "permission_count";
    private static final String KEY_PACKAGE_ACTIVE_COUNT = "active_count";
    private static final String KEY_PACKAGE_DENIED_COUNT = "denied_count";
    private static final String KEY_PACKAGE_SHARED = "shared";
    private static final String KEY_PACKAGE_USERID = "userid";
    private static final String KEY_PACKAGE_ACTIVE = "active";

    //App table columns
    public static final String TBL_APP = "app";
    private static final String KEY_APP_ROWID = "app_id";
    private static final String KEY_APP_PACKAGE_NAME = "package_name";
    private static final String KEY_APP_NAME = "name";
    private static final String KEY_APP_ICON = "icon";

    //Changed Permissions
    public static final String TBL_PERMISSION_CHANGED = "permissions_changed";
    private static final String KEY_PERMISSION_CHANGED_ROWID = "permissions_changed_id";
    private static final String KEY_PERMISSION_CHANGED_PERMISSION_NAME = "permissions_changed_permission_name";
    private static final String KEY_PERMISSION_CHANGED_PACKAGE_NAME = "permissions_changed_package_name";
    private static final String KEY_PERMISSION_CHANGED_ACTIVE = "permissions_changed_state";


    /**
	 * Database creation sql statement
	 */
    private static final String PERMISSIONS_DATABASE_CREATE = "create table "
            + TBL_PERMISSION + " (" + KEY_PERMISSION_ROWID
            + " integer primary key autoincrement, " +
            KEY_PERMISSION_PACKAGE_NAME + " integer not null, " +
            KEY_PERMISSION_NAME + " text not null, " +
            KEY_PERMISSION_ICON + " blob, " +
            KEY_PERMISSION_DESCRIPTION + " text, " +
            KEY_PERMISSION_OWNER + " text, " +
            KEY_PERMISSION_ACTIVE + " integer);";

    private static final String PACKAGE_DATABASE_CREATE = "create table "
            + TBL_PACKAGE + " (" + KEY_PACKAGE_ROWID
            + " integer primary key autoincrement, " +
            KEY_PACKAGE_NAME + " text not null unique, " +
            KEY_PACKAGE_APP_NAME + " text, " +
            KEY_PACKAGE_TYPE + " text, " +
            KEY_PACKAGE_ICON + " blob, " +
            KEY_PACKAGE_PERMISSIONS_COUNT + " integer, " +
            KEY_PACKAGE_ACTIVE_COUNT + " integer, " +
            KEY_PACKAGE_DENIED_COUNT + " integer, " +
            KEY_PACKAGE_USERID + " integer, " +
            KEY_PACKAGE_SHARED + " integer, " +
            KEY_PACKAGE_ACTIVE + " integer);";

    private static final String APP_DATABASE_CREATE = "create table "
            + TBL_APP + " (" + KEY_APP_ROWID
            + " integer primary key autoincrement, " +
            KEY_APP_NAME + " text not null unique, " +
            KEY_APP_PACKAGE_NAME + " text, " +
            KEY_APP_ICON + " blob);";

    private static final String PERMISSION_CHANGED_DATABASE_CREATE = "create table "
            + TBL_PERMISSION_CHANGED + " (" + KEY_PERMISSION_CHANGED_ROWID
            + " integer primary key autoincrement, " +
            KEY_PERMISSION_CHANGED_PERMISSION_NAME + " text not null, " +
            KEY_PERMISSION_CHANGED_PACKAGE_NAME + " text not null, " +
            KEY_PERMISSION_CHANGED_ACTIVE + " text not null);";

    private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
            db.execSQL(PERMISSIONS_DATABASE_CREATE);
            db.execSQL(PACKAGE_DATABASE_CREATE);
            db.execSQL(APP_DATABASE_CREATE);
            db.execSQL(PERMISSION_CHANGED_DATABASE_CREATE);
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			onCreate(db);
		}
	}

	public DBService(Context context)
	{
		this.context = context;
	}

	private DBService open() throws SQLException
	{
		if (new PreferenceService(context).getDeleteDatabase()) {
			deleteDatabase();
        }

		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		try
		{
			db.close();
			dbHelper.close();
		}
		catch (Exception ignore) {}
	}

    public void clearChangedPermissions()
    {
        open();

        db.rawQuery("delete from " + TBL_PERMISSION_CHANGED, null);

        close();
    }

	private void deleteDatabase()
	{
		context.deleteDatabase(DATABASE_NAME);
		new PreferenceService(context).setDeleteDatabase(false);
        //Close the database after this.
        close();
	}

    public List<AndroidPackage> getPackages() {

        open();
        List<AndroidPackage> list = new ArrayList<AndroidPackage>();

        Cursor cur = db.rawQuery("select * from " + TBL_PACKAGE, null);

        if (cur != null && cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                AndroidPackage p = new AndroidPackage();
                p.setPackageName(cur.getString(1));
                p.setAppName(cur.getString(2));
                p.setType(cur.getString(3));
                p.setIcon(Shared.getDrawable(cur.getBlob(4), context));
                p.setPermissionCount(cur.getInt(5));
                p.setActiveCount(cur.getInt(6));
                p.setDeniedCount(cur.getInt(7));
                p.setUserID(cur.getInt(8));
                p.setShared(cur.getInt(9) != 0);
                p.setActive(cur.getInt(10) != 0);

                Cursor cur2 = db.rawQuery("select * from " + TBL_APP + " where " + KEY_APP_PACKAGE_NAME + " = '" + p.getPackageName() + "'", null);

                if (cur2 != null && cur2.getCount() > 0)
                {
                    while (cur2.moveToNext())
                    {
                        p.getAppNames().add(cur2.getString(1));
                        p.getIcons().add(Shared.getDrawable(cur2.getBlob(3), context));
                    }
                }

                list.add(p);
            }
        }

        close();

        return list;
    }

    public List<Permission> getPermissions() {

        open();
        List<Permission> list = new ArrayList<Permission>();

        Cursor cur = db.rawQuery("select * from " + TBL_PERMISSION, null);

        if (cur != null && cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                Permission p = new Permission();
                p.setPackageName(cur.getString(1));
                p.setPermission(cur.getString(2));
                p.setIcon(Shared.getDrawable(cur.getBlob(3), context));
                p.setPermissionDescription(cur.getString(4));
                p.setOwner(cur.getString(5));
                p.setActive(cur.getInt(6) != 0);

                list.add(p);

            }
        }

        close();

        return list;
    }

    public List<Permission> getChangedPermissions() {

        open();
        List<Permission> list = new ArrayList<Permission>();

        Cursor cur = db.rawQuery("select * from " + TBL_PERMISSION_CHANGED, null);

        if (cur != null && cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                Permission p = new Permission();
                p.setPermission(cur.getString(1));
                p.setPackageName(cur.getString(2));
                p.setActive(cur.getInt(3) != 0);

                list.add(p);

            }
        }

        close();

        return list;
    }

	public boolean isEmpty(String table)
	{
		try
		{
			open();
			Cursor cur = db.rawQuery("select count(*) from " + table, null);
			
			if (cur != null)
			{
				cur.moveToFirst();
				if (cur.getCount() > 0)
				{
					return true;
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			close();
		}
		
		return false;
	}

    public boolean insertOrUpdatePackage(AndroidPackage p)
    {
        try
        {
            open();

            ContentValues initialValues = new ContentValues();

            if (!p.getPackageName().isEmpty()) {
                initialValues.put(KEY_PACKAGE_NAME, p.getPackageName());
            }
            if (!p.getAppName().isEmpty()) {
                initialValues.put(KEY_PACKAGE_APP_NAME, p.getAppName());
            }
            if (!p.getType().isEmpty()) {
                initialValues.put(KEY_PACKAGE_TYPE, p.getType());
            }
            if (p.getIcon() != null) {
                initialValues.put(KEY_PACKAGE_ICON, Shared.getByteArray(p.getIcon()));
            }

            initialValues.put(KEY_PACKAGE_PERMISSIONS_COUNT, p.getPermissionCount());
            initialValues.put(KEY_PACKAGE_ACTIVE_COUNT, p.getActiveCount());
            initialValues.put(KEY_PACKAGE_DENIED_COUNT, p.getDeniedCount());
            initialValues.put(KEY_PACKAGE_SHARED, p.isShared()  == false ? 0 : 1);
            initialValues.put(KEY_PACKAGE_USERID, p.getUserID());
            initialValues.put(KEY_PACKAGE_ACTIVE, p.isActive() == false ? 0 : 1);

            String[] value = { p.getPackageName().trim() };

            long lng;

            try
            {
                lng = db.update(TBL_PACKAGE, initialValues, KEY_PACKAGE_NAME + "= ?", value);
            }
            catch (Exception e)
            {
                RootTools.log("Could not Update " + p.getPackageName());
                return false;
            }

            if (lng > 0)
            {
                return true;
            }
            else
            {

                initialValues.put(KEY_PACKAGE_NAME, p.getPackageName());
                initialValues.put(KEY_PACKAGE_APP_NAME, p.getAppName());
                initialValues.put(KEY_PACKAGE_TYPE, p.getType());
                initialValues.put(KEY_PACKAGE_ICON, Shared.getByteArray(p.getIcon()));
                initialValues.put(KEY_PACKAGE_PERMISSIONS_COUNT, p.getPermissionCount());
                initialValues.put(KEY_PACKAGE_ACTIVE_COUNT, p.getActiveCount());
                initialValues.put(KEY_PACKAGE_DENIED_COUNT, p.getDeniedCount());
                initialValues.put(KEY_PACKAGE_SHARED, p.isShared() == false ? 0 : 1);
                initialValues.put(KEY_PACKAGE_USERID, p.getUserID());
                initialValues.put(KEY_PACKAGE_ACTIVE, p.isActive() == false ? 0 : 1);


                lng = db.insert(TBL_PACKAGE, null, initialValues);

                if (lng != -1)
                {
                    return true;
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }

        return false;

    }

    public void insertOrUpdateApps(AndroidPackage p)
    {
        try
        {
            open();

            ContentValues initialValues = new ContentValues();

            for (int i = 0; i < p.getAppNames().size(); i++) {
                String name = p.getAppNames().get(i);
                Drawable icon = p.getIcons().get(i);

                initialValues.put(KEY_APP_NAME, name);
                initialValues.put(KEY_APP_PACKAGE_NAME, p.getPackageName());

                if (icon != null) {
                    initialValues.put(KEY_PACKAGE_ICON, Shared.getByteArray(icon));
                }

                String[] value = { name.trim() };

                long lng = 0;

                try
                {
                    lng = db.update(TBL_APP, initialValues, KEY_APP_NAME + "= ?", value);
                }
                catch (Exception e)
                {
                    RootTools.log("Could not Update " + name);
                }

                if (lng <= 0)
                {

                    initialValues.put(KEY_APP_NAME, name);
                    initialValues.put(KEY_APP_PACKAGE_NAME, p.getPackageName());

                    if (icon != null) {
                        initialValues.put(KEY_PACKAGE_ICON, Shared.getByteArray(icon));
                    }

                    lng = db.insert(TBL_APP, null, initialValues);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }
    }

    public boolean insertOrUpdatePermission(Permission p)
    {
        try
        {
            open();

            ContentValues initialValues = new ContentValues();

            if (!p.getPermission().isEmpty()) {
                initialValues.put(KEY_PERMISSION_NAME, p.getPermission());
            }
            if (p.getIcon() != null) {
                initialValues.put(KEY_PERMISSION_ICON, Shared.getByteArray(p.getIcon()));
            }
            if (!p.getPermissionDescription().isEmpty()) {
                initialValues.put(KEY_PERMISSION_DESCRIPTION, p.getPermissionDescription());
            }
            if (!p.getPackageName().isEmpty()) {
                initialValues.put(KEY_PERMISSION_PACKAGE_NAME, p.getPackageName());
            }
            if (!p.getOwner().isEmpty()) {
                initialValues.put(KEY_PERMISSION_OWNER, p.getOwner());
            }

            initialValues.put(KEY_PERMISSION_ACTIVE, p.isActive() == false ? 0 : 1);

            String[] value = { p.getPermission().trim() };

            long lng;

            try
            {
                lng = db.update(TBL_PERMISSION, initialValues, KEY_PERMISSION_NAME + "= ?", value);
            }
            catch (Exception e)
            {
                RootTools.log("Could not Update " + p.getPermission());
                return false;
            }

            if (lng > 0)
            {
                return true;
            }
            else
            {

                initialValues.put(KEY_PERMISSION_NAME, p.getPermission());
                initialValues.put(KEY_PERMISSION_ICON, Shared.getByteArray(p.getIcon()));
                initialValues.put(KEY_PERMISSION_DESCRIPTION, p.getPermissionDescription());
                initialValues.put(KEY_PERMISSION_PACKAGE_NAME, p.getPackageName());
                initialValues.put(KEY_PERMISSION_OWNER, p.getOwner());
                initialValues.put(KEY_PERMISSION_ACTIVE, p.isActive() == false ? 0 : 1);


                lng = db.insert(TBL_PERMISSION, null, initialValues);

                if (lng != -1)
                {
                    return true;
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }

        return false;

    }

    public boolean insertOrUpdateChangedPermission(Permission p)
    {
        try
        {
            open();

            ContentValues initialValues = new ContentValues();

            if (!p.getPermission().isEmpty()) {
                initialValues.put(KEY_PERMISSION_CHANGED_PERMISSION_NAME, p.getPermission());
            }
            if (!p.getPackageName().isEmpty()) {
                initialValues.put(KEY_PERMISSION_CHANGED_PACKAGE_NAME, p.getPackageName());
            }

            initialValues.put(KEY_PERMISSION_CHANGED_ACTIVE, p.isActive() == false ? 0 : 1);

            String[] value = { p.getPermission().trim(), p.getPackageName().trim() };

            long lng;

            try
            {
                lng = db.update(TBL_PERMISSION_CHANGED, initialValues, KEY_PERMISSION_CHANGED_PERMISSION_NAME + "= ? AND " + KEY_PERMISSION_CHANGED_PACKAGE_NAME + "= ?", value);
            }
            catch (Exception e)
            {
                RootTools.log("Could not Update " + p.getPermission());
                return false;
            }

            if (lng > 0)
            {
                return true;
            }
            else
            {

                initialValues.put(KEY_PERMISSION_CHANGED_PERMISSION_NAME, p.getPermission());
                initialValues.put(KEY_PERMISSION_CHANGED_PACKAGE_NAME, p.getPackageName());
                initialValues.put(KEY_PERMISSION_CHANGED_ACTIVE, p.isActive() == false ? 0 : 1);


                lng = db.insert(TBL_PERMISSION_CHANGED, null, initialValues);

                if (lng != -1)
                {
                    return true;
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }

        return false;

    }
}
