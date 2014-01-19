package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public class EditActivity extends Activity {
    private Activiteit oldActiviteit;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
        this.oldActiviteit = loadActiviteit(ActivityUtils.getActiviteitNameFromIntent(this.getIntent()));
        ActivityUtils.updateGUI(this, oldActiviteit);
    }

    private void initGUI() {
        setContentView(R.layout.activity_edit);
    }

    private Activiteit loadActiviteit(String name) {
        return ((ApplicationVrijetijdsApp) getApplication()).getData().getActiviteit(name);
    }

    private boolean removeActiviteit(Activiteit oldActiviteit) {
        return ((ApplicationVrijetijdsApp) getApplication()).getData().removeActiviteit(oldActiviteit);
    }

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        Toast.makeText(this, R.string.toast_edit_cancel, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnDelete(View button) {
        if (!removeActiviteit(oldActiviteit)) {
            return;
        }
        Intent returnIntent = new Intent();
        ActivityUtils.storeActivityNameToIntent(returnIntent, oldActiviteit.getName());
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_edit_removed, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnSave(View button) {
        Activiteit a = ActivityUtils.updateActiviteit(this, oldActiviteit);
        if (a == null) {
            return;
        }
        Intent returnIntent = new Intent();
        ActivityUtils.storeActivityNameToIntent(returnIntent, a.getName());
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_edit_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}