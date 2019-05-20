package com.android.shamim.taketour;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by tmshamim on 1/4/2018.
 */

public class CityNameSuggestions extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.android.shamim.taketour.CityNameSuggestions";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public CityNameSuggestions() {
        setupSuggestions(AUTHORITY,MODE);
    }
}
