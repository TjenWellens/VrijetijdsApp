package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public class CreateActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
    }

    private void initGUI() {
        setContentView(R.layout.activity_create);
    }

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        Toast.makeText(this, R.string.toast_create_cancel, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnCreate(View button) {
        Activiteit a = ActivityUtils.createActiviteit(this);
        if (a == null) {
            return;
        }
        Intent returnIntent = new Intent();
        ActivityUtils.storeActivityNameToIntent(returnIntent, a.getName());
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_create_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}
