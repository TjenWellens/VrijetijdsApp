package eu.tjenwellens.vrijetijdsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tjen
 */
public class FilterActivity extends Activity {
//    private EditText txtName, txtDescription, txtManual;
    private EditText txtPersonen, txtPrijs, txtTijd, txtTags;
//    private RadioButton rbtnPlaatsBinnen, rbtnPlaatsBuiten, rbtnEnergieRustig, rbtnEnergieActief;
//    private Button btnCancel, btnCreate;
    private RadioGroup rgPlaats, rgEnergie;
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
        // txt
//        txtName = (EditText) findViewById(R.id.txtName);
//        txtDescription = (EditText) findViewById(R.id.txtDescription);
//        txtManual = (EditText) findViewById(R.id.txtManual);
        txtPersonen = (EditText) findViewById(R.id.txtPersonen);
        txtPrijs = (EditText) findViewById(R.id.txtPrijs);
        txtTijd = (EditText) findViewById(R.id.txtTijd);
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

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void btnSearch(View button) {
        application.getData().filterActiviteiten(createFilters());
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private Set<Filter> createFilters() {
        Set<Filter> filters = new HashSet<Filter>();
        // Personen
        String persString = txtPersonen.getText().toString().trim();
        if (!persString.isEmpty()) {
            int ppl = Integer.parseInt(persString);
            filters.add(new Filter(PropertyType.PEOPLE, String.valueOf(ppl)));
        }
        // Prijs
        String priceString = txtPrijs.getText().toString().trim();
        if (!priceString.isEmpty()) {
            int price = Integer.parseInt(priceString);
            filters.add(new Filter(PropertyType.PRICE, String.valueOf(price)));
        }
        // Tijd
        String timeString = txtTijd.getText().toString().trim();
        if (!timeString.isEmpty()) {
            int time = Integer.parseInt(timeString);
            filters.add(new Filter(PropertyType.TIME, String.valueOf(time)));
        }
        // Plaats
        int checkedId = rgPlaats.getCheckedRadioButtonId();
        if (checkedId != -1) {
            String location = null;
            if (checkedId == R.id.rbtnPlaatsBinnen) {
                location = "binnen";
            } else if (checkedId == R.id.rbtnPlaatsBuiten) {
                location = "buiten";
            } else {
                Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", findViewById(checkedId));
            }
            if (location != null) {
                filters.add(new Filter(PropertyType.LOCATION, location));
            }
        }
        // Energie
        checkedId = rgEnergie.getCheckedRadioButtonId();
        if (checkedId != -1) {
            String energy = null;
            if (checkedId == R.id.rbtnEnergieActief) {
                energy = "actief";
            } else if (checkedId == R.id.rbtnEnergieRustig) {
                energy = "rustig";
            } else {
                Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in energiegroup: {0}", findViewById(checkedId));
            }
            if (energy != null) {
                filters.add(new Filter(PropertyType.ENERGY, energy));
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
            filters.add(new Filter(PropertyType.TAGS, tagSet));
        }
        for (Filter filter : filters) {
            Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Filters:");
            Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Filter: " + filter);
        }
        return filters;
    }
}
