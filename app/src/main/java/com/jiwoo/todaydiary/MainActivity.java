package com.jiwoo.todaydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {
    private enum Screen { COVER, WRITE, HISTORY, DETAIL }
    private static final Locale KOREAN = Locale.KOREAN;
    private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("yyyy. MM. dd. EEEE", KOREAN);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyy년 M월", KOREAN);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable autosave = this::flushDraft;
    private DiaryStore store;
    private Typeface pen;
    private Typeface serif;
    private Bitmap leather;
    private Bitmap paper;
    private NotebookSurface surface;
    private LinearLayout page;
    private LinearLayout navigation;
    private EditText titleInput;
    private InkEditText bodyInput;
    private TextView draftStatus;
    private Screen screen = Screen.COVER;
    private LocalDate selectedDate = LocalDate.now();
    private YearMonth historyMonth = YearMonth.now();
    private boolean dirty;
    private boolean loading;

    @Override
    protected void attachBaseContext(Context base) {
        Configuration configuration = new Configuration(base.getResources().getConfiguration());
        configuration.setLocale(KOREAN);
        super.attachBaseContext(base.createConfigurationContext(configuration));
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::navigateBack);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 29) getWindow().setNavigationBarContrastEnforced(false);
        pen = Typeface.createFromAsset(getAssets(), "fonts/NanumPenScript-Regular.ttf");
        serif = Typeface.createFromAsset(getAssets(), "fonts/NanumMyeongjo-Regular.ttf");
        leather = NotebookSurface.loadLeather(this);
        paper = NotebookSurface.makePaper();
        store = new DiaryStore(this);
        try {
            store.getWritableDatabase();
            if (state != null) {
                selectedDate = LocalDate.parse(state.getString("date"));
                historyMonth = YearMonth.parse(state.getString("month"));
                screen = Screen.valueOf(state.getString("screen"));
            }
            showScreen();
            if (state != null && screen == Screen.WRITE) {
                loading = true;
                titleInput.setText(state.getString("title"));
                bodyInput.setText(state.getString("body"));
                loading = false;
                dirty = state.getBoolean("dirty");
                if (dirty) handler.postDelayed(autosave, 600);
            }
        } catch (SQLiteException error) {
            showStorageUnavailable();
        }
    }

    private void showStorageUnavailable() {
        TextView message = text("기록을 불러오지 못했어요.\n저장 공간을 확인한 뒤 다시 열어주세요.", 18, serif, NotebookSurface.INK);
        message.setGravity(Gravity.CENTER);
        message.setBackgroundColor(NotebookSurface.PAPER);
        setContentView(message);
    }

    private void showScreen() {
        titleInput = null;
        bodyInput = null;
        draftStatus = null;
        navigation = null;
        dirty = false;
        boolean cover = screen == Screen.COVER;
        surface = new NotebookSurface(this, cover, leather, paper);
        surface.setFocusableInTouchMode(true);
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(cover ? 40 : 33), dp(8), dp(cover ? 31 : 25), 0);
        surface.addView(page, new FrameLayout.LayoutParams(-1, -1));
        getWindow().getDecorView().setSystemUiVisibility(cover ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        if (Build.VERSION.SDK_INT >= 30) {
            int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
            getWindow().getInsetsController().setSystemBarsAppearance(cover ? 0 : lightBars, lightBars);
        }
        surface.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            boolean keyboard = false;
            if (Build.VERSION.SDK_INT >= 30) {
                top = insets.getInsets(WindowInsets.Type.statusBars()).top;
                bottom = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime()).bottom;
                keyboard = insets.isVisible(WindowInsets.Type.ime());
            }
            view.setPadding(0, top, 0, bottom);
            if (navigation != null) navigation.setVisibility(keyboard ? View.GONE : View.VISIBLE);
            return insets.consumeSystemWindowInsets();
        });
        setContentView(surface);
        if (Build.VERSION.SDK_INT >= 30) {
            surface.post(() -> {
                WindowInsetsController controller = getWindow().getInsetsController();
                int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(cover ? 0 : lightBars, lightBars);
                controller.show(WindowInsets.Type.navigationBars());
            });
        }
        switch (screen) {
            case COVER -> buildCover();
            case WRITE -> buildWriter();
            case HISTORY -> buildHistory();
            case DETAIL -> buildDetail();
        }
        surface.requestApplyInsets();
    }

    private void buildCover() {
        FrameLayout center = new FrameLayout(this);
        page.addView(center, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setGravity(Gravity.CENTER);
        TextView title = text("오늘하루는", 35, serif, NotebookSurface.PAPER);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(.025f);
        title.setShadowLayer(dp(1), 0, dp(1), Color.BLACK);
        words.addView(title, new LinearLayout.LayoutParams(-1, -2));
        TextView subtitle = text("자기 전 10분 정도만\n들려주세요.", 16, serif, 0xFFD8D0C0);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(5), 1);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.topMargin = dp(25);
        words.addView(subtitle, subtitleParams);
        FrameLayout.LayoutParams centered = new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER);
        centered.bottomMargin = dp(15);
        center.addView(words, centered);
        Button open = button("일기장 펼치기     →", false);
        open.setId(R.id.open_notebook);
        open.setTextColor(NotebookSurface.PAPER);
        open.setTypeface(serif);
        open.setTextSize(16);
        open.setBackground(outline(0xFFD8D0C0, 30));
        open.setOnClickListener(view -> {
            openDate(LocalDate.now());
            surface.setAlpha(0);
            surface.animate().alpha(1).setDuration(260).start();
        });
        page.addView(open, new LinearLayout.LayoutParams(-1, dp(53)));
        TextView caption = text("오늘의 이야기를 남겨보세요.", 11, serif, 0xFFB4A996);
        caption.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams captionParams = new LinearLayout.LayoutParams(-1, dp(58));
        captionParams.bottomMargin = dp(12);
        page.addView(caption, captionParams);
    }

    private void buildWriter() {
        header("오늘의 기록", true);
        Button date = button(selectedDate.format(FULL_DATE) + "   ⌄", false);
        date.setId(R.id.date_picker);
        date.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        date.setTypeface(serif);
        date.setTextColor(NotebookSurface.MUTED);
        date.setTextSize(12);
        date.setOnClickListener(view -> pickDate());
        page.addView(date, new LinearLayout.LayoutParams(-1, dp(47)));
        addRule(page);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(24), 0, dp(12));
        titleInput = new EditText(this);
        titleInput.setId(R.id.diary_title);
        titleInput.setSingleLine(true);
        titleInput.setHint("오늘을 한마디로 (선택)");
        titleInput.setTextSize(28);
        titleInput.setTypeface(pen);
        titleInput.setTextColor(NotebookSurface.INK);
        titleInput.setHintTextColor(0xFFAAA08F);
        titleInput.setBackgroundColor(Color.TRANSPARENT);
        titleInput.setPadding(0, dp(5), 0, dp(14));
        titleInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        titleInput.setTypeface(pen);
        content.addView(titleInput, new LinearLayout.LayoutParams(-1, -2));
        bodyInput = new InkEditText(this);
        bodyInput.setId(R.id.diary_body);
        bodyInput.setGravity(Gravity.TOP | Gravity.START);
        bodyInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        bodyInput.setTypeface(pen);
        bodyInput.setTextSize(24);
        bodyInput.setLineSpacing(dp(9), 1);
        bodyInput.setTextColor(NotebookSurface.INK);
        bodyInput.setHintTextColor(0xFFABA08E);
        bodyInput.setHint("오늘 하루는 어땠나요?\n짧은 한 줄도 괜찮아요.");
        bodyInput.setBackgroundColor(Color.TRANSPARENT);
        bodyInput.setPadding(0, dp(8), 0, dp(12));
        bodyInput.setMinHeight(dp(290));
        bodyInput.setMinLines(7);
        content.addView(bodyInput, new LinearLayout.LayoutParams(-1, -2));
        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        DiaryEntry draft = store.draft(selectedDate);
        DiaryEntry saved = store.entry(selectedDate);
        DiaryEntry editable = draft != null ? draft : saved;
        if (editable != null) {
            titleInput.setText(editable.title);
            bodyInput.setText(editable.body);
        }
        titleInput.setSaveEnabled(false);
        bodyInput.setSaveEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            public void onTextChanged(CharSequence value, int start, int before, int count) {
                if (loading) return;
                dirty = true;
                draftStatus.setText("기록 중…");
                draftStatus.setTextColor(NotebookSurface.MUTED);
                handler.removeCallbacks(autosave);
                handler.postDelayed(autosave, 600);
            }
            public void afterTextChanged(Editable value) { }
        };
        titleInput.addTextChangedListener(watcher);
        bodyInput.addTextChangedListener(watcher);

        draftStatus = text(draft != null ? "✓  임시 저장된 글을 이어 쓰고 있어요" : saved != null ? "✓  저장한 일기를 수정하고 있어요" : "한 줄만 남겨도 충분한 하루예요", 11, Typeface.DEFAULT, NotebookSurface.MUTED);
        draftStatus.setId(R.id.draft_status);
        draftStatus.setGravity(Gravity.CENTER_VERTICAL);
        draftStatus.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        page.addView(draftStatus, new LinearLayout.LayoutParams(-1, dp(33)));
        Button save = button(saved == null ? "오늘 기록 남기기" : "수정한 기록 남기기", true);
        save.setId(R.id.save_entry);
        save.setTypeface(serif);
        save.setOnClickListener(view -> saveEntry());
        page.addView(save, new LinearLayout.LayoutParams(-1, dp(49)));
        bottomNavigation(true);
        surface.requestFocus();
    }

    private void buildHistory() {
        header("지난 기록", false);
        LinearLayout monthRow = new LinearLayout(this);
        monthRow.setGravity(Gravity.CENTER_VERTICAL);
        Button previous = iconButton("‹", "이전 달", R.id.previous_month);
        previous.setOnClickListener(view -> changeMonth(-1));
        monthRow.addView(previous, new LinearLayout.LayoutParams(dp(46), dp(54)));
        TextView month = text(historyMonth.format(MONTH), 15, serif, NotebookSurface.INK);
        month.setGravity(Gravity.CENTER);
        monthRow.addView(month, new LinearLayout.LayoutParams(0, dp(54), 1));
        Button next = iconButton("›", "다음 달", R.id.next_month);
        next.setEnabled(historyMonth.isBefore(YearMonth.now()));
        next.setAlpha(next.isEnabled() ? 1 : .25f);
        next.setOnClickListener(view -> changeMonth(1));
        monthRow.addView(next, new LinearLayout.LayoutParams(dp(46), dp(54)));
        page.addView(monthRow);
        addRule(page);

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFillViewport(true);
        LinearLayout list = new LinearLayout(this);
        list.setId(R.id.entry_list);
        list.setOrientation(LinearLayout.VERTICAL);
        List<DiaryEntry> entries = store.entries(historyMonth);
        if (entries.isEmpty()) {
            LinearLayout empty = new LinearLayout(this);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            TextView message = text("아직 펼쳐보지 않은 페이지", 27, pen, NotebookSurface.INK);
            message.setGravity(Gravity.CENTER);
            empty.addView(message);
            TextView description = text("오늘의 작은 순간부터 남겨보세요.", 12, serif, NotebookSurface.MUTED);
            description.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
            descriptionParams.topMargin = dp(13);
            empty.addView(description, descriptionParams);
            Button write = button("오늘 이야기 쓰기", false);
            write.setOnClickListener(view -> openDate(LocalDate.now()));
            LinearLayout.LayoutParams writeParams = new LinearLayout.LayoutParams(-1, dp(48));
            writeParams.topMargin = dp(21);
            empty.addView(write, writeParams);
            list.addView(empty, new LinearLayout.LayoutParams(-1, -1));
        } else {
            for (DiaryEntry entry : entries) {
                list.addView(historyRow(entry));
                addRule(list);
            }
            TextView caption = text("차곡차곡 쌓이는 나의 하루", 11, serif, NotebookSurface.MUTED);
            caption.setGravity(Gravity.CENTER);
            list.addView(caption, new LinearLayout.LayoutParams(-1, dp(66)));
        }
        scroll.addView(list);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        bottomNavigation(false);
    }

    private View historyRow(DiaryEntry entry) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(19), 0, dp(19));
        row.setMinimumHeight(dp(104));
        row.setBackground(ripple());
        row.setFocusable(true);
        row.setContentDescription(entry.date.format(FULL_DATE) + ", " + entry.displayTitle() + (entry.draft ? ", 작성 중" : ""));
        row.setOnClickListener(view -> openDate(entry.date));
        LinearLayout date = new LinearLayout(this);
        date.setOrientation(LinearLayout.VERTICAL);
        date.setGravity(Gravity.CENTER);
        TextView day = text(String.valueOf(entry.date.getDayOfMonth()), 29, serif, NotebookSurface.INK);
        day.setGravity(Gravity.CENTER);
        date.addView(day);
        TextView weekday = text(entry.date.format(DateTimeFormatter.ofPattern("E", KOREAN)), 10, serif, NotebookSurface.MUTED);
        weekday.setGravity(Gravity.CENTER);
        date.addView(weekday);
        row.addView(date, new LinearLayout.LayoutParams(dp(44), -2));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(dp(15), 0, dp(7), 0);
        TextView title = text(entry.displayTitle(), 25, pen, NotebookSurface.INK);
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        words.addView(title);
        TextView preview = text((entry.draft ? "작성 중 · " : "") + entry.preview(), 12, serif, NotebookSurface.MUTED);
        preview.setMaxLines(1);
        preview.setEllipsize(TextUtils.TruncateAt.END);
        words.addView(preview);
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = text("›", 23, Typeface.DEFAULT, NotebookSurface.INK);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(13), -2));
        return row;
    }

    private void buildDetail() {
        DiaryEntry entry = store.entry(selectedDate);
        header("하루의 기록", false);
        TextView date = text(selectedDate.format(FULL_DATE), 12, serif, NotebookSurface.MUTED);
        date.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(date, new LinearLayout.LayoutParams(-1, dp(47)));
        addRule(page);
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(24), 0, dp(30));
        TextView title = text(entry.displayTitle(), 30, pen, NotebookSurface.INK);
        title.setPadding(0, 0, 0, dp(20));
        content.addView(title);
        TextView body = text(entry.body, 24, pen, NotebookSurface.INK);
        body.setLineSpacing(dp(9), 1);
        body.setTextIsSelectable(true);
        content.addView(body);
        TextView caption = text(entry.sample ? "시연을 위해 준비한 예시 일기입니다." : "오늘도 당신의 하루를 들려주셔서 고마워요.", 11, serif, NotebookSurface.MUTED);
        caption.setPadding(0, dp(33), 0, 0);
        content.addView(caption);
        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout actions = new LinearLayout(this);
        Button edit = button(store.draft(selectedDate) != null ? "수정 중인 글 이어 쓰기" : "이 기록 수정하기", true);
        edit.setId(R.id.edit_entry);
        edit.setTypeface(serif);
        edit.setOnClickListener(view -> navigate(Screen.WRITE));
        actions.addView(edit, new LinearLayout.LayoutParams(0, dp(49), 1));
        Button delete = iconButton("삭제", "이 기록 삭제", R.id.delete_entry);
        delete.setTextSize(12);
        delete.setTextColor(NotebookSurface.MUTED);
        delete.setOnClickListener(view -> confirmDelete());
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(dp(55), dp(49));
        deleteParams.leftMargin = dp(8);
        actions.addView(delete, deleteParams);
        page.addView(actions);
        bottomNavigation(selectedDate.equals(LocalDate.now()));
    }

    private void header(String label, boolean calendar) {
        FrameLayout header = new FrameLayout(this);
        TextView title = text(label, 18, serif, NotebookSurface.INK);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new FrameLayout.LayoutParams(-1, -1));
        Button back = iconButton("‹", screen == Screen.HISTORY ? "일기장 덮기" : "지난 기록으로", R.id.close_notebook);
        back.setOnClickListener(view -> {
            if (screen == Screen.HISTORY) navigate(Screen.COVER);
            else {
                historyMonth = YearMonth.from(selectedDate);
                navigate(Screen.HISTORY);
            }
        });
        header.addView(back, new FrameLayout.LayoutParams(dp(44), -1, Gravity.START));
        ImageButton right = new ImageButton(this);
        right.setImageResource(calendar ? R.drawable.ic_calendar : R.drawable.ic_history);
        right.setContentDescription(calendar ? "기록 날짜 선택" : "일기장 덮기");
        right.setBackground(ripple());
        right.setPadding(dp(11), dp(11), dp(11), dp(11));
        right.setOnClickListener(view -> {
            if (calendar) pickDate();
            else navigate(Screen.COVER);
        });
        header.addView(right, new FrameLayout.LayoutParams(dp(44), -1, Gravity.END));
        page.addView(header, new LinearLayout.LayoutParams(-1, dp(52)));
    }

    private void bottomNavigation(boolean today) {
        navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.VERTICAL);
        navigation.setPadding(0, dp(18), 0, 0);
        addRule(navigation);
        LinearLayout tabs = new LinearLayout(this);
        Button todayTab = button("오늘", false);
        todayTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0);
        todayTab.setCompoundDrawablePadding(dp(4));
        todayTab.setCompoundDrawableTintList(ColorStateList.valueOf(today ? NotebookSurface.INK : NotebookSurface.MUTED));
        todayTab.setId(R.id.today_tab);
        todayTab.setTextSize(13);
        todayTab.setTypeface(serif);
        todayTab.setTextColor(today ? NotebookSurface.INK : NotebookSurface.MUTED);
        todayTab.setContentDescription("오늘 일기");
        todayTab.setOnClickListener(view -> openDate(LocalDate.now()));
        tabs.addView(todayTab, new LinearLayout.LayoutParams(0, dp(62), 1));
        View divider = new View(this);
        divider.setBackgroundColor(NotebookSurface.RULE);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(dp(.7f), dp(25));
        dividerParams.gravity = Gravity.CENTER_VERTICAL;
        tabs.addView(divider, dividerParams);
        Button history = button("지난 기록", false);
        history.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_history, 0, 0);
        history.setCompoundDrawablePadding(dp(4));
        history.setCompoundDrawableTintList(ColorStateList.valueOf(today ? NotebookSurface.MUTED : NotebookSurface.INK));
        history.setId(R.id.history_tab);
        history.setTextSize(13);
        history.setTypeface(serif);
        history.setTextColor(today ? NotebookSurface.MUTED : NotebookSurface.INK);
        history.setContentDescription("지난 기록");
        history.setOnClickListener(view -> {
            historyMonth = YearMonth.from(selectedDate);
            navigate(Screen.HISTORY);
        });
        tabs.addView(history, new LinearLayout.LayoutParams(0, dp(62), 1));
        navigation.addView(tabs);
        page.addView(navigation);
    }

    void openDate(LocalDate date) {
        if (!flushDraft()) return;
        hideKeyboard();
        selectedDate = date;
        try {
            screen = store.draft(date) != null || store.entry(date) == null ? Screen.WRITE : Screen.DETAIL;
            showScreen();
        } catch (SQLiteException error) {
            showStorageUnavailable();
        }
    }

    private void navigate(Screen next) {
        if (!flushDraft()) return;
        hideKeyboard();
        Screen previous = screen;
        screen = next;
        try {
            showScreen();
        } catch (SQLiteException error) {
            screen = previous;
            showStorageUnavailable();
        }
    }

    private void changeMonth(int direction) {
        historyMonth = historyMonth.plusMonths(direction);
        navigate(Screen.HISTORY);
    }

    private void pickDate() {
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, day) -> openDate(LocalDate.of(year, month + 1, day)), selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());
        long endOfToday = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
        picker.getDatePicker().setMaxDate(endOfToday);
        picker.show();
    }

    private boolean flushDraft() {
        handler.removeCallbacks(autosave);
        if (screen != Screen.WRITE || !dirty) return true;
        try {
            String title = titleInput.getText().toString();
            String body = bodyInput.getText().toString();
            if (title.trim().isEmpty() && body.trim().isEmpty() && store.entry(selectedDate) == null) {
                store.discardDraft(selectedDate);
            } else {
                store.saveDraft(selectedDate, title, body);
            }
            dirty = false;
            draftStatus.setText("✓  임시 저장됨");
            draftStatus.setTextColor(NotebookSurface.MUTED);
            return true;
        } catch (SQLiteException error) {
            draftStatus.setText("저장하지 못했어요. 저장 공간을 확인해주세요.");
            draftStatus.setTextColor(0xFF8B4437);
            return false;
        }
    }

    private void saveEntry() {
        if (bodyInput.getText().toString().trim().isEmpty()) {
            bodyInput.setError("오늘의 이야기를 한 줄 남겨주세요.");
            bodyInput.requestFocus();
            return;
        }
        try {
            store.save(selectedDate, titleInput.getText().toString().trim(), bodyInput.getText().toString().trim());
            dirty = false;
            handler.removeCallbacks(autosave);
            hideKeyboard();
            screen = Screen.DETAIL;
            showScreen();
            Toast.makeText(this, "하루를 한 페이지에 남겼어요.", Toast.LENGTH_SHORT).show();
        } catch (SQLiteException error) {
            storageError();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("이 기록을 지울까요?")
                .setMessage("지운 기록은 다시 불러올 수 없어요.")
                .setNegativeButton("간직하기", null)
                .setPositiveButton("삭제", (dialog, which) -> {
                    try {
                        store.delete(selectedDate);
                        historyMonth = YearMonth.from(selectedDate);
                        navigate(Screen.HISTORY);
                    } catch (SQLiteException error) {
                        storageError();
                    }
                }).show();
    }

    private void storageError() {
        new AlertDialog.Builder(this).setTitle("기록을 저장하지 못했어요")
                .setMessage("저장 공간을 확인한 뒤 다시 시도해주세요. 작성 중인 내용은 화면에 남아 있어요.")
                .setPositiveButton("확인", null).show();
    }

    @Override
    protected void onPause() {
        flushDraft();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("screen", screen.name());
        state.putString("date", selectedDate.toString());
        state.putString("month", historyMonth.toString());
        if (screen == Screen.WRITE && titleInput != null) {
            state.putString("title", titleInput.getText().toString());
            state.putString("body", bodyInput.getText().toString());
            state.putBoolean("dirty", dirty);
        }
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(autosave);
        store.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    private void navigateBack() {
        if (screen == Screen.COVER) {
            finish();
        } else if (screen == Screen.HISTORY) {
            navigate(Screen.COVER);
        } else {
            historyMonth = YearMonth.from(selectedDate);
            navigate(Screen.HISTORY);
        }
    }

    private void hideKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(surface.getWindowToken(), 0);
    }

    private TextView text(String value, float size, Typeface font, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTypeface(font);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        return view;
    }

    private Button button(String label, boolean filled) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(14);
        button.setTextColor(filled ? NotebookSurface.PAPER : NotebookSurface.INK);
        button.setAllCaps(false);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(dp(8), 0, dp(8), 0);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(filled ? NotebookSurface.LEATHER : Color.TRANSPARENT);
        shape.setCornerRadius(dp(11));
        button.setBackground(new RippleDrawable(ColorStateList.valueOf(filled ? 0x33FFFFFF : 0x15302923), shape, null));
        button.setStateListAnimator(null);
        return button;
    }

    private Button iconButton(String icon, String description, int id) {
        Button button = button(icon, false);
        button.setTextSize(32);
        button.setId(id);
        button.setContentDescription(description);
        return button;
    }

    private RippleDrawable outline(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.TRANSPARENT);
        shape.setCornerRadius(dp(radius));
        shape.setStroke(dp(1), color);
        return new RippleDrawable(ColorStateList.valueOf(0x22FFFFFF), shape, null);
    }

    private RippleDrawable ripple() {
        return new RippleDrawable(ColorStateList.valueOf(0x14302923), null, new android.graphics.drawable.ColorDrawable(Color.WHITE));
    }

    private void addRule(LinearLayout parent) {
        View rule = new View(this);
        rule.setBackgroundColor(NotebookSurface.RULE);
        parent.addView(rule, new LinearLayout.LayoutParams(-1, Math.max(1, dp(.65f))));
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
