package eu.tjenwellens.vrijetijdsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.filters.Filter;
import eu.tjenwellens.vrijetijdsapp.filters.Filters;
import eu.tjenwellens.vrijetijdsapp.filters.MinMaxFilter;
import eu.tjenwellens.vrijetijdsapp.filters.MultiValueFilter;
import eu.tjenwellens.vrijetijdsapp.filters.SingleValueFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tjen
 */
public class CreateActivity extends Activity {
    private EditText txtName, txtDescription, txtManual, txtPersonenMin,
            txtPersonenMax, txtPrijsMin, txtPrijsMax, txtTijdMin, txtTijdMax, txtTags;
//    private RadioButton rbtnPlaatsBinnen, rbtnPlaatsBuiten, rbtnEnergieRustig, rbtnEnergieActief;
//    private Button btnCancel, btnCreate;
    private RadioGroup rgPlaats, rgEnergie;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        initGUI();
    }

    private void initGUI() {
        // txt
        txtName = (EditText) findViewById(R.id.txtName);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtManual = (EditText) findViewById(R.id.txtManual);
        txtPersonenMin = (EditText) findViewById(R.id.txtPersonenMin);
        txtPersonenMax = (EditText) findViewById(R.id.txtPersonenMax);
        txtPrijsMin = (EditText) findViewById(R.id.txtPrijsMin);
        txtPrijsMax = (EditText) findViewById(R.id.txtPrijsMax);
        txtTijdMin = (EditText) findViewById(R.id.txtTijdMin);
        txtTijdMax = (EditText) findViewById(R.id.txtTijdMax);
        txtTags = (EditText) findViewById(R.id.txtTags);
        //        // rbtn
        //        rbtnPlaatsBinnen = (RadioButton) findViewById(R.id.rbtnPlaatsBinnen);
        //        rbtnPlaatsBuiten = (RadioButton) findViewById(R.id.rbtnPlaatsBuiten);
        //        rbtnEnergieRustig = (RadioButton) findViewById(R.id.rbtnEnergieRustig);
        //        rbtnEnergieActief = (RadioButton) findViewById(R.id.rbtnEnergieActief);
        //        // btn
        //        btnCancel = (Button) findViewById(R.id.btnCancel);
        //        btnCreate = (Button) findViewById(R.id.btnCreate);
        // radiogroup
        rgPlaats = (RadioGroup) findViewById(R.id.rgPlaats);
        rgEnergie = (RadioGroup) findViewById(R.id.rgEnergie);
    }

    private Activiteit createActiviteit() {
        String name, description, manual;
        Set<Filter> filters = new HashSet<Filter>();
        name = txtName.getText().toString().trim();
        description = txtDescription.getText().toString().trim();
        manual = txtManual.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "You must fill in a name.", Toast.LENGTH_SHORT).show();
            return null;
        }
        // Personen
        String minPers = txtPersonenMin.getText().toString().trim();
        String maxPers = txtPersonenMax.getText().toString().trim();
        if (!minPers.isEmpty() || !minPers.isEmpty()) {
            int persMin = minPers.isEmpty() ? -1 : Integer.parseInt(minPers);
            int persMax = maxPers.isEmpty() ? 1000 : Integer.parseInt(maxPers);
            filters.add(new MinMaxFilter(Filters.PERSONEN, persMin, persMax));
        }
        // Prijs
        String minPrijs = txtPrijsMin.getText().toString().trim();
        String maxPrijs = txtPrijsMax.getText().toString().trim();
        if (!minPrijs.isEmpty() || !minPrijs.isEmpty()) {
            int min = minPrijs.isEmpty() ? -1 : Integer.parseInt(minPrijs);
            int max = maxPrijs.isEmpty() ? 1000 : Integer.parseInt(maxPrijs);
            filters.add(new MinMaxFilter(Filters.PRIJS, min, max));
        }
        // Tijd
        String minTijd = txtTijdMin.getText().toString().trim();
        String maxTijd = txtTijdMax.getText().toString().trim();
        if (!minTijd.isEmpty() || !minTijd.isEmpty()) {
            int min = minTijd.isEmpty() ? -1 : Integer.parseInt(minTijd);
            int max = maxTijd.isEmpty() ? 1000 : Integer.parseInt(maxTijd);
            filters.add(new MinMaxFilter(Filters.TIJD, min, max));
        }
        // Plaats
        int checkedId = rgPlaats.getCheckedRadioButtonId();
        if (checkedId != -1) {
            if (checkedId == R.id.rbtnPlaatsBinnen) {
                filters.add(new SingleValueFilter(Filters.PLAATS, "binnen"));
            } else if (checkedId == R.id.rbtnPlaatsBuiten) {
                filters.add(new SingleValueFilter(Filters.PLAATS, "buiten"));
            } else {
                Logger.getLogger(CreateActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: " + findViewById(checkedId));
            }
        }
        // Energie
        checkedId = rgEnergie.getCheckedRadioButtonId();
        if (checkedId != -1) {
            if (checkedId == R.id.rbtnEnergieActief) {
                filters.add(new SingleValueFilter(Filters.ENERGIE, "actief"));
            } else if (checkedId == R.id.rbtnEnergieRustig) {
                filters.add(new SingleValueFilter(Filters.ENERGIE, "rustig"));
            } else {
                Logger.getLogger(CreateActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in energiegroup: " + findViewById(checkedId));
            }
        }
        // Tags
        String tagText = txtTags.getText().toString().trim();
        if (!tagText.isEmpty()) {
            HashSet<String> tagSet = new HashSet<String>();
            String[] tags = tagText.split(",");
            for (int i = 0; i < tags.length; i++) {
                tagSet.add(tags[i].trim());
            }
            filters.add(new MultiValueFilter(Filters.TAGS, tagSet));
        }
        return new Activiteit(name, description, manual, filters);
    }

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void btnCreate(View button) {
        Activiteit a = createActiviteit();
        if (a == null) {
            return;
        }
        Intent returnIntent = new Intent();
        storeActivityToIntent(returnIntent, a);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void storeActivityToIntent(Intent intent, Activiteit a) {
        intent.putExtra("activiteit_name", a.getName());
        intent.putExtra("activiteit_description", a.getDescription());
        intent.putExtra("activiteit_manual", a.getManual());
        intent.putExtra("activiteit_filters", a.getFilters().values().toArray(new Filter[0]));
    }
}
