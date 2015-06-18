package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.storage.io.FileIO;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends Activity {
    private ViewGroup container;
    private SortedSet<Activiteit> activiteiten;
    ApplicationVrijetijdsApp application;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (ApplicationVrijetijdsApp) getApplication();
        initGUI();
        loadAllActiviteiten();
    }

    private void initGUI() {
        setContentView(R.layout.main);
        container = (ViewGroup) findViewById(R.id.mainContainer);
    }

    private void loadAllActiviteiten() {
        activiteiten = new TreeSet<Activiteit>(application.getData().selectAllActiviteiten());
        updateGUI();
    }

    private void loadSelection() {
        activiteiten = new TreeSet<Activiteit>(application.getData().getSelection());
        updateGUI();
    }

    private void updateGUI() {
        container.removeAllViews();
        TextView tv;
        for (final Activiteit a : activiteiten) {
            tv = new TextView(this);
            tv.setText(a.getName());
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    activiteitClicked(a);
                }
            });
            container.addView(tv);
        }
    }

    private void activiteitClicked(Activiteit a) {
        ActivityUtils.startDetailsActivity(this, a.getName());
    }

    /*
     * Handle input from started activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ActivityUtils.CODE_CREATE_ACTIVITY:
                    addActiviteit(readActiviteit(data));
                    break;
                case ActivityUtils.CODE_SEARCH_ACTIVITY:
                    loadSelection();
                    break;
                case ActivityUtils.CODE_DETAILS_ACTIVITY:
                    loadSelection();
                    break;
                case ActivityUtils.CODE_LIKE_ACTIVITY:
                    loadAllActiviteiten();
                    break;
                default:
                // ignore
            }
        }
    }

    private void addActiviteit(Activiteit a) {
        if (a == null) {
            return;
        }
        activiteiten.add(a);
        updateGUI();
    }

    private Activiteit readActiviteit(Intent intent) {
        String name = ActivityUtils.getActiviteitNameFromIntent(intent);
        if (name == null) {
            return null;
        }
        return ((ApplicationVrijetijdsApp) getApplication()).getData().getActiviteit(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_create:
                ActivityUtils.startCreateActivity(this);
                return true;
            case R.id.menu_settings:
                // todo
                return true;
            case R.id.menu_search:
                ActivityUtils.startFilterActivity(this);
                return true;
            case R.id.menu_show_all:
                loadAllActiviteiten();
                return true;
            case R.id.menu_random:
                ActivityUtils.startRandomActivity(this);
//                loadAllActiviteiten();
                return true;
            case R.id.menu_like:
                ActivityUtils.startLikeActivity(this);
                loadAllActiviteiten();
                return true;
            case R.id.menu_save:
                doSave();
                return true;
            case R.id.menu_load:
                doLoad();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSave() {
        FileIO.writeToFile(this, "activiteiten.txt", application.getData().getSelection());
    }

    private void doLoad() {
        this.activiteiten = new TreeSet<Activiteit>(FileIO.readFromFile(this, "activiteiten.txt"));
        Toast.makeText(this, "" + activiteiten.size() + " activiteiten geladen", Toast.LENGTH_SHORT);
        updateGUI();
    }
}
