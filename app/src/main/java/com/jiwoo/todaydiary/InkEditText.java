package com.jiwoo.todaydiary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.EditText;

final class InkEditText extends EditText {
    private final Paint rule = new Paint(Paint.ANTI_ALIAS_FLAG);

    InkEditText(Context context) {
        super(context);
        rule.setColor(NotebookSurface.RULE);
        rule.setStrokeWidth(getResources().getDisplayMetrics().density * .5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int spacing = getLineHeight();
        int baseline = getBaseline() + getScrollY();
        float offset = getResources().getDisplayMetrics().density * 7;
        for (float y = baseline + offset; y < getHeight() + getScrollY(); y += spacing) {
            canvas.drawLine(getScrollX(), y, getWidth() + getScrollX(), y, rule);
        }
        super.onDraw(canvas);
    }
}
