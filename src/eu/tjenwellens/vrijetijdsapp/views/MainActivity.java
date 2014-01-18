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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity {
    public static final int CODE_CREATE_ACTIVITY = 1;
    public static final int CODE_SEARCH_ACTIVITY = 2;
    public static final int CODE_EDIT_ACTIVITY = 3;
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
        activiteiten = new TreeSet<Activiteit>(application.getData().getAllActiviteiten());
        updateGUI();
    }

    private void loadSelection() {
        activiteiten = new TreeSet<Activiteit>(application.getData().getLatestSelection());
        updateGUI();
    }

    private void updateGUI() {
        container.removeAllViews();
        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Removed views");
        TextView tv;
        for (final Activiteit a : activiteiten) {
            tv = new TextView(this);
            tv.setText(a.toString());
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
        // TODO: start edit or menu
        Toast.makeText(this, "Activiteit clicked: " + a, Toast.LENGTH_SHORT).show();
        startEditActivity(a.getName());
    }

    /*
     * Handle input from started activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_CREATE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Activiteit a = readActiviteit(data);
                addActiviteit(a);
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == CODE_SEARCH_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                loadSelection();
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private void addActiviteit(Activiteit a) {
        activiteiten.add(a);
        updateGUI();
    }

    private Activiteit readActiviteit(Intent intent) {
        String name = intent.getStringExtra(ApplicationVrijetijdsApp.ACTIVITEIT_NAME);
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
                startCreateActivity();
                return true;
            case R.id.menu_settings:
                // todo
                return true;
            case R.id.menu_search:
                startFilterActivity();
                return true;
            case R.id.menu_show_all:
                loadAllActiviteiten();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startCreateActivity() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivityForResult(intent, CODE_CREATE_ACTIVITY);
    }

    private void startFilterActivity() {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivityForResult(intent, CODE_SEARCH_ACTIVITY);
    }

    private void startEditActivity(String activiteitName) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(ApplicationVrijetijdsApp.ACTIVITEIT_NAME, activiteitName);
        startActivityForResult(intent, CODE_EDIT_ACTIVITY);
    }
}