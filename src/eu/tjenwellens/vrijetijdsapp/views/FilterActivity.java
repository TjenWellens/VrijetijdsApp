package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;

/**
 *
 * @author Tjen
 */
public class FilterActivity extends Activity {
    ApplicationVrijetijdsApp application;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (ApplicationVrijetijdsApp) getApplication();
        initGUI();
    }

    private void initGUI() {
        setContentView(R.layout.activity_search);
    }

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        Toast.makeText(this, R.string.toast_search_cancel, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnAccept(View button) {
        application.getData().selectWithFilters(ActivityUtils.createFilters(this));
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_search_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}
