package com.jiwoo.todaydiary;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class NotebookUiTest {
    @Test public void coverOpensAndHistoryCanBeBrowsed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
            View cover = activity.findViewById(R.id.open_notebook);
            assertNotNull(cover);
            cover.performClick();
            assertNotNull(activity.findViewById(R.id.history_tab));
            activity.findViewById(R.id.history_tab).performClick();
            assertNotNull(activity.findViewById(R.id.entry_list));
            activity.findViewById(R.id.previous_month).performClick();
            assertNotNull(activity.findViewById(R.id.entry_list));
            activity.findViewById(R.id.close_notebook).performClick();
            assertNotNull(activity.findViewById(R.id.open_notebook));
            });
        }
    }
}
