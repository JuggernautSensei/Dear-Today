package com.jiwoo.todaydiary;

import java.time.LocalDate;

final class DiaryEntry {
    final LocalDate date;
    final String title;
    final String body;
    final boolean sample;
    final boolean draft;

    DiaryEntry(LocalDate date, String title, String body, boolean sample) {
        this(date, title, body, sample, false);
    }

    DiaryEntry(LocalDate date, String title, String body, boolean sample, boolean draft) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.sample = sample;
        this.draft = draft;
    }

    String displayTitle() {
        return title.trim().isEmpty() ? "제목 없는 하루" : title;
    }

    String preview() {
        return body.replace('\n', ' ');
    }
}
