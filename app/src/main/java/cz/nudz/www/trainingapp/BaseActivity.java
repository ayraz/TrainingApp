package cz.nudz.www.trainingapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;

import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.data.TrainingAppDbHelper;

public class BaseActivity extends AppCompatActivity {

    private volatile TrainingAppDbHelper helper;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;
    private static Logger logger = LoggerFactory.getLogger(OrmLiteBaseActivity.class);

    private SessionManager sessionManager;

    public BaseActivity() {
    }

    public SessionManager getSessionManager() {
        if (this.sessionManager == null) {
            this.sessionManager = new SessionManager(this);
        }
        return this.sessionManager;
    }

    public TrainingAppDbHelper getHelper() {
        if (this.helper == null) {
            if (!this.created) {
                throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
            } else if(this.destroyed) {
                throw new IllegalStateException("A call to onDestroy has already been made and the helper cannot be used after that point");
            } else {
                throw new IllegalStateException("Helper is null for some unknown reason");
            }
        } else {
            return this.helper;
        }
    }

    public ConnectionSource getConnectionSource() {
        return this.getHelper().getConnectionSource();
    }

    protected void onCreate(Bundle savedInstanceState) {
        if (this.helper == null) {
            this.helper = this.getHelperInternal(this);
            this.created = true;
        }

        super.onCreate(savedInstanceState);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.releaseHelper(this.helper);
        this.destroyed = true;
    }

    protected TrainingAppDbHelper getHelperInternal(Context context) {
        TrainingAppDbHelper newHelper = OpenHelperManager.getHelper(context, TrainingAppDbHelper.class);
        logger.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
        return newHelper;
    }

    protected void releaseHelper(TrainingAppDbHelper helper) {
        OpenHelperManager.releaseHelper();
        logger.trace("{}: helper {} was released, set to null", this, helper);
        this.helper = null;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }
}
