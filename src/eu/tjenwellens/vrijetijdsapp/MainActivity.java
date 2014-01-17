package eu.tjenwellens.vrijetijdsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends Activity {
    public static final int CODE_CREATE_ACTIVITY = 1;
    public static final int CODE_SEARCH_ACTIVITY = 2;
    private TextView lblMain;
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
        lblMain = (TextView) findViewById(R.id.lblMain);
    }

    private void loadAllActiviteiten() {
        activiteiten = new TreeSet<Activiteit>(application.getData().getAllActiviteiten());
        updateGUI();
    }

    private void loadSelection() {
        activiteiten = new TreeSet<Activiteit>(application.getData().getLatestSelection());
        Toast.makeText(this, R.string.toast_search_success, Toast.LENGTH_SHORT).show();
        updateGUI();
    }

    private void updateGUI() {
        StringBuilder sb = new StringBuilder();
        for (Activiteit a : activiteiten) {
            sb.append(a.toString()).append('\n');
        }
        lblMain.setText(sb);
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
                Toast.makeText(this, R.string.toast_create_cancel, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CODE_SEARCH_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                loadSelection();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, R.string.toast_search_cancel, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addActiviteit(Activiteit a) {
        activiteiten.add(a);
        updateGUI();
    }

    private Activiteit readActiviteit(Intent intent) {
        String name = intent.getStringExtra("activiteit_name");
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
}
