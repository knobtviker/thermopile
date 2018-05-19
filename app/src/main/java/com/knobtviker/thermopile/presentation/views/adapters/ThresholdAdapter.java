package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdLineViewHolder;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdAdapter extends RecyclerView.Adapter<ThresholdLineViewHolder> {
    private final static String TAG = ThresholdAdapter.class.getSimpleName();

    private String timeFormat = "HH:mm";

    private final LayoutInflater layoutInflater;
    private final ImmutableList<String> days;

    private ImmutableList<ThresholdInterval> intervals = ImmutableList.of();

    public ThresholdAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.days = ImmutableList.copyOf(context.getResources().getStringArray(R.array.weekdays));

        setEmptyDays();
    }

    @NonNull
    @Override
    public ThresholdLineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ThresholdLineViewHolder(layoutInflater.inflate(R.layout.item_threshold_line, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ThresholdLineViewHolder holder, int position) {
        final ThresholdInterval item = intervals.get(position);

        final Threshold threshold = item.threshold();
        final Interval interval = item.interval();

        holder.textViewTimeStart.setVisibility(threshold != null ? View.VISIBLE : View.GONE);
        holder.textViewTimeEnd.setVisibility(threshold != null ? View.VISIBLE : View.GONE);
        if (threshold != null) {
            holder.viewIndicator.setBackgroundColor(Color.parseColor(threshold.color));
            holder.textViewTemperature.setText(String.valueOf(threshold.temperature));

            buildTime(holder.textViewTimeStart, interval.getStart().toString(timeFormat));
            buildTime(holder.textViewTimeEnd, interval.getEnd().toString(timeFormat));
        } else {
            holder.viewIndicator.setBackgroundResource(R.drawable.empty_interval_line);
            holder.textViewTemperature.setText("");
        }

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.rootLayout);
        constraintSet.constrainWidth(holder.viewIndicator.getId(), calculateWidth(interval));
        constraintSet.applyTo(holder.rootLayout);
    }

    @Override
    public void onViewRecycled(@NonNull ThresholdLineViewHolder holder) {

        holder.viewIndicator.setBackgroundResource(R.drawable.empty_interval_line);
        holder.textViewTimeStart.setVisibility(View.GONE);
        holder.textViewTimeEnd.setVisibility(View.GONE);

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return intervals.size();
    }

    public void setData(@NonNull final List<Threshold> thresholds) {
        if (thresholds.isEmpty()) {
            this.intervals = emptyDays();
        } else {
            final List<ThresholdInterval> intervalsAll = thresholds
                .stream()
                .map(threshold -> {
                        return ThresholdInterval.builder()
                            .threshold(threshold)
                            .interval(convertThresholdToInterval(threshold))
                            .build();
                    }
                )
                .collect(Collectors.toList());

            IntStream.range(0, 7).forEach(day -> dayMargins(thresholds, intervalsAll, day));

            intervalsAll.sort((interval, other) -> {
                if (interval.equals(other)) {
                    return 0;
                } else {
                    return interval.interval().isBefore(other.interval()) ? -1 : 1;
                }
            });

            this.intervals = ImmutableList.copyOf(intervalsAll);
        }

        notifyDataSetChanged();
    }

    private Interval convertThresholdToInterval(@NonNull final Threshold threshold) {
        final DateTime start = new DateTime()
            .withDayOfWeek(threshold.day + 1) //must be in range of 1 to 7
            .withHourOfDay(threshold.startHour)
            .withMinuteOfHour(threshold.startMinute)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0);

        //this works for everyhing but 00:00 end time. which should not be allowed
        final DateTime end = new DateTime()
            .withDayOfWeek(threshold.day + 1)
            .withHourOfDay(threshold.endHour)
            .withMinuteOfHour(threshold.endMinute)
//            .withSecondOfMinute(59)
//            .withMillisOfSecond(999);
            .withSecondOfMinute(0)
            .withMillisOfSecond(0)
            .minusMillis(1);

        return new Interval(start, end);
    }
//        final List<Pair<Threshold, Interval>> intervalsAll = thresholds
//            .stream()
//            .map(threshold -> {
//                final DateTime start = new DateTime()
//                    .withDayOfWeek(threshold.day + 1) //must be in range of 1 to 7
//                    .withHourOfDay(threshold.startHour)
//                    .withMinuteOfHour(threshold.startMinute)
//                    .withSecondOfMinute(0)
//                    .withMillisOfSecond(0);
//
//                if (threshold.endHour == 23 && threshold.endMinute == 59) {
//                    final DateTime end = new DateTime()
//                        .withDayOfWeek(threshold.day + 1)
//                        .withHourOfDay(threshold.endHour)
//                        .withMinuteOfHour(threshold.endMinute)
//                        .withSecondOfMinute(59)
//                        .withMillisOfSecond(999);
//
//                    return Pair.create(threshold, new Interval(start, end));
//                } else {
//                    final DateTime end = new DateTime()
//                        .withDayOfWeek(threshold.day + 1)
//                        .withHourOfDay(threshold.endHour)
//                        .withMinuteOfHour(threshold.endMinute)
//                        .withSecondOfMinute(0)
//                        .withMillisOfSecond(0)
//                        .minusMillis(1);
//
//                    return Pair.create(threshold, new Interval(start, end));
//                }
//            })
//            .collect(Collectors.toList());
//
//        IntStream.range(0, 7).forEach(day -> dayMargins(thresholds, intervalsAll, day));
//
//        intervalsAll.sort((interval, other) -> {
//            if (interval.equals(other)) {
//                return 0;
//            } else {
//                return interval.second.isBefore(other.second) ? -1 : 1;
//            }
//        });
//
//        intervalsAll
//            .forEach(pair ->
//                Timber.i(pair.second.toString() + " --- " + (pair.first == null ? "GAP" : pair.first.toString()))
//            );
//
//        this.intervals = ImmutableList.copyOf(intervalsAll);

//    notifyDataSetChanged();

//}

    public void setTimeFormat(@NonNull final String timeFormat) {
        this.timeFormat = timeFormat;
        notifyDataSetChanged();
    }

    private int calculateWidth(@NonNull final Interval interval) {
        return Minutes.minutesBetween(interval.getStart(), interval.getEnd()).getMinutes();
    }

    private void buildTime(@NonNull final TextView textView, @NonNull final String time) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(time);
//        spannableStringBuilder.setSpan(typefaceSpanMedium, 0, time.indexOf(":"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    public String getItemDay(final int firstVisibleItemPosition) {
        return days.get(intervals.get(firstVisibleItemPosition).interval().getStart().getDayOfWeek() - 1);
    }

    private void setEmptyDays() {
        this.intervals = emptyDays();

        notifyDataSetChanged();
    }

    private ImmutableList<ThresholdInterval> emptyDays() {
        return ImmutableList.copyOf(
            IntStream.range(1, 8)
                .mapToObj(day ->
                    ThresholdInterval.builder()
                        .interval(
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(day)
                                    .withHourOfDay(0)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0),
                                new DateTime()
                                    .withDayOfWeek(day)
                                    .withHourOfDay(23)
                                    .withMinuteOfHour(59)
                                    .withSecondOfMinute(59)
                                    .withMillisOfSecond(999)
                            )
                        )
                        .build()
                )
                .collect(Collectors.toList())
        );
    }

    private void dayMargins(@NonNull final List<Threshold> thresholds, @NonNull final List<ThresholdInterval> thresholdIntervals, final int day) {
        final List<Threshold> dayThresholds = thresholds.stream()
            .filter(threshold -> threshold.day == day)
            .sorted((threshold1, threshold2) -> {
                final DateTime dateTime1 = new DateTime()
                    .withDayOfWeek(threshold1.day + 1) //must be in range of 1 to 7
                    .withHourOfDay(threshold1.startHour)
                    .withMinuteOfHour(threshold1.startMinute)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

                final DateTime dateTime2 = new DateTime()
                    .withDayOfWeek(threshold2.day + 1) //must be in range of 1 to 7
                    .withHourOfDay(threshold2.startHour)
                    .withMinuteOfHour(threshold2.startMinute)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

                return dateTime1.equals(dateTime2) ? 0 : dateTime1.isBefore(dateTime2) ? -1 : 1;
            })
            .collect(Collectors.toList());

        if (!dayThresholds.isEmpty()) {
            final Threshold first = dayThresholds.get(0);
            final Threshold last = dayThresholds.get(dayThresholds.size() - 1);

            if (first.startHour != 0 || first.startMinute != 0) {
                thresholdIntervals.add(
                    ThresholdInterval.builder()
                        .interval(
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(0)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0),
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(first.startHour)
                                    .withMinuteOfHour(first.startMinute)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)
                                    .minusMillis(1)
                            )
                        )
                        .build()
                );
            }
            if (last.endHour != 23 || last.endMinute != 59) {
                thresholdIntervals.add(
                    ThresholdInterval.builder()
                        .interval(
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(last.endHour)
                                    .withMinuteOfHour(last.endMinute)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)
                                    .minusMillis(1),
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(23)
                                    .withMinuteOfHour(59)
                                    .withSecondOfMinute(59)
                                    .withMillisOfSecond(999)
                            )
                        )
                        .build()
                );
            }

            if (dayThresholds.size() > 1) {
                IntStream.range(0, dayThresholds.size() - 1)
                    .forEach(index -> {
                        final Interval current = thresholdIntervals.get(index).interval();
                        final Interval next = thresholdIntervals.get(index + 1).interval();
                        final Interval gap = current.gap(next);
                        if (gap != null) {
                            thresholdIntervals.add(
                                ThresholdInterval.builder()
                                    .interval(
                                        new Interval(
                                            gap.getStart().plusMillis(1),
                                            gap.getEnd().minusMillis(1)
                                        )
                                    )
                                    .build()
                            );
                        }
                    });
            }
        }
    }
}
