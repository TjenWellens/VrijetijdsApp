package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.EnumProperty;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyTypeUnknownException;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
import eu.tjenwellens.vrijetijdsapp.storage.StorageStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tjen
 */
public class LikeActivity extends Activity {
    private List<Activiteit> activiteiten;
    private int activiteitIndex = 0;
    private ViewGroup likeContainer;
    private RadioGroup rgRating;
    private ProgressBar progress;
    private Button btnPrevious, btnNext;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK, new Intent());
        initGUI();
        loadSelection(makeLikeSelection());
        updateGUI();
    }

    private List<Activiteit> makeLikeSelection() {
        Set<Filter> filters = new HashSet<Filter>();
        // These Rating filters contradict eachother.
        // This way only those without the Rating filter get selected.
        filters.add(new Filter(PropertyType.RATING, Rating.FUN.name()));
        filters.add(new Filter(PropertyType.RATING, Rating.NOT_FUN.name()));
        return ((ApplicationVrijetijdsApp) getApplication()).getData().selectWithFilters(filters);
    }

    private void loadSelection(List<Activiteit> activiteiten) {
        this.activiteiten = activiteiten;
        if (activiteiten == null || activiteiten.isEmpty()) {
            Toast.makeText(this, R.string.toast_like_empty_selection, Toast.LENGTH_SHORT).show();
            finish();
        }
        activiteitIndex = 0;
        progress.setMax(activiteiten.size());
    }

    private void initGUI() {
        setContentView(R.layout.activity_like);
        likeContainer = (ViewGroup) findViewById(R.id.likeContainer);
        rgRating = (RadioGroup) findViewById(R.id.rgRating);
        progress = (ProgressBar) findViewById(R.id.progress);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    private TextView createView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        return tv;
    }

    private void updateGUI() {
        Activiteit a = activiteiten.get(activiteitIndex);
        updateProgressBar();
        updateButtons();
        updateDetails(a);
        updateRating(a);
    }

    private void updateProgressBar() {
        progress.setProgress(activiteitIndex + 1);
    }

    private void updateRating(Activiteit a) {
        EnumProperty<Rating> ratingProp = (EnumProperty<Rating>) a.getProperties().get(PropertyType.RATING);
        if (ratingProp == null) {
            rgRating.clearCheck();
        } else {
            ActivityUtils.UpdateGUI.setRating(this, ratingProp.getValue());
        }
    }

    private void updateButtons() {
        if (activiteitIndex > 0) {
            btnPrevious.setEnabled(true);
        } else {
            btnPrevious.setEnabled(false);
        }
        if (activiteitIndex < activiteiten.size() - 1) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    private void updateDetails(Activiteit a) {
        if (a == null) {
            finish();
        }
        likeContainer.removeAllViews();
        likeContainer.addView(createView(a.getName()));
        String text = a.getDescription();
        if (text != null && !text.isEmpty()) {
            likeContainer.addView(createView(getString(R.string.description) + ": " + text));
        }
        text = a.getManual();
        if (text != null && !text.isEmpty()) {
            likeContainer.addView(createView(getString(R.string.manual) + ": " + text));
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
                    // ignore rating
                    continue;
                default:
                    throw new PropertyTypeUnknownException(p.getType());
            }
            likeContainer.addView(createView(text));
        }
    }

    public void btnPrevious(View button) {
        saveCurrentRating();
        // go to previous
        activiteitIndex--;
        if (activiteitIndex < 0) {
            activiteitIndex = 0;
        }
        updateGUI();
    }

    public void btnNext(View button) {
        saveCurrentRating();
        // go to next
        activiteitIndex++;
        if (activiteitIndex >= activiteiten.size()) {
            activiteitIndex = activiteiten.size() - 1;
        }
        updateGUI();
    }

    private boolean saveCurrentRating() {
        StorageStrategy data = ((ApplicationVrijetijdsApp) getApplication()).getData();
        Activiteit currentActiviteit = activiteiten.get(activiteitIndex);
        Rating newRating = ActivityUtils.Properties.getRating(this);
        // TODO: handle if rating changed from something to null (not possible with current GUI)
        if (newRating == null) {
            return false;
        }
        if (data.updateRating(currentActiviteit, newRating)) {
            currentActiviteit.getProperties().put(PropertyType.RATING, PropertyType.createRatingProperty(newRating));
            return true;
        }
        return false;
    }
}
