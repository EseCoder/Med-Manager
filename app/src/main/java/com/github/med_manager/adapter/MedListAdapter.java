/*
 * Copyright 2018 Open Source Project
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
package com.github.med_manager.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.med_manager.R;
import com.github.med_manager.database.DatabaseUtil;
import com.github.med_manager.model.Medication;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ese Udom on 4/9/2018.
 */
public class MedListAdapter extends RecyclerView.Adapter<MedListAdapter.MedHolder> {

    private final List<Medication> list;
    private Context context;
    private int itemResource;
    private RelativeLayout mRelativeLayout;

    public MedListAdapter(Context context, int itemResource, List<Medication> list, RelativeLayout mRelativeLayout) {
        this.list = list;
        this.context = context;
        this.itemResource = itemResource;
        this.mRelativeLayout = mRelativeLayout;
    }

    @Override
    public MedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate list item view resource and pass the object to holder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new MedHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(MedHolder holder, int position) {
        //Bind each data object to recyclerview holder
        Medication medication = this.list.get(position);

        holder.bindMedication(medication);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class MedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Medication medication;
        private Context context;

        private ImageView remove;
        private RelativeLayout image;
        private TextView drugName, interval, startEndDate, description;

        public MedHolder(final Context context, View itemView) {
            super(itemView);

            this.context = context;

            this.image = (RelativeLayout) itemView.findViewById(R.id.med_image);
            this.drugName = (TextView) itemView.findViewById(R.id.drug_name);
            this.interval = (TextView) itemView.findViewById(R.id.interval);
            this.startEndDate = (TextView) itemView.findViewById(R.id.start_date);
            this.description = (TextView) itemView.findViewById(R.id.med_description);
            this.remove = (ImageView) itemView.findViewById(R.id.remove);

            this.remove.setOnClickListener(this);
        }

        /** Binds each object to their view */
        private void bindMedication(Medication medication) {
            this.medication = medication;

            setItemImage();
            this.drugName.setText(medication.getDrugName());
            this.interval.setText(medication.getInterval());
            this.startEndDate.setText(formatDateTime(medication.getStartDate(), medication.getEndDate()));
            this.description.setText(medication.getDesc());
        }

        /** Formats start and end date from milliseconds */
        private String formatDateTime(String start, String end) {
            String date = DateUtils.formatDateRange(context, Long.parseLong(start),
                    Long.parseLong(end) + 1000, DateUtils.FORMAT_SHOW_DATE |
                            DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL);

            /*String date1 = DateUtils.formatDateTime(context, Long.parseLong(end), DateUtils.FORMAT_SHOW_DATE |
                            DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL);*/

            String fd = (date).substring(0, 17) + "…";//+ " - " + (date1).substring(0, 17) + "…";
            if (fd.length() > 19) {
                return fd.substring(0, 19) + "…";
            } else
                return fd;
        }

        /** Converts a bitmap to drawable */
        private Drawable bitmapToDrawable(Bitmap bitmap) {
            return new BitmapDrawable(this.context.getResources(), bitmap);
        }

        /** Recursive function to set image and check out of memory error due to image size */
        private void setItemImage() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            try {
                this.image.setBackgroundDrawable(bitmapToDrawable(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.background_image, options)));
            } catch (OutOfMemoryError oome) {
                setItemImage();
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            DatabaseUtil.removeMedData(context, pos);
            list.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, list.size());
            removeDate(drugName.getText().toString());
        }

        /**Removes alarm dates from the store since medication has ended*/
        private void removeDate(String drugName) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
            String dates = sharedPreferences.getString(context.getResources().getString(R.string.prefs_file_med_dates_key), "");

            if (!dates.isEmpty()) {
                if (dates.contains(",")) {
                    String ds[] = dates.split(",");
                    List<String> list = new LinkedList<>(Arrays.asList(ds));
                    for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
                        String s = iterator.next();
                        if (s.contains(drugName)) {
                            iterator.remove();
                        }
                    }
                    if (list.size() > 1) {
                        dates = "";
                        for (String s : list) {
                            dates = dates + "," + s;
                        }
                    } else {
                        dates = list.get(0);
                    }
                } else {
                    dates = "";
                }
                sharedPreferences.edit()
                        .putString(context.getResources().getString(R.string.prefs_file_med_dates_key), dates)
                        .apply();
            }
        }
    }
}
