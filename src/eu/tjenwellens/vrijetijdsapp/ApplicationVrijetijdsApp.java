package eu.tjenwellens.vrijetijdsapp;

import android.app.Application;
import eu.tjenwellens.vrijetijdsapp.storage.StorageStrategy;
import eu.tjenwellens.vrijetijdsapp.storage.database.DatabaseStorage;

/**
 *
 * @author Tjen
 */
public class ApplicationVrijetijdsApp extends Application {
    private StorageStrategy data;

    public StorageStrategy getData() {
        return data;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        data = new DatabaseStorage(this.getApplicationContext());
    }
}
