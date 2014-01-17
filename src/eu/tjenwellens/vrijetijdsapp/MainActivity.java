package eu.tjenwellens.vrijetijdsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MainActivity extends Activity {
    public static final int CODE_CREATE_ACTIVITY = 1;
    public static final int CODE_SEARCH_ACTIVITY = 1;
    private TextView lblMain;
    private List<Activiteit> activiteiten;
    ApplicationVrijetijdsApp application;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (ApplicationVrijetijdsApp) getApplication();
        initGUI();
        loadActiviteiten();
    }

    private void initGUI() {
        setContentView(R.layout.main);
        lblMain = (TextView) findViewById(R.id.lblMain);
    }

    private void loadActiviteiten() {
        activiteiten = application.getData().getAllActiviteiten();
        updateGUI();
    }

    private void updateGUI() {
        StringBuilder sb = new StringBuilder();
        for (Activiteit a : activiteiten) {
            sb.append(a.getName()).append('\n');
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
                Toast.makeText(this, "Changes cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addActiviteit(Activiteit a) {
        activiteiten.add(a);
        updateGUI();
        Toast.makeText(this, "Activeit " + a.getName() + " added", Toast.LENGTH_SHORT).show();
    }

    private Activiteit readActiviteit(Intent intent) {
        String name = intent.getStringExtra("activiteit_name");
        if (name == null) {
            return null;
        }
        return ((ApplicationVrijetijdsApp) getApplication()).getData().getActiviteit(name);
    }

    private void startCreateActivity() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivityForResult(intent, CODE_CREATE_ACTIVITY);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
