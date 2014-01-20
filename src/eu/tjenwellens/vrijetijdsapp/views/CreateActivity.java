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
    private Activiteit created = null;

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

    public void btnAccept(View button) {
        created = ActivityUtils.createActiviteit(this);
        if (created == null) {
            return;
        } else {
            Toast.makeText(this, R.string.toast_create_success, Toast.LENGTH_SHORT).show();
        }
        ActivityUtils.startDetailsActivity(this, created.getName());

    }

    /*
     * Handle input from started activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.CODE_DETAILS_ACTIVITY) {
            Intent returnIntent = new Intent();
            String name = ActivityUtils.getActiviteitNameFromIntent(data);
            if (name == null && created != null) {
                name = created.getName();
            }
            if (name != null) {
                ActivityUtils.storeActivityNameToIntent(returnIntent, name);
            }
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
