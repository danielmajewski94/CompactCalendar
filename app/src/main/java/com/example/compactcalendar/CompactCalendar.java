package com.example.compactcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class CompactCalendar extends View {
    private static final int VIEW_MODE_MONTH = 0;
    private static final int VIEW_MODE_WEEK = 1;

    private int viewMode = VIEW_MODE_MONTH; // Standard: Monatsansicht
    private Paint paint;
    private Calendar calendar;
    private int daysInMonth;
    private int firstDayOfWeek;

    public CompactCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);

        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Montag als erster Wochentag
        updateCalendarData();

        // Attribute auslesen
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CompactCalendar);
            viewMode = typedArray.getInt(R.styleable.CompactCalendar_viewMode, VIEW_MODE_MONTH);
            typedArray.recycle();
        }
    }

    private void updateCalendarData() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7; // Montag als Start
    }

    public void setViewMode(int mode) {
        viewMode = mode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Hintergrund
        canvas.drawColor(Color.WHITE);

        // Kopfzeile
        drawHeader(canvas);

        // Tage basierend auf der Ansicht zeichnen
        if (viewMode == VIEW_MODE_MONTH) {
            drawMonthView(canvas);
        } else if (viewMode == VIEW_MODE_WEEK) {
            drawWeekView(canvas);
        }
    }

    private void drawHeader(Canvas canvas) {
        Paint headerPaint = new Paint();
        headerPaint.setTextSize(60);
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextAlign(Paint.Align.CENTER);

        String monthName = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        canvas.drawText(monthName, getWidth() / 2, 100, headerPaint);
    }

    private void drawMonthView(Canvas canvas) {
        Paint dayPaint = new Paint();
        dayPaint.setTextSize(40);
        dayPaint.setColor(Color.BLACK);
        dayPaint.setTextAlign(Paint.Align.CENTER);

        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / 7;
        int cellHeight = (height - 200) / 6;

        // Wochentage
        String[] days = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        for (int i = 0; i < 7; i++) {
            canvas.drawText(days[i], (i + 0.5f) * cellWidth, 150, dayPaint);
        }

        // Tage des Monats
        int day = 1;
        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                int cellX = col * cellWidth;
                int cellY = 200 + (row - 1) * cellHeight;

                if (row == 1 && col < firstDayOfWeek) {
                    continue; // Leere Felder vor dem ersten Tag
                }

                if (day > daysInMonth) {
                    break; // Nach dem letzten Tag stoppen
                }

                canvas.drawText(String.valueOf(day), cellX + (cellWidth / 2), cellY + (cellHeight / 2), dayPaint);
                day++;
            }
        }
    }

    private void drawWeekView(Canvas canvas) {
        Paint dayPaint = new Paint();
        dayPaint.setTextSize(40);
        dayPaint.setColor(Color.BLACK);
        dayPaint.setTextAlign(Paint.Align.CENTER);

        int width = getWidth();
        int cellWidth = width / 7;
        int cellHeight = (getHeight() - 200);

        // Wochentage
        String[] days = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        for (int i = 0; i < 7; i++) {
            canvas.drawText(days[i], (i + 0.5f) * cellWidth, 150, dayPaint);
        }

        // Tage der Woche
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Woche beginnt mit Montag

        for (int col = 0; col < 7; col++) {
            int cellX = col * cellWidth;
            canvas.drawText(String.valueOf(tempCalendar.get(Calendar.DAY_OF_MONTH)),
                    cellX + (cellWidth / 2), 200 + (cellHeight / 2), dayPaint);
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
