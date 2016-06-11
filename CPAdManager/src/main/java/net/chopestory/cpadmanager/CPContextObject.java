package net.chopestory.cpadmanager;

import android.content.Context;

import junit.framework.Assert;

import java.lang.ref.WeakReference;

/**
 * CPAdManager
 * Created by Chope on 2014. 7. 25..
 */
public class CPContextObject {
    private WeakReference<Context> contextReference;

    public CPContextObject(Context context) {
        Assert.assertNotNull(context);

        this.contextReference = new WeakReference<>(context);
    }

    public Context getContext() {
        if (contextReference != null) {
            return contextReference.get();
        }
        return null;
    }
}
