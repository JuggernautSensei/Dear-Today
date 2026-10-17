package com.jiwoo.todaydiary;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public final class DiaryStoreTest {
    private Context context;
    private DiaryStore store;
    private final LocalDate day = LocalDate.of(2020, 2, 29);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("verification.db");
        store = new DiaryStore(context, "verification.db");
    }

    @After
    public void tearDown() {
        store.close();
        context.deleteDatabase("verification.db");
    }

    @Test public void koreanAndMultilineSurviveReopen() {
        String body = "커피 한 잔과 함께\n오늘의 기록을 남겼다.\n\n내일도 괜찮기를. ☕";
        store.save(day, "조용한 하루", body);
        store.close();
        store = new DiaryStore(context, "verification.db");
        DiaryEntry saved = store.entry(day);
        assertEquals("조용한 하루", saved.title);
        assertEquals(body, saved.body);
        assertFalse(saved.sample);
    }

    @Test public void draftDoesNotOverwriteSavedEntry() {
        store.save(day, "원래 제목", "완료한 기록");
        store.saveDraft(day, "수정 중", "아직 작성하고 있는 내용");
        store.close();
        store = new DiaryStore(context, "verification.db");
        assertEquals("완료한 기록", store.entry(day).body);
        assertEquals("아직 작성하고 있는 내용", store.draft(day).body);
    }

    @Test public void completingDraftReplacesOneDayAndRemovesDraft() {
        store.save(day, "이전 제목", "이전 본문");
        store.saveDraft(day, "새 제목", "새 본문");
        store.save(day, "새 제목", "새 본문");
        assertEquals("새 제목", store.entry(day).title);
        assertNull(store.draft(day));
        assertEquals(1, store.entries(YearMonth.from(day)).size());
    }

    @Test public void monthsUseEntryDatesAndNewestFirst() {
        store.save(LocalDate.of(2020, 2, 1), "첫날", "처음");
        store.save(day, "마지막 날", "마지막");
        store.save(LocalDate.of(2020, 3, 1), "다음 달", "구분");
        List<DiaryEntry> entries = store.entries(YearMonth.of(2020, 2));
        assertEquals(2, entries.size());
        assertEquals(day, entries.get(0).date);
        assertEquals(LocalDate.of(2020, 2, 1), entries.get(1).date);
    }

    @Test public void unfinishedPastDateCanBeFoundInHistory() {
        store.saveDraft(day, "밤에 쓰던 글", "자정을 지나도 같은 날짜");
        List<DiaryEntry> entries = store.entries(YearMonth.from(day));
        assertEquals(1, entries.size());
        assertTrue(entries.get(0).draft);
        assertEquals(day, entries.get(0).date);
    }

    @Test public void deleteRemovesEntryAndDraft() {
        store.save(day, "기록", "저장한 글");
        store.saveDraft(day, "초안", "수정하던 글");
        store.delete(day);
        assertNull(store.entry(day));
        assertNull(store.draft(day));
        assertTrue(store.entries(YearMonth.from(day)).isEmpty());
    }

    @Test public void titleIsOptionalAndNotAFilePath() {
        store.save(day, "", "본문만 있는 하루");
        assertEquals("제목 없는 하루", store.entry(day).displayTitle());
        store.save(day, "커피 / 책 : 오늘?", "제목에 경로 문자가 있어도 기록 가능");
        assertEquals("커피 / 책 : 오늘?", store.entry(day).title);
    }

    @Test public void emptyDraftPreservesIntentionalClearing() {
        store.save(day, "기존 제목", "기존 본문");
        store.saveDraft(day, "", "");
        assertEquals("", store.draft(day).body);
        assertEquals("기존 본문", store.entry(day).body);
    }
}
