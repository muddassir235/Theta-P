/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.ui.auth.ui.email;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.ui.auth.R;
import com.firebase.ui.auth.ui.AcquireEmailHelper;
import com.firebase.ui.auth.ui.ActivityHelper;
import com.firebase.ui.auth.ui.AppCompatBase;
import com.firebase.ui.auth.ui.FlowParameters;
import com.firebase.ui.auth.util.FirebaseAuthWrapper;
import com.firebase.ui.auth.util.FirebaseAuthWrapperFactory;
import com.google.android.gms.auth.api.credentials.Credential;

public class EmailHintContainerActivity extends AppCompatBase {
    private static final int RC_HINT = 13;
    private static final String TAG = EmailHintContainerActivity.class.getName()+": ";
    private AcquireEmailHelper mAcquireEmailHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAcquireEmailHelper = new AcquireEmailHelper(mActivityHelper);
        FirebaseAuthWrapper apiWrapper =
                FirebaseAuthWrapperFactory.getFirebaseAuthWrapper(mActivityHelper.getAppName());

        Log.v(TAG, " inside on create");
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.color_gradient_top));
        }

        PendingIntent hintIntent = apiWrapper.getEmailHintIntent(this);
        if (hintIntent != null) {
            try {
                startIntentSenderForResult(hintIntent.getIntentSender(), RC_HINT, null, 0, 0, 0);
                return;
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        finish(RESULT_CANCELED, new Intent());
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT && data != null) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential == null) {
                // If the hint picker is cancelled show the SignInNoPasswordActivity
                startActivityForResult(
                        SignInNoPasswordActivity.createIntent(
                                this,
                                mActivityHelper.getFlowParams(),
                                null),
                        AcquireEmailHelper.RC_SIGN_IN);
                return;
            }
            mAcquireEmailHelper.checkAccountExists(credential.getId());
        } else {
            mAcquireEmailHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Intent createIntent(
            Context context,
            FlowParameters flowParams) {
        return ActivityHelper.createBaseIntent(
                context,
                EmailHintContainerActivity.class,
                flowParams);
    }
}
