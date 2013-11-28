/*
 * Copyright (C) 2013 Gummy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.gummy.widgets;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.settings.R;
import com.android.settings.gummy.StatusBarToggles;

public class CustomTogglePref extends Preference {

    private StatusBarToggles mParent;

    public CustomTogglePref(Context context) {
        super(context);
        setLayoutResource(R.layout.custom_toggle_pref);
    }
    public CustomTogglePref(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.custom_toggle_pref);
    }

    public void setParent(StatusBarToggles parent){
        mParent = parent;
    }

    @Override
    public void onBindView (View view) {
        super.onBindView(view);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.widget_frame);
        mParent.setupToggleViews(ll);
    }
}
