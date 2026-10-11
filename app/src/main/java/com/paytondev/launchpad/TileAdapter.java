package com.paytondev.launchpad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

final class TileAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Favorites favorites;
    private final List<AppCatalog.Entry> items = new ArrayList<>();
    private int knobPosition = -1;

    TileAdapter(LayoutInflater inflater, Favorites favorites) {
        this.inflater = inflater;
        this.favorites = favorites;
    }

    void setItems(List<AppCatalog.Entry> entries) {
        items.clear();
        items.addAll(entries);
        notifyDataSetChanged();
    }

    /** Position outlined for TUNE-knob navigation, or -1. */
    void setKnobPosition(int position) {
        if (position == knobPosition) return;
        knobPosition = position;
        notifyDataSetChanged();
    }

    @Override public int getCount() { return items.size(); }
    @Override public AppCatalog.Entry getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return position; }
    @Override public boolean areAllItemsEnabled() { return false; }
    @Override public boolean isEnabled(int position) { return items.get(position).launchable; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView != null ? convertView : inflater.inflate(R.layout.tile, parent, false);
        AppCatalog.Entry e = items.get(position);
        ((ImageView) v.findViewById(R.id.icon)).setImageDrawable(e.icon);
        ((TextView) v.findViewById(R.id.label)).setText(e.label);
        v.findViewById(R.id.badge).setVisibility(favorites.contains(e.key) ? View.VISIBLE : View.GONE);
        v.setActivated(position == knobPosition);
        v.setEnabled(e.launchable);
        v.setAlpha(e.launchable ? 1f : 0.45f);
        v.setContentDescription(e.launchable ? e.label : e.label + ". Unavailable");
        return v;
    }
}
