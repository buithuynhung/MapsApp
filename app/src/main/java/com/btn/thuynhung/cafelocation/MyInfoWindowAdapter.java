package com.btn.thuynhung.cafelocation;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    private String description;
    private TextView tvNumbPeople;

    public MyInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = this.context.getLayoutInflater().inflate(R.layout.custom_info, null);
        TextView tvCafeName = v.findViewById(R.id.cafe_name);
        TextView tvContact = v.findViewById(R.id.contact);
        TextView tvOpenTime = v.findViewById(R.id.time_open);
        TextView tvCloseTime = v.findViewById(R.id.time_close);
        tvNumbPeople = v.findViewById(R.id.line);
         tvCafeName.setText(marker.getTitle());

        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        builder.setView(editText);
        builder.setMessage(context.getString(R.string.people_numb));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                description = editText.getText().toString();
                tvNumbPeople.setText(context.getString(R.string.people_numb) + description);

            }
        });

        //tvNumbPeople.setText(context.getString(R.string.people_numb) + description);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();*/

        return v;
    }
}
