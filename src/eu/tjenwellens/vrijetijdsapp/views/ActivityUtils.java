package eu.tjenwellens.vrijetijdsapp.views;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import eu.tjenwellens.vrijetijdsapp.Activiteit;
import eu.tjenwellens.vrijetijdsapp.ApplicationVrijetijdsApp;
import eu.tjenwellens.vrijetijdsapp.R;
import eu.tjenwellens.vrijetijdsapp.properties.Energy;
import eu.tjenwellens.vrijetijdsapp.properties.EnumProperty;
import eu.tjenwellens.vrijetijdsapp.properties.Filter;
import eu.tjenwellens.vrijetijdsapp.properties.Location;
import eu.tjenwellens.vrijetijdsapp.properties.MinMaxProperty;
import eu.tjenwellens.vrijetijdsapp.properties.MultiValueProperty;
import eu.tjenwellens.vrijetijdsapp.properties.Property;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyType;
import eu.tjenwellens.vrijetijdsapp.properties.PropertyTypeUnknownException;
import eu.tjenwellens.vrijetijdsapp.properties.Rating;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tjen
 */
abstract class ActivityUtils {
    public static final int CODE_CREATE_ACTIVITY = 1;
    public static final int CODE_SEARCH_ACTIVITY = 2;
    public static final int CODE_DETAILS_ACTIVITY = 3;
    public static final int CODE_EDIT_ACTIVITY = 4;
    public static final String ACTIVITEIT_NAME = "activiteit_name";

    public static Activiteit createActiviteit(Activity activity) {
        String name = Properties.getName(activity);
        if (name.isEmpty()) {
            Toast.makeText(activity, R.string.toast_create_nameless, Toast.LENGTH_SHORT).show();
            return null;
        }
        String description = Properties.getDescription(activity);
        String manual = Properties.getManual(activity);
        Set<Property> properties = new HashSet<Property>();
        Properties.addProperties(activity, properties);
        Activiteit activiteit = ((ApplicationVrijetijdsApp) activity.getApplication()).getData().createActiviteit(name, description, manual, properties);
        if (activiteit == null) {
            Toast.makeText(activity, R.string.toast_create_fail, Toast.LENGTH_SHORT).show();
            return null;
        }
        return activiteit;
    }

    public static Set<Filter> createFilters(Activity activity) {
        Set<Filter> filters = new HashSet<Filter>();
        Filters.addFilters(activity, filters);
        return filters;
    }

    public static void storeActivityNameToIntent(Intent intent, String name) {
        intent.putExtra(ACTIVITEIT_NAME, name);
    }

    public static String getActiviteitNameFromIntent(Intent intent) {
        return intent.getExtras().getString(ACTIVITEIT_NAME);
    }

    public static Activiteit updateActiviteit(Activity activity, Activiteit oldActiviteit) {
        String newName = Properties.getName(activity);
        if (newName.isEmpty()) {
            Toast.makeText(activity, R.string.toast_edit_nameless, Toast.LENGTH_SHORT).show();
            return null;
        }
        String newDescription = Properties.getDescription(activity);
        String newManual = Properties.getManual(activity);
        Set<Property> newProperties = new HashSet<Property>();
        Properties.addProperties(activity, newProperties);
        // Create activiteit
        Activiteit a = ((ApplicationVrijetijdsApp) activity.getApplication()).getData().updateActiviteit(oldActiviteit, newName, newDescription, newManual, newProperties);
        if (a == null) {
            Toast.makeText(activity, R.string.toast_edit_fail, Toast.LENGTH_SHORT).show();
            return null;
        }
        return a;
    }

    public static void updateGUI(Activity a, Activiteit activiteit) {
        UpdateGUI.setName(a, activiteit.getName());
        UpdateGUI.setDescription(a, activiteit.getDescription());
        UpdateGUI.setManual(a, activiteit.getManual());
        UpdateGUI.setProperties(a, activiteit.getProperties().values());
    }

    public static void startCreateActivity(Activity activity) {
        Intent intent = new Intent(activity, CreateActivity.class);
        activity.startActivityForResult(intent, CODE_CREATE_ACTIVITY);
    }

    public static void startFilterActivity(Activity activity) {
        Intent intent = new Intent(activity, FilterActivity.class);
        activity.startActivityForResult(intent, CODE_SEARCH_ACTIVITY);
    }

    public static void startDetailsActivity(Activity activity, String activiteitName) {
        if (activiteitName == null) {
            return;
        }
        Intent intent = new Intent(activity, DetailsActivity.class);
        storeActivityNameToIntent(intent, activiteitName);
        activity.startActivityForResult(intent, CODE_DETAILS_ACTIVITY);
    }

    public static void startRandomActivity(Activity activity) {
        startDetailsActivity(activity, getRandomNameFromSelection(activity));
    }

    public static void startEditActivity(Activity activity, String activiteitName) {
        Intent intent = new Intent(activity, EditActivity.class);
        storeActivityNameToIntent(intent, activiteitName);
        activity.startActivityForResult(intent, CODE_EDIT_ACTIVITY);
    }

    //<editor-fold defaultstate="collapsed" desc="private functions (bundled in classes)">
    private static String getRandomNameFromSelection(Activity activity) {
        String name = ((ApplicationVrijetijdsApp) activity.getApplication()).getData().getRandomNameFromSelection();
        if (name == null) {
            Toast.makeText(activity, R.string.toast_random_fail, Toast.LENGTH_SHORT).show();
        }
        return name;
    }

    private static class Properties {
        private static String getName(Activity a) {
            return ((EditText) a.findViewById(R.id.txtName)).getText().toString().trim();
        }

        private static String getDescription(Activity a) {
            return ((EditText) a.findViewById(R.id.txtDescription)).getText().toString().trim();
        }

        private static String getManual(Activity a) {
            return ((EditText) a.findViewById(R.id.txtManual)).getText().toString().trim();
        }

        private static void addProperties(Activity a, Collection<Property> properties) {
            properties.add(getPeopleProperty(a));
            properties.add(getPriceProperty(a));
            properties.add(getTimeProperty(a));
            properties.add(getPlaceProperty(a));
            properties.add(getEnergyProperty(a));
            properties.add(getTagsProperty(a));
            properties.add(getRatingProperty(a));
            while (properties.remove(null)) {
            }
        }

        private static Property getPeopleProperty(Activity a) {
            Property p = null;
            String minPers = ((EditText) a.findViewById(R.id.txtPersonenMin)).getText().toString().trim();
            String maxPers = ((EditText) a.findViewById(R.id.txtPersonenMax)).getText().toString().trim();
            if (!minPers.isEmpty() || !maxPers.isEmpty()) {
                int min = minPers.isEmpty() ? -1 : Integer.parseInt(minPers);
                int max = maxPers.isEmpty() ? 1000 : Integer.parseInt(maxPers);
                p = PropertyType.createPeopleProperty(min, max);
            }
            return p;
        }

        private static Property getPriceProperty(Activity a) {
            Property p = null;
            String minPrijs = ((EditText) a.findViewById(R.id.txtPrijsMin)).getText().toString().trim();
            String maxPrijs = ((EditText) a.findViewById(R.id.txtPrijsMax)).getText().toString().trim();
            if (!minPrijs.isEmpty() || !maxPrijs.isEmpty()) {
                int min = minPrijs.isEmpty() ? -1 : Integer.parseInt(minPrijs);
                int max = maxPrijs.isEmpty() ? 1000 : Integer.parseInt(maxPrijs);
                p = PropertyType.createPriceProperty(min, max);
            }
            return p;
        }

        private static Property getTimeProperty(Activity a) {
            Property p = null;
            String minTijd = ((EditText) a.findViewById(R.id.txtTijdMin)).getText().toString().trim();
            String maxTijd = ((EditText) a.findViewById(R.id.txtTijdMax)).getText().toString().trim();
            if (!minTijd.isEmpty() || !maxTijd.isEmpty()) {
                int min = minTijd.isEmpty() ? -1 : Integer.parseInt(minTijd);
                int max = maxTijd.isEmpty() ? 1000 : Integer.parseInt(maxTijd);
                p = PropertyType.createTimeProperty(min, max);
            }
            return p;
        }

        private static Property getPlaceProperty(Activity a) {
            Property p = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgPlaats)).getCheckedRadioButtonId();
            Location location = null;
            switch (checkedId) {
                case R.id.rbtnPlaatsBinnen:
                    location = Location.INSIDE;
                    break;
                case R.id.rbtnPlaatsBuiten:
                    location = Location.OUTSIDE;
                    break;
                case -1:
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (location != null) {
                p = PropertyType.createLocationProperty(location);
            }
            return p;
        }

        private static Property getEnergyProperty(Activity a) {
            Property p = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgEnergie)).getCheckedRadioButtonId();
            Energy energy = null;
            switch (checkedId) {
                case R.id.rbtnEnergieActief:
                    energy = Energy.ACTIVE;
                    break;
                case R.id.rbtnEnergieRustig:
                    energy = Energy.CALM;
                    break;
                case -1:
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (energy != null) {
                p = PropertyType.createEnergyProperty(energy);
            }
            return p;
        }

        private static Property getTagsProperty(Activity a) {
            Property p = null;
            String tagText = ((EditText) a.findViewById(R.id.txtTags)).getText().toString().trim();
            if (!tagText.isEmpty()) {
                HashSet<String> tagSet = new HashSet<String>();
                String[] tags = tagText.split(",");
                for (int i = 0; i < tags.length; i++) {
                    tagSet.add(tags[i].trim());
                }
                p = PropertyType.createTagsProperty(tagSet);
            }
            return p;
        }

        private static Property getRatingProperty(Activity a) {
            Property p = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgRating)).getCheckedRadioButtonId();
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
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (rating != null) {
                p = PropertyType.createRatingProperty(rating);
            }
            return p;
        }
    }

    private static class Filters {
        private static void addFilters(Activity a, Collection<Filter> filters) {
            filters.add(getPeopleFilter(a));
            filters.add(getPriceFilter(a));
            filters.add(getTimeFilter(a));
            filters.add(getPlaceFilter(a));
            filters.add(getEnergyFilter(a));
            filters.add(getTagsFilter(a));
            filters.add(getRatingFilter(a));
            while (filters.remove(null)) {
            }
        }

        private static Filter getPeopleFilter(Activity a) {
            Filter f = null;
            String valueString = ((EditText) a.findViewById(R.id.txtPersonen)).getText().toString().trim();
            if (!valueString.isEmpty()) {
                int valueInt = Integer.parseInt(valueString);
                f = new Filter(PropertyType.PEOPLE, String.valueOf(valueInt));
            }
            return f;
        }

        private static Filter getPriceFilter(Activity a) {
            Filter f = null;
            String valueString = ((EditText) a.findViewById(R.id.txtPrijs)).getText().toString().trim();
            if (!valueString.isEmpty()) {
                int valueInt = Integer.parseInt(valueString);
                f = new Filter(PropertyType.PRICE, String.valueOf(valueInt));
            }
            return f;
        }

        private static Filter getTimeFilter(Activity a) {
            Filter f = null;
            String valueString = ((EditText) a.findViewById(R.id.txtTijd)).getText().toString().trim();
            if (!valueString.isEmpty()) {
                int valueInt = Integer.parseInt(valueString);
                f = new Filter(PropertyType.TIME, String.valueOf(valueInt));
            }
            return f;
        }

        private static Filter getPlaceFilter(Activity a) {
            Filter f = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgPlaats)).getCheckedRadioButtonId();
            Location location = null;
            switch (checkedId) {
                case R.id.rbtnPlaatsBinnen:
                    location = Location.INSIDE;
                    break;
                case R.id.rbtnPlaatsBuiten:
                    location = Location.OUTSIDE;
                    break;
                case -1:
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (location != null) {
                f = new Filter(PropertyType.LOCATION, location.name());
            }
            return f;
        }

        private static Filter getEnergyFilter(Activity a) {
            Filter f = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgEnergie)).getCheckedRadioButtonId();
            Energy energy = null;
            switch (checkedId) {
                case R.id.rbtnEnergieActief:
                    energy = Energy.ACTIVE;
                    break;
                case R.id.rbtnEnergieRustig:
                    energy = Energy.CALM;
                    break;
                case -1:
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (energy != null) {
                f = new Filter(PropertyType.ENERGY, energy.name());
            }
            return f;
        }

        private static Filter getTagsFilter(Activity a) {
            Filter f = null;
            String tagText = ((EditText) a.findViewById(R.id.txtTags)).getText().toString().trim();
            if (!tagText.isEmpty()) {
                HashSet<String> tagSet = new HashSet<String>();
                String[] tags = tagText.split(",");
                for (int i = 0; i < tags.length; i++) {
                    tagSet.add(tags[i].trim());
                }
                f = new Filter(PropertyType.TAGS, tagSet);
            }
            return f;
        }

        private static Filter getRatingFilter(Activity a) {
            Filter f = null;
            int checkedId = ((RadioGroup) a.findViewById(R.id.rgRating)).getCheckedRadioButtonId();
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
                    // ignore
                    break;
                default:
                    Logger.getLogger(FilterActivity.class.toString()).log(Level.SEVERE, "Wrong radiobutton found in plaatsgroup: {0}", a.findViewById(checkedId));
            }
            if (rating != null) {
                f = new Filter(PropertyType.RATING, rating.name());
            }
            return f;
        }
    }

    private static class UpdateGUI {
        private static void setName(Activity a, String name) {
            ((EditText) a.findViewById(R.id.txtName)).setText(name);
        }

        private static void setDescription(Activity a, String description) {
            ((EditText) a.findViewById(R.id.txtDescription)).setText(description);
        }

        private static void setManual(Activity a, String manual) {
            ((EditText) a.findViewById(R.id.txtManual)).setText(manual);
        }

        private static void setProperties(Activity a, Collection<Property> properties) {
            for (Property property : properties) {
                switch (property.getType()) {
                    case ENERGY:
                        Energy energy = ((EnumProperty<Energy>) property).getValue();
                        setEnergy(a, energy);
                        break;
                    case LOCATION:
                        Location location = ((EnumProperty<Location>) property).getValue();
                        setLocation(a, location);
                        break;
                    case TIME:
                        int minTime = ((MinMaxProperty) property).getMin();
                        int maxTime = ((MinMaxProperty) property).getMax();
                        setTime(a, minTime, maxTime);
                        break;
                    case PEOPLE:
                        int minPpl = ((MinMaxProperty) property).getMin();
                        int maxPpl = ((MinMaxProperty) property).getMax();
                        setPeople(a, minPpl, maxPpl);
                        break;
                    case PRICE:
                        int minPrice = ((MinMaxProperty) property).getMin();
                        int maxPrice = ((MinMaxProperty) property).getMax();
                        setPrice(a, minPrice, maxPrice);
                        break;
                    case RATING:
                        Rating rating = ((EnumProperty<Rating>) property).getValue();
                        setRating(a, rating);
                        break;
                    case TAGS:
                        String tags = ((MultiValueProperty) property).getValue(",");
                        setTags(a, tags);
                        break;
                    default:
                        throw new PropertyTypeUnknownException(property.getType());
                }
            }
        }

        private static void setEnergy(Activity a, Energy energy) {
            switch (energy) {
                case ACTIVE:
                    ((RadioButton) a.findViewById(R.id.rbtnEnergieActief)).setChecked(true);
                    break;
                case CALM:
                    ((RadioButton) a.findViewById(R.id.rbtnEnergieRustig)).setChecked(true);
                    break;
            }
        }

        private static void setLocation(Activity a, Location location) {
            switch (location) {
                case INSIDE:
                    ((RadioButton) a.findViewById(R.id.rbtnPlaatsBinnen)).setChecked(true);
                    break;
                case OUTSIDE:
                    ((RadioButton) a.findViewById(R.id.rbtnPlaatsBuiten)).setChecked(true);
                    break;
            }
        }

        private static void setTime(Activity a, int minTime, int maxTime) {
            ((EditText) a.findViewById(R.id.txtTijdMin)).setText(String.valueOf(minTime));
            ((EditText) a.findViewById(R.id.txtTijdMax)).setText(String.valueOf(maxTime));
        }

        private static void setPeople(Activity a, int minPpl, int maxPpl) {
            ((EditText) a.findViewById(R.id.txtPersonenMin)).setText(String.valueOf(minPpl));
            ((EditText) a.findViewById(R.id.txtPersonenMax)).setText(String.valueOf(maxPpl));
        }

        private static void setPrice(Activity a, int minPrice, int maxPrice) {
            ((EditText) a.findViewById(R.id.txtPrijsMin)).setText(String.valueOf(minPrice));
            ((EditText) a.findViewById(R.id.txtPrijsMax)).setText(String.valueOf(maxPrice));
        }

        private static void setRating(Activity a, Rating rating) {
            switch (rating) {
                case FUN:
                    ((RadioButton) a.findViewById(R.id.rbtnRatingFun)).setChecked(true);
                    break;
                case NOT_FUN:
                    ((RadioButton) a.findViewById(R.id.rbtnRatingNoFun)).setChecked(true);
                    break;
                case NOT_TRY:
                    ((RadioButton) a.findViewById(R.id.rbtnRatingNoTry)).setChecked(true);
                    break;
                case TRY:
                    ((RadioButton) a.findViewById(R.id.rbtnRatingTry)).setChecked(true);
                    break;
            }
        }

        private static void setTags(Activity a, String tags) {
            ((EditText) a.findViewById(R.id.txtTags)).setTag(tags);
        }
    }
    //</editor-fold>
}
