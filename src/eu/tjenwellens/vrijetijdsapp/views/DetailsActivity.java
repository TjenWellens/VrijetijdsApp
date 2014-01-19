package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.EnumProperty;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyTypeUnknownException;

/**
 *
 * @author Tjen
 */
public class DetailsActivity extends Activity {
    public static final int CODE_EDIT_ACTIVITY = 3;
    private Activiteit activiteit;
    private ViewGroup detailsContainer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
        this.activiteit = loadActiviteit(ActivityUtils.getActiviteitNameFromIntent(this.getIntent()));
        updateGUI(activiteit);
    }

    private void initGUI() {
        setContentView(R.layout.activity_details);
        detailsContainer = (ViewGroup) findViewById(R.id.detailsContainer);
    }

    private TextView createView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        return tv;
    }

    private void updateGUI(Activiteit a) {
        if (a == null) {
            finish();
        }
        detailsContainer.removeAllViews();
        detailsContainer.addView(createView(a.getName()));
        String text = a.getDescription();
        if (text != null && !text.isEmpty()) {
            detailsContainer.addView(createView(getString(R.string.description) + ": " + text));
        }
        text = a.getManual();
        if (text != null && !text.isEmpty()) {
            detailsContainer.addView(createView(getString(R.string.manual) + ": " + text));
        }
        for (Property p : a.getProperties().values()) {
            text = getString(p.getType().getResourceId()) + ": ";
            switch (p.getType()) {
                case PEOPLE:
                case PRICE:
                case TAGS:
                case TIME:
                    text += p.toString();
                    break;
                case ENERGY:
                case LOCATION:
                case RATING:
                    text += getString(((EnumProperty) p).getValue().getResourceId());
                    break;
                default:
                    throw new PropertyTypeUnknownException(p.getType());
            }
            detailsContainer.addView(createView(text));
        }
    }

    private Activiteit loadActiviteit(String oldName) {
        return ((ApplicationVrijetijdsApp) getApplication()).getData().getActiviteit(oldName);
    }

    private void startEditActivity(String activiteitName) {
        Intent intent = new Intent(this, EditActivity.class);
        ActivityUtils.storeActivityNameToIntent(intent, activiteitName);
        startActivityForResult(intent, CODE_EDIT_ACTIVITY);
    }

    /*
     * Handle input from started activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_EDIT_ACTIVITY:
                    String oldName = activiteit.getName();
                    String newName = ActivityUtils.getActiviteitNameFromIntent(data);
                    this.activiteit = loadActiviteit(newName);
                    if (activiteit == null || !oldName.equals(newName)) {
                        Intent returnIntent = new Intent();
                        ActivityUtils.storeActivityNameToIntent(data, newName);
                        setResult(RESULT_OK, returnIntent);
                    }
                    // if removed, exit this activity
                    if (activiteit == null) {
                        finish();
                    } else {
                        updateGUI(activiteit);
                    }
                    break;
                default:
                // ignore
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_edit:
                startEditActivity(activiteit.getName());
                return true;
            case R.id.menu_random:
                nextRandom();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nextRandom() {
        this.activiteit = loadActiviteit(((ApplicationVrijetijdsApp) getApplication()).getData().getRandomNameFromSelection());
        updateGUI(activiteit);
    }
}
