package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.MinMaxProperty;
import eu.tjenwellens.vrijetijdsapp.properties.MultiValueProperty;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyTypeUnknownException;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
import eu.tjenwellens.vrijetijdsapp.properties.RatingProperty;
import eu.tjenwellens.vrijetijdsapp.properties.SingleValueProperty;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tjen
 */
public class EditActivity extends Activity {
    private EditText txtName, txtDescription, txtManual, txtPersonenMin,
            txtPersonenMax, txtPrijsMin, txtPrijsMax, txtTijdMin, txtTijdMax, txtTags;
    private RadioButton rbtnPlaatsBinnen, rbtnPlaatsBuiten, rbtnEnergieRustig, rbtnEnergieActief, rbtnRatingFun, rbtnRatingNoFun, rbtnRatingTry, rbtnRatingNoTry;
//    private Button btnCancel, btnCreate;
    private RadioGroup rgPlaats, rgEnergie, rgRating;
    //
    private Activiteit oldActiviteit;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
        this.oldActiviteit = loadActiviteit(getOldNameFromIntent());
        updateGUI(oldActiviteit);
    }

    private void initGUI() {
        setContentView(R.layout.activity_edit);
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
        // rbtn
        rbtnPlaatsBinnen = (RadioButton) findViewById(R.id.rbtnPlaatsBinnen);
        rbtnPlaatsBuiten = (RadioButton) findViewById(R.id.rbtnPlaatsBuiten);
        rbtnEnergieRustig = (RadioButton) findViewById(R.id.rbtnEnergieRustig);
        rbtnEnergieActief = (RadioButton) findViewById(R.id.rbtnEnergieActief);

        rbtnRatingFun = (RadioButton) findViewById(R.id.rbtnRatingFun);
        rbtnRatingNoFun = (RadioButton) findViewById(R.id.rbtnRatingNoFun);
        rbtnRatingTry = (RadioButton) findViewById(R.id.rbtnRatingTry);
        rbtnRatingNoTry = (RadioButton) findViewById(R.id.rbtnRatingNoTry);
        //        // btn
        //        btnCancel = (Button) findViewById(R.id.btnCancel);
        //        btnCreate = (Button) findViewById(R.id.btnCreate);
        // radiogroup
        rgPlaats = (RadioGroup) findViewById(R.id.rgPlaats);
        rgEnergie = (RadioGroup) findViewById(R.id.rgEnergie);
        rgRating = (RadioGroup) findViewById(R.id.rgRating);
    }

    private String getOldNameFromIntent() {
        return getIntent().getExtras().getString(ApplicationVrijetijdsApp.ACTIVITEIT_NAME);
    }

    private Activiteit loadActiviteit(String oldName) {
        return ((ApplicationVrijetijdsApp) getApplication()).getData().getActiviteit(oldName);
    }

    private void updateGUI(Activiteit a) {
        txtName.setText(a.getName());
        txtDescription.setText(a.getDescription());
        txtManual.setText(a.getManual());
        for (Map.Entry<PropertyType, Property> entry : a.getProperties().entrySet()) {
            PropertyType type = entry.getKey();
            Property property = entry.getValue();
            switch (type) {
                case ENERGY:
                    String energy = ((SingleValueProperty) property).getValue();
                    if ("rustig".equalsIgnoreCase(energy)) {
                        rbtnEnergieRustig.setChecked(true);
                    } else if ("actief".equalsIgnoreCase(energy)) {
                        rbtnEnergieActief.setChecked(true);
                    }
                    break;
                case LOCATION:
                    String location = ((SingleValueProperty) property).getValue();
                    if ("binnen".equalsIgnoreCase(location)) {
                        rbtnPlaatsBinnen.setChecked(true);
                    } else if ("buiten".equalsIgnoreCase(location)) {
                        rbtnPlaatsBuiten.setChecked(true);
                    }
                    break;
                case TIME:
                    int minTime = ((MinMaxProperty) property).getMin();
                    int maxTime = ((MinMaxProperty) property).getMax();
                    txtTijdMin.setText(String.valueOf(minTime));
                    txtTijdMax.setText(String.valueOf(maxTime));
                    break;
                case PEOPLE:
                    int minPpl = ((MinMaxProperty) property).getMin();
                    int maxPpl = ((MinMaxProperty) property).getMax();
                    txtPersonenMin.setText(String.valueOf(minPpl));
                    txtPersonenMax.setText(String.valueOf(maxPpl));
                    break;
                case PRICE:
                    int minPrice = ((MinMaxProperty) property).getMin();
                    int maxPrice = ((MinMaxProperty) property).getMax();
                    txtPrijsMin.setText(String.valueOf(minPrice));
                    txtPrijsMax.setText(String.valueOf(maxPrice));
                    break;
                case RATING:
                    Rating rating = ((RatingProperty) property).getRating();
                    switch (rating) {
                        case FUN:
                            rbtnRatingFun.setChecked(true);
                            break;
                        case NOT_FUN:
                            rbtnRatingNoFun.setChecked(true);
                            break;
                        case NOT_TRY:
                            rbtnRatingNoTry.setChecked(true);
                            break;
                        case TRY:
                            rbtnRatingTry.setChecked(true);
                            break;
                    }
                    break;
                case TAGS:
                    String tags = ((MultiValueProperty) property).getValue(",");
                    txtTags.setTag(tags);
                    break;
                default:
                    throw new PropertyTypeUnknownException(type);
            }
        }
    }

    private Activiteit updateActiviteit(Activiteit oldActiviteit) {
        String newName, newDescription, newManual;
        Set<Property> newProperties = new HashSet<Property>();
        newName = txtName.getText().toString().trim();
        newDescription = txtDescription.getText().toString().trim();
        newManual = txtManual.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, R.string.toast_edit_nameless, Toast.LENGTH_SHORT).show();
            return null;
        }
        // Personen
        String minPers = txtPersonenMin.getText().toString().trim();
        String maxPers = txtPersonenMax.getText().toString().trim();
        if (!minPers.isEmpty() || !maxPers.isEmpty()) {
            int persMin = minPers.isEmpty() ? -1 : Integer.parseInt(minPers);
            int persMax = maxPers.isEmpty() ? 1000 : Integer.parseInt(maxPers);
            newProperties.add(PropertyType.createPeopleProperty(persMin, persMax));
        }
        // Prijs
        String minPrijs = txtPrijsMin.getText().toString().trim();
        String maxPrijs = txtPrijsMax.getText().toString().trim();
        if (!minPrijs.isEmpty() || !maxPrijs.isEmpty()) {
            int min = minPrijs.isEmpty() ? -1 : Integer.parseInt(minPrijs);
            int max = maxPrijs.isEmpty() ? 1000 : Integer.parseInt(maxPrijs);
            newProperties.add(PropertyType.createPriceProperty(min, max));
        }
        // Tijd
        String minTijd = txtTijdMin.getText().toString().trim();
        String maxTijd = txtTijdMax.getText().toString().trim();
        if (!minTijd.isEmpty() || !maxTijd.isEmpty()) {
            int min = minTijd.isEmpty() ? -1 : Integer.parseInt(minTijd);
            int max = maxTijd.isEmpty() ? 1000 : Integer.parseInt(maxTijd);
            newProperties.add(PropertyType.createTimeProperty(min, max));
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
                newProperties.add(PropertyType.createLocationProperty(location));
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
                newProperties.add(PropertyType.createEnergyProperty(energy));
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
            newProperties.add(PropertyType.createTagsProperty(tagSet));
        }
        // Rating
        checkedId = rgRating.getCheckedRadioButtonId();
        Rating rating = null;
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
            newProperties.add(PropertyType.createRatingProperty(rating));
        }
        // Create activiteit
        Activiteit a = ((ApplicationVrijetijdsApp) getApplication()).getData().updateActiviteit(oldActiviteit, newName, newDescription, newManual, newProperties);
        if (a == null) {
            Toast.makeText(this, R.string.toast_edit_fail, Toast.LENGTH_SHORT).show();
            return null;
        }
        return a;
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
        storeActivityNameToIntent(returnIntent, oldActiviteit);
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_edit_removed, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btnSave(View button) {
        Activiteit a = updateActiviteit(oldActiviteit);
        if (a == null) {
            return;
        }
        Intent returnIntent = new Intent();
        storeActivityNameToIntent(returnIntent, a);
        setResult(RESULT_OK, returnIntent);
        Toast.makeText(this, R.string.toast_edit_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void storeActivityNameToIntent(Intent intent, Activiteit a) {
        intent.putExtra(ApplicationVrijetijdsApp.ACTIVITEIT_NAME, a.getName());
    }
}