package com.jiwoo.todaydiary;

import android.content.Context;
import android.widget.EditText;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.LocalDate;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class DiaryLifecycleTest {
    @Test public void koreanDraftSurvivesRecreationAndCanBeEdited() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LocalDate date = LocalDate.of(2000, 2, 29);
        String body = "따뜻한 커피 한 잔을 마시고\n오랜만에 천천히 걸었다.\n이 정도면 괜찮은 하루였다.";
        try (DiaryStore store = new DiaryStore(context)) {
            DiaryEntry original = store.entry(date);
            DiaryEntry draft = store.draft(date);
            try {
                try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                    scenario.onActivity(activity -> {
                        activity.openDate(date);
                        if (activity.findViewById(R.id.edit_entry) != null) activity.findViewById(R.id.edit_entry).performClick();
                        ((EditText) activity.findViewById(R.id.diary_title)).setText("조용히 마무리한 하루");
                        ((EditText) activity.findViewById(R.id.diary_body)).setText(body);
                    });
                    scenario.recreate();
                    scenario.onActivity(activity -> {
                        assertEquals(body, ((EditText) activity.findViewById(R.id.diary_body)).getText().toString());
                        activity.findViewById(R.id.save_entry).performClick();
                        assertNotNull(activity.findViewById(R.id.edit_entry));
                        activity.findViewById(R.id.edit_entry).performClick();
                        ((EditText) activity.findViewById(R.id.diary_title)).setText("수정한 하루");
                        activity.findViewById(R.id.save_entry).performClick();
                    });
                }
                assertEquals("수정한 하루", store.entry(date).title);
                assertEquals(body, store.entry(date).body);
                assertNull(store.draft(date));
            } finally {
                store.delete(date);
                if (original != null) store.save(date, original.title, original.body);
                if (draft != null) store.saveDraft(date, draft.title, draft.body);
            }
        }
    }
}
