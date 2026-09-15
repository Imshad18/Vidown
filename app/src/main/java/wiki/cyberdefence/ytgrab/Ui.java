package wiki.cyberdefence.ytgrab;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public final class Ui {
    private Ui() {}

    public static int dp(Context c, int v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    public static int primaryColor(Context c) {
        return ContextCompat.getColor(c, R.color.text_primary);
    }

    public static int secondaryColor(Context c) {
        return ContextCompat.getColor(c, R.color.text_secondary);
    }

    public static int surfaceColor(Context c) {
        return ContextCompat.getColor(c, R.color.surface);
    }

    public static int backgroundColor(Context c) {
        return ContextCompat.getColor(c, R.color.background);
    }

    public static int errorColor(Context c) {
        return ContextCompat.getColor(c, R.color.error);
    }

    public static TextView text(Context c, String s, int sp, boolean bold) {
        TextView v = new TextView(c);
        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(primaryColor(c));
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    public static Button button(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setAllCaps(false);
        return b;
    }

    public static LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
