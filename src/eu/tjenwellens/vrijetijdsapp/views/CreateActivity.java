package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
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
    private RadioGroup rgPlaats, rgEnergie, rgRating;

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
        rgRating = (RadioGroup) findViewById(R.id.rgRating);
    }

    private Activiteit createActiviteit() {
        String name, description, manual;
        Set<Property> properties = new HashSet<Property>();
        name = txtName.getText().toString().trim();
        description = txtDescription.getText().toString().trim();
        manual = txtManual.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.toast_create_nameless, Toast.LENGTH_SHORT).show();
            return null;
        }
        // Personen
        String minPers = txtPersonenMin.getText().toString().trim();
        String maxPers = txtPersonenMax.getText().toString().trim();
        if (!minPers.isEmpty() || !maxPers.isEmpty()) {
            int persMin = minPers.isEmpty() ? -1 : Integer.parseInt(minPers);
            int persMax = maxPers.isEmpty() ? 1000 : Integer.parseInt(maxPers);
            properties.add(PropertyType.createPeopleProperty(persMin, persMax));
        }
        // Prijs
        String minPrijs = txtPrijsMin.getText().toString().trim();
        String maxPrijs = txtPrijsMax.getText().toString().trim();
        if (!minPrijs.isEmpty() || !maxPrijs.isEmpty()) {
            int min = minPrijs.isEmpty() ? -1 : Integer.parseInt(minPrijs);
            int max = maxPrijs.isEmpty() ? 1000 : Integer.parseInt(maxPrijs);
            properties.add(PropertyType.createPriceProperty(min, max));
        }
        // Tijd
        String minTijd = txtTijdMin.getText().toString().trim();
        String maxTijd = txtTijdMax.getText().toString().trim();
        if (!minTijd.isEmpty() || !maxTijd.isEmpty()) {
            int min = minTijd.isEmpty() ? -1 : Integer.parseInt(minTijd);
            int max = maxTijd.isEmpty() ? 1000 : Integer.parseInt(maxTijd);
            properties.add(PropertyType.createTimeProperty(min, max));
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
                Logger.getLogger(CreateActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", findViewById(checkedId));
            }
            if (location != null) {
                properties.add(PropertyType.createLocationProperty(location));
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
                Logger.getLogger(CreateActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in energiegroup: {0}", findViewById(checkedId));
            }
            if (energy != null) {
                properties.add(PropertyType.createEnergyProperty(energy));
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
            properties.add(PropertyType.createTagsProperty(tagSet));
        }
        // Rating
        checkedId = rgRating.getCheckedRadioButtonId();
        Rating rating=null;
        switch (checkedId) {
            case R.id.rbtnRatingFun:
                rating = Rating.FUN;
                break;
            case R.id.rbtnRatingNoFun:
                rating = Rating.NOT_FUN;
                break;
            case R.id.rbtnRatingTry:
                rating = Rating.TRY;
                break;
            case R.id.rbtnRatingNoTry:
                rating = Rating.NOT_TRY;
                break;
            case -1:
            default:
                Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", findViewById(checkedId));
        }
        if (rating != null) {
            properties.add(PropertyType.createRatingProperty(rating));
        }
        // Create activiteit
        Activiteit a = ((ApplicationVrijetijdsApp) getApplication()).getData().createActiviteit(name, description, manual, properties);
        if (a == null) {
            Toast.makeText(this, R.string.toast_create_fail, Toast.LENGTH_SHORT).show();
            return null;
        }
        return a;
    }

    public void btnCancel(View button) {
        setResult(RESULT_CANCELED, new Intent());
        Toast.makeText(this, R.string.toast_create_cancel, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnCreate(View button) {
        Activiteit a = createActiviteit();
        if (a == null) {
            return;
        }
        Intent returnIntent = new Intent();
        storeActivityNameToIntent(returnIntent, a);
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_create_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void storeActivityNameToIntent(Intent intent, Activiteit a) {
        intent.putExtra(ApplicationVrijetijdsApp.ACTIVITEIT_NAME, a.getName());
    }
}
